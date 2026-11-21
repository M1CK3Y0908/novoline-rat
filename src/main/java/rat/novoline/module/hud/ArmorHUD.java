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
   public final CheckboxSetting showDamage = settingManager.register(new CheckboxSetting(this, "Show Durability", true));
   public final ComboSetting font = settingManager.register(
      new ComboSetting(this, "Font", 0, "SF", "Rubik", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Tahoma", "Minecraft")
   );
   public final NumberSetting fontSize = settingManager.register(new NumberSetting(this, "Font Size", 14, 10, 20));

   public FontAdapter getFont() {
      return Fonts.getFont(this.font.getCurrentElementAsString(), this.fontSize.getCurrentValue());
   }

   public ArmorHUD() {
      super(Category.VISUAL, "ArmorHUD", "Displays equipped armor and held item status");
      this.setEnabled(true);
   }
}
