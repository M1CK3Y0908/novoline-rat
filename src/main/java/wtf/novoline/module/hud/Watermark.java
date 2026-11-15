package wtf.novoline.module.hud;

import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.utils.render.FontAdapter;

public class Watermark extends Module {
   public final ComboSetting watermark = settingManager.register(new ComboSetting(this, "Type", 0, "OneTap", "NeverLose"));
   public final ComboSetting font = settingManager.register(new ComboSetting(this, "Font", 0, "Default", "Rubik", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Minecraft"));
   public final NumberSetting fontSize = settingManager.register(new NumberSetting(this, "Font Size", 14, 10, 24));

   public FontAdapter getFont(boolean bold) {
      if (this.font.getCurrentElement() == 0) {
         return null;
      }
      return Fonts.getFont(this.font.getCurrentElementAsString(), this.fontSize.getCurrentValue(), bold);
   }
   public final CheckboxSetting showPlayTime = settingManager.register(new CheckboxSetting(this, "Show Play Time", true));

   public int getColor() {
      return ModuleManager.themeColor.getColor();
   }

   public int getColor(double x, double y) {
      return ModuleManager.themeColor.getColor(x, y);
   }

   public Watermark() {
      super(Category.VISUAL, "Watermark", "");
      this.setEnabled(true);
   }
}
