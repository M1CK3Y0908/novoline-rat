package rat.novoline.utils.render;

public interface FontAdapter {
   float drawString(String text, float x, float y, int color);

   default float drawString(String text, double x, double y, int color) {
      return this.drawString(text, (float) x, (float) y, color);
   }

   float drawStringWithShadow(String text, float x, float y, int color);

   default float drawStringWithShadow(String text, double x, double y, int color) {
      return this.drawStringWithShadow(text, (float) x, (float) y, color);
   }

   float drawStringWithNiceShadow(String text, float x, float y, int color);

   default float drawStringWithNiceShadow(String text, double x, double y, int color) {
      return this.drawStringWithNiceShadow(text, (float) x, (float) y, color);
   }

   float drawStringWithNiceShadowNoColor(String text, float x, float y, int color);

   default float drawStringWithNiceShadowNoColor(String text, double x, double y, int color) {
      return this.drawStringWithNiceShadowNoColor(text, (float) x, (float) y, color);
   }

   int getStringWidth(String text);

   int getHeight();
}
