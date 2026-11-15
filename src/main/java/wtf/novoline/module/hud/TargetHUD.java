package wtf.novoline.module.hud;

import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.utils.render.FontAdapter;

public class TargetHUD extends Module {
   public final ComboSetting targetHUD = settingManager.register(new ComboSetting(this, "Mode", 0, "Type 1", "Type 2", "Type 3", "Classic"));
   public final ComboSetting font = settingManager.register(new ComboSetting(this, "Font", 0, "Rubik", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Minecraft"));

   public FontAdapter getTitleFont() {
      return Fonts.getFont(this.font.getCurrentElementAsString(), 18.0F, true);
   }

   public FontAdapter getInfoFont() {
      return Fonts.getFont(this.font.getCurrentElementAsString(), 14.0F, false);
   }
   public final MultiComboSetting addons = settingManager.register(new MultiComboSetting(this, "Addons").add("Shadow", true));

   public TargetHUD() {
      super(Category.VISUAL, "TargetHUD", "");
      this.setEnabled(true);
   }
}
