package rat.novoline.module.hud;

import rat.novoline.Novoline.Fonts;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ColorSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.MultiComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.utils.render.VanillaFontAdapter;

public class Scoreboard extends Module {
   public final CheckboxSetting customFont = settingManager.register(new CheckboxSetting(this, "Custom Font", true));
   public final ComboSetting font = settingManager.register(new ComboSetting(this, "Font", 0, "SF", "Rubik", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Tahoma", "Minecraft") {
      @Override
      public boolean shouldShow() {
         return Scoreboard.this.customFont.isEnabled();
      }
   });
   public final NumberSetting fontSize = settingManager.register(new NumberSetting(this, "Font Size", 18, 10, 24) {
      @Override
      public boolean shouldShow() {
         return Scoreboard.this.customFont.isEnabled();
      }
   });

   public final CheckboxSetting hideNumbers = settingManager.register(new CheckboxSetting(this, "Hide Numbers", false));
   public final CheckboxSetting avoidArrayList = settingManager.register(new CheckboxSetting(this, "Avoid ArrayList", true));

   public final CheckboxSetting background = settingManager.register(new CheckboxSetting(this, "Background", true));
   public final ColorSetting backgroundColor = settingManager.register(new ColorSetting(this, "Background", 0x32000000) {
      @Override
      public boolean shouldShow() {
         return Scoreboard.this.background.isEnabled();
      }
   });

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
      if (!this.customFont.isEnabled() || "Minecraft".equalsIgnoreCase(this.font.getCurrentElementAsString())) {
         return VanillaFontAdapter.INSTANCE;
      }
      return Fonts.getFont(this.font.getCurrentElementAsString(), this.fontSize.getCurrentValue(), false);
   }

   public FontAdapter getTitleFont() {
      if (!this.customFont.isEnabled() || "Minecraft".equalsIgnoreCase(this.font.getCurrentElementAsString())) {
         return VanillaFontAdapter.INSTANCE;
      }
      return Fonts.getFont(this.font.getCurrentElementAsString(), this.fontSize.getCurrentValue(), true);
   }

   public Scoreboard() {
      super(Category.VISUAL, "Scoreboard", "Tenacity-styled scoreboard with post-processing, outline, and auto-avoid");
      this.setEnabled(true);
   }
}
