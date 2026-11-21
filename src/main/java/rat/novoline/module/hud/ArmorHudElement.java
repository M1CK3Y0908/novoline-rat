package rat.novoline.module.hud;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import rat.novoline.module.ModuleManager;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.utils.render.RenderUtils;

public class ArmorHudElement extends HudElement {
   public static ArmorHudElement instance;
   private static final Minecraft minecraft = Minecraft.getMinecraft();
   private boolean hasUserMoved = false;
   private float width = 94.0F;
   private float height = 32.0F;

   public ArmorHudElement() {
      super("armor-hud", 0.0F, 0.0F, true);
      instance = this;
   }

   @Override
   public HudElement setX(float f) {
      super.setX(f);
      this.hasUserMoved = true;
      return this;
   }

   @Override
   public HudElement setY(float f) {
      super.setY(f);
      this.hasUserMoved = true;
      return this;
   }

   @Override
   public float getX() {
      if (!this.hasUserMoved) {
         ScaledResolution sr = new ScaledResolution(minecraft);
         return sr.getScaledWidth() / 2.0F + 10.0F;
      }
      return this.x;
   }

   @Override
   public float getY() {
      if (!this.hasUserMoved) {
         ScaledResolution sr = new ScaledResolution(minecraft);
         return sr.getScaledHeight() - 56.0F;
      }
      return this.y;
   }

   @Override
   public float getWidth() {
      return this.width;
   }

   @Override
   public float getHeight() {
      return this.height;
   }

   @Override
   public void draw(ScaledResolution scaledResolution, float f, boolean flag) {
      ArmorHUD armorHUD = ModuleManager.armorHUD;
      if (armorHUD == null || !armorHUD.isEnabled()) {
         return;
      }
      EntityPlayerSP player = minecraft.thePlayer;
      if (player == null && !flag) {
         return;
      }

      List<ItemStack> armorStacks = getArmorStacks(player, flag);
      if (armorStacks.isEmpty()) {
         return;
      }

      int count = armorStacks.size();
      float slotWidth = 22.0F;
      float totalWidth = count * slotWidth + 6.0F;
      FontAdapter font = armorHUD.getFont();
      RenderItem renderItem = minecraft.getRenderItem();
      boolean isPercentage = "Percentage".equalsIgnoreCase(armorHUD.durabilityFormat.getCurrentElementAsString());
      boolean isTheme = "Theme".equalsIgnoreCase(armorHUD.colorMode.getCurrentElementAsString());

      float fontHeight = (float) font.getHeight();
      float lineWidth = 1.0F; // 耐久条宽度/粗度
      float topPadding = 2.0F;
      float bottomPadding = lineWidth; // 与底边缘距离

      float itemY = y + topPadding;
      float textY = itemY + 16.0F + lineWidth; // 图标距离数字一个耐久条的宽度
      float lineY = textY + fontHeight + lineWidth; // 数字距离耐久条一个耐久条的宽度
      float totalHeight = lineY + lineWidth + bottomPadding - y;
      this.height = totalHeight;

      if (armorHUD.background.isEnabled()) {
         boolean postProcessingActive = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isEnabled();
         if (!postProcessingActive) {
            RenderUtils.drawShadow(x, y, x + totalWidth, y + totalHeight, 1.5F, 1610612736);
         }
         int bgColor = ModuleManager.themeColor.getBackgroundColor();
         RenderUtils.drawRect(x, y, x + totalWidth, y + totalHeight, bgColor);
      }

      float startX = x + 3.0F;
      for (int i = 0; i < count; i++) {
         ItemStack itemStack = armorStacks.get(i);
         if (itemStack == null) continue;

         float slotX = startX + i * slotWidth;
         float itemX = slotX + (slotWidth - 16.0F) / 2.0F;

         boolean itemStackDamageable = itemStack.isItemStackDamageable();
         int maxDamage = itemStackDamageable ? itemStack.getMaxDamage() : 1;
         int currentDamage = itemStackDamageable ? itemStack.getItemDamage() : 0;
         int remaining = Math.max(0, maxDamage - currentDamage);
         float fraction = itemStackDamageable ? MathHelper.clamp_float((float)remaining / (float)maxDamage, 0.0F, 1.0F) : 1.0F;

         // 1. Durability text
         String text;
         if (isPercentage) {
            int pct = Math.round(fraction * 100.0F);
            text = pct + "%";
         } else {
            text = String.valueOf(itemStackDamageable ? remaining : itemStack.stackSize);
         }

         // 2. Color (Theme or Durability)
         int color;
         if (isTheme) {
            color = ModuleManager.themeColor.getColor(slotX + slotWidth / 2.0F, lineY);
         } else {
            color = TargetHudElement.getHealthColor((float)remaining, (float)maxDamage);
         }

         // 3. Render item icon (clean icon only, no vanilla damage bar)
         renderItem.zLevel = 100.0F;
         RenderHelper.enableGUIStandardItemLighting();
         renderItem.renderItemAndEffectIntoGUI(itemStack, (int)itemX, (int)itemY);
         RenderHelper.disableStandardItemLighting();
         renderItem.zLevel = 0.0F;
         GlStateManager.disableLighting();

         // 4. Render durability number
         float textX = slotX + (slotWidth - font.getStringWidth(text)) / 2.0F;
         font.drawStringWithShadow(text, textX, textY, color);

         // 5. Render durability line (width/stroke = 1.0F, margin from edge = 1.0F)
         float fullLineWidth = slotWidth - 4.0F;
         float lineX = slotX + 2.0F;
         float currentLineWidth = fullLineWidth * fraction;

         // Background track
         RenderUtils.drawRect(lineX, lineY, lineX + fullLineWidth, lineY + lineWidth, 0x40000000);
         // Durability bar
         if (currentLineWidth > 0.0F) {
            RenderUtils.drawRect(lineX, lineY, lineX + currentLineWidth, lineY + lineWidth, color);
         }
      }
   }

   public static void renderArmorHudEffects(boolean bloom, boolean glow, ScaledResolution sr) {
      if (instance == null || ModuleManager.armorHUD == null || !ModuleManager.armorHUD.isEnabled()) {
         return;
      }
      if (!ModuleManager.armorHUD.background.isEnabled()) {
         return;
      }
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.thePlayer == null) {
         return;
      }
      List<ItemStack> armorStacks = getArmorStacks(mc.thePlayer, false);
      if (armorStacks.isEmpty()) {
         return;
      }
      float x = instance.getX();
      float y = instance.getY();
      float w = instance.width;
      float h = instance.height;
      if (w <= 0.0F || h <= 0.0F) {
         return;
      }
      if (!bloom || !glow) {
         RenderUtils.drawRect(x, y, x + w, y + h, -16777216);
      }
   }

   private static List<ItemStack> getArmorStacks(EntityPlayerSP player, boolean inEditor) {
      List<ItemStack> list = new ArrayList<>();
      if (player != null && player.inventory != null) {
         for (int i = 3; i >= 0; i--) {
            ItemStack itemStack = player.inventory.armorInventory[i];
            if (itemStack != null) {
               list.add(itemStack);
            }
         }
      }
      if (list.isEmpty() && inEditor) {
         list.add(new ItemStack(Items.diamond_helmet));
         list.add(new ItemStack(Items.diamond_chestplate));
         list.add(new ItemStack(Items.diamond_leggings));
         list.add(new ItemStack(Items.diamond_boots));
      }
      return list;
   }
}
