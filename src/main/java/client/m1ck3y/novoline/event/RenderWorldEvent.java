package client.m1ck3y.novoline.event;

public class RenderWorldEvent extends Event {
   public float partialTicks;

   public RenderWorldEvent(float f) {
      this.partialTicks = f;
   }
}
