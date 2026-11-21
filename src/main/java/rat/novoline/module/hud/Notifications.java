package rat.novoline.module.hud;

import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.Novoline.Fonts;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.setting.MultiComboSetting;

public class Notifications extends Module {
   public final ComboSetting font = settingManager.register(new ComboSetting(this, "Font", 0, "SF", "Rubik", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Tahoma", "Minecraft"));

   public FontAdapter getTitleFont() {
      return Fonts.getFont(this.font.getCurrentElementAsString(), 18.0F, false);
   }

   public FontAdapter getSubTextFont() {
      return Fonts.getFont(this.font.getCurrentElementAsString(), 16.0F, false);
   }

   public final CheckboxSetting shadow = settingManager.register(new CheckboxSetting(this, "Shadow", true));
   public final MultiComboSetting rules = settingManager.register(new MultiComboSetting(this, "Rules").add("On feature toggle", true));

   public Notifications() {
      super(Category.VISUAL, "Notifications", "");
      this.setEnabled(true);
   }
}
