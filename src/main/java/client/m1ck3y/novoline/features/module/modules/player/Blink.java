package client.m1ck3y.novoline.features.module.modules.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import client.m1ck3y.novoline.event.PacketSendEvent;
import client.m1ck3y.novoline.event.RunTickEvent;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.setting.CheckboxSetting;
import client.m1ck3y.novoline.utils.player.PlayerUtils;

public class Blink extends Module {
   private Entity entity;
   private final CheckboxSetting blink = settingManager.register(new CheckboxSetting(this, "Ignore KeepAlive", false));
   private final Queue<Packet<?>> queue = new ConcurrentLinkedQueue<>();
   @EventLink
   private final Listener<RunTickEvent> onRunTick = runTickEvent -> this.setSuffix(String.valueOf(this.queue.size()));
   @EventLink
   private final Listener<PacketSendEvent> onPacketSend = packetSendEvent -> {
      if (!(packetSendEvent.packet instanceof C00PacketKeepAlive) || this.blink.isEnabled()) {
         this.queue.add(packetSendEvent.packet);
         packetSendEvent.cancel();
      }
   };

   @Override
   protected void onDisable() {
      if (minecraft.theWorld != null) {
         minecraft.theWorld.removeEntityFromWorld(this.entity.getEntityId());

         while (!this.queue.isEmpty()) {
            this.sendPacketUnregistered(this.queue.poll());
         }
      }
   }

   public Blink() {
      super(Category.PLAYER, "Blink", "Chokes packets to make it look like you're lagging");
   }

   @Override
   protected void onEnable() {
      this.entity = PlayerUtils.getEntityOtherPlayerMP();
      minecraft.theWorld.addEntityToWorld(this.entity.getEntityId(), this.entity);
   }
}
