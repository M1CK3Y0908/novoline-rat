package client.m1ck3y.novoline.utils.render;

import net.minecraft.client.Minecraft;

public class VanillaFontAdapter implements FontAdapter {
   public static final VanillaFontAdapter INSTANCE = new VanillaFontAdapter();
   private final Minecraft mc = Minecraft.getMinecraft();

   @Override
   public float drawString(String text, float x, float y, int color) {
      if (text == null) return 0.0F;
      return (float) this.mc.fontRendererObj.drawString(text, x, y, color, false);
   }

   @Override
   public float drawStringWithShadow(String text, float x, float y, int color) {
      if (text == null) return 0.0F;
      return (float) this.mc.fontRendererObj.drawStringWithShadow(text, x, y, color);
   }

   @Override
   public float drawStringWithNiceShadow(String text, float x, float y, int color) {
      return this.drawStringWithShadow(text, x, y, color);
   }

   @Override
   public float drawStringWithNiceShadowNoColor(String text, float x, float y, int color) {
      return this.drawStringWithShadow(text, x, y, color);
   }

   @Override
   public int getStringWidth(String text) {
      if (text == null) return 0;
      return this.mc.fontRendererObj.getInt3(text);
   }

   @Override
   public int getHeight() {
      return this.mc.fontRendererObj.FONT_HEIGHT;
   }
}
