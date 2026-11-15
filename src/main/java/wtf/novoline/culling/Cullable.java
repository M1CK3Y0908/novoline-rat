package wtf.novoline.culling;

public interface Cullable {
   void setCullTimeout();

   boolean isForcedVisible();

   void setCulled(boolean culled);

   boolean isCulled();

   void setOutOfCamera(boolean outOfCamera);

   boolean isOutOfCamera();
}
