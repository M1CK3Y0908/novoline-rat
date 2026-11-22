package client.m1ck3y.novoline.features.module.modules.hud;

import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.features.module.ModuleManager;
import client.m1ck3y.novoline.setting.CheckboxSetting;
import client.m1ck3y.novoline.setting.ComboSetting;
import client.m1ck3y.novoline.setting.NumberSetting;
import client.m1ck3y.novoline.Novoline.Fonts;
import client.m1ck3y.novoline.utils.render.FontAdapter;

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
