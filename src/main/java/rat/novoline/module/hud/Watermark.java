package rat.novoline.module.hud;

import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.Novoline.Fonts;
import rat.novoline.utils.render.FontAdapter;

public class Watermark extends Module {
   public final ComboSetting watermark = settingManager.register(new ComboSetting(this, "Type", 0, "Classic", "OneTap", "NeverLose"));
   public final CheckboxSetting showTime = settingManager.register(new CheckboxSetting(this, "Show Time", true) {
      @Override
      public boolean shouldShow() {
         return "Classic".equalsIgnoreCase(watermark.getCurrentElementAsString());
      }
   });
   public final CheckboxSetting showPlayTime = settingManager.register(new CheckboxSetting(this, "Show Play Time", true) {
      @Override
      public boolean shouldShow() {
         return !"Classic".equalsIgnoreCase(watermark.getCurrentElementAsString());
      }
   });

   public FontAdapter getFont(boolean bold) {
      return ModuleManager.font != null ? ModuleManager.font.getFont(bold) : Fonts.rubikRegularSize16;
   }

   public int getColor() {
      return ModuleManager.themeColor.getColor();
   }

   public int getColor(double x, double y) {
      return ModuleManager.themeColor.getColor(x, y);
   }

   public Watermark() {
      super(Category.VISUAL, "Watermark", "");
      this.setEnabled(true);
   }
}
