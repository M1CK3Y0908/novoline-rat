package rat.novoline.module.visual;

import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ColorSetting;
import rat.novoline.setting.ComboSetting;

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
