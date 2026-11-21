package rat.novoline.module.hud;

import rat.novoline.Novoline.Fonts;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.FontAdapter;

public class PotionHUD extends Module {
   public final ComboSetting font = settingManager.register(
      new ComboSetting(this, "Font", 0, "SF", "Rubik", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Tahoma", "Minecraft")
   );
   public final NumberSetting fontSize = settingManager.register(new NumberSetting(this, "Font Size", 13, 10, 20));
   public final CheckboxSetting background = settingManager.register(new CheckboxSetting(this, "Background", true));
   public final NumberSetting alpha = settingManager.register(new NumberSetting(this, "Alpha", 100, 0, 255));
   public final CheckboxSetting durationBar = settingManager.register(new CheckboxSetting(this, "Duration Bar", true));

   public FontAdapter getFont() {
      return Fonts.getFont(this.font.getCurrentElementAsString(), this.fontSize.getCurrentValue());
   }

   public PotionHUD() {
      super(Category.VISUAL, "PotionHUD", "Displays active potion effects with status and duration");
      this.setEnabled(true);
   }
}
