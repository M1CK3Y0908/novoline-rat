package rat.novoline.culling;

import java.util.BitSet;
import rat.novoline.culling.cache.ArrayOcclusionCache;
import rat.novoline.culling.cache.OcclusionCache;
import rat.novoline.culling.util.MathUtilities;
import rat.novoline.culling.util.Vec3d;

public class OcclusionCullingInstance {
   private final int reach;
   private final double aabbExpansion;
   private final DataProvider provider;
   private final OcclusionCache cache;
   private final BitSet skipList;
   private final boolean[] onFaceEdge;
   private final Vec3d[] targetPoints;
   private final Vec3d targetPos;
   private final int[] cameraPos;

   private enum Relative {
      INSIDE,
      POSITIVE,
      NEGATIVE;

      public static Relative from(int min, int max, int cameraPos) {
         if (max > cameraPos && min > cameraPos) {
            return POSITIVE;
         }
         if (min < cameraPos && max < cameraPos) {
            return NEGATIVE;
         }
         return INSIDE;
      }
   }

   public OcclusionCullingInstance(int reach, DataProvider provider) {
      this(reach, provider, new ArrayOcclusionCache(reach), 0.5D);
   }

   public OcclusionCullingInstance(int reach, DataProvider provider, OcclusionCache cache, double aabbExpansion) {
      this.skipList = new BitSet();
      this.onFaceEdge = new boolean[6];
      this.targetPoints = new Vec3d[8];
      this.targetPos = new Vec3d(0.0D, 0.0D, 0.0D);
      this.cameraPos = new int[3];
      this.reach = reach;
      this.provider = provider;
      this.cache = cache;
      this.aabbExpansion = aabbExpansion;

      for (int i = 0; i < this.targetPoints.length; i++) {
         this.targetPoints[i] = new Vec3d(0.0D, 0.0D, 0.0D);
      }
   }

   public boolean isAABBVisible(Vec3d min, Vec3d max, Vec3d camera) {
      try {
         int maxX = MathUtilities.floor(max.x + this.aabbExpansion);
         int maxY = MathUtilities.floor(max.y + this.aabbExpansion);
         int maxZ = MathUtilities.floor(max.z + this.aabbExpansion);
         int minX = MathUtilities.floor(min.x - this.aabbExpansion);
         int minY = MathUtilities.floor(min.y - this.aabbExpansion);
         int minZ = MathUtilities.floor(min.z - this.aabbExpansion);

         this.cameraPos[0] = MathUtilities.floor(camera.x);
         this.cameraPos[1] = MathUtilities.floor(camera.y);
         this.cameraPos[2] = MathUtilities.floor(camera.z);

         Relative relX = Relative.from(minX, maxX, this.cameraPos[0]);
         Relative relY = Relative.from(minY, maxY, this.cameraPos[1]);
         Relative relZ = Relative.from(minZ, maxZ, this.cameraPos[2]);

         if (relX == Relative.INSIDE && relY == Relative.INSIDE && relZ == Relative.INSIDE) {
            return true;
         }

         this.skipList.clear();
         int idx = 0;
         for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
               for (int z = minZ; z <= maxZ; z++) {
                  int cacheVal = this.getCacheValue(x, y, z);
                  if (cacheVal == 1) {
                     return true;
                  }
                  if (cacheVal != 0) {
                     this.skipList.set(idx);
                  }
                  idx++;
               }
            }
         }

         idx = 0;
         for (int x = minX; x <= maxX; x++) {
            this.onFaceEdge[0] = (x == minX);
            this.onFaceEdge[1] = (x == maxX);
            for (int y = minY; y <= maxY; y++) {
               this.onFaceEdge[2] = (y == minY);
               this.onFaceEdge[3] = (y == maxY);
               for (int z = minZ; z <= maxZ; z++) {
                  this.onFaceEdge[4] = (z == minZ);
                  this.onFaceEdge[5] = (z == maxZ);

                  if (this.skipList.get(idx)) {
                     idx++;
                     continue;
                  }

                  if ((this.onFaceEdge[0] && relX == Relative.POSITIVE)
                     || (this.onFaceEdge[1] && relX == Relative.NEGATIVE)
                     || (this.onFaceEdge[2] && relY == Relative.POSITIVE)
                     || (this.onFaceEdge[3] && relY == Relative.NEGATIVE)
                     || (this.onFaceEdge[4] && relZ == Relative.POSITIVE)
                     || (this.onFaceEdge[5] && relZ == Relative.NEGATIVE)) {
                     this.targetPos.set(x, y, z);
                     if (this.isVoxelVisible(camera, this.targetPos, this.onFaceEdge)) {
                        return true;
                     }
                  }
                  idx++;
               }
            }
         }
         return false;
      } catch (Throwable t) {
         return true;
      }
   }

   private boolean isVoxelVisible(Vec3d camera, Vec3d targetPos, boolean[] onFaceEdge) {
      int count = 0;
      if (onFaceEdge[0] || onFaceEdge[4] || onFaceEdge[2]) {
         this.targetPoints[count++].setAdd(targetPos, 0.05D, 0.05D, 0.05D);
      }
      if (onFaceEdge[1]) {
         this.targetPoints[count++].setAdd(targetPos, 0.95D, 0.05D, 0.05D);
      }
      if (onFaceEdge[3]) {
         this.targetPoints[count++].setAdd(targetPos, 0.05D, 0.95D, 0.05D);
      }
      if (onFaceEdge[5]) {
         this.targetPoints[count++].setAdd(targetPos, 0.05D, 0.05D, 0.95D);
      }
      if ((onFaceEdge[4] && onFaceEdge[1] && onFaceEdge[3]) || (onFaceEdge[1] && onFaceEdge[3])) {
         this.targetPoints[count++].setAdd(targetPos, 0.95D, 0.95D, 0.05D);
      }
      if (!((onFaceEdge[0] && onFaceEdge[5] && !onFaceEdge[3])) && (onFaceEdge[5] && onFaceEdge[3])) {
         this.targetPoints[count++].setAdd(targetPos, 0.05D, 0.95D, 0.95D);
      }
      if (onFaceEdge[5] && onFaceEdge[1]) {
         this.targetPoints[count++].setAdd(targetPos, 0.95D, 0.05D, 0.95D);
      }
      if (onFaceEdge[1] && onFaceEdge[3] && onFaceEdge[5]) {
         this.targetPoints[count++].setAdd(targetPos, 0.95D, 0.95D, 0.95D);
      }
      return this.isVisible(camera, this.targetPoints, count);
   }

   private boolean isVisible(Vec3d camera, Vec3d[] points, int count) {
      int camX = this.cameraPos[0];
      int camY = this.cameraPos[1];
      int camZ = this.cameraPos[2];

      for (int i = 0; i < count; i++) {
         Vec3d target = points[i];
         double diffX = camera.x - target.getX();
         double diffY = camera.y - target.getY();
         double diffZ = camera.z - target.getZ();
         double absDiffX = Math.abs(diffX);
         double absDiffY = Math.abs(diffY);
         double absDiffZ = Math.abs(diffZ);
         double invX = 1.0D / absDiffX;
         double invY = 1.0D / absDiffY;
         double invZ = 1.0D / absDiffZ;
         int stepCount = 1;

         int stepX;
         double tMaxX;
         if (absDiffX == 0.0D) {
            stepX = 0;
            tMaxX = invX;
         } else if (target.x > camera.x) {
            stepX = 1;
            stepCount += MathUtilities.floor(target.x) - camX;
            tMaxX = (double) ((float) (((double) (camX + 1) - camera.x) * invX));
         } else {
            stepX = -1;
            stepCount += camX - MathUtilities.floor(target.x);
            tMaxX = (double) ((float) ((camera.x - (double) camX) * invX));
         }

         int stepY;
         double tMaxY;
         if (absDiffY == 0.0D) {
            stepY = 0;
            tMaxY = invY;
         } else if (target.y > camera.y) {
            stepY = 1;
            stepCount += MathUtilities.floor(target.y) - camY;
            tMaxY = (double) ((float) (((double) (camY + 1) - camera.y) * invY));
         } else {
            stepY = -1;
            stepCount += camY - MathUtilities.floor(target.y);
            tMaxY = (double) ((float) ((camera.y - (double) camY) * invY));
         }

         int stepZ;
         double tMaxZ;
         if (absDiffZ == 0.0D) {
            stepZ = 0;
            tMaxZ = invZ;
         } else if (target.z > camera.z) {
            stepZ = 1;
            stepCount += MathUtilities.floor(target.z) - camZ;
            tMaxZ = (double) ((float) (((double) (camZ + 1) - camera.z) * invZ));
         } else {
            stepZ = -1;
            stepCount += camZ - MathUtilities.floor(target.z);
            tMaxZ = (double) ((float) ((camera.z - (double) camZ) * invZ));
         }

         boolean visible = this.stepRay(camera, camX, camY, camZ, invX, invY, invZ, stepCount, stepX, stepY, stepZ, tMaxY, tMaxX, tMaxZ);
         this.provider.cleanup();
         if (visible) {
            this.cacheResult(points[0], true);
            return true;
         }
      }
      this.cacheResult(points[0], false);
      return false;
   }

   private boolean stepRay(Vec3d camera, int currentX, int currentY, int currentZ,
                           double invX, double invY, double invZ,
                           int stepCount, int stepX, int stepY, int stepZ,
                           double tMaxY, double tMaxX, double tMaxZ) {
      while (stepCount > 1) {
         int cacheVal = this.getCacheValue(currentX, currentY, currentZ);
         if (cacheVal == 2) {
            return false;
         }
         if (cacheVal == 0) {
            int chunkX = currentX >> 4;
            int chunkZ = currentZ >> 4;
            if (!this.provider.prepareChunk(chunkX, chunkZ)) {
               return false;
            }
            if (this.provider.isOpaqueFullCube(currentX, currentY, currentZ)) {
               this.cache.setLastHidden();
               return false;
            }
            this.cache.setLastVisible();
         }

         if (tMaxY < tMaxX && tMaxY < tMaxZ) {
            currentY += stepY;
            tMaxY += invY;
         } else if (tMaxX < tMaxY && tMaxX < tMaxZ) {
            currentX += stepX;
            tMaxX += invX;
         } else {
            currentZ += stepZ;
            tMaxZ += invZ;
         }
         stepCount--;
      }
      return true;
   }

   private int getCacheValue(int x, int y, int z) {
      int diffX = x - this.cameraPos[0];
      int diffY = y - this.cameraPos[1];
      int diffZ = z - this.cameraPos[2];
      if (Math.abs(diffX) > this.reach - 2 || Math.abs(diffY) > this.reach - 2 || Math.abs(diffZ) > this.reach - 2) {
         return -1;
      }
      return this.cache.getState(diffX + this.reach, diffY + this.reach, diffZ + this.reach);
   }

   private void cacheResult(int x, int y, int z, boolean visible) {
      int cx = x - this.cameraPos[0] + this.reach;
      int cy = y - this.cameraPos[1] + this.reach;
      int cz = z - this.cameraPos[2] + this.reach;
      if (visible) {
         this.cache.setVisible(cx, cy, cz);
      } else {
         this.cache.setHidden(cx, cy, cz);
      }
   }

   private void cacheResult(Vec3d pos, boolean visible) {
      int cx = MathUtilities.floor(pos.x) - this.cameraPos[0] + this.reach;
      int cy = MathUtilities.floor(pos.y) - this.cameraPos[1] + this.reach;
      int cz = MathUtilities.floor(pos.z) - this.cameraPos[2] + this.reach;
      if (visible) {
         this.cache.setVisible(cx, cy, cz);
      } else {
         this.cache.setHidden(cx, cy, cz);
      }
   }

   public void resetCache() {
      this.cache.resetCache();
   }
}
