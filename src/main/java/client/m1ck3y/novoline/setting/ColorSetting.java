package client.m1ck3y.novoline.setting;

import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.setting.ModuleSetting.CheckboxType;

public class ColorSetting extends ModuleSetting {
   private int color;
   private final int defaultColor;

   public ColorSetting(Module module2, String text, int i) {
      super(module2, text, "color", CheckboxType.COLOR);
      this.color = i;
      this.defaultColor = i;
   }

   public int getColor() {
      return this.color;
   }

   public void setColor(int i) {
      this.color = i;
   }

   public int getDefaultColor() {
      return this.defaultColor;
   }
}
