package rat.novoline.module.hud;

import rat.novoline.Novoline.Fonts;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.MultiComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.FontAdapter;

public class TargetHUD extends Module {
   public final ComboSetting targetHUD = settingManager.register(new ComboSetting(this, "Mode", 0, "Type 1", "Type 2", "Type 3", "Classic"));
   public final ComboSetting healthColor = settingManager.register(new ComboSetting(this, "Health Color", 0, "Health", "Theme") {
      @Override
      public boolean shouldShow() {
         return !"Type 1".equalsIgnoreCase(TargetHUD.this.targetHUD.getCurrentElementAsString());
      }
   });
   public final MultiComboSetting addons = settingManager.register(new MultiComboSetting(this, "Addons").add("Shadow", true));
   public final NumberSetting maxPerRow = settingManager.register(new NumberSetting(this, "Max Per Row", 2, 1, 5));

   public FontAdapter getTitleFont() {
      return ModuleManager.font != null ? ModuleManager.font.getFont(ModuleManager.font.getFontSize() + 2.0F, true) : Fonts.rubikSemiboldSize18;
   }

   public FontAdapter getInfoFont() {
      return ModuleManager.font != null ? ModuleManager.font.getFont(Math.max(10.0F, ModuleManager.font.getFontSize() - 2.0F), false) : Fonts.rubikRegularSize14;
   }

   public TargetHUD() {
      super(Category.VISUAL, "TargetHUD", "");
      this.setEnabled(true);
   }
}
