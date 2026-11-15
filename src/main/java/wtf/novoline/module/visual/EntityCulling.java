package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import wtf.novoline.culling.CullTask;
import wtf.novoline.culling.Cullable;
import wtf.novoline.culling.CullingProvider;
import wtf.novoline.culling.OcclusionCullingInstance;
import wtf.novoline.event.LoadWorldEvent;
import wtf.novoline.event.RunTickEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.NumberSetting;

public class EntityCulling extends Module {
   public final CheckboxSetting cullEntities = settingManager.register(new CheckboxSetting(this, "Cull entities", true));
   public final CheckboxSetting cullTileEntities = settingManager.register(new CheckboxSetting(this, "Cull tile entities", true));
   public final CheckboxSetting cullPlayers = settingManager.register(new CheckboxSetting(this, "Cull players", true));
   public final CheckboxSetting renderNametags = settingManager.register(new CheckboxSetting(this, "Render nametags", true));
   public final NumberSetting tracingDistance = settingManager.register(new NumberSetting(this, "Distance", 128, 32, 256));
   public final NumberSetting sleepDelay = settingManager.register(new NumberSetting(this, "Delay (ms)", 10, 1, 50));
   public final NumberSetting hitboxLimit = settingManager.register(new NumberSetting(this, "Hitbox limit", 50, 10, 100));

   private OcclusionCullingInstance culling;
   private CullTask cullTask;
   private Thread cullThread;

   public EntityCulling() {
      super(Category.VISUAL, "EntityCulling", "Occlusion culling for entities and tile entities");
   }

   @Override
   protected void onInitialized() {
      super.onInitialized();
      this.setEnabled(true);
   }

   @Override
   public void onEnable() {
      this.startTask();
   }

   @Override
   public void onDisable() {
      this.stopTask();
      this.resetAllCulled();
   }

   private synchronized void startTask() {
      this.stopTask();
      int reach = (int) this.tracingDistance.getCurrentValue();
      CullingProvider provider = new CullingProvider();
      this.culling = new OcclusionCullingInstance(reach, provider);
      this.cullTask = new CullTask(this.culling);
      this.cullThread = new Thread(this.cullTask, "EntityCulling-Worker");
      this.cullThread.setDaemon(true);
      this.cullThread.setPriority(Thread.NORM_PRIORITY - 1);
      this.cullThread.start();
   }

   private synchronized void stopTask() {
      if (this.cullTask != null) {
         this.cullTask.running = false;
         this.cullTask = null;
      }
      if (this.cullThread != null) {
         this.cullThread.interrupt();
         this.cullThread = null;
      }
   }

   private void resetAllCulled() {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.theWorld != null) {
         try {
            for (Entity entity : mc.theWorld.loadedEntityList) {
               if (entity instanceof Cullable) {
                  ((Cullable) entity).setCulled(false);
               }
            }
            for (TileEntity te : mc.theWorld.loadedTileEntityList) {
               if (te instanceof Cullable) {
                  ((Cullable) te).setCulled(false);
               }
            }
         } catch (Throwable ignored) {
         }
      }
   }

   @EventLink
   public final Listener<RunTickEvent> onRunTick = event -> {
      if (this.cullTask != null) {
         this.cullTask.requestCull = true;
      }
   };

   @EventLink
   public final Listener<LoadWorldEvent> onLoadWorld = event -> {
      if (this.culling != null) {
         this.culling.resetCache();
      }
   };
}
