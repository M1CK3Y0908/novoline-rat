package rat.novoline.module.movement.flight;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import rat.novoline.module.ModuleManager;
import rat.novoline.module.movement.Flight;

/** DrugClient's PremiumFlyAddon, owned and invoked by Flight rather than registered twice. */
public final class FlightAddon {
   private static final Minecraft mc = Minecraft.getMinecraft();
   private double flycrit = 0.0;
   private int count;
   private final Flight flyModule;

   public FlightAddon(Flight flyModule) {
      this.flyModule = flyModule;
   }

   public void onEnable() {
      this.flycrit = 0.0;
      this.count = 0;
      if (mc.theWorld != null
         && this.flyModule.getMode().equalsIgnoreCase("Hypixel")
         && !this.flyModule.getHypixelMode().equalsIgnoreCase("Basic")) {
         double x = mc.thePlayer.posX;
         double y = mc.thePlayer.posY;
         double z = mc.thePlayer.posZ;
         if (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && FlightMotion.isOnGround(0.01)) {
            String var7 = this.flyModule.getHypixelMode();
            switch (var7) {
               case "Fast1":
                  for (int index = 0; index < 49; index++) {
                     mc.thePlayer
                        .sendQueue
                        .getNetworkManager()
                        .sendPacketNoEvent(
                           new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.06249, mc.thePlayer.posZ, false)
                        );
                     mc.thePlayer
                        .sendQueue
                        .getNetworkManager()
                        .sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                  }

                  mc.thePlayer
                     .sendQueue
                     .getNetworkManager()
                     .sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                  FlightMotion.setMotion(0.3 + FlightMotion.getSpeedEffect() * 0.05F);
                  mc.thePlayer.motionY = 0.42F + FlightMotion.getJumpEffect() * 0.1;
                  Flight.fastFlew = 25;
                  double speed = 13.0 + this.flyModule.hypixelSpeedValue();
                  Flight.hypixel = speed;
                  break;
               case "Fast2":
                  for (int index = 0; index < 49; index++) {
                     mc.thePlayer
                        .sendQueue
                        .getNetworkManager()
                        .sendPacketNoEvent(
                           new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.06249, mc.thePlayer.posZ, false)
                        );
                     mc.thePlayer
                        .sendQueue
                        .getNetworkManager()
                        .sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                  }

                  mc.thePlayer.onGround = false;
                  FlightMotion.setMotion(0.0);
                  mc.thePlayer.jumpMovementFactor = 0.0F;
                  break;
               case "OldFast":
                  FlightMotion.setMotion(0.361 + FlightMotion.getSpeedEffect() * 0.05F);
                  mc.thePlayer.motionY = 0.42F + FlightMotion.getJumpEffect() * 0.1;
                  Flight.fastFlew = 200;
                  Flight.hypixel = 17.0;
                  break;
            }
         }
      }
   }

   public void onDisable() {
      if (this.flyModule.getMode().equalsIgnoreCase("Hypixel")
         && !this.flyModule.getHypixelMode().equalsIgnoreCase("Basic")) {
         mc.thePlayer.motionX *= 0.0;
         mc.thePlayer.motionZ *= 0.0;
         mc.thePlayer.jumpMovementFactor = 0.1F;
      }
   }

   public void onPacket(Packet p) {
      Block block = FlightMotion.getBlockUnderPlayer(mc.thePlayer, 0.2);
      if (p instanceof C03PacketPlayer && !FlightMotion.isOnGround(1.0E-7) && !block.isFullBlock() && !(block instanceof BlockGlass)) {
         C03PacketPlayer packet = (C03PacketPlayer)p;
         packet.onGround = false;
      }
   }

   public void onUpdate() {
      if (!ModuleManager.speed.isEnabled()) {
         mc.thePlayer.motionX *= 0.0;
         mc.thePlayer.motionZ *= 0.0;
         mc.thePlayer.jumpMovementFactor = 0.31F + FlightMotion.getSpeedEffect() * 0.05F;
      }

      mc.thePlayer.motionY = 0.0;
      mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-8, mc.thePlayer.posZ);
      mc.thePlayer.onGround = false;
      if (mc.gameSettings.keyBindJump.isKeyDown()) {
         mc.thePlayer.motionY = 0.4;
      } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
         mc.thePlayer.motionY = -0.4;
      }
   }
}
