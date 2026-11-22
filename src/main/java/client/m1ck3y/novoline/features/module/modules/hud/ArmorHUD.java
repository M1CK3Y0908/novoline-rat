package client.m1ck3y.novoline.features.module.modules.hud;

import client.m1ck3y.novoline.Novoline.Fonts;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.features.module.ModuleManager;
import client.m1ck3y.novoline.setting.CheckboxSetting;
import client.m1ck3y.novoline.setting.ComboSetting;
import client.m1ck3y.novoline.utils.render.FontAdapter;

public class ArmorHUD extends Module {
   public final CheckboxSetting background = settingManager.register(new CheckboxSetting(this, "Background", true));
   public final ComboSetting durabilityFormat = settingManager.register(
      new ComboSetting(this, "Format", 0, "Percentage", "Value")
   );
   public final ComboSetting colorMode = settingManager.register(
      new ComboSetting(this, "Color Mode", 0, "Theme", "Durability")
   );

   public FontAdapter getFont() {
      if (ModuleManager.font != null) {
         float size = Math.max(8.0F, Math.round(ModuleManager.font.getFontSize() * 12.0F / 16.0F));
         return ModuleManager.font.getFont(size, false);
      }
      return Fonts.rubikRegularSize14;
   }

   public ArmorHUD() {
      super(Category.VISUAL, "ArmorHUD", "Displays equipped armor status");
      this.setEnabled(true);
   }
}
