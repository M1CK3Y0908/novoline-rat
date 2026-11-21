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
   public final ComboSetting font = settingManager.register(new ComboSetting(this, "Font", 0, "SF", "Rubik", "Default", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Tahoma", "Minecraft"));
   public final NumberSetting fontSize = settingManager.register(new NumberSetting(this, "Font Size", 20, 10, 24));
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
      if ("Default".equals(this.font.getCurrentElementAsString())) {
         return null;
      }
      return Fonts.getFont(this.font.getCurrentElementAsString(), this.fontSize.getCurrentValue(), bold);
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
