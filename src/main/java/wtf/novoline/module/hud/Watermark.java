package wtf.novoline.module.hud;

import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;

public class Watermark extends Module {
   public final ComboSetting watermark = settingManager.register(new ComboSetting(this, "Type", 0, "OneTap", "NeverLose"));
   public final CheckboxSetting showPlayTime = settingManager.register(new CheckboxSetting(this, "Show Play Time", true));

   public int getColor() {
      return ModuleManager.themeColor.getColor();
   }

   public int getColor(double x, double y) {
      return ModuleManager.themeColor.getColor(x, y);
   }

   public Watermark() {
      super(Category.CLIENT, "Watermark", "");
      this.setEnabled(true);
   }
}
