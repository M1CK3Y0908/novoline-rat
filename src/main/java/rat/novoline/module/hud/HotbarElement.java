package rat.novoline.module.hud;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.optifine.CustomColors;
import org.lwjgl.opengl.GL11;
import rat.novoline.module.ModuleManager;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.utils.render.RenderUtils;

public class HotbarElement extends HudElement {
   public static HotbarElement instance;
   private static final Minecraft minecraft = Minecraft.getMinecraft();
   private boolean hasUserMoved = false;
   private float width = 182.0F;
   private float height = 22.0F;

   private static float cachedX = 0.0F;
   private static float cachedY = 0.0F;
   private static float cachedWidth = 182.0F;
   private static float cachedHeight = 22.0F;
   private static int cachedSelectedSlot = 0;
   private static float cachedSelectedBarWidth = 22.0F;

   // Animation state for Fancy mode: smoothly sliding hover box with non-linear transition
   private float fancyHoverOffset = -1.0F;
   private float fancyHoverFromOffset = -1.0F;
   private float fancyHoverTargetOffset = -1.0F;
   private long fancyHoverStartTime = 0L;
   private float fancyAnimDuration = 180.0F;
   private int fancyLastSlot = -1;

   // Animation state for Lol "New" mode: slot zoom scale per slot
   private final float[] slotScales = new float[]{1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F};

   // Animation state for Lol "Old" mode: smoothly sliding selection box offset
   private float slidingSlotOffset = 0.0F;
   private int lastSelectedSlot = 0;

   private static ItemStack[] sampleItems;

   public HotbarElement() {
      super("hotbar", 0.0F, 0.0F, true);
      instance = this;
      this.updateDimensions();
      this.width = cachedWidth;
      this.height = cachedHeight;
   }

   private void updateDimensions() {
      if (ModuleManager.hotbar == null) {
         return;
      }
      String mode = ModuleManager.hotbar.mode.getCurrentElementAsString();
      if ("Fancy".equalsIgnoreCase(mode) || "ArmorHUD".equalsIgnoreCase(mode)) {
         this.width = 182.0F;
         this.height = 22.0F;
      } else {
         String style = ModuleManager.hotbar.lolStyle.getCurrentElementAsString();
         if ("Old".equalsIgnoreCase(style) || "Classic".equalsIgnoreCase(style)) {
            this.width = 181.0F;
            this.height = 22.5F;
         } else {
            this.width = 286.0F;
            this.height = 26.0F;
         }
      }
   }

   public void checkDefaultPosition(ScaledResolution sr) {
      updateDimensions();
      if (!this.hasUserMoved) {
         this.x = (sr.getFloat() - this.width) / 2.0F;
         this.y = sr.getFloat2() - this.height;
         cachedX = this.x;
         cachedY = this.y;
      }
   }

   @Override
   public HudElement setX(float f) {
      super.setX(f);
      this.hasUserMoved = true;
      cachedX = this.x;
      return this;
   }

   @Override
   public HudElement setY(float f) {
      super.setY(f);
      this.hasUserMoved = true;
      cachedY = this.y;
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
         updateDimensions();
         this.x = (screenW - this.width) / 2.0F;
         this.y = screenH - this.height;
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

   public static void updateHotbar(ScaledResolution sr) {
      if (instance == null || ModuleManager.hotbar == null || !ModuleManager.hotbar.isEnabled()) {
         return;
      }
      instance.checkDefaultPosition(sr);
      cachedX = instance.getX();
      cachedY = instance.getY();
      cachedWidth = instance.getWidth();
      cachedHeight = instance.getHeight();
      if (minecraft.thePlayer != null && minecraft.thePlayer.inventory != null) {
         cachedSelectedSlot = minecraft.thePlayer.inventory.currentItem;
      }
   }

   @Override
   public void draw(ScaledResolution scaledResolution, float partialTicks, boolean inEditor) {
      updateHotbar(scaledResolution);

      Hotbar hotbar = ModuleManager.hotbar;
      if (hotbar == null || !hotbar.isEnabled()) {
         return;
      }

      EntityPlayerSP player = minecraft.thePlayer;
      if (player == null && !inEditor) {
         return;
      }
      if (minecraft.playerController != null && minecraft.playerController.isSpectator()) {
         return;
      }

      String mode = hotbar.mode.getCurrentElementAsString();
      if ("Fancy".equalsIgnoreCase(mode) || "ArmorHUD".equalsIgnoreCase(mode)) {
         drawFancyHotbar(hotbar, player, partialTicks, inEditor);
      } else {
         String style = hotbar.lolStyle.getCurrentElementAsString();
         if ("Old".equalsIgnoreCase(style) || "Classic".equalsIgnoreCase(style)) {
            drawLolOldHotbar(hotbar, player, partialTicks, inEditor);
         } else {
            drawLolNewHotbar(hotbar, player, partialTicks, inEditor);
         }
      }
   }

   /**
    * Transcribed Novoline-lol "New" hotbar mode:
    * 32px slot spacing floating layout with smooth zoom scale animation (1.0 -> 1.5),
    * expanding multi-layer rounded drop shadow / halo, scaled item and stack count,
    * and 30px bottom durability / accent line.
    */
   private void drawLolNewHotbar(Hotbar hotbar, EntityPlayerSP player, float partialTicks, boolean inEditor) {
      this.width = 286.0F;
      this.height = 26.0F;

      float startX = this.x + 6.0F;
      float startY = this.y + 2.0F;

      float delta = minecraft.getDebugFPS() > 0 ? (2000.0F / (float) minecraft.getDebugFPS()) : 33.3F;
      int selectedSlot = (player != null && player.inventory != null) ? player.inventory.currentItem : 0;
      cachedSelectedSlot = selectedSlot;

      RenderItem renderItem = minecraft.getRenderItem();
      FontAdapter font = hotbar.getFont();

      float slotSpacing = 32.0F;
      int themeOffset = 0;

      for (int i = 0; i < 9; i++) {
         float slotX = startX + 6.0F + (float) i * slotSpacing;
         float slotY = startY + 4.5F;
         boolean isSelected = (i == selectedSlot);

         // Smooth zoom animation scale (1.0 -> 1.5)
         if (isSelected) {
            this.slotScales[i] = (float) MathHelper.clamp_double(this.slotScales[i] + 0.00125 * (double) delta, 0.0, 1.5);
         } else {
            this.slotScales[i] = (float) MathHelper.clamp_double(this.slotScales[i] - 0.00125 * (double) delta, 1.0, 999.0);
         }
         float scale = this.slotScales[i];

         // Theme color calculation with offset
         int rawColor = ModuleManager.themeColor.getColor((long) themeOffset);
         int slotColor = isSelected ? new Color(rawColor).brighter().getRGB() : rawColor;
         themeOffset -= 120;

         // 1. Multi-layer expanding drop shadow / halo
         if (hotbar.background.isEnabled()) {
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
            for (float d4 = 0.0F; d4 < 14.0F; d4 += 0.5F) {
               float rectX = slotX - 12.5F;
               float rectY = slotY - d4 + 20.0F;
               float rectW = 30.0F;
               float rectH = d4;
               float radius = d4 / 2.0F;
               int shadowAlpha = (int) (14.0F - d4);
               int shadowColor = RenderUtils.withAlpha(slotColor, shadowAlpha);
               RenderUtils.drawRoundedRect(rectX, rectY, rectW, rectH, radius, shadowColor);
            }
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
         }

         ItemStack itemStack = (player != null && player.inventory != null) ? player.inventory.mainInventory[i] : null;
         if (itemStack == null && inEditor) {
            itemStack = getSampleItem(i);
         }

         // 2. Render item with scale zoom around slot center
         if (itemStack != null) {
            GlStateManager.pushMatrix();
            GL11.glScaled(scale, scale, scale);
            double itemX = ((double) slotX * (1.0 / (double) scale)) - 6.5;
            double itemY = ((double) slotY * (1.0 / (double) scale)) - 6.0;

            renderItem.zLevel = 100.0F;
            RenderHelper.enableGUIStandardItemLighting();

            float anim = (float) itemStack.animationsToGo - partialTicks;
            if (anim > 0.0F) {
               GlStateManager.pushMatrix();
               float s = 1.0F + anim / 5.0F;
               GlStateManager.translate(itemX + 8.0, itemY + 12.0, 0.0);
               GlStateManager.scale(1.0F / s, (s + 1.0F) / 2.0F, 1.0F);
               GlStateManager.translate(-(itemX + 8.0), -(itemY + 12.0), 0.0);
            }

            renderItem.renderItemAndEffectIntoGUI(itemStack, (int) itemX, (int) itemY);

            if (anim > 0.0F) {
               GlStateManager.popMatrix();
            }

            RenderHelper.disableStandardItemLighting();
            renderItem.zLevel = 0.0F;
            GlStateManager.disableLighting();

            // Stack size count
            if (itemStack.stackSize > 1) {
               String countStr = String.valueOf(itemStack.stackSize);
               float offset = (countStr.length() == 1) ? 11.0F : 7.0F;
               GlStateManager.disableDepth();
               GlStateManager.enableBlend();
               font.drawStringWithShadow(countStr, (float) itemX + offset, (float) itemY + 9.5F, 16777215);
               GlStateManager.enableDepth();
            }

            GlStateManager.popMatrix();
         }

         // 3. Durability / bottom bar (Y range [slotY + 18.5, slotY + 19.5])
         float barLeft = slotX - 12.5F;
         float barTop = slotY + 18.5F;
         float barRight = slotX + 17.5F;
         float barBottom = slotY + 19.5F;

         boolean itemStackDamageable = itemStack != null && itemStack.isItemStackDamageable();
         int maxDamage = itemStackDamageable ? itemStack.getMaxDamage() : 1;
         int currentDamage = itemStackDamageable ? itemStack.getItemDamage() : 0;
         int remaining = Math.max(0, maxDamage - currentDamage);

         if (itemStackDamageable && hotbar.durabilityBar.isEnabled() && remaining > 0) {
            // Black background track
            RenderUtils.drawRect(barLeft, barTop, barRight, barBottom, -16777216);
            // Durability bar
            float durWidth = 30.0F * ((float) remaining / (float) maxDamage);
            RenderUtils.drawRect(barLeft, barTop, barLeft + durWidth, barBottom, slotColor);
         } else {
            // Full 30px colored accent line
            RenderUtils.drawRect(barLeft, barTop, barRight, barBottom, slotColor);
         }
      }
   }

   /**
    * Transcribed Novoline-lol "Old" / "Classic" hotbar mode:
    * 181x22.5px dark rounded bar with smoothly sliding selection box,
    * item icons and bottom 18px durability / theme tick bars.
    */
   private void drawLolOldHotbar(Hotbar hotbar, EntityPlayerSP player, float partialTicks, boolean inEditor) {
      this.width = 181.0F;
      this.height = 22.5F;

      float x = this.x;
      float y = this.y;

      int selectedSlot = (player != null && player.inventory != null) ? player.inventory.currentItem : 0;
      cachedSelectedSlot = selectedSlot;

      RenderItem renderItem = minecraft.getRenderItem();
      FontAdapter font = hotbar.getFont();

      // 1. Dark translucent background
      if (hotbar.background.isEnabled()) {
         int alpha = (int) hotbar.alpha.getCurrentValue();
         int bgColor = RenderUtils.withAlpha(0, alpha);
         RenderUtils.drawRoundedRect(x, y, 181.0F, 22.5F, 2.0F, bgColor);
      }

      // 2. Smoothly sliding selection box
      float delta = minecraft.getDebugFPS() > 0 ? (2000.0F / (float) minecraft.getDebugFPS()) : 33.3F;
      float targetOffset = 20.0F * (float) selectedSlot;

      if (selectedSlot != this.lastSelectedSlot) {
         if (Math.abs(selectedSlot - this.lastSelectedSlot) > 7) {
            this.slidingSlotOffset = targetOffset;
         }
         this.lastSelectedSlot = selectedSlot;
      }

      if (this.slidingSlotOffset < targetOffset) {
         this.slidingSlotOffset = Math.min(targetOffset, this.slidingSlotOffset + 0.125F * delta);
      } else if (this.slidingSlotOffset > targetOffset) {
         this.slidingSlotOffset = Math.max(targetOffset, this.slidingSlotOffset - 0.125F * delta);
      }

      float selX = x + 0.5F + this.slidingSlotOffset - 0.5F;
      float selY = y;
      int selColor = 0x30FFFFFF;
      RenderUtils.drawRoundedRect(selX, selY, 20.5F, 22.5F, 2.0F, selColor);

      // 3. Slots & item icons
      int themeOffset = 0;
      for (int i = 0; i < 9; i++) {
         float itemX = x + 0.5F + 20.0F * (float) i + 2.0F;
         float itemY = y + 2.0F;

         ItemStack itemStack = (player != null && player.inventory != null) ? player.inventory.mainInventory[i] : null;
         if (itemStack == null && inEditor) {
            itemStack = getSampleItem(i);
         }

         if (itemStack != null) {
            renderItem.zLevel = 100.0F;
            RenderHelper.enableGUIStandardItemLighting();

            float anim = (float) itemStack.animationsToGo - partialTicks;
            if (anim > 0.0F) {
               GlStateManager.pushMatrix();
               float s = 1.0F + anim / 5.0F;
               GlStateManager.translate(itemX + 8.0F, itemY + 12.0F, 0.0F);
               GlStateManager.scale(1.0F / s, (s + 1.0F) / 2.0F, 1.0F);
               GlStateManager.translate(-(itemX + 8.0F), -(itemY + 12.0F), 0.0F);
            }

            renderItem.renderItemAndEffectIntoGUI(itemStack, (int) itemX, (int) itemY);

            if (anim > 0.0F) {
               GlStateManager.popMatrix();
            }

            RenderHelper.disableStandardItemLighting();
            renderItem.zLevel = 0.0F;
            GlStateManager.disableLighting();

            // Stack count
            if (itemStack.stackSize > 1) {
               String countStr = String.valueOf(itemStack.stackSize);
               int len = countStr.length();
               float textOffset = (len == 1) ? 9.0F : 4.0F;
               GlStateManager.disableDepth();
               GlStateManager.enableBlend();
               font.drawStringWithShadow(countStr, itemX + 2.0F + textOffset, y + 12.5F, 16777215);
               GlStateManager.enableDepth();
            }
         }

         // 4. Durability / bottom tick bar (18px wide per slot)
         float barLeft = x + 0.5F + 20.0F * (float) i + 1.0F;
         float barRight = x + 0.5F + 20.0F * (float) (i + 1) - 1.0F;
         float barTop = y + 22.5F - 2.0F;
         float barBottom = y + 22.5F - 1.0F;

         int slotColor = ModuleManager.themeColor.getColor((long) themeOffset);
         themeOffset -= 180;

         boolean itemStackDamageable = itemStack != null && itemStack.isItemStackDamageable();
         int maxDamage = itemStackDamageable ? itemStack.getMaxDamage() : 1;
         int currentDamage = itemStackDamageable ? itemStack.getItemDamage() : 0;
         int remaining = Math.max(0, maxDamage - currentDamage);

         if (itemStackDamageable && hotbar.durabilityBar.isEnabled() && remaining > 0) {
            RenderUtils.drawRect(barLeft, barTop, barRight, barBottom, 0x80000000);
            float fraction = (float) remaining / (float) maxDamage;
            RenderUtils.drawRect(barLeft, barTop, barLeft + 18.0F * fraction, barBottom, slotColor);
         } else {
            RenderUtils.drawRect(barLeft, barTop, barRight, barBottom, slotColor);
         }
      }
   }

   private float easeOutCubic(float t) {
      float f = 1.0F - t;
      return 1.0F - f * f * f;
   }

   /**
    * Fancy style hotbar layout:
    * 182x22px uniform bar matching vanilla Minecraft hotbar dimensions,
    * smoothly sliding hover highlight box with non-linear transition animation,
    * dynamic theme gradient colored line across the very bottom,
    * standard item durability bar, and bottom-left white custom font stack count.
    */
   private void drawFancyHotbar(Hotbar hotbar, EntityPlayerSP player, float partialTicks, boolean inEditor) {
      int count = 9;
      float slotWidth = 20.0F;
      float totalWidth = 182.0F;
      float totalHeight = 22.0F;

      this.width = totalWidth;
      this.height = totalHeight;

      float x = cachedX;
      float y = cachedY;

      // 1. Background & Shadow
      if (hotbar.background.isEnabled()) {
         boolean postProcessingActive = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isEnabled();
         if (!postProcessingActive) {
            RenderUtils.drawShadow(x, y, x + totalWidth, y + totalHeight, 1.5F, 1610612736);
         }
         int alpha = (int) hotbar.alpha.getCurrentValue();
         int bgColor = RenderUtils.withAlpha(ModuleManager.themeColor.getBackgroundColor(), alpha);
         RenderUtils.drawRect(x, y, x + totalWidth, y + totalHeight, bgColor);
      }

      int selectedSlot = (player != null && player.inventory != null) ? player.inventory.currentItem : 0;
      cachedSelectedSlot = selectedSlot;

      // 2. Smoothly sliding hover box with non-linear transition animation
      float targetOffset = 1.0F + (float) selectedSlot * slotWidth;
      long now = System.currentTimeMillis();

      if (this.fancyHoverOffset < 0.0F) {
         this.fancyHoverOffset = targetOffset;
         this.fancyHoverFromOffset = targetOffset;
         this.fancyHoverTargetOffset = targetOffset;
         this.fancyLastSlot = selectedSlot;
         this.fancyHoverStartTime = now;
      } else if (selectedSlot != this.fancyLastSlot) {
         this.fancyHoverFromOffset = this.fancyHoverOffset;
         this.fancyHoverTargetOffset = targetOffset;
         int slotDiff = Math.abs(selectedSlot - this.fancyLastSlot);
         this.fancyAnimDuration = 160.0F + (float) Math.min(8, slotDiff) * 10.0F;
         this.fancyLastSlot = selectedSlot;
         this.fancyHoverStartTime = now;
      }

      long elapsed = now - this.fancyHoverStartTime;
      float progress = MathHelper.clamp_float((float) elapsed / this.fancyAnimDuration, 0.0F, 1.0F);
      float eased = easeOutCubic(progress);
      this.fancyHoverOffset = this.fancyHoverFromOffset + (this.fancyHoverTargetOffset - this.fancyHoverFromOffset) * eased;

      // Draw hover box background (only retain background hover effect, same size as vanilla grid: 20x20)
      float hoverX = x + this.fancyHoverOffset;
      float hoverY = y + 1.0F;
      float hoverW = slotWidth;
      float hoverH = 20.0F;
      RenderUtils.drawRect(hoverX, hoverY, hoverX + hoverW, hoverY + hoverH, 0x2CFFFFFF);

      // 3. Colored theme gradient lines: moved up by 1 body height (y + 20 to y + 21),
      // separated per slot, indented by 1 bar width on left and right inside each slot
      float lineTop = y + totalHeight - 2.0F;
      float lineBottom = y + totalHeight - 1.0F;
      for (int i = 0; i < count; i++) {
         float slotX = x + 1.0F + (float) i * slotWidth;
         float barLeft = slotX + 1.0F;
         float barRight = slotX + slotWidth - 1.0F;
         RenderUtils.drawThemeGradientBar(barLeft, lineTop, barRight, lineBottom);
      }

      RenderItem renderItem = minecraft.getRenderItem();
      FontAdapter font = hotbar.getFont();

      // 4. Slots & item icons
      for (int i = 0; i < count; i++) {
         float slotX = x + 1.0F + (float) i * slotWidth;
         float itemX = slotX + 2.0F;
         float itemY = y + 3.0F;

         ItemStack itemStack = (player != null && player.inventory != null) ? player.inventory.mainInventory[i] : null;
         if (itemStack == null && inEditor) {
            itemStack = getSampleItem(i);
         }

         if (itemStack != null) {
            renderItem.zLevel = 100.0F;
            RenderHelper.enableGUIStandardItemLighting();

            float anim = (float) itemStack.animationsToGo - partialTicks;
            if (anim > 0.0F) {
               GlStateManager.pushMatrix();
               float s = 1.0F + anim / 5.0F;
               GlStateManager.translate(itemX + 8.0F, itemY + 12.0F, 0.0F);
               GlStateManager.scale(1.0F / s, (s + 1.0F) / 2.0F, 1.0F);
               GlStateManager.translate(-(itemX + 8.0F), -(itemY + 12.0F), 0.0F);
            }

            renderItem.renderItemAndEffectIntoGUI(itemStack, (int) itemX, (int) itemY);

            if (anim > 0.0F) {
               GlStateManager.popMatrix();
            }

            RenderHelper.disableStandardItemLighting();
            renderItem.zLevel = 0.0F;
            GlStateManager.disableLighting();

            // White custom font count at bottom-left of the item
            if (itemStack.stackSize > 1) {
               String countStr = String.valueOf(itemStack.stackSize);
               float textX = itemX + 1.0F;
               float textY = itemY + 16.0F - (float) font.getHeight() + 1.0F;
               GlStateManager.disableDepth();
               GlStateManager.enableBlend();
               font.drawStringWithShadow(countStr, textX, textY, -1);
               GlStateManager.enableDepth();
            }

            // Normal item durability bar display
            if (itemStack.isItemDamaged() && hotbar.durabilityBar.isEnabled()) {
               int maxDamage = itemStack.getMaxDamage();
               int damage = itemStack.getItemDamage();
               int barLength = (int) Math.round(13.0 - (double) damage * 13.0 / (double) maxDamage);
               int colorVal = (int) Math.round(255.0 - (double) damage * 255.0 / (double) maxDamage);

               int r = 255 - colorVal;
               int g = colorVal;
               int b = 0;
               if (Config.isCustomColors()) {
                  int durabilityColor = CustomColors.getDurabilityColor(colorVal);
                  if (durabilityColor >= 0) {
                     r = durabilityColor >> 16 & 0xFF;
                     g = durabilityColor >> 8 & 0xFF;
                     b = durabilityColor & 0xFF;
                  }
               }

               float barX = itemX + 2.0F;
               float barY = itemY + 13.0F;

               GlStateManager.disableLighting();
               GlStateManager.disableDepth();
               GlStateManager.disableTexture2D();
               GlStateManager.disableAlpha();
               GlStateManager.disableBlend();

               // Background track
               RenderUtils.drawRect(barX, barY, barX + 13.0F, barY + 2.0F, 0xFF000000);
               int trackColor = 0xFF000000 | ((255 - colorVal) / 4 << 16) | (64 << 8);
               RenderUtils.drawRect(barX, barY, barX + 12.0F, barY + 1.0F, trackColor);

               // Durability color bar
               int barCol = 0xFF000000 | (r << 16) | (g << 8) | b;
               RenderUtils.drawRect(barX, barY, barX + (float) barLength, barY + 1.0F, barCol);

               GlStateManager.enableBlend();
               GlStateManager.enableAlpha();
               GlStateManager.enableTexture2D();
               GlStateManager.enableLighting();
               GlStateManager.enableDepth();
            }
         }
      }
   }

   public static void renderHotbarEffects(boolean bloom, boolean glow, ScaledResolution sr) {
      if (instance == null || ModuleManager.hotbar == null || !ModuleManager.hotbar.isEnabled()) {
         return;
      }
      if (minecraft.playerController != null && minecraft.playerController.isSpectator()) {
         return;
      }
      float x = cachedX;
      float y = cachedY;
      float w = cachedWidth;
      float h = cachedHeight;

      Hotbar hotbar = ModuleManager.hotbar;
      String mode = hotbar.mode.getCurrentElementAsString();

      if (!bloom || !glow) {
         // Blur pass: mask background
         if (hotbar.background.isEnabled()) {
            if ("Fancy".equalsIgnoreCase(mode) || "ArmorHUD".equalsIgnoreCase(mode)) {
               RenderUtils.drawRect(x, y, x + w, y + h, -16777216);
            } else {
               String style = hotbar.lolStyle.getCurrentElementAsString();
               if ("Old".equalsIgnoreCase(style) || "Classic".equalsIgnoreCase(style)) {
                  RenderUtils.drawRoundedRect(x, y, 181.0F, 22.5F, 2.0F, -16777216);
               } else {
                  float startX = x + 6.0F;
                  float startY = y + 2.0F;
                  for (int i = 0; i < 9; i++) {
                     float slotX = startX + 6.0F + (float) i * 32.0F;
                     float slotY = startY + 4.5F;
                     RenderUtils.drawRoundedRect(slotX - 12.5F, slotY + 6.0F, 30.0F, 14.0F, 4.0F, -16777216);
                  }
               }
            }
         }
      } else {
         // Bloom pass: render glowing elements into bloom buffer
         if ("Fancy".equalsIgnoreCase(mode) || "ArmorHUD".equalsIgnoreCase(mode)) {
            float lineTop = y + h - 2.0F;
            float lineBottom = y + h - 1.0F;
            for (int i = 0; i < 9; i++) {
               float slotX = x + 1.0F + (float) i * 20.0F;
               RenderUtils.drawThemeGradientBar(slotX + 1.0F, lineTop, slotX + 19.0F, lineBottom);
            }
         } else {
            String style = hotbar.lolStyle.getCurrentElementAsString();
            if ("Old".equalsIgnoreCase(style) || "Classic".equalsIgnoreCase(style)) {
               float barLeft = x + 0.5F + 20.0F * (float) cachedSelectedSlot + 1.0F;
               float barRight = x + 0.5F + 20.0F * (float) (cachedSelectedSlot + 1) - 1.0F;
               float barTop = y + 22.5F - 2.0F;
               float barBottom = y + 22.5F - 1.0F;
               int col = ModuleManager.themeColor.getColor((long) (cachedSelectedSlot * -180));
               RenderUtils.drawRect(barLeft, barTop, barRight, barBottom, col);
            } else {
               float startX = x + 6.0F;
               float startY = y + 2.0F;
               float slotX = startX + 6.0F + (float) cachedSelectedSlot * 32.0F;
               float slotY = startY + 4.5F;
               int rawColor = ModuleManager.themeColor.getColor((long) (cachedSelectedSlot * -120));
               int slotColor = new Color(rawColor).brighter().getRGB();
               RenderUtils.drawRect(slotX - 12.5F, slotY + 18.5F, slotX + 17.5F, slotY + 19.5F, slotColor);
            }
         }
      }
   }

   private static ItemStack getSampleItem(int slot) {
      if (sampleItems == null) {
         ItemStack sword = new ItemStack(Items.diamond_sword);
         sword.setItemDamage(250);
         ItemStack bow = new ItemStack(Items.bow);
         bow.setItemDamage(120);
         ItemStack pick = new ItemStack(Items.diamond_pickaxe);
         pick.setItemDamage(400);
         sampleItems = new ItemStack[]{
            sword,
            new ItemStack(Items.golden_apple, 16),
            bow,
            new ItemStack(Items.arrow, 64),
            new ItemStack(Items.ender_pearl, 16),
            pick,
            new ItemStack(Item.getItemFromBlock(Blocks.planks), 64),
            new ItemStack(Items.water_bucket),
            new ItemStack(Items.golden_carrot, 64)
         };
      }
      return (slot >= 0 && slot < sampleItems.length) ? sampleItems[slot] : null;
   }
}
