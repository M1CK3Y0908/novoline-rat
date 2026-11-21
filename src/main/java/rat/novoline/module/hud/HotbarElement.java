package rat.novoline.module.hud;

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
import net.minecraft.util.MathHelper;
import rat.novoline.module.ModuleManager;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.utils.render.RenderUtils;

public class HotbarElement extends HudElement {
   public static HotbarElement instance;
   private static final Minecraft minecraft = Minecraft.getMinecraft();
   private boolean hasUserMoved = false;
   private float width = 208.0F;
   private float height = 22.0F;

   private static float cachedX = 0.0F;
   private static float cachedY = 0.0F;
   private static float cachedWidth = 208.0F;
   private static float cachedHeight = 22.0F;
   private static int cachedSelectedSlot = 0;
   private static float cachedSelectedBarWidth = 22.0F;

   private static ItemStack[] sampleItems;

   public HotbarElement() {
      super("hotbar", 0.0F, 0.0F, true);
      instance = this;
      this.width = cachedWidth;
      this.height = cachedHeight;
   }

   public void checkDefaultPosition(ScaledResolution sr) {
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

      int count = 9;
      float barWidth = 22.0F;
      float lineWidth = 1.0F;
      float totalWidth = count * (barWidth + lineWidth) + lineWidth; // 208.0F
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
         int bgColor = ModuleManager.themeColor.getBackgroundColor();
         RenderUtils.drawRect(x, y, x + totalWidth, y + totalHeight, bgColor);
      }

      int selectedSlot = (player != null && player.inventory != null) ? player.inventory.currentItem : 0;
      cachedSelectedSlot = selectedSlot;

      RenderItem renderItem = minecraft.getRenderItem();
      FontAdapter font = hotbar.getFont();

      // 2. Slots
      for (int i = 0; i < count; i++) {
         float barX = x + lineWidth + i * (barWidth + lineWidth);
         float itemX = barX + (barWidth - 16.0F) / 2.0F;
         float itemY = y + 2.0F;
         float lineY = itemY + 16.0F + 1.0F;
         boolean isSelected = (i == selectedSlot);

         // Selection slot subtle highlight
         if (isSelected) {
            RenderUtils.drawRect(barX, y + 1.0F, barX + barWidth, lineY, 0x18FFFFFF);
         }

         ItemStack itemStack = (player != null && player.inventory != null) ? player.inventory.mainInventory[i] : null;
         if (itemStack == null && inEditor) {
            itemStack = getSampleItem(i);
         }

         // Durability & fraction
         boolean itemStackDamageable = itemStack != null && itemStack.isItemStackDamageable();
         int maxDamage = itemStackDamageable ? itemStack.getMaxDamage() : 1;
         int currentDamage = itemStackDamageable ? itemStack.getItemDamage() : 0;
         int remaining = Math.max(0, maxDamage - currentDamage);
         float fraction = itemStackDamageable ? MathHelper.clamp_float((float)remaining / (float)maxDamage, 0.0F, 1.0F) : 1.0F;

         // Bottom track
         RenderUtils.drawRect(barX, lineY, barX + barWidth, lineY + lineWidth, 0x40000000);

         // Bottom bar width
         float curBarWidth;
         if (itemStackDamageable && hotbar.durabilityBar.isEnabled()) {
            curBarWidth = barWidth * fraction;
         } else {
            curBarWidth = (itemStack != null || isSelected) ? barWidth : 0.0F;
         }

         // Bottom colored bar & glow effect when selected
         if (curBarWidth > 0.0F) {
            if (isSelected) {
               cachedSelectedBarWidth = curBarWidth;
               int themeCol = ModuleManager.themeColor.getColor(barX + curBarWidth / 2.0F, lineY);
               // Glow effect: soft luminous halo around the selected bottom bar
               RenderUtils.drawShadow(
                  barX - 2.5F,
                  lineY - 2.5F,
                  barX + curBarWidth + 2.5F,
                  lineY + lineWidth + 2.5F,
                  4.0F,
                  RenderUtils.withAlphaFraction(themeCol, 0.55F)
               );
               RenderUtils.drawShadow(
                  barX - 1.2F,
                  lineY - 1.2F,
                  barX + curBarWidth + 1.2F,
                  lineY + lineWidth + 1.2F,
                  2.0F,
                  RenderUtils.withAlphaFraction(themeCol, 0.9F)
               );
               RenderUtils.drawThemeGradientBar(barX, lineY, barX + curBarWidth, lineY + lineWidth);
            } else {
               RenderUtils.drawThemeGradientBar(barX, lineY, barX + curBarWidth, lineY + lineWidth, 0.65F);
            }
         }

         // 3. Render item icon
         if (itemStack != null) {
            renderItem.zLevel = 100.0F;
            RenderHelper.enableGUIStandardItemLighting();

            float anim = itemStack.animationsToGo - partialTicks;
            if (anim > 0.0F) {
               GlStateManager.pushMatrix();
               float s = 1.0F + anim / 5.0F;
               GlStateManager.translate(itemX + 8.0F, itemY + 12.0F, 0.0F);
               GlStateManager.scale(1.0F / s, (s + 1.0F) / 2.0F, 1.0F);
               GlStateManager.translate(-(itemX + 8.0F), -(itemY + 12.0F), 0.0F);
            }

            renderItem.renderItemAndEffectIntoGUI(itemStack, (int)itemX, (int)itemY);

            if (anim > 0.0F) {
               GlStateManager.popMatrix();
            }

            RenderHelper.disableStandardItemLighting();
            renderItem.zLevel = 0.0F;
            GlStateManager.disableLighting();

            // 4. White custom font count at bottom-left of the item
            if (itemStack.stackSize > 1) {
               String countStr = String.valueOf(itemStack.stackSize);
               float textX = itemX + 1.0F;
               float textY = itemY + 16.0F - (float) font.getHeight() + 1.0F;
               GlStateManager.disableDepth();
               GlStateManager.enableBlend();
               font.drawStringWithShadow(countStr, textX, textY, -1);
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

      if (!bloom || !glow) {
         // Blur pass: mask background
         if (ModuleManager.hotbar.background.isEnabled()) {
            RenderUtils.drawRect(x, y, x + w, y + h, -16777216);
         }
      } else {
         // Bloom pass: render selected slot's bottom bar into bloom buffer
         if (cachedSelectedBarWidth > 0.0F && cachedSelectedSlot >= 0 && cachedSelectedSlot < 9) {
            float barWidth = 22.0F;
            float lineWidth = 1.0F;
            float barX = x + lineWidth + cachedSelectedSlot * (barWidth + lineWidth);
            float lineY = y + 2.0F + 16.0F + 1.0F;
            RenderUtils.drawThemeGradientBar(barX, lineY, barX + cachedSelectedBarWidth, lineY + lineWidth);
         }
      }
   }

   private static ItemStack getSampleItem(int slot) {
      if (sampleItems == null) {
         sampleItems = new ItemStack[]{
            new ItemStack(Items.diamond_sword),
            new ItemStack(Items.golden_apple, 16),
            new ItemStack(Items.bow),
            new ItemStack(Items.arrow, 64),
            new ItemStack(Items.ender_pearl, 16),
            new ItemStack(Items.diamond_pickaxe),
            new ItemStack(Item.getItemFromBlock(Blocks.planks), 64),
            new ItemStack(Items.water_bucket),
            new ItemStack(Items.golden_carrot, 64)
         };
      }
      return (slot >= 0 && slot < sampleItems.length) ? sampleItems[slot] : null;
   }
}
