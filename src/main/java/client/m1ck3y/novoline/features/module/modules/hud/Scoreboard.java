package client.m1ck3y.novoline.features.module.modules.hud;

import client.m1ck3y.novoline.Novoline.Fonts;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.features.module.ModuleManager;
import client.m1ck3y.novoline.setting.CheckboxSetting;
import client.m1ck3y.novoline.setting.ColorSetting;
import client.m1ck3y.novoline.setting.ComboSetting;
import client.m1ck3y.novoline.setting.MultiComboSetting;
import client.m1ck3y.novoline.setting.NumberSetting;
import client.m1ck3y.novoline.utils.render.FontAdapter;
import client.m1ck3y.novoline.utils.render.VanillaFontAdapter;

public class Scoreboard extends Module {
   public final CheckboxSetting hideNumbers = settingManager.register(new CheckboxSetting(this, "Hide Numbers", false));
   public final CheckboxSetting avoidArrayList = settingManager.register(new CheckboxSetting(this, "Avoid ArrayList", true));

   public final CheckboxSetting background = settingManager.register(new CheckboxSetting(this, "Background", true));

   public final MultiComboSetting outline = settingManager.register(
      new MultiComboSetting(this, "Outline")
         .add("Top", false)
         .add("Bottom", false)
         .add("Left", false)
         .add("Right", true)
   );
   public final NumberSetting lineWidth = settingManager.register(new NumberSetting(this, "Line Width", 1, 1, 5) {
      @Override
      public boolean shouldShow() {
         return Scoreboard.this.hasOutline();
      }
   });

   public final ComboSetting serverIp = settingManager.register(new ComboSetting(this, "Server IP", 1, "Normal", "Novoline", "None"));

   public boolean hasOutline() {
      for (MultiComboSetting.Option option : this.outline.getElements()) {
         if (option.isSelected()) {
            return true;
         }
      }
      return false;
   }

   public FontAdapter getFont() {
      return ModuleManager.font != null ? ModuleManager.font.getFont(false) : Fonts.rubikRegularSize16;
   }

   public FontAdapter getTitleFont() {
      return ModuleManager.font != null ? ModuleManager.font.getFont(true) : Fonts.rubikSemiboldSize16;
   }

   public Scoreboard() {
      super(Category.VISUAL, "Scoreboard", "Tenacity-styled scoreboard with post-processing, outline, and auto-avoid");
      this.setEnabled(true);
   }
}
