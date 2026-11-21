package rat.novoline.module.hud;

import rat.novoline.Novoline.Fonts;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.utils.render.FontAdapter;

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
