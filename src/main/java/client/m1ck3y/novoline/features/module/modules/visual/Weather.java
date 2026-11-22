package client.m1ck3y.novoline.features.module.modules.visual;

import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.setting.CheckboxSetting;
import client.m1ck3y.novoline.setting.ColorSetting;
import client.m1ck3y.novoline.setting.ComboSetting;

public class Weather extends Module {
   public final ComboSetting weather = settingManager.register(new ComboSetting(this, "Type", 0, "Snow", "Rain"));
   public final CheckboxSetting customColor = settingManager.register(new CheckboxSetting(this, "Custom color", true));
   public final ColorSetting color = settingManager.register(new ColorSetting(this, "Color", -12986881) {
      @Override
      public boolean shouldShow() {
         return Weather.this.customColor.isEnabled();
      }
   });

   public Weather() {
      super(Category.VISUAL, "Weather", "");
   }
}
