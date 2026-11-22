package client.m1ck3y.novoline.features.module.modules.hud;

import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.features.module.ModuleManager;
import client.m1ck3y.novoline.setting.CheckboxSetting;
import client.m1ck3y.novoline.setting.ComboSetting;
import client.m1ck3y.novoline.Novoline.Fonts;
import client.m1ck3y.novoline.utils.render.FontAdapter;
import client.m1ck3y.novoline.setting.MultiComboSetting;

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
