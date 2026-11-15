package wtf.novoline.module.hud;

import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.utils.render.FontAdapter;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;

public class Notifications extends Module {
   public final ComboSetting font = settingManager.register(new ComboSetting(this, "Font", 0, "Rubik", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Minecraft"));

   public FontAdapter getTitleFont() {
      return Fonts.getFont(this.font.getCurrentElementAsString(), 18.0F, false);
   }

   public FontAdapter getSubTextFont() {
      return Fonts.getFont(this.font.getCurrentElementAsString(), 16.0F, false);
   }

   public final CheckboxSetting notifications = settingManager.register(new CheckboxSetting(this, "Glow", true));
   public final NumberSetting glowIntensity = settingManager.register(new NumberSetting(this, "Glow Intensity", 8, 1, 10) {
      @Override
      public boolean shouldShow() {
         return Notifications.this.notifications.isEnabled();
      }
   });
   public final CheckboxSetting blur = settingManager.register(new CheckboxSetting(this, "Blur", true));
   public final CheckboxSetting shadow = settingManager.register(new CheckboxSetting(this, "Shadow", true));
   public final MultiComboSetting rules = settingManager.register(new MultiComboSetting(this, "Rules").add("On feature toggle", true));

   public Notifications() {
      super(Category.CLIENT, "Notifications", "");
      this.setEnabled(true);
   }
}
