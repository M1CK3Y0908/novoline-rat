package client.m1ck3y.novoline.features.module.modules.hud;

import java.awt.Color;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.setting.ColorSetting;
import client.m1ck3y.novoline.setting.ComboSetting;
import client.m1ck3y.novoline.setting.NumberSetting;
import client.m1ck3y.novoline.utils.render.RenderUtils;

public class ThemeColor extends Module {
   public final ComboSetting mode = settingManager.register(
      new ComboSetting(this, "Mode", 1, "Single Color", "Double Color", "Triple Color", "Rainbow", "Astolfo", "Dynamic")
   );
   public final ComboSetting animationMode = settingManager.register(
      new ComboSetting(this, "Animation Mode", 0, "Gradient", "Breathing") {
         @Override
         public boolean shouldShow() {
            return !ThemeColor.this.mode.getCurrentElementAsString().equals("Single Color");
         }
      }
   );
   public final ColorSetting backgroundColor = settingManager.register(
      new ColorSetting(this, "Background Color", 0x32000000)
   );
   public final ColorSetting firstColor = settingManager.register(
      new ColorSetting(this, "First Color", 0xFF8A8AFF) {
         @Override
         public boolean shouldShow() {
            String m = ThemeColor.this.mode.getCurrentElementAsString();
            return m.equals("Single Color") || m.equals("Double Color") || m.equals("Triple Color") || m.equals("Dynamic");
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

   public int getBackgroundColor() {
      return this.backgroundColor.getColor();
   }

   public int getColor() {
      return this.getColor(0.0, 0.0, -1, 0);
   }

   public int getColor(int index) {
      return this.getColor(0.0, 0.0, index, 0);
   }

   public int getColor(int index, int totalLength) {
      return this.getColor(0.0, 0.0, index, totalLength);
   }

   public int getColor(long timeOffset) {
      return this.getColor((double)timeOffset);
   }

   public int getColor(double offset) {
      double rad = Math.toRadians(this.angle.getCurrentValue());
      return this.getColor(offset * Math.cos(rad), offset * Math.sin(rad), -1, 0);
   }

   public int getColor(double x, double y) {
      return this.getColor(x, y, -1, 0);
   }

   public int getColor(double x, double y, int index) {
      return this.getColor(x, y, index, 0);
   }

   public int getColor(double x, double y, int index, int totalLength) {
      String m = this.mode.getCurrentElementAsString();
      if (m.equals("Single Color")) {
         return this.firstColor.getColor();
      }

      String animMode = this.animationMode.getCurrentElementAsString();

      if (animMode.equals("Breathing")) {
         long breathMs = Math.max(200L, (long)(4000.0F / Math.max(0.1F, this.breathingSpeed.getCurrentValue())));
         double timeProgress = (double)(System.currentTimeMillis() % breathMs) / (double)breathMs;
         float factor = (float)((1.0 - Math.cos(timeProgress * 2.0 * Math.PI)) / 2.0);

         return switch (m) {
            case "Dynamic" -> {
               float[] hsb = Color.RGBtoHSB(
                  (this.firstColor.getColor() >> 16) & 0xFF,
                  (this.firstColor.getColor() >> 8) & 0xFF,
                  this.firstColor.getColor() & 0xFF,
                  null
               );
               float brightness = 1.0F - factor * 0.6F;
               yield Color.HSBtoRGB(hsb[0], hsb[1], Math.max(0.0F, Math.min(1.0F, brightness * hsb[2])));
            }
            case "Double Color" -> {
               int c1 = this.firstColor.getColor();
               int c2 = this.secondColor.getColor();
               yield RenderUtils.lerpColor(c1, c2, factor);
            }
            case "Triple Color" -> {
               double p = timeProgress * 3.0;
               int c1 = this.firstColor.getColor();
               int c2 = this.secondColor.getColor();
               int c3 = this.thirdColor.getColor();
               if (p < 1.0) {
                  float f = (float)((1.0 - Math.cos(p * Math.PI)) / 2.0);
                  yield RenderUtils.lerpColor(c1, c2, f);
               } else if (p < 2.0) {
                  float f = (float)((1.0 - Math.cos((p - 1.0) * Math.PI)) / 2.0);
                  yield RenderUtils.lerpColor(c2, c3, f);
               } else {
                  float f = (float)((1.0 - Math.cos((p - 2.0) * Math.PI)) / 2.0);
                  yield RenderUtils.lerpColor(c3, c1, f);
               }
            }
            case "Rainbow" -> {
               float sat = this.saturation.getCurrentValue() / 100.0F;
               float bri = this.brightness.getCurrentValue() / 100.0F;
               yield Color.HSBtoRGB((float)timeProgress, sat, bri);
            }
            case "Astolfo" -> {
               float hue = (float)(timeProgress < 0.5 ? 1.0 - timeProgress : timeProgress);
               float sat = this.saturation.getCurrentValue() / 100.0F;
               float bri = this.brightness.getCurrentValue() / 100.0F;
               yield Color.HSBtoRGB(hue, sat, bri);
            }
            default -> this.firstColor.getColor();
         };
      } else {
         float gLen = this.gradientLength.getCurrentValue();
         long speedMs = Math.max(200L, (long)(18000.0F / Math.max(0.1F, this.gradientSpeed.getCurrentValue())));
         double timeProgress = (double)(System.currentTimeMillis() % (speedMs * 3600000L)) / (double)speedMs;

         double spatialProgress = 0.0;
         if (gLen > 0.0F) {
            if (index >= 0) {
               double step = Math.max(1.0, 20.0 * (gLen / 50.0));
               spatialProgress = (double) index / step;
            } else {
               double rad = Math.toRadians(this.angle.getCurrentValue());
               double projected = x * Math.cos(rad) + y * Math.sin(rad);
               double waveLength = Math.max(10.0, 120.0 * (gLen / 50.0));
               spatialProgress = projected / waveLength;
            }
         }

         double progress = timeProgress - spatialProgress;

         return switch (m) {
            case "Dynamic" -> {
               float[] hsb = Color.RGBtoHSB(
                  (this.firstColor.getColor() >> 16) & 0xFF,
                  (this.firstColor.getColor() >> 8) & 0xFF,
                  this.firstColor.getColor() & 0xFF,
                  null
               );
               float factor = Math.abs((float)Math.sin(progress * 2.0 * Math.PI));
               float brightness = 1.0F - factor * 0.6F;
               yield Color.HSBtoRGB(hsb[0], hsb[1], Math.max(0.0F, Math.min(1.0F, brightness * hsb[2])));
            }
            case "Double Color" -> {
               float factor = (float)((1.0 - Math.cos(progress * 2.0 * Math.PI)) / 2.0);
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
               double p = (progress % 1.0 + 1.0) % 1.0;
               float hue = (float)(p < 0.5 ? 1.0 - p : p);
               float sat = this.saturation.getCurrentValue() / 100.0F;
               float bri = this.brightness.getCurrentValue() / 100.0F;
               yield Color.HSBtoRGB(hue, sat, bri);
            }
            default -> this.firstColor.getColor();
         };
      }
   }
}
