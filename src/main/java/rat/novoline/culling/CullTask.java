package rat.novoline.culling;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import rat.novoline.culling.util.Vec3d;
import rat.novoline.module.ModuleManager;
import rat.novoline.module.visual.EntityCulling;

public class CullTask implements Runnable {
   public volatile boolean requestCull = false;
   public volatile boolean running = true;

   private final OcclusionCullingInstance culling;
   private final Minecraft mc = Minecraft.getMinecraft();
   private final Vec3d lastPos = new Vec3d(0.0D, 0.0D, 0.0D);
   private final Vec3d aabbMin = new Vec3d(0.0D, 0.0D, 0.0D);
   private final Vec3d aabbMax = new Vec3d(0.0D, 0.0D, 0.0D);

   public long lastTime = 0L;

   public CullTask(OcclusionCullingInstance culling) {
      this.culling = culling;
   }

   @Override
   public void run() {
      while (this.running) {
         try {
            EntityCulling module = ModuleManager.entityCulling;
            int sleep = module != null ? (int) module.sleepDelay.getCurrentValue() : 10;
            Thread.sleep(Math.max(1, sleep));

            if (module == null || !module.isEnabled()) {
               continue;
            }

            WorldClient world = this.mc.theWorld;
            EntityPlayerSP player = this.mc.thePlayer;
            if (world == null || player == null || player.ticksExisted <= 10) {
               continue;
            }

            Entity renderView = this.mc.getRenderViewEntity();
            if (renderView == null) {
               continue;
            }

            Vec3 camVec = renderView.getPositionEyes(0.0F);
            if (camVec == null) {
               continue;
            }

            if (!this.requestCull
               && camVec.xCoord == this.lastPos.x
               && camVec.yCoord == this.lastPos.y
               && camVec.zCoord == this.lastPos.z) {
               continue;
            }

            long startTime = System.currentTimeMillis();
            this.requestCull = false;
            this.lastPos.set(camVec.xCoord, camVec.yCoord, camVec.zCoord);
            this.culling.resetCache();

            boolean isSpectatorOrThirdPerson = player.isSpectator() || this.mc.gameSettings.thirdPersonView != 0;
            int tracingDist = (int) module.tracingDistance.getCurrentValue();
            double maxDistSq = (double) tracingDist * (double) tracingDist;
            int hitboxMax = (int) module.hitboxLimit.getCurrentValue();
            boolean cullTiles = module.cullTileEntities.isEnabled();
            boolean cullEntities = module.cullEntities.isEnabled();
            boolean cullPlayers = module.cullPlayers.isEnabled();
            boolean chamsEnabled = ModuleManager.chams != null && ModuleManager.chams.isEnabled();

            // 1. Cull TileEntities
            if (cullTiles) {
               List<TileEntity> tileList = world.loadedTileEntityList;
               TileEntity[] tileEntities;
               try {
                  tileEntities = tileList.toArray(new TileEntity[0]);
               } catch (Throwable t) {
                  tileEntities = null;
               }

               if (tileEntities != null) {
                  for (TileEntity te : tileEntities) {
                     if (te == null || !(te instanceof Cullable)) {
                        continue;
                     }

                     Cullable cullable = (Cullable) te;
                     if (cullable.isForcedVisible()) {
                        continue;
                     }

                     if (isSpectatorOrThirdPerson || te instanceof TileEntityBeacon || te instanceof TileEntityEndPortal) {
                        cullable.setCulled(false);
                        continue;
                     }

                     BlockPos pos = te.getPos();
                     if (pos == null) {
                        continue;
                     }

                     double distSq = pos.distanceSq(camVec.xCoord, camVec.yCoord, camVec.zCoord);
                     if (distSq > 4096.0D) {
                        continue;
                     }

                     this.aabbMin.set(pos.getX(), pos.getY(), pos.getZ());
                     this.aabbMax.set(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);

                     boolean visible = this.culling.isAABBVisible(this.aabbMin, this.aabbMax, this.lastPos);
                     cullable.setCulled(!visible);
                  }
               }
            }

            // 2. Cull Entities
            if (cullEntities) {
               List<Entity> entityList = world.loadedEntityList;
               Entity[] entities;
               try {
                  entities = entityList.toArray(new Entity[0]);
               } catch (Throwable t) {
                  entities = null;
               }

               Entity auraTarget = ModuleManager.killaura != null ? ModuleManager.killaura.getTarget() : null;
               Entity mouseHit = this.mc.objectMouseOver != null ? this.mc.objectMouseOver.entityHit : null;

               if (entities != null) {
                  for (Entity entity : entities) {
                     if (entity == null || !(entity instanceof Cullable)) {
                        continue;
                     }

                     Cullable cullable = (Cullable) entity;
                     if (entity == player || entity == renderView || entity.riddenByEntity == player || player.ridingEntity == entity) {
                        cullable.setCulled(false);
                        continue;
                     }

                     if (cullable.isForcedVisible() || entity == auraTarget || entity == mouseHit) {
                        cullable.setCulled(false);
                        continue;
                     }

                     if (entity instanceof EntityPlayer && (!cullPlayers || chamsEnabled)) {
                        cullable.setCulled(false);
                        continue;
                     }

                     if (this.isSkippableArmorStand(entity)) {
                        cullable.setCulled(false);
                        continue;
                     }

                     double distSq = entity.getPositionVector().squareDistanceTo(camVec);
                     if (distSq > maxDistSq) {
                        cullable.setCulled(false);
                        continue;
                     }

                     // If entity is very close (< 2 blocks), keep visible
                     if (distSq < 4.0D) {
                        cullable.setCulled(false);
                        continue;
                     }

                     AxisAlignedBB bb = entity.getEntityBoundingBox();
                     if (bb == null) {
                        continue;
                     }

                     if (bb.maxX - bb.minX > hitboxMax || bb.maxY - bb.minY > hitboxMax || bb.maxZ - bb.minZ > hitboxMax) {
                        cullable.setCulled(false);
                        continue;
                     }

                     this.aabbMin.set(bb.minX, bb.minY, bb.minZ);
                     this.aabbMax.set(bb.maxX, bb.maxY, bb.maxZ);

                     boolean visible = this.culling.isAABBVisible(this.aabbMin, this.aabbMax, this.lastPos);
                     cullable.setCulled(!visible);
                  }
               }
            }

            this.lastTime = System.currentTimeMillis() - startTime;
         } catch (InterruptedException e) {
            // normal thread shutdown or wake
         } catch (Throwable t) {
            // prevent worker thread dying on unexpected exception
         }
      }
   }

   private boolean isSkippableArmorStand(Entity entity) {
      if (entity instanceof EntityArmorStand) {
         EntityArmorStand stand = (EntityArmorStand) entity;
         return stand.hasMarker() || (stand.isInvisible() && stand.hasCustomName());
      }
      return false;
   }
}
