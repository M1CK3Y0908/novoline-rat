package client.m1ck3y.novoline.event;

public abstract class Event {
   private final boolean cancellable;
   private boolean cancelled;

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void cancel() {
      if (!this.cancellable) {
         throw new RuntimeException("Tried to cancel not cancellable event!");
      }

      this.cancelled = true;
   }

   public void setCancelled(boolean cancelled) {
      if (cancelled) {
         this.cancel();
      } else {
         this.cancelled = false;
      }
   }

   public Event() {
      this.cancellable = false;
   }

   public Event(long l, boolean flag) {
      this.cancellable = true;
   }
}
