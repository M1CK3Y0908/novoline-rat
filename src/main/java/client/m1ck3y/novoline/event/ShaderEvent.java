package client.m1ck3y.novoline.event;

public class ShaderEvent extends Event {
   private final boolean bloom;
   private final boolean glow;

   public ShaderEvent(boolean bloom) {
      this(bloom, bloom);
   }

   public ShaderEvent(boolean bloom, boolean glow) {
      this.bloom = bloom;
      this.glow = glow;
   }

   public boolean isBloom() {
      return this.bloom;
   }

   public boolean isGlow() {
      return this.glow;
   }

   public boolean isGlow(String target) {
      return this.glow;
   }
}
