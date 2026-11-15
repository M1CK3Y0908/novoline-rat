package wtf.novoline.module.hud;

import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;

public class Watermark extends Module {
   public final ComboSetting watermark = settingManager.register(new ComboSetting(this, "Type", 0, "OneTap", "NeverLose"));
   public final ComboSetting colorMode = settingManager.register(new ComboSetting(this, "Color Mode", 0, "Theme", "Custom"));
   public final ColorSetting main = settingManager.register(new ColorSetting(this, "Main", -5917444) {
      @Override
      public boolean shouldShow() {
         return Watermark.this.colorMode.getCurrentElementAsString().equals("Custom");
      }
   });
   public final CheckboxSetting showPlayTime = settingManager.register(new CheckboxSetting(this, "Show Play Time", true));

   public int getColor() {
      return this.colorMode.getCurrentElementAsString().equals("Theme") ? ModuleManager.themeColor.getColor() : this.main.getColor();
   }

   public int getColor(double x, double y) {
      return this.colorMode.getCurrentElementAsString().equals("Theme") ? ModuleManager.themeColor.getColor(x, y) : this.main.getColor();
   }

   public Watermark() {
      super(Category.CLIENT, "Watermark", "");
      this.setEnabled(true);
   }
}