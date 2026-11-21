package rat.novoline.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import rat.novoline.event.PacketReceiveEvent;
import rat.novoline.event.PacketSendEvent;
import rat.novoline.event.PlayerMoveEvent;
import rat.novoline.event.PreUpdateEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.module.movement.flight.FlightAddon;
import rat.novoline.module.movement.flight.FlightMotion;
import rat.novoline.module.movement.flight.FlightTimer;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.notification.NotificationType;

public class Flight extends Module {

   public final ComboSetting mode = settingManager.register(
      new ComboSetting(this, "Mode", 4, "Vanilla", "Guardian", "AntiKick", "Glide", "Motion", "Hypixel", "BowFly", "Cube1", "Cube2", "Cube3")
   );
   public final ComboSetting hypixelMode = settingManager.register(
      new ComboSetting(this, "Hypixel Mode", 0, "Basic", "OldFast", "Fast1", "Fast2") {
         @Override
         public boolean shouldShow() {
            return Flight.this.mode.getCurrentElementAsString().equalsIgnoreCase("Hypixel");
         }
      }
   );
   public final NumberSetting speed = settingManager.register(
      new NumberSetting(this, "Speed", 2.0F, 0.25F, 5.0F) {
         @Override
         public boolean shouldShow() {
            String m = Flight.this.mode.getCurrentElementAsString();
            return m.equalsIgnoreCase("AntiKick") || m.equalsIgnoreCase("Motion") || m.equalsIgnoreCase("Glide");
         }
      }
   );
   public final NumberSetting hypixelSpeed = settingManager.register(
      new NumberSetting(this, "Hypixel Speed", 4.8F, 1.0F, 6.5F) {
         @Override
         public boolean shouldShow() {
            return Flight.this.mode.getCurrentElementAsString().equalsIgnoreCase("Hypixel");
         }
      }
   );
   public final CheckboxSetting bobbing = settingManager.register(
      new CheckboxSetting(this, "Bobbing", false)
   );

   private final FlightAddon premiumAddon = new FlightAddon(this);
   private final FlightTimer kickTimer = new FlightTimer();
   private final FlightTimer bowfly = new FlightTimer();
   private double flyHeight;
   private double startY;
   private double count;
   public static double hypixel = 0.0;
   public static int fastFlew;
   private int stage;
   private boolean shouldSpeed;

   public Flight() {
      super(Category.MOVEMENT, "Flight", "Flux/DrugClient flight");
   }

   public String getMode() {
      return this.mode.getCurrentElementAsString();
   }

   public String getHypixelMode() {
      return this.hypixelMode.getCurrentElementAsString();
   }

   public double hypixelSpeedValue() {
      return this.hypixelSpeed.getCurrentValue();
   }

   @Override
   protected void onInitialized() {
      this.addAutoDisableTargets(ModuleManager.fly, ModuleManager.speed, ModuleManager.scaffold, ModuleManager.antiVoid);
   }

   @Override
   protected void onEnable() {
      this.count = 0.0;
      if (minecraft.thePlayer != null) {
         if (this.mode.getCurrentElementAsString().equalsIgnoreCase("Hypixel")
            && this.hypixelMode.getCurrentElementAsString().equalsIgnoreCase("Fast2")) {
            fastFlew = 0;
         } else {
            fastFlew = 100;
         }

         minecraft.timer.timerSpeed = 1.0F;
         if (this.mode.getCurrentElementAsString().contains("Cube")) {
            this.shouldSpeed = false;
            if (!FlightMotion.isOnGround(0.001)) {
               minecraft.timer.timerSpeed = 0.3F;
            }

            this.stage = 0;
         } else {
            this.stage = 1;
         }

         this.startY = minecraft.thePlayer.posY;
         hypixel = 0.0;
         if (this.premiumAddon != null) {
            this.premiumAddon.onEnable();
         }
      }

      this.setSuffix(this.mode.getCurrentElementAsString());
   }

   @Override
   protected void onDisable() {
      if (minecraft.thePlayer == null) {
         this.count = 0.0;
         fastFlew = 100;
         minecraft.timer.timerSpeed = 1.0F;
         return;
      }

      if (this.mode.getCurrentElementAsString().equalsIgnoreCase("BowFly")) {
         this.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      }

      if (this.mode.getCurrentElementAsString().equalsIgnoreCase("Vanilla")) {
         minecraft.thePlayer.capabilities.isFlying = false;
         if (!minecraft.thePlayer.capabilities.isCreative()) {
            minecraft.thePlayer.capabilities.allowFlying = false;
         }
      }

      if (this.premiumAddon != null) {
         this.premiumAddon.onDisable();
      }

      this.count = 0.0;
      fastFlew = 100;
      FlightMotion.setMotion(0.2);
      minecraft.thePlayer.jumpMovementFactor = 0.0F;
      minecraft.timer.timerSpeed = 1.0F;
      if (this.mode.getCurrentElementAsString().contains("Cube")) {
         minecraft.timer.timerSpeed = 1.0F;
         minecraft.thePlayer.onGround = false;
         minecraft.thePlayer.jumpMovementFactor = 0.0F;
         minecraft.thePlayer.motionY = -0.4;
         FlightMotion.setMotion(0.2);
      }
   }

   @EventLink
   public final Listener<PacketSendEvent> onPacketSend = event -> {
      if (minecraft.thePlayer == null || minecraft.theWorld == null) return;
      if (handlePacket(event.packet, true)) {
         event.setCancelled(true);
      }
   };

   @EventLink
   public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
      if (minecraft.thePlayer == null || minecraft.theWorld == null) return;
      if (handlePacket(event.packet, false)) {
         event.setCancelled(true);
      }
   };

   private boolean handlePacket(Packet<?> packet, boolean outgoing) {
      String currentMode = this.mode.getCurrentElementAsString();
      boolean cancelled = false;

      if (packet instanceof S08PacketPlayerPosLook) {
         hypixel = 0.0;
      }

      if (!currentMode.equalsIgnoreCase("BowFly")) {
         if (packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity vel = (S12PacketEntityVelocity)packet;
            if (vel.getEntityID() == minecraft.thePlayer.getEntityId()) {
               cancelled = true;
            }
         }

         if (packet instanceof S27PacketExplosion) {
            cancelled = true;
         }
      }

      if (packet instanceof C03PacketPlayer) {
         C03PacketPlayer c03 = (C03PacketPlayer)packet;
         if (currentMode.equalsIgnoreCase("Hypixel")) {
            c03.onGround = FlightMotion.isOnGround(0.001);
         }
      }

      if (outgoing && currentMode.equalsIgnoreCase("Guardian") && !ModuleManager.noFall.isEnabled() && this.premiumAddon != null) {
         this.premiumAddon.onPacket(packet);
      }

      return cancelled;
   }

   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = event -> {
      if (minecraft.thePlayer == null || minecraft.theWorld == null) return;

      this.setSuffix(this.mode.getCurrentElementAsString());

      if (this.bobbing.isEnabled() && (minecraft.thePlayer.moveForward != 0.0F || minecraft.thePlayer.moveStrafing != 0.0F)) {
         minecraft.thePlayer.cameraYaw = 0.05F;
      }

      String currentMode = this.mode.getCurrentElementAsString();
      double var22 = Math.max((double)this.speed.getCurrentValue(), this.getBaseMoveSpeed());

      if (currentMode.equalsIgnoreCase("Hypixel")) {
         fastFlew++;
         if (this.premiumAddon != null
            && this.hypixelMode.getCurrentElementAsString().equalsIgnoreCase("Fast2")
            && minecraft.thePlayer.onGround
            && minecraft.thePlayer.isCollidedVertically
            && FlightMotion.isOnGround(0.01)) {
            if (minecraft.thePlayer.hurtResistantTime == 19) {
               FlightMotion.setMotion(0.3 + FlightMotion.getSpeedEffect() * 0.05F);
               minecraft.thePlayer.motionY = 0.42F + FlightMotion.getJumpEffect() * 0.1;
               fastFlew = 25;
               hypixel = 13.0 + this.hypixelSpeedValue();
            } else if (fastFlew < 25) {
               minecraft.thePlayer.motionX = 0.0;
               minecraft.thePlayer.motionZ = 0.0;
               minecraft.thePlayer.jumpMovementFactor = 0.0F;
               minecraft.thePlayer.onGround = false;
            }
         }

         Block X = FlightMotion.getBlockUnderPlayer(minecraft.thePlayer, 0.2);
         if (!FlightMotion.isOnGround(1.0E-7) && !X.isFullBlock() && !(X instanceof BlockGlass)) {
            minecraft.thePlayer.motionY = 0.0;
            minecraft.thePlayer.motionX = 0.0;
            minecraft.thePlayer.motionZ = 0.0;
            float forward = 0.29F + FlightMotion.getSpeedEffect() * 0.06F;
            if (hypixel > 0.0 && this.premiumAddon != null) {
               if (minecraft.thePlayer.moveForward == 0.0F && minecraft.thePlayer.moveStrafing == 0.0F || minecraft.thePlayer.isCollidedHorizontally) {
                  hypixel = 0.0;
               }

               forward = (float)(forward + hypixel / 18.0);
               if (this.hypixelMode.getCurrentElementAsString().equalsIgnoreCase("OldFast")) {
                  hypixel--;
               } else if (this.hypixelMode.getCurrentElementAsString().equalsIgnoreCase("Fast3")) {
                  hypixel = hypixel - (0.175 + FlightMotion.getSpeedEffect() * 0.006);
               } else {
                  hypixel = hypixel - (0.155 + FlightMotion.getSpeedEffect() * 0.006);
               }
            }

            this.setSpecialMotion(forward);
            minecraft.thePlayer.jumpMovementFactor = 0.0F;
            minecraft.thePlayer.onGround = false;
            if (minecraft.gameSettings.keyBindJump.isKeyDown()) {
               minecraft.thePlayer.motionY = 0.4;
            }

            this.count++;
            minecraft.thePlayer.lastReportedPosY = 0.0;
            if (this.count <= 2.0) {
               minecraft.thePlayer.setPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.9999E-8, minecraft.thePlayer.posZ);
            } else if (this.count == 4.0) {
               minecraft.thePlayer.setPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.0E-8, minecraft.thePlayer.posZ);
            } else if (this.count >= 5.0) {
               minecraft.thePlayer.setPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 1.99999E-8, minecraft.thePlayer.posZ);
               this.count = 0.0;
            }
         }
      }

      double Y = minecraft.thePlayer.posY;
      switch (currentMode) {
         case "Cube1":
            if (!FlightMotion.isOnGround(0.001)) {
               this.stage++;
               minecraft.thePlayer.onGround = false;
               minecraft.thePlayer.lastReportedPosY = 0.0;
               minecraft.thePlayer.jumpMovementFactor = 0.0F;
               float var31x = 0.3F;
               double var33x = 0.0;
               var22 = 0.0;
               if (this.stage == 1) {
                  var33x = 0.28;
                  var22 = 2.4;
               } else if (this.stage == 2) {
                  var33x = 0.28;
               } else if (this.stage == 3) {
                  var33x = 0.28;
                  var22 = 2.4;
               } else if (this.stage == 4) {
                  var33x = -0.9;
                  this.stage = 0;
               }

               minecraft.timer.timerSpeed = var31x;
               minecraft.thePlayer.motionY = var33x;
               FlightMotion.setMotion(var22);
            } else if (minecraft.timer.timerSpeed == 0.3F) {
               minecraft.timer.timerSpeed = 1.0F;
               FlightMotion.setMotion(0.0);
            }
            break;
         case "Cube2":
            if (!FlightMotion.isOnGround(0.001)) {
               this.stage++;
               minecraft.timer.timerSpeed = 0.27F;
               minecraft.thePlayer.lastReportedPosY = 0.0;
               minecraft.thePlayer.jumpMovementFactor = 0.0F;
               minecraft.thePlayer.onGround = false;
               double var35 = minecraft.thePlayer.motionY;
               var22 = 0.0;
               event.setOnGround(false);
               if (this.stage == 1) {
                  boolean var36 = minecraft.theWorld
                     .getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().offset(0.0, 0.4, 0.0))
                     .isEmpty();
                  double var10002 = var36 ? 0.4 : 0.2;
                  event.setY(Y + var10002);
                  var35 = 0.4;
               } else if (this.stage == 2) {
                  var35 = 0.28;
                  boolean var36 = minecraft.theWorld
                     .getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().offset(0.0, 0.68, 0.0))
                     .isEmpty();
                  double var10002 = var36 ? 0.68 : 0.2;
                  event.setY(Y + var10002);
                  if (this.shouldSpeed) {
                     var22 = 2.4;
                  } else {
                     this.shouldSpeed = true;
                     var22 = 2.2;
                  }
               } else if (this.stage == 3) {
                  var35 = -0.68;
               } else if (this.stage == 4) {
                  var35 = 0.0;
                  var22 = 2.4;
                  this.stage = 0;
               }

               FlightMotion.setMotion(var22);
               minecraft.thePlayer.motionY = 0.0;
            } else if (this.shouldSpeed) {
               FlightMotion.setMotion(0.0);
               this.shouldSpeed = !this.shouldSpeed;
            } else if (minecraft.timer.timerSpeed != 1.0F) {
               minecraft.timer.timerSpeed = 1.0F;
            }
            break;
         case "Cube3":
            if (!FlightMotion.isOnGround(0.001)) {
               this.stage++;
               minecraft.thePlayer.onGround = false;
               minecraft.thePlayer.lastReportedPosY = 0.0;
               minecraft.thePlayer.jumpMovementFactor = 0.0F;
               float var31 = 0.2F;
               double var33 = 0.0;
               var22 = 0.0;
               double var38 = 4.0;
               byte var39;
               if (!minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().expand(var38, var38, var38)).isEmpty()) {
                  var39 = 1;
               } else {
                  var39 = 0;
               }

               if (this.stage == 1) {
                  var33 = 0.4;
                  if (this.shouldSpeed) {
                     var22 = 2.3;
                  } else {
                     var22 = 2.0;
                     this.shouldSpeed = !this.shouldSpeed;
                  }
               } else if (this.stage == 2) {
                  var33 = 0.0;
                  var31 = 1.0F;
               } else if (this.stage == 3) {
                  var33 = 0.4;
                  var22 = 2.3;
                  if (var39 != 0) {
                     var31 = 1.0F;
                  }
               } else if (this.stage == 4) {
                  var33 = -0.9;
                  this.stage = 0;
                  if (var39 != 0) {
                     var31 = 1.0F;
                  }
               }

               minecraft.timer.timerSpeed = var31;
               minecraft.thePlayer.motionY = var33;
               FlightMotion.setMotion(var22);
            } else if (this.shouldSpeed) {
               FlightMotion.setMotion(0.0);
               this.shouldSpeed = !this.shouldSpeed;
            } else if (minecraft.timer.timerSpeed != 1.0F) {
               minecraft.timer.timerSpeed = 1.0F;
            }
            break;
         case "Guardian":
            if (this.premiumAddon != null) {
               this.premiumAddon.onUpdate();
            }
            break;
         case "Glide":
            byte var39x;
            if (minecraft.thePlayer.posY + 0.1 >= this.startY && minecraft.gameSettings.keyBindJump.isKeyDown()) {
               var39x = 1;
            } else {
               var39x = 0;
            }

            if (minecraft.thePlayer.isSneaking()) {
               minecraft.thePlayer.motionY = -0.4F;
            } else if (minecraft.gameSettings.keyBindJump.isKeyDown() && var39x == 0) {
               minecraft.thePlayer.motionY = 0.4F;
            } else {
               minecraft.thePlayer.motionY = -0.01;
            }
            break;
         case "BowFly":
            if (this.getBowCount() == 0 && this.getArrowCount() == 0) {
               if (this.bowfly.delay(5000.0F)) {
                  this.showNotification(NotificationType.WARNING, "BowFly", "Please get a bow and arrows.", 5000L);
                  this.bowfly.reset();
               }
               return;
            }

            if (this.getBowCount() == 0) {
               if (this.bowfly.delay(5000.0F)) {
                  this.showNotification(NotificationType.WARNING, "BowFly", "Please get a bow.", 5000L);
                  this.bowfly.reset();
               }
               return;
            }

            if (this.getArrowCount() == 0) {
               if (this.bowfly.delay(5000.0F)) {
                  this.showNotification(NotificationType.WARNING, "BowFly", "Please get arrows.", 5000L);
                  this.bowfly.reset();
               }
               return;
            }

            if (this.invCheck()) {
               ItemStack C07 = new ItemStack(Item.getItemById(261));

               for (int C08 = 9; C08 < 36; C08++) {
                  if (minecraft.thePlayer.inventoryContainer.getSlot(C08).getHasStack()) {
                     Item yaw1 = minecraft.thePlayer.inventoryContainer.getSlot(C08).getStack().getItem();
                     int pitch = 0;
                     if (yaw1 instanceof ItemBow) {
                        for (int a = 36; a < 45; a++) {
                           if (Container.canAddItemToSlot(minecraft.thePlayer.inventoryContainer.getSlot(a), C07, true)) {
                              minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, C08, a - 36, 2, minecraft.thePlayer);
                              pitch++;
                              break;
                           }
                        }

                        if (pitch == 0) {
                           minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, C08, 7, 2, minecraft.thePlayer);
                        }
                        break;
                     }
                  }
               }
            }

            this.getBow();
            if (minecraft.thePlayer.getCurrentEquippedItem() != null && minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow) {
               C07PacketPlayerDigging var29 = new C07PacketPlayerDigging(
                  C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN
               );
               C08PacketPlayerBlockPlacement var32 = new C08PacketPlayerBlockPlacement(minecraft.thePlayer.inventory.getCurrentItem());
               float var34 = minecraft.thePlayer.rotationYaw;
               float var37 = -90.0F;
               if (minecraft.thePlayer.moveForward != 0.0F || minecraft.thePlayer.moveStrafing != 0.0F) {
                  var37 = -80.0F;
               }

               if (minecraft.thePlayer.moveForward < 0.0F) {
                  var34 -= 180.0F;
               }

               if (minecraft.thePlayer.motionY < -0.1) {
                  var37 = 90.0F;
               }

               event.setPitch(var37);
               event.setYaw(var34);
               if (minecraft.thePlayer.onGround && minecraft.thePlayer.isCollidedVertically && FlightMotion.isOnGround(1.0E-4)) {
                  minecraft.thePlayer.jump();
               } else if (minecraft.thePlayer.motionY < 0.0) {
                  minecraft.timer.timerSpeed = 0.1F;
               } else if (minecraft.timer.timerSpeed == 0.1F) {
                  minecraft.timer.timerSpeed = 1.0F;
               }

               this.count++;
               if (this.count >= 4.0) {
                  this.sendPacket(var29);
                  this.count = 0.0;
               } else if (this.count == 1.0) {
                  this.sendPacket(var32);
               }
            }
            break;
         case "Vanilla":
            minecraft.thePlayer.capabilities.isFlying = true;
            minecraft.thePlayer.capabilities.allowFlying = true;
            break;
         case "Motion":
            minecraft.thePlayer.onGround = false;
            boolean var10001;
            if (!FlightMotion.isOnGround(0.001) && !ModuleManager.noFall.isEnabled()) {
               var10001 = false;
            } else {
               var10001 = true;
            }

            event.setOnGround(var10001);
            if (minecraft.thePlayer.movementInput.jump) {
               minecraft.thePlayer.motionY = var22 * 0.6;
            } else if (minecraft.thePlayer.movementInput.sneak) {
               minecraft.thePlayer.motionY = -var22 * 0.6;
            } else {
               minecraft.thePlayer.motionY = 0.0;
            }
            break;
         case "AntiKick":
            if (minecraft.thePlayer.movementInput.jump) {
               minecraft.thePlayer.motionY = var22 * 0.6;
            } else if (minecraft.thePlayer.movementInput.sneak) {
               minecraft.thePlayer.motionY = -var22 * 0.6;
            } else {
               minecraft.thePlayer.motionY = 0.0;
            }

            this.updateFlyHeight();
            this.sendPacket(new C03PacketPlayer(true));
            if (this.flyHeight <= 290.0 && this.kickTimer.delay(500.0F) || this.flyHeight > 290.0 && this.kickTimer.delay(100.0F)) {
               this.goToGround();
               this.kickTimer.reset();
            }
            break;
      }
   };

   @EventLink
   public final Listener<PlayerMoveEvent> onPlayerMove = event -> {
      if (minecraft.thePlayer == null || minecraft.theWorld == null) return;

      String var28 = this.mode.getCurrentElementAsString();
      if (var28.equalsIgnoreCase("Antikick") || var28.equalsIgnoreCase("Motion") || var28.equalsIgnoreCase("glide")) {
         double var25 = (double)this.speed.getCurrentValue();
         double var27 = minecraft.thePlayer.movementInput.moveForward;
         double strafe = minecraft.thePlayer.movementInput.moveStrafe;
         float yaw = minecraft.thePlayer.rotationYaw;
         if (var27 == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
         } else {
            if (var27 != 0.0) {
               if (strafe > 0.0) {
                  byte var40;
                  if (var27 > 0.0) {
                     var40 = -45;
                  } else {
                     var40 = 45;
                  }

                  yaw += var40;
               } else if (strafe < 0.0) {
                  byte var40;
                  if (var27 > 0.0) {
                     var40 = 45;
                  } else {
                     var40 = -45;
                  }

                  yaw += var40;
               }

               strafe = 0.0;
               if (var27 > 0.0) {
                  var27 = 1.0;
               } else if (var27 < 0.0) {
                  var27 = -1.0;
               }
            }

            event.setX(var27 * var25 * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * var25 * Math.sin(Math.toRadians(yaw + 90.0F)));
            event.setZ(var27 * var25 * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * var25 * Math.cos(Math.toRadians(yaw + 90.0F)));
         }
      }
   };

   public double getBaseMoveSpeed() {
      double baseSpeed = 0.2873;
      if (minecraft.thePlayer.isPotionActive(Potion.moveSpeed)) {
         int amplifier = minecraft.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
         baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
      }

      return baseSpeed;
   }

   public void updateFlyHeight() {
      double h = 1.0;
      AxisAlignedBB box = minecraft.thePlayer.getEntityBoundingBox().expand(0.0625, 0.0625, 0.0625);

      for (this.flyHeight = 0.0; this.flyHeight < minecraft.thePlayer.posY; this.flyHeight += h) {
         AxisAlignedBB nextBox = box.offset(0.0, -this.flyHeight, 0.0);
         if (minecraft.theWorld.checkBlockCollision(nextBox)) {
            if (h < 0.0625) {
               break;
            }

            this.flyHeight -= h;
            h /= 2.0;
         }
      }
   }

   public void goToGround() {
      if (this.flyHeight <= 300.0) {
         double minY = minecraft.thePlayer.posY - this.flyHeight;
         if (minY > 0.0) {
            double y = minecraft.thePlayer.posY;

            while (!(y <= minY)) {
               y -= 8.0;
               if (y < minY) {
                  y = minY;
               }

               C03PacketPlayer.C04PacketPlayerPosition packet = new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, y, minecraft.thePlayer.posZ, true);
               this.sendPacket(packet);
            }

            y = minY;

            while (!(y >= minecraft.thePlayer.posY)) {
               y += 8.0;
               if (y > minecraft.thePlayer.posY) {
                  y = minecraft.thePlayer.posY;
               }

               C03PacketPlayer.C04PacketPlayerPosition packet = new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, y, minecraft.thePlayer.posZ, true);
               this.sendPacket(packet);
            }
         }
      }
   }

   private boolean invCheck() {
      for (int i = 36; i < 45; i++) {
         if (minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            Item item = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
            if (item instanceof ItemBow) {
               return false;
            }
         }
      }

      return true;
   }

   public int getArrowCount() {
      int arrowCount = 0;

      for (int i = 0; i < 45; i++) {
         if (minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack is = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = is.getItem();
            Item arrow = Item.getItemById(262);
            if (item == arrow) {
               arrowCount += is.stackSize;
            }
         }
      }

      return arrowCount;
   }

   public int getBowCount() {
      int bowCount = 0;

      for (int i = 0; i < 45; i++) {
         if (minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack is = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (is.getItem() instanceof ItemBow) {
               bowCount += is.stackSize;
            }
         }
      }

      return bowCount;
   }

   private void getBow() {
      ItemStack is = new ItemStack(Item.getItemById(262));

      try {
         if (minecraft.thePlayer.inventory.getCurrentItem() != null && !(minecraft.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemBow)) {
            for (int i = 36; i < 45; i++) {
               int theSlot = i - 36;
               if (!Container.canAddItemToSlot(minecraft.thePlayer.inventoryContainer.getSlot(i), is, true)
                  && minecraft.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemBow
                  && minecraft.thePlayer.inventoryContainer.getSlot(i).getStack() != null) {
                  minecraft.thePlayer.inventory.currentItem = theSlot;
                  this.sendPacket(new C09PacketHeldItemChange(minecraft.thePlayer.inventory.currentItem));
                  minecraft.playerController.updateController();
                  break;
               }
            }
         }
      } catch (Exception ignored) {
      }
   }

   private void setSpecialMotion(double speed) {
      double forward = minecraft.thePlayer.movementInput.moveForward;
      double strafe = minecraft.thePlayer.movementInput.moveStrafe;
      float yaw = minecraft.thePlayer.rotationYaw;
      if (forward == 0.0 && strafe == 0.0) {
         minecraft.thePlayer.motionX = 0.0;
         minecraft.thePlayer.motionZ = 0.0;
      } else {
         if (forward != 0.0) {
            if (hypixel <= 0.0) {
               if (strafe > 0.0) {
                  byte var10001;
                  if (forward > 0.0) {
                     var10001 = -45;
                  } else {
                     var10001 = 45;
                  }

                  yaw += var10001;
               } else if (strafe < 0.0) {
                  byte var10001;
                  if (forward > 0.0) {
                     var10001 = 45;
                  } else {
                     var10001 = -45;
                  }

                  yaw += var10001;
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
}
