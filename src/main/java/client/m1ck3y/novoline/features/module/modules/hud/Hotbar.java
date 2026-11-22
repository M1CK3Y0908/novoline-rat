package client.m1ck3y.novoline.features.module.modules.hud;

import client.m1ck3y.novoline.Novoline.Fonts;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.features.module.ModuleManager;
import client.m1ck3y.novoline.setting.CheckboxSetting;
import client.m1ck3y.novoline.setting.ComboSetting;
import client.m1ck3y.novoline.setting.NumberSetting;
import client.m1ck3y.novoline.utils.render.FontAdapter;

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
      return ModuleManager.font != null ? ModuleManager.font.getFont() : Fonts.rubikRegularSize16;
   }

   public Hotbar() {
      super(Category.VISUAL, "Hotbar", "Replaces vanilla hotbar with customizable hotbar styles");
      this.setEnabled(true);
   }
}
