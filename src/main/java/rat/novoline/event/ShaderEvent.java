package rat.novoline.event;

import rat.novoline.setting.MultiComboSetting;

public class ShaderEvent extends Event {
   private final boolean bloom;
   private final boolean glow;
   private final MultiComboSetting bloomOptions;

   public ShaderEvent(boolean bloom) {
      this(bloom, false, null);
   }

   public ShaderEvent(boolean bloom, boolean glow) {
      this(bloom, glow, null);
   }

   public ShaderEvent(boolean bloom, MultiComboSetting bloomOptions) {
      this(bloom, false, bloomOptions);
   }

   public ShaderEvent(boolean bloom, boolean glow, MultiComboSetting bloomOptions) {
      this.bloom = bloom;
      this.glow = glow;
      this.bloomOptions = bloomOptions;
   }

   public boolean isBloom() {
      return this.bloom;
   }

   public boolean isGlow() {
      return this.glow;
   }

   public MultiComboSetting getBloomOptions() {
      return this.bloomOptions;
   }
}
