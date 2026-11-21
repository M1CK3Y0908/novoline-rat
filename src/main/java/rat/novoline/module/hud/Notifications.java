package rat.novoline.module.hud;

import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.Novoline.Fonts;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.setting.MultiComboSetting;

public class Notifications extends Module {
   public FontAdapter getTitleFont() {
      return ModuleManager.font != null ? ModuleManager.font.getFont(ModuleManager.font.getFontSize() + 2.0F, false) : Fonts.rubikRegularSize18;
   }

   public FontAdapter getSubTextFont() {
      return ModuleManager.font != null ? ModuleManager.font.getFont(false) : Fonts.rubikRegularSize16;
   }

   public final CheckboxSetting shadow = settingManager.register(new CheckboxSetting(this, "Shadow", true));
   public final MultiComboSetting rules = settingManager.register(new MultiComboSetting(this, "Rules").add("On feature toggle", true));

   public Notifications() {
      super(Category.VISUAL, "Notifications", "");
      this.setEnabled(true);
   }
}
