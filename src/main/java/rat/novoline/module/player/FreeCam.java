package rat.novoline.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import rat.novoline.event.BlockCollisionEvent;
import rat.novoline.event.PacketSendEvent;
import rat.novoline.event.PlayerMoveEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.player.PlayerUtils;

public class FreeCam extends Module {
   private final NumberSetting freeCam = settingManager.register(new NumberSetting(this, "Speed", 2.0F, 1.0F, 5.0F));
   private Entity entity;
   @EventLink
   private final Listener<PacketSendEvent> onPacketSend = packetSendEvent -> {
      if (packetSendEvent.packet instanceof C03PacketPlayer c03PacketPlayer) {
         if (c03PacketPlayer.getClass() != C03PacketPlayer.class) {
            packetSendEvent.cancel();
         } else {
            c03PacketPlayer.onGround = this.entity.onGround;
         }
      }
   };
   @EventLink
   private final Listener<BlockCollisionEvent> onBlockCollision = blockCollisionEvent -> blockCollisionEvent.axisAlignedBB = null;
   @EventLink
   private final Listener<PlayerMoveEvent> onPlayerMove = playerMoveEvent -> {
      minecraft.thePlayer.noClip = true;
      float currentValue = this.freeCam.getCurrentValue();
      if (minecraft.gameSettings.keyBindJump.isKeyDown()) {
         playerMoveEvent.y += currentValue;
      } else if (minecraft.gameSettings.keyBindSneak.isKeyDown()) {
         playerMoveEvent.y -= currentValue;
      } else {
         playerMoveEvent.y = 0.0;
      }

      playerMoveEvent.setSpeed(currentValue);
   };

   public FreeCam() {
      super(Category.PLAYER, "FreeCam", "Move your camera freely");
   }

   @Override
   protected void onDisable() {
      minecraft.theWorld.removeEntityFromWorld(this.entity.getEntityId());
      minecraft.thePlayer.setPositionAndRotation(this.entity.posX, this.entity.posY, this.entity.posZ, this.entity.rotationYaw, this.entity.rotationPitch);
      minecraft.thePlayer.noClip = false;
   }

   @Override
   protected void onEnable() {
      this.entity = PlayerUtils.getEntityOtherPlayerMP();
      minecraft.theWorld.addEntityToWorld(this.entity.getEntityId(), this.entity);
   }
}
