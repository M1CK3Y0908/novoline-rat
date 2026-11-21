package rat.novoline.module.hud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import rat.novoline.module.ModuleManager;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.utils.render.RenderUtils;

public class PotionHudElement extends HudElement {
   public static PotionHudElement instance;
   private static final Minecraft minecraft = Minecraft.getMinecraft();
   private boolean hasUserMoved = false;
   private float width = 110.0F;
   private float height = 40.0F;

   private static float cachedX = 0.0F;
   private static float cachedY = 0.0F;
   private static float cachedWidth = 110.0F;
   private static float cachedHeight = 40.0F;
   private static int cachedPotionCount = 0;
   private static final List<PotionEntry> cachedEntries = new ArrayList<>();

   private static final Map<Integer, Integer> maxDurationMap = new ConcurrentHashMap<>();

   public static class PotionEntry {
      public final String name;
      public final String duration;
      public final float fraction;

      public PotionEntry(String name, String duration, float fraction) {
         this.name = name;
         this.duration = duration;
         this.fraction = fraction;
      }
   }

   public PotionHudElement() {
      super("potion-hud", 0.0F, 0.0F, true);
      instance = this;
      this.width = cachedWidth;
      this.height = cachedHeight;
   }

   public void checkDefaultPosition(ScaledResolution sr) {
      if (!this.hasUserMoved || (this.x == 0.0F && this.y == 0.0F)) {
         this.x = sr.getFloat() - this.width - 6.0F;
         this.y = sr.getFloat2() - 110.0F;
         cachedX = this.x;
         cachedY = this.y;
      }
   }

   @Override
   public HudElement setX(float f) {
      super.setX(f);
      if (f != 0.0F) {
         this.hasUserMoved = true;
         cachedX = this.x;
      }
      return this;
   }

   @Override
   public HudElement setY(float f) {
      super.setY(f);
      if (f != 0.0F) {
         this.hasUserMoved = true;
         cachedY = this.y;
      }
      return this;
   }

   @Override
   public void updateDragging(ScaledResolution scaledResolution, float f, float f2) {
      checkDefaultPosition(scaledResolution);
      super.updateDragging(scaledResolution, f, f2);
      if (this.isDragging()) {
         this.hasUserMoved = true;
         cachedX = this.x;
         cachedY = this.y;
      }
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      if (!this.hasUserMoved) {
         ScaledResolution sr = new ScaledResolution(minecraft);
         float scaleFactor = sr.getScaleFactor();
         float screenW = sr.getScaledWidth() * scaleFactor / 2.0F;
         float screenH = sr.getScaledHeight() * scaleFactor / 2.0F;
         this.x = screenW - this.width - 6.0F;
         this.y = screenH - 110.0F;
         cachedX = this.x;
         cachedY = this.y;
      }
      return super.handleMouseClick(f, f2, i);
   }

   @Override
   public float getWidth() {
      return this.width;
   }

   @Override
   public float getHeight() {
      return this.height;
   }

   public static void updatePotionHud(ScaledResolution sr) {
      if (instance == null || ModuleManager.potionHUD == null || !ModuleManager.potionHUD.isEnabled()) {
         return;
      }
      instance.checkDefaultPosition(sr);
      cachedX = instance.getX();
      cachedY = instance.getY();
      cachedWidth = instance.getWidth();
      cachedHeight = instance.getHeight();
   }

   @Override
   public void draw(ScaledResolution scaledResolution, float partialTicks, boolean inEditor) {
      updatePotionHud(scaledResolution);

      PotionHUD potionHUD = ModuleManager.potionHUD;
      if (potionHUD == null || !potionHUD.isEnabled()) {
         return;
      }

      EntityPlayerSP player = minecraft.thePlayer;
      List<PotionEntry> entries = new ArrayList<>();

      if (player != null) {
         Collection<PotionEffect> activeEffects = player.getActivePotionEffects();
         for (PotionEffect effect : activeEffects) {
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            if (potion != null) {
               String fullName = I18n.format(potion.getName()) + getAmplifierString(effect.getAmplifier());
               String duration = Potion.getDurationString(effect);
               int dur = effect.getDuration();
               int maxDur = maxDurationMap.compute(effect.getPotionID(), (k, v) -> (v == null || dur > v) ? dur : v);
               float fraction = MathHelper.clamp_float((float) dur / (float) Math.max(1, maxDur), 0.0F, 1.0F);
               entries.add(new PotionEntry(fullName, duration, fraction));
            }
         }
      }

      if (entries.isEmpty() && inEditor) {
         entries.add(new PotionEntry("Speed II", "01:30", 0.75F));
         entries.add(new PotionEntry("Strength I", "00:45", 0.35F));
         entries.add(new PotionEntry("Fire Resistance", "02:15", 0.50F));
      }

      cachedEntries.clear();
      cachedEntries.addAll(entries);
      cachedPotionCount = entries.size();

      if (entries.isEmpty()) {
         this.width = 60.0F;
         this.height = 16.0F;
         return;
      }

      FontAdapter font = potionHUD.getFont();
      float lineHeight = (float) font.getHeight() + (potionHUD.durationBar.isEnabled() ? 6.0F : 4.0F);
      float spacing = 3.0F;

      float maxWidth = 0.0F;
      for (PotionEntry entry : entries) {
         float w = (float) (font.getStringWidth(entry.name) + font.getStringWidth(": ") + font.getStringWidth(entry.duration));
         if (w > maxWidth) {
            maxWidth = w;
         }
      }

      float cardWidth = maxWidth + (potionHUD.background.isEnabled() ? 10.0F : 0.0F);
      this.width = cardWidth;
      this.height = entries.size() * (lineHeight + spacing) - spacing;

      cachedWidth = this.width;
      cachedHeight = this.height;

      float x = this.x;
      float y = this.y;

      int alpha = (int) potionHUD.alpha.getCurrentValue();
      int bgColor = RenderUtils.withAlpha(0, alpha);

      for (int i = 0; i < entries.size(); i++) {
         PotionEntry entry = entries.get(i);
         float itemY = y + (float) i * (lineHeight + spacing);

         // 1. Background
         if (potionHUD.background.isEnabled()) {
            boolean postProcessingActive = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isEnabled();
            if (!postProcessingActive) {
               RenderUtils.drawShadow(x, itemY, x + cardWidth, itemY + lineHeight, 1.5F, 1610612736);
            }
            RenderUtils.drawRoundedRect(x, itemY, cardWidth, lineHeight, 3.0F, bgColor);
         }

         // 2. Text layout: Name in Theme Gradient, Colon in Gray, Duration in White
         float textX = x + (potionHUD.background.isEnabled() ? 5.0F : 0.0F);
         float textY = itemY + (potionHUD.durationBar.isEnabled() ? 1.5F : 2.0F);

         // Name in theme color gradient
         float curX = textX;
         for (int c = 0; c < entry.name.length(); c++) {
            String ch = String.valueOf(entry.name.charAt(c));
            int color = ModuleManager.themeColor.getColor((double) curX, (double) textY, c, entry.name.length());
            font.drawStringWithShadow(ch, curX, textY, color);
            curX += (float) font.getStringWidth(ch);
         }

         // Colon in gray
         font.drawStringWithShadow(": ", curX, textY, 0xFFAAAAAA);
         curX += (float) font.getStringWidth(": ");

         // Duration in white
         font.drawStringWithShadow(entry.duration, curX, textY, -1);

         // 3. Duration progress bar
         if (potionHUD.durationBar.isEnabled()) {
            float barX = x + 1.5F;
            float barY = itemY + lineHeight - 2.0F;
            float maxBarW = cardWidth - 3.0F;
            float curBarW = maxBarW * entry.fraction;
            RenderUtils.drawRect(barX, barY, barX + maxBarW, barY + 1.0F, 0x40000000);
            if (curBarW > 0.0F) {
               RenderUtils.drawThemeGradientBar(barX, barY, barX + curBarW, barY + 1.0F);
            }
         }
      }
   }

   public static void renderPotionHudEffects(boolean bloom, boolean glow, ScaledResolution sr) {
      if (instance == null || ModuleManager.potionHUD == null || !ModuleManager.potionHUD.isEnabled()) {
         return;
      }
      if (cachedEntries.isEmpty() && (minecraft.thePlayer == null || minecraft.thePlayer.getActivePotionEffects().isEmpty())) {
         return;
      }

      PotionHUD potionHUD = ModuleManager.potionHUD;
      FontAdapter font = potionHUD.getFont();
      float lineHeight = (float) font.getHeight() + (potionHUD.durationBar.isEnabled() ? 6.0F : 4.0F);
      float spacing = 3.0F;
      float x = cachedX;
      float y = cachedY;
      float cardWidth = cachedWidth;

      if (!bloom || !glow) {
         // Blur pass: mask background rounded rectangles
         if (potionHUD.background.isEnabled()) {
            for (int i = 0; i < cachedEntries.size(); i++) {
               float itemY = y + (float) i * (lineHeight + spacing);
               RenderUtils.drawRoundedRect(x, itemY, cardWidth, lineHeight, 3.0F, -16777216);
            }
         }
      } else {
         // Bloom pass: bloom duration bars
         if (potionHUD.durationBar.isEnabled()) {
            for (int i = 0; i < cachedEntries.size(); i++) {
               PotionEntry entry = cachedEntries.get(i);
               float itemY = y + (float) i * (lineHeight + spacing);
               float barX = x + 1.5F;
               float barY = itemY + lineHeight - 2.0F;
               float maxBarW = cardWidth - 3.0F;
               float curBarW = maxBarW * entry.fraction;
               if (curBarW > 0.0F) {
                  RenderUtils.drawThemeGradientBar(barX, barY, barX + curBarW, barY + 1.0F);
               }
            }
         }
      }
   }

   private static String getAmplifierString(int amplifier) {
      return switch (amplifier) {
         case 1 -> " II";
         case 2 -> " III";
         case 3 -> " IV";
         case 4 -> " V";
         case 5 -> " VI";
         case 6 -> " VII";
         case 7 -> " VIII";
         case 8 -> " IX";
         case 9 -> " X";
         default -> "";
      };
   }
}
