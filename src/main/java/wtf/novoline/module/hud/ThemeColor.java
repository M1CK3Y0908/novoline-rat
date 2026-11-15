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
      new ComboSetting(this, "Animation Mode", 0, "Gradient", "Breathing")
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
            return ThemeColor.this.animationMode.getCurrentElementAsString().equals("Gradient");
         }
      }
   );
   public final NumberSetting angle = settingManager.register(
      new NumberSetting(this, "Angle", 0.0F, 0.0F, 360.0F) {
         @Override
         public boolean shouldShow() {
            return ThemeColor.this.animationMode.getCurrentElementAsString().equals("Gradient");
         }
      }
   );
   public final NumberSetting breathingSpeed = settingManager.register(
      new NumberSetting(this, "Breathing Speed", 1.5F, 0.2F, 5.0F) {
         @Override
         public boolean shouldShow() {
            return ThemeColor.this.animationMode.getCurrentElementAsString().equals("Breathing");
         }
      }
   );
   public final NumberSetting saturation = settingManager.register(
      new NumberSetting(this, "Saturation", 0.8F, 0.0F, 1.0F) {
         @Override
         public boolean shouldShow() {
            String m = ThemeColor.this.mode.getCurrentElementAsString();
            return m.equals("Rainbow") || m.equals("Astolfo");
         }
      }
   );
   public final NumberSetting brightness = settingManager.register(
      new NumberSetting(this, "Brightness", 1.0F, 0.0F, 1.0F) {
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
      return this.getColor(index * 25.0, 0.0);
   }

   public int getColor(long timeOffset) {
      return this.getColor((double)timeOffset, 0.0);
   }

   public int getColor(double offset) {
      return this.getColor(offset, 0.0);
   }

   public int getColor(double x, double y) {
      String animMode = this.animationMode.getCurrentElementAsString();
      String m = this.mode.getCurrentElementAsString();

      if (animMode.equals("Breathing")) {
         long breathMs = Math.max(200L, (long)(4000.0F / Math.max(0.1F, this.breathingSpeed.getCurrentValue())));
         double timeProgress = (System.currentTimeMillis() % breathMs) / (double)breathMs;
         float factor = (float)((Math.cos(timeProgress * 2.0 * Math.PI) + 1.0) / 2.0);

         return switch (m) {
            case "Single Color" -> {
               int c1 = this.firstColor.getColor();
               float minBri = 0.45F;
               float currentFactor = minBri + (1.0F - minBri) * factor;
               yield this.darken(c1, currentFactor);
            }
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
               float sat = this.saturation.getCurrentValue();
               float bri = this.brightness.getCurrentValue();
               yield Color.HSBtoRGB((float)timeProgress, sat, bri);
            }
            case "Astolfo" -> {
               float sat = this.saturation.getCurrentValue();
               float bri = this.brightness.getCurrentValue();
               float hue = 0.5F + 0.35F * factor;
               yield Color.HSBtoRGB(hue, sat, bri);
            }
            default -> this.firstColor.getColor();
         };
      } else {
         double rad = Math.toRadians(this.angle.getCurrentValue());
         double projected = x * Math.cos(rad) + y * Math.sin(rad);
         long speedMs = Math.max(200L, (long)(10000.0F / Math.max(0.1F, this.gradientSpeed.getCurrentValue())));
         double timeProgress = (System.currentTimeMillis() % speedMs) / (double)speedMs;
         double progress = (projected / 600.0 + timeProgress) % 1.0;
         if (progress < 0.0) {
            progress += 1.0;
         }
         float factor = (float)((Math.cos(progress * 2.0 * Math.PI) + 1.0) / 2.0);

         return switch (m) {
            case "Single Color" -> {
               int c1 = this.firstColor.getColor();
               float minBri = 0.60F;
               float currentFactor = minBri + (1.0F - minBri) * factor;
               yield this.darken(c1, currentFactor);
            }
            case "Double Color" -> {
               int c1 = this.firstColor.getColor();
               int c2 = this.secondColor.getColor();
               yield RenderUtils.lerpColor(c1, c2, factor);
            }
            case "Triple Color" -> {
               int c1 = this.firstColor.getColor();
               int c2 = this.secondColor.getColor();
               int c3 = this.thirdColor.getColor();
               if (progress < 1.0 / 3.0) {
                  yield RenderUtils.lerpColor(c1, c2, (float)(progress * 3.0));
               } else if (progress < 2.0 / 3.0) {
                  yield RenderUtils.lerpColor(c2, c3, (float)((progress - 1.0 / 3.0) * 3.0));
               } else {
                  yield RenderUtils.lerpColor(c3, c1, (float)((progress - 2.0 / 3.0) * 3.0));
               }
            }
            case "Rainbow" -> {
               float sat = this.saturation.getCurrentValue();
               float bri = this.brightness.getCurrentValue();
               yield Color.HSBtoRGB((float)progress, sat, bri);
            }
            case "Astolfo" -> {
               float sat = this.saturation.getCurrentValue();
               float bri = this.brightness.getCurrentValue();
               float hue = 0.5F + 0.35F * factor;
               yield Color.HSBtoRGB(hue, sat, bri);
            }
            default -> this.firstColor.getColor();
         };
      }
   }

   private int darken(int color, float factor) {
      int a = (color >> 24) & 0xFF;
      int r = Math.min(255, Math.max(0, (int)(((color >> 16) & 0xFF) * factor)));
      int g = Math.min(255, Math.max(0, (int)(((color >> 8) & 0xFF) * factor)));
      int b = Math.min(255, Math.max(0, (int)((color & 0xFF) * factor)));
      return (a << 24) | (r << 16) | (g << 8) | b;
   }
}
