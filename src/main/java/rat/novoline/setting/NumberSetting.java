package rat.novoline.setting;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.minecraft.util.MathHelper;
import rat.novoline.module.Module;
import rat.novoline.setting.ModuleSetting.CheckboxType;

public class NumberSetting extends ModuleSetting {
   private float currentValue;
   private final float defaultValue;
   private final NumberSetting.IntType intType;
   private final float minValue;
   private final float maxValue;
   private String unit = "";
   private java.util.Map<Float, String> customLabels;

   private static final DecimalFormat decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

   public void setCurrentValueIgnoreNormalize(float f) {
      if ("%".equals(this.unit) && f > 0.0F && f < 1.0F && this.maxValue > 10.0F) {
         f *= 100.0F;
      }

      if (this.intType.equals(NumberSetting.IntType.DECIMAL)) {
         this.currentValue = MathHelper.clamp_float(f, this.minValue, this.maxValue);
      } else {
         this.currentValue = (int)MathHelper.clamp_float(Math.round(f), this.minValue, this.maxValue);
      }

      this.interactCallback();
   }

   public NumberSetting.IntType getType() {
      return this.intType;
   }

   public float getCurrentValue() {
      return this.currentValue;
   }

   public float getMaxValue() {
      return this.maxValue;
   }

   public void setCurrentValue(float f) {
      if (this.intType.equals(NumberSetting.IntType.DECIMAL)) {
         this.currentValue = MathHelper.clamp_float(((int)(f * 100.0F) + 4) / 5 * 5 / 100.0F, this.minValue, this.maxValue);
      } else {
         this.currentValue = (int)MathHelper.clamp_float(Math.round(f), this.minValue, this.maxValue);
      }

      this.interactCallback();
   }

   public NumberSetting(Module module2, String text, int i, int i2, int i3) {
      this(module2, text, i, i2, i3, "");
   }

   public NumberSetting(Module module2, String text, int i, int i2, int i3, String unit) {
      super(module2, text, "slider", CheckboxType.SLIDER);
      this.currentValue = i;
      this.defaultValue = i;
      this.intType = NumberSetting.IntType.INT;
      this.minValue = i2;
      this.maxValue = i3;
      this.unit = unit != null ? unit : "";
   }

   public float getDefaultState() {
      return this.defaultValue;
   }

   public float getMinValue() {
      return this.minValue;
   }

   public NumberSetting(Module module2, String text, float f, float f2, float f3) {
      this(module2, text, f, f2, f3, "");
   }

   public NumberSetting(Module module2, String text, float f, float f2, float f3, String unit) {
      super(module2, text, "slider", CheckboxType.SLIDER);
      this.currentValue = f;
      this.defaultValue = f;
      this.intType = NumberSetting.IntType.DECIMAL;
      this.minValue = f2;
      this.maxValue = f3;
      this.unit = unit != null ? unit : "";
   }

   public String getUnit() {
      return this.unit;
   }

   public NumberSetting setUnit(String unit) {
      this.unit = unit != null ? unit : "";
      return this;
   }

   public NumberSetting addLabel(float value, String label) {
      if (this.customLabels == null) {
         this.customLabels = new java.util.HashMap<>();
      }
      this.customLabels.put(value, label);
      return this;
   }

   public boolean isNotDefault() {
      return Math.abs(this.currentValue - this.defaultValue) > 0.001F;
   }

   public String getFormattedValue() {
      return this.getFormattedValue(this.currentValue);
   }

   public String getFormattedValue(float val) {
      if (this.customLabels != null) {
         for (java.util.Map.Entry<Float, String> entry : this.customLabels.entrySet()) {
            if (Math.abs(entry.getKey() - val) < 0.001F) {
               return entry.getValue();
            }
         }
      }

      String numStr = this.intType == IntType.DECIMAL ? decimalFormat.format(val) : String.valueOf(Math.round(val));
      if (numStr.equals("-0")) {
         numStr = "0";
      }
      if (this.unit != null && !this.unit.isEmpty()) {
         try {
            if (Double.parseDouble(numStr) != 0.0) {
               return numStr + this.unit;
            }
         } catch (NumberFormatException ignored) {
            if (Math.abs(val) > 0.0001F) {
               return numStr + this.unit;
            }
         }
      }
      return numStr;
   }

   public enum IntType {
      INT,
      DECIMAL;
   }
}
