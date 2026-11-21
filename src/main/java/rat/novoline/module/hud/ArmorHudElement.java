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
   private float width = 93.0F;
   private float height = 32.0F;

   private static float cachedX = 0.0F;
   private static float cachedY = 0.0F;
   private static float cachedWidth = 0.0F;
   private static float cachedHeight = 0.0F;
   private static boolean cachedHasArmor = false;

   public ArmorHudElement() {
      super("armor-hud", 0.0F, 0.0F, true);
      instance = this;
   }

   public void checkDefaultPosition(ScaledResolution sr) {
      if (!this.hasUserMoved || (this.x == 0.0F && this.y == 0.0F)) {
         this.x = sr.getFloat() / 2.0F + 10.0F;
         this.y = sr.getFloat2() - 56.0F;
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
      if (!this.hasUserMoved || (this.x == 0.0F && this.y == 0.0F)) {
         ScaledResolution sr = new ScaledResolution(minecraft);
         float scaleFactor = sr.getScaleFactor();
         this.x = (sr.getScaledWidth() * scaleFactor / 2.0F) / 2.0F + 10.0F;
         this.y = (sr.getScaledHeight() * scaleFactor / 2.0F) - 56.0F;
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

   public static void updateArmorHud(ScaledResolution sr) {
      if (instance == null || ModuleManager.armorHUD == null || !ModuleManager.armorHUD.isEnabled()) {
         cachedHasArmor = false;
         return;
      }
      Minecraft mc = Minecraft.getMinecraft();
      EntityPlayerSP player = mc.thePlayer;
      boolean inEditor = mc.currentScreen instanceof net.minecraft.client.gui.GuiChat;
      if (player == null && !inEditor) {
         cachedHasArmor = false;
         return;
      }

      instance.checkDefaultPosition(sr);

      List<ItemStack> armorStacks = getArmorStacks(player, inEditor);
      if (armorStacks.isEmpty()) {
         cachedHasArmor = false;
         return;
      }

      int count = armorStacks.size();
      float barWidth = 22.0F;
      float lineWidth = 1.0F;
      float totalWidth = count * (barWidth + lineWidth) + lineWidth;

      ArmorHUD armorHUD = ModuleManager.armorHUD;
      FontAdapter font = armorHUD.getFont();
      float fontHeight = (float) font.getHeight();
      float topPadding = 2.0F;
      float bottomPadding = lineWidth;

      float itemY = instance.y + topPadding;
      float textY = itemY + 16.0F + lineWidth;
      float lineY = textY + fontHeight + lineWidth;
      float totalHeight = lineY + lineWidth + bottomPadding - instance.y;

      instance.width = totalWidth;
      instance.height = totalHeight;

      cachedX = instance.getX();
      cachedY = instance.getY();
      cachedWidth = totalWidth;
      cachedHeight = totalHeight;
      cachedHasArmor = true;
   }

   @Override
   public void draw(ScaledResolution scaledResolution, float f, boolean flag) {
      updateArmorHud(scaledResolution);

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
      float barWidth = 22.0F;
      float lineWidth = 1.0F; // 耐久条宽度/粗度
      float totalWidth = cachedWidth > 0.0F ? cachedWidth : (count * (barWidth + lineWidth) + lineWidth);
      float totalHeight = cachedHeight > 0.0F ? cachedHeight : 32.0F;
      float x = cachedX;
      float y = cachedY;

      FontAdapter font = armorHUD.getFont();
      RenderItem renderItem = minecraft.getRenderItem();
      boolean isPercentage = "Percentage".equalsIgnoreCase(armorHUD.durabilityFormat.getCurrentElementAsString());
      boolean isTheme = "Theme".equalsIgnoreCase(armorHUD.colorMode.getCurrentElementAsString());

      float fontHeight = (float) font.getHeight();
      float topPadding = 2.0F;
      float itemY = y + topPadding;
      float textY = itemY + 16.0F + lineWidth; // 图标距离数字一个耐久条的宽度
      float lineY = textY + fontHeight + lineWidth; // 数字距离耐久条一个耐久条的宽度

      if (armorHUD.background.isEnabled()) {
         boolean postProcessingActive = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isEnabled();
         if (!postProcessingActive) {
            RenderUtils.drawShadow(x, y, x + totalWidth, y + totalHeight, 1.5F, 1610612736);
         }
         int bgColor = ModuleManager.themeColor.getBackgroundColor();
         RenderUtils.drawRect(x, y, x + totalWidth, y + totalHeight, bgColor);
      }

      for (int i = 0; i < count; i++) {
         ItemStack itemStack = armorStacks.get(i);
         if (itemStack == null) continue;

         // 每个耐久条宽度为 barWidth，两个物品耐久条之间的距离以及耐久条到左右边缘的距离均为 lineWidth (1.0F)
         float barX = x + lineWidth + i * (barWidth + lineWidth);
         float itemX = barX + (barWidth - 16.0F) / 2.0F;

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
            color = ModuleManager.themeColor.getColor(barX + barWidth / 2.0F, lineY);
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
         float textX = barX + (barWidth - font.getStringWidth(text)) / 2.0F;
         font.drawStringWithShadow(text, textX, textY, color);

         // 5. Render durability line (width/stroke = 1.0F, margin from edge = 1.0F, gap between bars = 1.0F)
         float currentLineWidth = barWidth * fraction;

         // Background track
         RenderUtils.drawRect(barX, lineY, barX + barWidth, lineY + lineWidth, 0x40000000);
         // Durability bar
         if (currentLineWidth > 0.0F) {
            RenderUtils.drawRect(barX, lineY, barX + currentLineWidth, lineY + lineWidth, color);
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
      if (!cachedHasArmor || cachedWidth <= 0.0F || cachedHeight <= 0.0F) {
         return;
      }

      float x = cachedX;
      float y = cachedY;
      float w = cachedWidth;
      float h = cachedHeight;

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
