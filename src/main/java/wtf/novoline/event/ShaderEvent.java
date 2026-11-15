package wtf.novoline.event;

import wtf.novoline.setting.MultiComboSetting;

public class ShaderEvent extends Event {
   private final boolean bloom;
   private final MultiComboSetting bloomOptions;

   public ShaderEvent(boolean bloom, MultiComboSetting bloomOptions) {
      this.bloom = bloom;
      this.bloomOptions = bloomOptions;
   }

   public boolean isBloom() {
      return this.bloom;
   }

   public MultiComboSetting getBloomOptions() {
      return this.bloomOptions;
   }
}
