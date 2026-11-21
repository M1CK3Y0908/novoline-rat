package rat.novoline.module.hud;

import rat.novoline.Novoline.Fonts;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.utils.render.FontAdapter;

public class HUD extends Module {
   public FontAdapter getFont() {
      return ModuleManager.font != null ? ModuleManager.font.getFont() : Fonts.rubikRegularSize16;
   }

   public HUD() {
      super(Category.CLIENT, "HUD", "Legacy HUD module (split into Information and PotionHUD)");
      this.setEnabled(false);
   }
}
