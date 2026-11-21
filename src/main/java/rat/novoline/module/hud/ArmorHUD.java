package rat.novoline.module.hud;

import rat.novoline.Novoline.Fonts;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.FontAdapter;

public class ArmorHUD extends Module {
   public final CheckboxSetting background = settingManager.register(new CheckboxSetting(this, "Background", true));
   public final ComboSetting durabilityFormat = settingManager.register(
      new ComboSetting(this, "Format", 0, "Percentage", "Value")
   );
   public final ComboSetting colorMode = settingManager.register(
      new ComboSetting(this, "Color Mode", 0, "Theme", "Durability")
   );
   public final ComboSetting font = settingManager.register(
      new ComboSetting(this, "Font", 0, "SF", "Rubik", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Tahoma", "Minecraft")
   );
   public final NumberSetting fontSize = settingManager.register(new NumberSetting(this, "Font Size", 12, 10, 18));

   public FontAdapter getFont() {
      return Fonts.getFont(this.font.getCurrentElementAsString(), this.fontSize.getCurrentValue());
   }

   public ArmorHUD() {
      super(Category.VISUAL, "ArmorHUD", "Displays equipped armor status");
      this.setEnabled(true);
   }
}
