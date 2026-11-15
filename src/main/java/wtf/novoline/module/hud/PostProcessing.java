package wtf.novoline.module.hud;

import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;

public class PostProcessing extends Module {
   public final CheckboxSetting blur = settingManager.register(new CheckboxSetting(this, "Blur", true));
   public final MultiComboSetting blurTargets = settingManager.register(
      (new MultiComboSetting(this, "Blur targets") {
         @Override
         public boolean shouldShow() {
            return PostProcessing.this.blur.isEnabled();
         }
      }).add("TargetHUD", true).add("Notifications", true).add("ClickGui", true)
   );
   public final NumberSetting blurRadius = settingManager.register(
      new NumberSetting(this, "Blur radius", 18, 1, 40) {
         @Override
         public boolean shouldShow() {
            return PostProcessing.this.blur.isEnabled();
         }
      }
   );
   public final NumberSetting blurSigma = settingManager.register(
      new NumberSetting(this, "Blur sigma", 0.6F, 0.01F, 10.0F) {
         @Override
         public boolean shouldShow() {
            return PostProcessing.this.blur.isEnabled();
         }
      }
   );

   public final CheckboxSetting bloom = settingManager.register(new CheckboxSetting(this, "Bloom", true));
   public final MultiComboSetting glowTargets = settingManager.register(
      (new MultiComboSetting(this, "Glow targets") {
         @Override
         public boolean shouldShow() {
            return PostProcessing.this.bloom.isEnabled();
         }
      }).add("ArrayList", true).add("Notifications", true).add("TargetHUD", true).add("Watermark", true).add("ClickGui", true)
   );
   public final NumberSetting bloomRadius = settingManager.register(
      new NumberSetting(this, "Bloom radius", 18, 1, 50) {
         @Override
         public boolean shouldShow() {
            return PostProcessing.this.bloom.isEnabled();
         }
      }
   );
   public final NumberSetting bloomSigma = settingManager.register(
      new NumberSetting(this, "Bloom sigma", 18.0F, 0.1F, 40.0F) {
         @Override
         public boolean shouldShow() {
            return PostProcessing.this.bloom.isEnabled();
         }
      }
   );
   public final NumberSetting bloomAlpha = settingManager.register(
      new NumberSetting(this, "Bloom alpha", 230, 0, 255) {
         @Override
         public boolean shouldShow() {
            return PostProcessing.this.bloom.isEnabled();
         }
      }
   );

   public PostProcessing() {
      super(Category.CLIENT, "PostProcessing", "Controls UI blur and glow effects");
      this.setEnabled(true);
   }

   public boolean isBlurEnabled() {
      return this.isEnabled() && this.blur.isEnabled();
   }

   public boolean isBlurEnabled(String target) {
      return this.isBlurEnabled() && this.blurTargets.isSelected(target);
   }

   public boolean isBloomEnabled() {
      return this.isEnabled() && this.bloom.isEnabled();
   }

   public boolean isBloomEnabled(String target) {
      return this.isBloomEnabled() && this.glowTargets.isSelected(target);
   }

   public int getBlurRadius() {
      return (int) this.blurRadius.getCurrentValue();
   }

   public float getBlurSigma() {
      return this.blurSigma.getCurrentValue();
   }

   public int getBloomRadius() {
      return (int) this.bloomRadius.getCurrentValue();
   }

   public float getBloomSigma() {
      return this.bloomSigma.getCurrentValue();
   }

   public float getBloomAlpha() {
      return this.bloomAlpha.getCurrentValue();
   }
}
