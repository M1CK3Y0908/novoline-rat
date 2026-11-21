package rat.novoline.module.hud;

import rat.novoline.Novoline.Fonts;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.FontAdapter;

public class PotionHUD extends Module {
   public final CheckboxSetting background = settingManager.register(new CheckboxSetting(this, "Background", true));
   public final NumberSetting alpha = settingManager.register(new NumberSetting(this, "Alpha", 100, 0, 255));
   public final CheckboxSetting durationBar = settingManager.register(new CheckboxSetting(this, "Duration Bar", true));

   public FontAdapter getFont() {
      if (ModuleManager.font != null) {
         float size = Math.max(8.0F, Math.round(ModuleManager.font.getFontSize() * 13.0F / 16.0F));
         return ModuleManager.font.getFont(size, false);
      }
      return Fonts.rubikRegularSize14;
   }

   public PotionHUD() {
      super(Category.VISUAL, "PotionHUD", "Displays active potion effects with status and duration");
      this.setEnabled(true);
   }
}
