package rat.novoline.module.hud;

import java.awt.Color;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.ColorSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.RenderUtils;

public class ThemeColor extends Module {
   public final ComboSetting mode = settingManager.register(
      new ComboSetting(this, "Mode", 1, "Single Color", "Double Color", "Triple Color", "Rainbow", "Astolfo")
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
      new ColorSetting(this, "First Color", 0xFF35314D) {
         @Override
         public boolean shouldShow() {
            String m = ThemeColor.this.mode.getCurrentElementAsString();
            return m.equals("Single Color") || m.equals("Double Color") || m.equals("Triple Color");
         }
      }
   );
   public final ColorSetting secondColor = settingManager.register(
      new ColorSetting(this, "Second Color", 0xFFC7C3E5) {
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
      new NumberSetting(this, "Gradient Length", 50, 0, 100, "%") {
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
      return this.getColor(0.0, index * 14.0, index);
   }

   public int getColor(long timeOffset) {
      return this.getColor((double)timeOffset);
   }

   public int getColor(double offset) {
      double rad = Math.toRadians(this.angle.getCurrentValue());
      return this.getColor(offset * Math.cos(rad), offset * Math.sin(rad));
   }

   public int getColor(double x, double y) {
      return this.getColor(x, y, -1);
   }

   public int getColor(double x, double y, int index) {
      String m = this.mode.getCurrentElementAsString();
      if (m.equals("Single Color")) {
         return this.firstColor.getColor();
      }

      String animMode = this.animationMode.getCurrentElementAsString();

      if (animMode.equals("Breathing")) {
         long breathMs = Math.max(200L, (long)(4000.0F / Math.max(0.1F, this.breathingSpeed.getCurrentValue())));
         double timeProgress = (double)(System.currentTimeMillis() % (breathMs * 3600000L)) / (double)breathMs;
         float factor = (float)((Math.cos(timeProgress * 2.0 * Math.PI) + 1.0) / 2.0);

         return switch (m) {
            case "Double Color" -> {
               int c1 = this.firstColor.getColor();
               int c2 = this.secondColor.getColor();
               yield RenderUtils.lerpColor(c1, c2, factor);
            }
            case "Triple Color" -> {
               double p = timeProgress % 1.0;
               if (p < 0.0) p += 1.0;
               int c1 = this.firstColor.getColor();
               int c2 = this.secondColor.getColor();
               int c3 = this.thirdColor.getColor();
               if (p < 1.0 / 3.0) {
                  float u = (float)(p * 3.0);
                  float f = (float)((1.0 - Math.cos(u * Math.PI)) / 2.0);
                  yield RenderUtils.lerpColor(c1, c2, f);
               } else if (p < 2.0 / 3.0) {
                  float u = (float)((p - 1.0 / 3.0) * 3.0);
                  float f = (float)((1.0 - Math.cos(u * Math.PI)) / 2.0);
                  yield RenderUtils.lerpColor(c2, c3, f);
               } else {
                  float u = (float)((p - 2.0 / 3.0) * 3.0);
                  float f = (float)((1.0 - Math.cos(u * Math.PI)) / 2.0);
                  yield RenderUtils.lerpColor(c3, c1, f);
               }
            }
            case "Rainbow" -> {
               double p = timeProgress % 1.0;
               if (p < 0.0) p += 1.0;
               float sat = this.saturation.getCurrentValue() / 100.0F;
               float bri = this.brightness.getCurrentValue() / 100.0F;
               yield Color.HSBtoRGB((float)p, sat, bri);
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
         float gLen = this.gradientLength.getCurrentValue();
         long speedMs = Math.max(200L, (long)(10000.0F / Math.max(0.1F, this.gradientSpeed.getCurrentValue())));

         // 0%: 一行一个颜色 无渐变
         if (gLen <= 0.0F) {
            int row = index >= 0 ? index : (int) Math.floor(projected / 14.0);
            int timeStep = (int)((System.currentTimeMillis() % (speedMs * 3600000L)) / speedMs);
            int step = row - timeStep;

            return switch (m) {
               case "Double Color" -> {
                  int k = (step % 2 + 2) % 2;
                  yield k == 0 ? this.firstColor.getColor() : this.secondColor.getColor();
               }
               case "Triple Color" -> {
                  int k = (step % 3 + 3) % 3;
                  yield switch (k) {
                     case 0 -> this.firstColor.getColor();
                     case 1 -> this.secondColor.getColor();
                     default -> this.thirdColor.getColor();
                  };
               }
               case "Rainbow" -> {
                  int k = (step % 12 + 12) % 12;
                  float hue = (float)k / 12.0F;
                  float sat = this.saturation.getCurrentValue() / 100.0F;
                  float bri = this.brightness.getCurrentValue() / 100.0F;
                  yield Color.HSBtoRGB(hue, sat, bri);
               }
               case "Astolfo" -> {
                  int k = (step % 6 + 6) % 6;
                  float hue = 0.5F + 0.35F * ((float)k / 5.0F);
                  float sat = this.saturation.getCurrentValue() / 100.0F;
                  float bri = this.brightness.getCurrentValue() / 100.0F;
                  yield Color.HSBtoRGB(hue, sat, bri);
               }
               default -> this.firstColor.getColor();
            };
         }

         // > 0%: 整体/10 (100% now corresponds to previous 10%, i.e. 100px)
         double length = Math.max(1.0, (double)gLen);
         double timeProgress = (double)(System.currentTimeMillis() % (speedMs * 3600000L)) / (double)speedMs;
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
