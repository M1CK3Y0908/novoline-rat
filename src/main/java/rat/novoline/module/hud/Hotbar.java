package rat.novoline.module.hud;

import rat.novoline.Novoline.Fonts;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.FontAdapter;

public class Hotbar extends Module {
   public final ComboSetting mode = settingManager.register(
      new ComboSetting(this, "Mode", 0, "Fancy", "Lol")
   );
   public final ComboSetting lolStyle = settingManager.register(
      new ComboSetting(this, "Lol Style", 0, "New", "Old")
   );
   public final CheckboxSetting background = settingManager.register(new CheckboxSetting(this, "Background", true));
   public final NumberSetting alpha = settingManager.register(new NumberSetting(this, "Alpha", 100, 0, 255));
   public final ComboSetting font = settingManager.register(
      new ComboSetting(this, "Font", 0, "SF", "Rubik", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Tahoma", "Minecraft")
   );
   public final NumberSetting fontSize = settingManager.register(new NumberSetting(this, "Font Size", 11, 8, 16));
   public final CheckboxSetting durabilityBar = settingManager.register(new CheckboxSetting(this, "Durability Bar", true));

   public FontAdapter getFont() {
      return Fonts.getFont(this.font.getCurrentElementAsString(), this.fontSize.getCurrentValue(), true);
   }

   public Hotbar() {
      super(Category.VISUAL, "Hotbar", "Replaces vanilla hotbar with customizable hotbar styles");
      this.setEnabled(true);
   }
}
