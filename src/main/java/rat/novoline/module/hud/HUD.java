package rat.novoline.module.hud;

import rat.novoline.Novoline.Fonts;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.FontAdapter;

public class HUD extends Module {
   public final ComboSetting font = settingManager.register(
      new ComboSetting(this, "Font", 0, "SF", "Rubik", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Tahoma", "Minecraft")
   );
   public final NumberSetting fontSize = settingManager.register(new NumberSetting(this, "Font Size", 16, 10, 22));

   public FontAdapter getFont() {
      return Fonts.getFont(this.font.getCurrentElementAsString(), this.fontSize.getCurrentValue());
   }

   public HUD() {
      super(Category.CLIENT, "HUD", "Legacy HUD module (split into Information and PotionHUD)");
      this.setEnabled(false);
   }
}
