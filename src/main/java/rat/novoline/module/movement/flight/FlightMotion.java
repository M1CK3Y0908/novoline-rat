package rat.novoline.module.movement.flight;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;

/** Only the MoveUtils methods used by DrugClient Fly, preserving its formulas. */
public final class FlightMotion {
   private static final Minecraft mc = Minecraft.getMinecraft();

   public static void setMotion(double speed) {
      double forward = mc.thePlayer.movementInput.moveForward;
      double strafe = mc.thePlayer.movementInput.moveStrafe;
      float yaw = mc.thePlayer.rotationYaw;
      if (forward == 0.0 && strafe == 0.0) {
         mc.thePlayer.motionX = 0.0;
         mc.thePlayer.motionZ = 0.0;
      } else {
         if (forward != 0.0) {
            if (strafe > 0.0) {
               yaw += forward > 0.0 ? -45 : 45;
            } else if (strafe < 0.0) {
               yaw += forward > 0.0 ? 45 : -45;
            }

            strafe = 0.0;
            if (forward > 0.0) {
               forward = 1.0;
            } else if (forward < 0.0) {
               forward = -1.0;
            }
         }

         mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
         mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
      }
   }

   public static boolean isOnGround(double height) {
      return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
   }

   public static int getJumpEffect() {
      return mc.thePlayer.isPotionActive(Potion.jump) ? mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1 : 0;
   }

   public static int getSpeedEffect() {
      return mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;
   }

   public static Block getBlockUnderPlayer(EntityPlayer inPlayer, double height) {
      return Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(inPlayer.posX, inPlayer.posY - height, inPlayer.posZ)).getBlock();
   }
}
