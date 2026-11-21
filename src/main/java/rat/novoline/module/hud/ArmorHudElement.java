package rat.novoline.module.hud;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import rat.novoline.module.ModuleManager;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.utils.render.RenderUtils;

public class ArmorHudElement extends HudElement {
   public static ArmorHudElement instance;
   private static final Minecraft minecraft = Minecraft.getMinecraft();
   private boolean hasUserMoved = false;
   private float width = 104.0F;
   private float height = 30.0F;

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
      float slotWidth = 20.0F;
      float totalWidth = count * slotWidth + 4.0F;
      float totalHeight = 30.0F;
      this.width = totalWidth;
      this.height = totalHeight;

      float x = this.getX();
      float y = this.getY();

      if (armorHUD.background.isEnabled()) {
         boolean postProcessingActive = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isEnabled();
         if (!postProcessingActive) {
            RenderUtils.drawShadow(x, y, x + totalWidth, y + totalHeight, 1.5F, 1610612736);
         }
         int bgColor = ModuleManager.themeColor.getBackgroundColor();
         RenderUtils.drawRect(x, y, x + totalWidth, y + totalHeight, bgColor);
      }

      FontAdapter font = armorHUD.getFont();
      RenderItem renderItem = minecraft.getRenderItem();

      float startX = x + 2.0F;
      for (int i = 0; i < count; i++) {
         ItemStack itemStack = armorStacks.get(i);
         if (itemStack == null) continue;

         float itemX = startX + i * slotWidth + 2.0F;
         float itemY = y + 10.0F;

         boolean itemStackDamageable = itemStack.isItemStackDamageable();
         if (armorHUD.showDamage.isEnabled()) {
            if (itemStackDamageable || itemStack.getItem().getItemStackLimit() != 1 || itemStack.stackSize != 1) {
               String text = String.valueOf(itemStackDamageable ? itemStack.getMaxDamage() - itemStack.getItemDamage() : itemStack.stackSize);
               float textX = itemX + (16.0F - font.getStringWidth(text)) / 2.0F;
               font.drawStringWithShadow(text, textX, y + 1.5F, -1);
            }
         }

         renderItem.zLevel = 100.0F;
         RenderHelper.enableGUIStandardItemLighting();
         renderItem.renderItemAndEffectIntoGUI(itemStack, (int)itemX, (int)itemY);
         RenderHelper.disableStandardItemLighting();
         renderItem.zLevel = 0.0F;

         if (itemStack.isItemDamaged()) {
            int i5 = (int)Math.round(13.0 - itemStack.getItemDamage() * 13.0 / itemStack.getMaxDamage());
            int i6 = (int)Math.round(255.0 - itemStack.getItemDamage() * 255.0 / itemStack.getMaxDamage());
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.disableBlend();
            WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
            drawColoredQuad(worldRenderer, (int)itemX + 2, (int)itemY + 13, 13, 2, 0, 0);
            drawColoredQuad(worldRenderer, (int)itemX + 2, (int)itemY + 13, 12, 1, (255 - i6) / 4, 64);
            drawColoredQuad(worldRenderer, (int)itemX + 2, (int)itemY + 13, i5, 1, 255 - i6, i6);
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
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
         ItemStack held = player.getCurrentEquippedItem();
         if (held != null) {
            list.add(held);
         }
      }
      if (list.isEmpty() && inEditor) {
         list.add(new ItemStack(Items.diamond_helmet));
         list.add(new ItemStack(Items.diamond_chestplate));
         list.add(new ItemStack(Items.diamond_leggings));
         list.add(new ItemStack(Items.diamond_boots));
         list.add(new ItemStack(Items.diamond_sword));
      }
      return list;
   }

   private static void drawColoredQuad(WorldRenderer worldRenderer, int i, int i2, int i3, int i4, int i5, int i6) {
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(i, i2, 0.0).color(i5, i6, 0, 255).endVertex();
      worldRenderer.pos(i, i2 + i4, 0.0).color(i5, i6, 0, 255).endVertex();
      worldRenderer.pos(i + i3, i2 + i4, 0.0).color(i5, i6, 0, 255).endVertex();
      worldRenderer.pos(i + i3, i2, 0.0).color(i5, i6, 0, 255).endVertex();
      Tessellator.getInstance().draw();
   }
}
