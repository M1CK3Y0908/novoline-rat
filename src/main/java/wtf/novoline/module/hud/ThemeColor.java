package wtf.novoline.module.hud;

import java.awt.Color;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.render.RenderUtils;

public class ThemeColor extends Module {
   public final ComboSetting mode = settingManager.register(
      new ComboSetting(this, "Mode", 0, "Single Color", "Double Color", "Triple Color", "Rainbow", "Astolfo")
   );
   public final ComboSetting animationMode = settingManager.register(
      new ComboSetting(this, "Animation Mode", 0, "Gradient", "Breathing") {
         @Override
         public boolean shouldShow() {
            return !ThemeColor.this.mode.getCurrentElementAsString().equals("Single Color");
         }
      }
   );
   public final ColorSetting firstColor = settingManager.register(
      new ColorSetting(this, "First Color", -13204766) {
         @Override
         public boolean shouldShow() {
            String m = ThemeColor.this.mode.getCurrentElementAsString();
            return m.equals("Single Color") || m.equals("Double Color") || m.equals("Triple Color");
         }
      }
   );
   public final ColorSetting secondColor = settingManager.register(
      new ColorSetting(this, "Second Color", -7383320) {
         @Override
         public boolean shouldShow() {
            String m = ThemeColor.this.mode.getCurrentElementAsString();
            return m.equals("Double Color") || m.equals("Triple Color");
         }
      }
   );
   public final ColorSetting thirdColor = settingManager.register(
      new ColorSetting(this, "Third Color", -1550696) {
         @Override
         public boolean shouldShow() {
            return ThemeColor.this.mode.getCurrentElementAsString().equals("Triple Color");
         }
      }
   );
   public final NumberSetting gradientSpeed = settingManager.register(
      new NumberSetting(this, "Gradient Speed", 3.0F, 0.5F, 10.0F) {
         @Override
         public boolean shouldShow() {
            return !ThemeColor.this.mode.getCurrentElementAsString().equals("Single Color")
               && ThemeColor.this.animationMode.getCurrentElementAsString().equals("Gradient");
         }
      }
   );
   public final NumberSetting gradientLength = settingManager.register(
      new NumberSetting(this, "Gradient Length", 300, 50, 1000, "px") {
         @Override
         public boolean shouldShow() {
            return !ThemeColor.this.mode.getCurrentElementAsString().equals("Single Color")
               && ThemeColor.this.animationMode.getCurrentElementAsString().equals("Gradient");
         }
      }
   );
   public final NumberSetting angle = settingManager.register(
      new NumberSetting(this, "Angle", 0.0F, 0.0F, 360.0F) {
         @Override
         public boolean shouldShow() {
            return !ThemeColor.this.mode.getCurrentElementAsString().equals("Single Color")
               && ThemeColor.this.animationMode.getCurrentElementAsString().equals("Gradient");
         }
      }
   );
   public final NumberSetting breathingSpeed = settingManager.register(
      new NumberSetting(this, "Breathing Speed", 1.5F, 0.2F, 5.0F) {
         @Override
         public boolean shouldShow() {
            return !ThemeColor.this.mode.getCurrentElementAsString().equals("Single Color")
               && ThemeColor.this.animationMode.getCurrentElementAsString().equals("Breathing");
         }
      }
   );
   public final NumberSetting saturation = settingManager.register(
      new NumberSetting(this, "Saturation", 80, 0, 100, "%") {
         @Override
         public boolean shouldShow() {
            String m = ThemeColor.this.mode.getCurrentElementAsString();
            return m.equals("Rainbow") || m.equals("Astolfo");
         }
      }
   );
   public final NumberSetting brightness = settingManager.register(
      new NumberSetting(this, "Brightness", 100, 0, 100, "%") {
         @Override
         public boolean shouldShow() {
            String m = ThemeColor.this.mode.getCurrentElementAsString();
            return m.equals("Rainbow") || m.equals("Astolfo");
         }
      }
   );

   @Override
   public boolean canBeToggled() {
      return false;
   }

   @Override
   public void setEnabled(boolean flag) {
      super.setEnabled(true);
   }

   @Override
   public void disable() {
   }

   @Override
   public void toggle() {
   }

   public ThemeColor() {
      super(Category.CLIENT, "ThemeColor", "Controls client theme colors");
      this.setEnabled(true);
   }

   public int getColor() {
      return this.getColor(0.0, 0.0);
   }

   public int getColor(int index) {
      return this.getColor((double)(index * 20));
   }

   public int getColor(long timeOffset) {
      return this.getColor((double)timeOffset);
   }

   public int getColor(double offset) {
      double rad = Math.toRadians(this.angle.getCurrentValue());
      return this.getColor(offset * Math.cos(rad), offset * Math.sin(rad));
   }

   public int getColor(double x, double y) {
      String m = this.mode.getCurrentElementAsString();
      if (m.equals("Single Color")) {
         return this.firstColor.getColor();
      }

      String animMode = this.animationMode.getCurrentElementAsString();

      if (animMode.equals("Breathing")) {
         long breathMs = Math.max(200L, (long)(4000.0F / Math.max(0.1F, this.breathingSpeed.getCurrentValue())));
         double timeProgress = (System.currentTimeMillis() % breathMs) / (double)breathMs;
         float factor = (float)((Math.cos(timeProgress * 2.0 * Math.PI) + 1.0) / 2.0);

         return switch (m) {
            case "Double Color" -> {
               int c1 = this.firstColor.getColor();
               int c2 = this.secondColor.getColor();
               yield RenderUtils.lerpColor(c1, c2, factor);
            }
            case "Triple Color" -> {
               int c1 = this.firstColor.getColor();
               int c2 = this.secondColor.getColor();
               int c3 = this.thirdColor.getColor();
               if (timeProgress < 1.0 / 3.0) {
                  yield RenderUtils.lerpColor(c1, c2, (float)(timeProgress * 3.0));
               } else if (timeProgress < 2.0 / 3.0) {
                  yield RenderUtils.lerpColor(c2, c3, (float)((timeProgress - 1.0 / 3.0) * 3.0));
               } else {
                  yield RenderUtils.lerpColor(c3, c1, (float)((timeProgress - 2.0 / 3.0) * 3.0));
               }
            }
            case "Rainbow" -> {
               float sat = this.saturation.getCurrentValue() / 100.0F;
               float bri = this.brightness.getCurrentValue() / 100.0F;
               yield Color.HSBtoRGB((float)timeProgress, sat, bri);
            }
            case "Astolfo" -> {
               float sat = this.saturation.getCurrentValue() / 100.0F;
               float bri = this.brightness.getCurrentValue() / 100.0F;
               float hue = 0.5F + 0.35F * factor;
               yield Color.HSBtoRGB(hue, sat, bri);
            }
            default -> this.firstColor.getColor();
         };
      } else {
         double rad = Math.toRadians(this.angle.getCurrentValue());
         double projected = x * Math.cos(rad) + y * Math.sin(rad);
         double length = Math.max(10.0, (double)this.gradientLength.getCurrentValue());
         long speedMs = Math.max(200L, (long)(10000.0F / Math.max(0.1F, this.gradientSpeed.getCurrentValue())));
         double timeProgress = (System.currentTimeMillis() % speedMs) / (double)speedMs;
         double progress = projected / length + timeProgress;

         return switch (m) {
            case "Double Color" -> {
               float factor = (float)((1.0 - Math.cos(progress * Math.PI)) / 2.0);
               yield RenderUtils.lerpColor(this.firstColor.getColor(), this.secondColor.getColor(), factor);
            }
            case "Triple Color" -> {
               double p = (progress % 3.0 + 3.0) % 3.0;
               int c1 = this.firstColor.getColor();
               int c2 = this.secondColor.getColor();
               int c3 = this.thirdColor.getColor();
               if (p < 1.0) {
                  float factor = (float)((1.0 - Math.cos(p * Math.PI)) / 2.0);
                  yield RenderUtils.lerpColor(c1, c2, factor);
               } else if (p < 2.0) {
                  float factor = (float)((1.0 - Math.cos((p - 1.0) * Math.PI)) / 2.0);
                  yield RenderUtils.lerpColor(c2, c3, factor);
               } else {
                  float factor = (float)((1.0 - Math.cos((p - 2.0) * Math.PI)) / 2.0);
                  yield RenderUtils.lerpColor(c3, c1, factor);
               }
            }
            case "Rainbow" -> {
               double p = (progress % 1.0 + 1.0) % 1.0;
               float sat = this.saturation.getCurrentValue() / 100.0F;
               float bri = this.brightness.getCurrentValue() / 100.0F;
               yield Color.HSBtoRGB((float)p, sat, bri);
            }
            case "Astolfo" -> {
               float factor = (float)((1.0 - Math.cos(progress * Math.PI)) / 2.0);
               float sat = this.saturation.getCurrentValue() / 100.0F;
               float bri = this.brightness.getCurrentValue() / 100.0F;
               float hue = 0.5F + 0.35F * factor;
               yield Color.HSBtoRGB(hue, sat, bri);
            }
            default -> this.firstColor.getColor();
         };
      }
   }
}
