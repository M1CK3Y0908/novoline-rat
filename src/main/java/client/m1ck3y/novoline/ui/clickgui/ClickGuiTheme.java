package client.m1ck3y.novoline.ui.clickgui;

import client.m1ck3y.novoline.features.module.ModuleManager;
import client.m1ck3y.novoline.utils.render.RenderUtils;

public class ClickGuiTheme {
   public int getDimColor() {
      return 1426063360;
   }

   public int getSecondaryColor(boolean flag) {
      if (flag) {
         return ModuleManager.themeColor.getColor();
      }
      return -1438102703;
   }

   public int getSettingBackgroundColor() {
      return -15329511;
   }

   public int getModuleBackgroundColor() {
      return -14539994;
   }

   public int getPrimaryColor(boolean flag) {
      if (flag) {
         return ModuleManager.themeColor.getColor();
      }
      return -1434550140;
   }

   public int getTextColor() {
      return -2039584;
   }

   public int getCategoryBackgroundColor() {
      return -15592683;
   }
}