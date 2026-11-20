package rat.novoline.module.hud;

import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.Novoline.Fonts;
import rat.novoline.utils.render.FontAdapter;

public class Watermark extends Module {
   public final ComboSetting watermark = settingManager.register(new ComboSetting(this, "Type", 0, "OneTap", "NeverLose"));
   public final ComboSetting font = settingManager.register(new ComboSetting(this, "Font", 0, "Rubik", "Default", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Minecraft"));
   public final NumberSetting fontSize = settingManager.register(new NumberSetting(this, "Font Size", 16, 10, 24));

   public FontAdapter getFont(boolean bold) {
      if ("Default".equals(this.font.getCurrentElementAsString())) {
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
