package client.m1ck3y.novoline.features.module.modules.hud;

import client.m1ck3y.novoline.Novoline.Fonts;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.features.module.ModuleManager;
import client.m1ck3y.novoline.setting.CheckboxSetting;
import client.m1ck3y.novoline.setting.NumberSetting;
import client.m1ck3y.novoline.utils.render.FontAdapter;

public class PotionHUD extends Module {
   public final CheckboxSetting background = settingManager.register(new CheckboxSetting(this, "Background", true));
   public final NumberSetting alpha = settingManager.register(new NumberSetting(this, "Alpha", 100, 0, 255));

   public FontAdapter getFont() {
      return ModuleManager.font != null ? ModuleManager.font.getFont() : Fonts.rubikRegularSize16;
   }

   public PotionHUD() {
      super(Category.VISUAL, "PotionHUD", "Displays active potion effects with status and duration");
      this.setEnabled(true);
   }
}
