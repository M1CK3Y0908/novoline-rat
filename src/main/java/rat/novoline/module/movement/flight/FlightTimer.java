package rat.novoline.module.movement.flight;

public class FlightTimer {
   private long prevMS = 0L;

   public boolean delay(float milliSec) {
      return (float)(this.getTime() - this.prevMS) >= milliSec;
   }

   public void reset() {
      this.prevMS = this.getTime();
   }

   public long getTime() {
      return System.nanoTime() / 1000000L;
   }

   public long getDifference() {
      return this.getTime() - this.prevMS;
   }

   public void setDifference(long difference) {
      this.prevMS = this.getTime() - difference;
   }

   public boolean isDelayComplete(int i) {
      return false;
   }
}
