package rat.novoline.module.hud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import rat.novoline.module.ModuleManager;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.utils.render.RenderUtils;

public class PotionHudElement extends HudElement {
   public static PotionHudElement instance;
   private static final Minecraft minecraft = Minecraft.getMinecraft();
   private static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation("textures/gui/container/inventory.png");
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
      public final Potion potion;

      public PotionEntry(String name, String duration, float fraction, Potion potion) {
         this.name = name;
         this.duration = duration;
         this.fraction = fraction;
         this.potion = potion;
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
               entries.add(new PotionEntry(fullName, duration, fraction, potion));
            }
         }
      }

      if (entries.isEmpty() && inEditor) {
         entries.add(new PotionEntry("Speed II", "01:30", 0.75F, Potion.moveSpeed));
         entries.add(new PotionEntry("Strength I", "00:45", 0.35F, Potion.damageBoost));
         entries.add(new PotionEntry("Fire Resistance", "02:15", 0.50F, Potion.fireResistance));
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
      float fontHeight = (float) font.getHeight();
      float iconSize = Math.max(10.0F, fontHeight + 1.0F);
      float iconSpacing = 3.5F;
      float paddingY = 3.5F;
      float lineHeight = Math.max(iconSize, fontHeight) + paddingY * 2.0F;
      float spacing = 3.0F;

      float maxWidth = 0.0F;
      for (PotionEntry entry : entries) {
         float iconSpace = (entry.potion != null && entry.potion.hasStatusIcon()) ? (iconSize + iconSpacing) : 0.0F;
         float w = iconSpace + (float) (font.getStringWidth(entry.name) + font.getStringWidth(": ") + font.getStringWidth(entry.duration));
         if (w > maxWidth) {
            maxWidth = w;
         }
      }

      float paddingX = potionHUD.background.isEnabled() ? 5.0F : 2.0F;
      float cardWidth = maxWidth + paddingX * 2.0F;
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

         // 2. Icon & Text layout
         float curX = x + paddingX;

         // Potion Icon
         if (entry.potion != null && entry.potion.hasStatusIcon()) {
            int statusIconIndex = entry.potion.getStatusIconIndex();
            float u = (float) (statusIconIndex % 8 * 18);
            float v = (float) (198 + statusIconIndex / 8 * 18);
            float iconX = curX;
            float iconY = itemY + (lineHeight - iconSize) / 2.0F;

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            minecraft.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            RenderUtils.drawScaledTexturedRect(iconX, iconY, u, v, 18.0F, 18.0F, iconSize, iconSize, 256.0F, 256.0F);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GlStateManager.popMatrix();

            curX += iconSize + iconSpacing;
         }

         float textY = itemY + (lineHeight - fontHeight) / 2.0F;

         // Name in theme color gradient
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
      float fontHeight = (float) font.getHeight();
      float iconSize = Math.max(10.0F, fontHeight + 1.0F);
      float iconSpacing = 3.5F;
      float paddingY = 3.5F;
      float lineHeight = Math.max(iconSize, fontHeight) + paddingY * 2.0F;
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
         // Bloom pass: bloom theme-colored potion text
         float paddingX = potionHUD.background.isEnabled() ? 5.0F : 2.0F;
         for (int i = 0; i < cachedEntries.size(); i++) {
            PotionEntry entry = cachedEntries.get(i);
            float itemY = y + (float) i * (lineHeight + spacing);
            float textY = itemY + (lineHeight - fontHeight) / 2.0F;
            float curX = x + paddingX;
            if (entry.potion != null && entry.potion.hasStatusIcon()) {
               curX += iconSize + iconSpacing;
            }
            for (int c = 0; c < entry.name.length(); c++) {
               String ch = String.valueOf(entry.name.charAt(c));
               int color = ModuleManager.themeColor.getColor((double) curX, (double) textY, c, entry.name.length());
               font.drawString(ch, curX, textY, color);
               curX += (float) font.getStringWidth(ch);
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
