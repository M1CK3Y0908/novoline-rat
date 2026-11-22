package client.m1ck3y.novoline.features.module.modules.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import client.m1ck3y.novoline.event.PacketReceiveEvent;
import client.m1ck3y.novoline.event.PacketSendEvent;
import client.m1ck3y.novoline.event.PlayerMoveEvent;
import client.m1ck3y.novoline.event.PreUpdateEvent;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.features.module.ModuleManager;
import client.m1ck3y.novoline.setting.CheckboxSetting;
import client.m1ck3y.novoline.setting.ComboSetting;
import client.m1ck3y.novoline.setting.NumberSetting;
import client.m1ck3y.novoline.utils.player.PlayerUtils;

public class Fly extends Module {

   //region Settings
   private final ComboSetting fly = settingManager.register(
      new ComboSetting(this, "Mode", 0, "Creative", "Motion", "DamageBoost", "Hypixel", "Zoom")
   );

   private final NumberSetting speed = settingManager.register(
      new NumberSetting(this, "Speed", 2.0F, 0.0F, 10.0F) {
         @Override
         public boolean shouldShow() {
            return Fly.this.fly.getCurrentElement() == 1;
         }
      }
   );

   private final NumberSetting boostValue = settingManager.register(
      new NumberSetting(this, "Boost value", 0.5F, 0.0F, 1.0F) {
         @Override
         public boolean shouldShow() {
            return Fly.this.fly.getCurrentElement() == 2;
         }
      }
   );

   private final NumberSetting zoomSpeed = settingManager.register(
      new NumberSetting(this, "Zoom Speed", 4.8F, 1.0F, 6.5F) {
         @Override
         public boolean shouldShow() {
            return Fly.this.fly.getCurrentElementAsString().equalsIgnoreCase("Zoom");
         }
      }
   );

   private final CheckboxSetting bobbing = settingManager.register(
      new CheckboxSetting(this, "Bobbing", false) {
         @Override
         public boolean shouldShow() {
            return Fly.this.fly.getCurrentElementAsString().equalsIgnoreCase("Zoom");
         }
      }
   );
   //endregion

   //region State Fields
   // DamageBoost & Hypixel state
   private boolean jumped;
   private double boostSpeed = 0.0;
   private boolean hovering;
   private int ticksSinceDamage = 0;

   // Zoom state
   private double zoomSpeedVal;
   private int zoomFastFlew;
   private double zoomCount;
   //endregion

   public Fly() {
      super(Category.MOVEMENT, "Fly", "Allows you to fly");
   }

   @Override
   protected void onInitialized() {
      this.addAutoDisableTargets(ModuleManager.speed, ModuleManager.scaffold, ModuleManager.antiVoid);
   }

   @Override
   protected void onEnable() {
      this.boostSpeed = 0.0;
      this.ticksSinceDamage = 0;
      this.hovering = false;
      this.jumped = false;

      this.zoomCount = 0.0;
      this.zoomFastFlew = 100;
      this.zoomSpeedVal = 0.0;

      if (this.fly.getCurrentElementAsString().equalsIgnoreCase("Zoom")) {
         minecraft.timer.timerSpeed = 1.0F;
         if (minecraft.theWorld != null && minecraft.thePlayer != null) {
            if (minecraft.thePlayer.onGround && minecraft.thePlayer.isCollidedVertically && isOnGround(0.01)) {
               for (int index = 0; index < 49; index++) {
                  minecraft.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(
                     new C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.06249, minecraft.thePlayer.posZ, false)
                  );
                  minecraft.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(
                     new C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, false)
                  );
               }
               minecraft.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(
                  new C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, true)
               );
               setMotion(0.3 + getSpeedEffect() * 0.05F);
               minecraft.thePlayer.motionY = 0.42F + getJumpEffect() * 0.1;
               this.zoomFastFlew = 25;
               this.zoomSpeedVal = 13.0 + this.zoomSpeed.getCurrentValue();
            }
         }
      }
   }

   @Override
   protected void onDisable() {
      if (this.fly.getCurrentElement() == 0) {
         if (minecraft.thePlayer != null) {
            minecraft.thePlayer.capabilities.isFlying = false;
         }
      } else if (this.fly.getCurrentElementAsString().equalsIgnoreCase("Zoom")) {
         if (minecraft.thePlayer != null) {
            minecraft.thePlayer.motionX = 0.0;
            minecraft.thePlayer.motionZ = 0.0;
            minecraft.thePlayer.jumpMovementFactor = 0.1F;
            setMotion(0.2);
            minecraft.thePlayer.jumpMovementFactor = 0.0F;
            minecraft.timer.timerSpeed = 1.0F;
         }
         this.zoomCount = 0.0;
         this.zoomFastFlew = 100;
      }
   }

   //region Event Listeners
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = event -> {
      if (minecraft.thePlayer == null || minecraft.theWorld == null) return;

      this.setSuffix(this.fly.getCurrentElementAsString());
      int currentElement = this.fly.getCurrentElement();

      switch (currentElement) {
         case 0: // Creative
            minecraft.thePlayer.capabilities.isFlying = true;
            break;

         case 1: // Motion
            minecraft.thePlayer.motionY = 0.0;
            if (PlayerUtils.isMoving()) {
               PlayerUtils.setMotionInMoveDirection(this.speed.getCurrentValue());
            } else {
               PlayerUtils.setMotionInMoveDirection(0.0);
            }

            if (minecraft.gameSettings.keyBindJump.isKeyDown()) {
               minecraft.thePlayer.motionY += 0.6F;
            } else if (minecraft.gameSettings.keyBindSneak.isKeyDown()) {
               minecraft.thePlayer.motionY -= 0.6F;
            }
            break;

         case 3: // Hypixel
            if (!this.jumped && minecraft.thePlayer.onGround) {
               minecraft.thePlayer.motionY = 0.42F;
               this.jumped = true;
               return;
            }

            if (minecraft.thePlayer.onGround && !this.hovering) {
               this.sendPacketUnregistered(new C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY - 0.42F, minecraft.thePlayer.posZ, true));
               this.hovering = true;
            }

            if (this.hovering) {
               minecraft.thePlayer.motionY = 0.0;
            }
            break;

         case 4: // Zoom (Hypixel Fast1)
            this.zoomFastFlew++;

            if (this.bobbing.isEnabled() && (minecraft.thePlayer.moveForward != 0.0F || minecraft.thePlayer.moveStrafing != 0.0F)) {
               minecraft.thePlayer.cameraYaw = 0.05F;
            }

            Block block = getBlockUnderPlayer(minecraft.thePlayer, 0.2);
            if (!isOnGround(1.0E-7) && !block.isFullBlock() && !(block instanceof BlockGlass)) {
               minecraft.thePlayer.motionY = 0.0;
               minecraft.thePlayer.motionX = 0.0;
               minecraft.thePlayer.motionZ = 0.0;
               float forward = 0.29F + getSpeedEffect() * 0.06F;
               if (this.zoomSpeedVal > 0.0) {
                  if ((minecraft.thePlayer.moveForward == 0.0F && minecraft.thePlayer.moveStrafing == 0.0F) || minecraft.thePlayer.isCollidedHorizontally) {
                     this.zoomSpeedVal = 0.0;
                  }

                  forward = (float)(forward + this.zoomSpeedVal / 18.0);
                  this.zoomSpeedVal = this.zoomSpeedVal - (0.155 + getSpeedEffect() * 0.006);
               }

               this.setZoomMotion(forward);
               minecraft.thePlayer.jumpMovementFactor = 0.0F;
               minecraft.thePlayer.onGround = false;
               if (minecraft.gameSettings.keyBindJump.isKeyDown()) {
                  minecraft.thePlayer.motionY = 0.4;
               }

               this.zoomCount++;
               minecraft.thePlayer.lastReportedPosY = 0.0;
               if (this.zoomCount <= 2.0) {
                  minecraft.thePlayer.setPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.9999E-8, minecraft.thePlayer.posZ);
               } else if (this.zoomCount == 4.0) {
                  minecraft.thePlayer.setPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.0E-8, minecraft.thePlayer.posZ);
               } else if (this.zoomCount >= 5.0) {
                  minecraft.thePlayer.setPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.99999E-8, minecraft.thePlayer.posZ);
                  this.zoomCount = 0.0;
               }
            }
            break;
      }
   };

   @EventLink
   public final Listener<PlayerMoveEvent> onPlayerMove = event -> {
      if (minecraft.thePlayer == null || minecraft.theWorld == null) return;

      int currentElement = this.fly.getCurrentElement();
      if (currentElement == 2) { // DamageBoost
         minecraft.thePlayer.setSprinting(true);
         this.ticksSinceDamage++;
         if (minecraft.thePlayer.hurtTime > 0 && minecraft.thePlayer.hurtTime < 7) {
            if (minecraft.thePlayer.hurtTime > 4) {
               this.ticksSinceDamage = 0;
            }

            if (minecraft.thePlayer.onGround) {
               this.boostSpeed = (int)this.boostValue.getCurrentValue();
               this.boostSpeed = this.boostSpeed * (minecraft.thePlayer.isPotionActive(Potion.jump) ? 2.0 : 2.15);
               event.y = minecraft.thePlayer.motionY = 4.0;
            }
         }

         if (this.ticksSinceDamage > 12 && this.boostSpeed > 0.0) {
            this.boostSpeed *= 0.93;
         }

         event.setSpeed(Math.max(this.boostSpeed, PlayerUtils.getDefaultMoveSpeed()));
      } else if (currentElement == 3) { // Hypixel
         if (!this.hovering) {
            event.x = event.z = 0.0;
         } else {
            event.setSpeed(PlayerUtils.getDefaultMoveSpeed());
         }
      }
   };

   @EventLink
   public final Listener<PacketSendEvent> onPacketSend = event -> {
      if (minecraft.thePlayer == null || minecraft.theWorld == null) return;

      if (this.fly.getCurrentElementAsString().equalsIgnoreCase("Zoom")) {
         if (event.packet instanceof C03PacketPlayer) {
            C03PacketPlayer c03 = (C03PacketPlayer)event.packet;
            c03.onGround = isOnGround(0.001);
         }
      }
   };

   @EventLink
   public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
      if (minecraft.thePlayer == null || minecraft.theWorld == null) return;

      if (this.fly.getCurrentElementAsString().equalsIgnoreCase("Zoom")) {
         if (event.packet instanceof S08PacketPlayerPosLook) {
            this.zoomSpeedVal = 0.0;
         } else if (event.packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity vel = (S12PacketEntityVelocity)event.packet;
            if (vel.getEntityID() == minecraft.thePlayer.getEntityId()) {
               event.setCancelled(true);
            }
         } else if (event.packet instanceof S27PacketExplosion) {
            event.setCancelled(true);
         }
      }
   };
   //endregion

   //region Motion & Math Helpers for Zoom
   private void setZoomMotion(double speed) {
      if (minecraft.thePlayer == null) return;
      double forward = minecraft.thePlayer.movementInput.moveForward;
      double strafe = minecraft.thePlayer.movementInput.moveStrafe;
      float yaw = minecraft.thePlayer.rotationYaw;
      if (forward == 0.0 && strafe == 0.0) {
         minecraft.thePlayer.motionX = 0.0;
         minecraft.thePlayer.motionZ = 0.0;
      } else {
         if (forward != 0.0) {
            if (this.zoomSpeedVal <= 0.0) {
               if (strafe > 0.0) {
                  yaw += (forward > 0.0 ? -45 : 45);
               } else if (strafe < 0.0) {
                  yaw += (forward > 0.0 ? 45 : -45);
               }
            }

            strafe = 0.0;
            if (forward > 0.0) {
               forward = 1.0;
            } else if (forward < 0.0) {
               forward = -1.0;
            }
         }

         minecraft.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
         minecraft.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
      }
   }

   private static void setMotion(double speed) {
      if (minecraft.thePlayer == null) return;
      double forward = minecraft.thePlayer.movementInput.moveForward;
      double strafe = minecraft.thePlayer.movementInput.moveStrafe;
      float yaw = minecraft.thePlayer.rotationYaw;
      if (forward == 0.0 && strafe == 0.0) {
         minecraft.thePlayer.motionX = 0.0;
         minecraft.thePlayer.motionZ = 0.0;
      } else {
         if (forward != 0.0) {
            if (strafe > 0.0) {
               yaw += (forward > 0.0 ? -45 : 45);
            } else if (strafe < 0.0) {
               yaw += (forward > 0.0 ? 45 : -45);
            }

            strafe = 0.0;
            if (forward > 0.0) {
               forward = 1.0;
            } else if (forward < 0.0) {
               forward = -1.0;
            }
         }

         minecraft.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
         minecraft.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
      }
   }

   private static boolean isOnGround(double height) {
      return !minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
   }

   private static int getJumpEffect() {
      return minecraft.thePlayer.isPotionActive(Potion.jump) ? minecraft.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1 : 0;
   }

   private static int getSpeedEffect() {
      return minecraft.thePlayer.isPotionActive(Potion.moveSpeed) ? minecraft.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;
   }

   private static Block getBlockUnderPlayer(EntityPlayer inPlayer, double height) {
      return minecraft.theWorld.getBlockState(new BlockPos(inPlayer.posX, inPlayer.posY - height, inPlayer.posZ)).getBlock();
   }
   //endregion
}
