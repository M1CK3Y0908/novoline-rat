package rat.novoline.module.hud;

import rat.novoline.Novoline.Fonts;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.FontAdapter;

public class Hotbar extends Module {
   public final ComboSetting mode = settingManager.register(
      new ComboSetting(this, "Mode", 0, "Fancy", "Lol")
   );
   public final ComboSetting lolStyle = settingManager.register(
      new ComboSetting(this, "Lol Style", 0, "New", "Old") {
         @Override
         public boolean shouldShow() {
            return !"Fancy".equalsIgnoreCase(mode.getCurrentElementAsString());
         }
      }
   );
   public final ComboSetting expBarColor = settingManager.register(
      new ComboSetting(this, "Exp Bar Color", 0, "Vanilla", "Gradient", "Theme") {
         @Override
         public boolean shouldShow() {
            return "Fancy".equalsIgnoreCase(mode.getCurrentElementAsString());
         }
      }
   );
   public final ComboSetting healthBarColor = settingManager.register(
      new ComboSetting(this, "Health Bar Color", 0, "Vanilla", "Value") {
         @Override
         public boolean shouldShow() {
            return "Fancy".equalsIgnoreCase(mode.getCurrentElementAsString());
         }
      }
   );
   public final CheckboxSetting background = settingManager.register(new CheckboxSetting(this, "Background", true));
   public final NumberSetting alpha = settingManager.register(new NumberSetting(this, "Alpha", 100, 0, 255));
   public final CheckboxSetting durabilityBar = settingManager.register(new CheckboxSetting(this, "Durability Bar", true));

   public FontAdapter getFont() {
      if (ModuleManager.font != null) {
         float size = Math.max(8.0F, Math.round(ModuleManager.font.getFontSize() * 11.0F / 16.0F));
         return ModuleManager.font.getFont(size, true);
      }
      return Fonts.rubikSemiboldSize16;
   }

   public Hotbar() {
      super(Category.VISUAL, "Hotbar", "Replaces vanilla hotbar with customizable hotbar styles");
      this.setEnabled(true);
   }
}
