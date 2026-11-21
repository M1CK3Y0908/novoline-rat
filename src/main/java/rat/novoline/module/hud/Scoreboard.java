package rat.novoline.module.hud;

import rat.novoline.Novoline.Fonts;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ColorSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.MultiComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.utils.render.VanillaFontAdapter;

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
