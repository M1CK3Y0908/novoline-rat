package client.m1ck3y.novoline.features.module.modules.hud;

import client.m1ck3y.novoline.Novoline;
import client.m1ck3y.novoline.Novoline.Fonts;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.setting.ComboSetting;
import client.m1ck3y.novoline.setting.NumberSetting;
import client.m1ck3y.novoline.utils.render.FontAdapter;
import client.m1ck3y.novoline.utils.render.VanillaFontAdapter;

public class Font extends Module {
   public final ComboSetting font = settingManager.register(
      new ComboSetting(this, "Font", 0, "Rubik", "SF", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Tahoma", "Minecraft") {
         @Override
         public void interactCallback() {
            if (Novoline.getNovoline() != null && Novoline.getNovoline().getModuleManager() != null) {
               Novoline.getNovoline().getModuleManager().updateCachedFeatures();
            }
         }
      }
   );

   public final NumberSetting fontSize = settingManager.register(
      new NumberSetting(this, "Font Size", 16, 10, 24) {
         @Override
         public void interactCallback() {
            if (Novoline.getNovoline() != null && Novoline.getNovoline().getModuleManager() != null) {
               Novoline.getNovoline().getModuleManager().updateCachedFeatures();
            }
         }
      }
   );

   public FontAdapter getFont() {
      return getFont(false);
   }

   public FontAdapter getFont(boolean bold) {
      float size = this.fontSize != null ? this.fontSize.getCurrentValue() : 16.0F;
      return getFont(size, bold);
   }

   public FontAdapter getFont(float size) {
      return getFont(size, false);
   }

   public FontAdapter getFont(float size, boolean bold) {
      String fontName = this.font != null ? this.font.getCurrentElementAsString() : "Rubik";
      if ("Minecraft".equalsIgnoreCase(fontName)) {
         return VanillaFontAdapter.INSTANCE;
      }
      float s = size > 0.0F ? size : 16.0F;
      return Fonts.getFont(fontName, s, bold);
   }

   public String getFontName() {
      return this.font != null ? this.font.getCurrentElementAsString() : "Rubik";
   }

   public float getFontSize() {
      return this.fontSize != null ? this.fontSize.getCurrentValue() : 16.0F;
   }

   @Override
   public boolean canBeToggled() {
      return false;
   }

   @Override
   public void setEnabled(boolean flag) {
      super.setEnabled(true);
   }

   public Font() {
      super(Category.CLIENT, "Font", "Controls global client font");
      this.setEnabled(true);
   }
}
