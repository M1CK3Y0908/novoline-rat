package client.m1ck3y.novoline.ui.menu;

import client.m1ck3y.novoline.enums.WidgetType;
import client.m1ck3y.novoline.utils.render.shader.FloatSupplier;

public interface MenuWidget {
   void reset();

   void renderText(FloatSupplier floatSupplier, WidgetType widgetType, float f, boolean flag);

   void render(float f, float f2, float f3);

   void handleMouseRelease(float f, float f2, int i);

   void tick();

   void handleMouseClick(float f, float f2, int i);

   boolean keyTyped(char c, int i);
}
