package wtf.novoline.module.world;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Mouse;
import wtf.novoline.enums.ServerType;
import wtf.novoline.event.GameEndEvent;
import wtf.novoline.event.LoadWorldEvent;
import wtf.novoline.event.MoveEntityEvent;
import wtf.novoline.event.MoveFlyingEvent;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.PlayerMoveEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.RenderWorldEvent;
import wtf.novoline.event.SyncCurrentSlotEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.combat.RotationUtils;
import wtf.novoline.utils.combat.RotationUtils.Precision;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.player.MoveUtils;
import wtf.novoline.utils.player.PlayerUtils;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.world.BlockUtils;

public class Scaffold extends Module {

   public enum RotationMode {
      None, Strict, Smooth, Simple, Lazy, Offset;

      public float side;
      public float sideJitter;
      public float groundSide;

      public void onDisable() {
         this.groundSide = 0.0f;
         this.sideJitter = 0.0f;
         this.side = 0.0f;
      }
   }

   public enum FastScaffoldMode {
      Disabled(false, false, false),
      Vanilla(true, false, false),
      Constant_A(true, false, false),
      Constant_B(false, false, false),
      Float_A(true, false, false),
      Float_B(true, false, false),
      Jump_A(false, true, false),
      Jump_B(false, true, false),
      Jump_C(true, true, false),
      Jump_D(true, true, false),
      Keep_Y_A(false, false, true),
      Keep_Y_B(false, false, true),
      Side(true, false, false);

      public final boolean sprint;
      public final boolean jump;
      public final boolean keepY;
      public int state;
      public int ticks;
      public double height;

      FastScaffoldMode(boolean sprint, boolean jump, boolean keepY) {
         this.sprint = sprint;
         this.jump = jump;
         this.keepY = keepY;
      }

      public boolean canSprint(Scaffold scaffold) {
         if (!this.canFastScaffold(scaffold)) {
            if (scaffold.minecraft.thePlayer.ticksExisted == scaffold.startTick && scaffold.jumpState == 2) {
               return false;
            }
            return false;
         }
         if (scaffold.legitMotion.isEnabled() && (this.jump || this.keepY)) {
            return scaffold.legitTicks > 0;
         }
         if (!this.sprint) {
            return false;
         }
         return this != Side || this.ticks != -1;
      }

      public void onEnable(Scaffold scaffold) {
         scaffold.enableTick = scaffold.minecraft.thePlayer.ticksExisted;
         this.state = 0;
         if (this == Float_A) {
            if (scaffold.minecraft.thePlayer.onGround) {
               this.ticks = 1;
               scaffold.delay = 1;
               if (scaffold.minecraft.thePlayer.motionY == -0.0784000015258789) {
                  scaffold.minecraft.thePlayer.jump();
               }
               scaffold.keptY = (int) Math.floor(scaffold.minecraft.thePlayer.posY) - 1;
            }
         } else if (this == Jump_B || this == Jump_C) {
            this.height = 2.2;
         } else if (this == Jump_D) {
            this.height = 2.5;
         } else if (this == Side) {
            this.ticks = 4;
         }
      }

      public boolean canFastScaffold(Scaffold scaffold) {
         if (!scaffold.active || this == Disabled) {
            return false;
         }
         if ((scaffold.tower.isEnabled() && MoveUtils.jumpKeyDown()) || MoveUtils.jumpKeyDown()) {
            return false;
         }
         if (this != Float_A) {
            if (!MoveUtils.isMoving() || scaffold.minecraft.thePlayer.isSneaking()) {
               return false;
            }
            if (this.sprint && !this.jump && !MoveUtils.direction().forward) {
               return false;
            }
         }
         if (this == Side) {
            return !MoveUtils.isDiagonal();
         }
         return true;
      }

      public void onDisable(Scaffold scaffold, boolean disabling) {
         if (this.jump || this.keepY) {
            scaffold.keptY = -1;
         }
         if (this.jump && !disabling && !(scaffold.tower.isEnabled() && MoveUtils.jumpKeyDown())) {
            MoveUtils.setSpeed(0.05);
         }
         if (this == Float_A) {
            if (scaffold.disablingModule) {
               if (scaffold.minecraft.thePlayer.onGround && MoveUtils.isAirBelow()) {
                  scaffold.minecraft.thePlayer.jump();
               }
               if (scaffold.getRotationMode() != RotationMode.Offset) {
                  MoveUtils.multiplyMotion(0.58);
               }
            }
         } else if (this == Float_B && scaffold.disablingModule) {
            MoveUtils.multiplyMotion(0.85);
         }
         this.ticks = 0;
      }
   }

   public enum MultiPlaceMode {
      Disabled, _1_extra, _2_extra
   }

   /* Settings */
   private final ComboSetting rotationSetting = settingManager.register(
      new ComboSetting(this, "Rotation", 1, "Strict", "Smooth", "Simple", "Lazy", "Offset", "None")
   );
   private final ComboSetting fastScaffoldSetting = settingManager.register(
      new ComboSetting(this, "Fast Scaffold", 0, "Disabled", "Constant A", "Constant B", "Jump A", "Jump B", "Jump C", "Jump D", "Keep Y A", "Keep Y B", "Float A", "Float B", "Side", "Vanilla")
   );
   private final ComboSetting fastScaffoldRmbSetting = settingManager.register(
      new ComboSetting(this, "Fast Scaffold RMB", 0, "Disabled", "Constant A", "Constant B", "Jump A", "Jump B", "Jump C", "Jump D", "Keep Y A", "Keep Y B", "Float A", "Float B", "Side", "Vanilla")
   );
   private final NumberSetting motion = settingManager.register(new NumberSetting(this, "Motion", 1.0F, 0.5F, 1.2F));
   private final NumberSetting fastScaffoldMotion = settingManager.register(new NumberSetting(this, "Fast Scaffold Motion", 1.0F, 0.5F, 1.2F));
   private final ComboSetting multiPlaceSetting = settingManager.register(new ComboSetting(this, "Multi Place", 0, "Disabled", "1 Extra", "2 Extra"));
   private final ComboSetting precisionSetting = settingManager.register(new ComboSetting(this, "Precision", 2, "Low", "Medium", "High", "Very High"));
   private final NumberSetting sneakEvery = settingManager.register(new NumberSetting(this, "Sneak Every", 0, 0, 10));
   private final NumberSetting speedBoost = settingManager.register(new NumberSetting(this, "Speed Boost", 0.0F, 0.0F, 10.0F));

   private final CheckboxSetting autoSwap = settingManager.register(new CheckboxSetting(this, "Auto Swap", true));
   private final CheckboxSetting cancelKnockback = settingManager.register(new CheckboxSetting(this, "Cancel Knockback", false));
   private final CheckboxSetting highlightBlocks = settingManager.register(new CheckboxSetting(this, "Highlight Blocks", true));
   private final CheckboxSetting jumpFacingForward = settingManager.register(new CheckboxSetting(this, "Jump Facing Forward", false));
   private final CheckboxSetting legitMotion = settingManager.register(new CheckboxSetting(this, "Legit Motion", false));
   private final CheckboxSetting safeWalk = settingManager.register(new CheckboxSetting(this, "Safe Walk", true));
   public final CheckboxSetting blockCounter = settingManager.register(new CheckboxSetting(this, "Block Counter", true));
   private final CheckboxSetting silentSwing = settingManager.register(new CheckboxSetting(this, "Silent Swing", false));
   private final CheckboxSetting slowOnEnable = settingManager.register(new CheckboxSetting(this, "Slow On Enable", false));
   private final CheckboxSetting slowSpeedPotion = settingManager.register(new CheckboxSetting(this, "Slow Speed Potion", false));

   /* Tower (Original Novoline Tower retained) */
   public final CheckboxSetting tower = settingManager.register(new CheckboxSetting(this, "Tower", true));
   public final ComboSetting towerMode = settingManager.register(new ComboSetting(this, "Tower Mode", 0, "Normal", "Double Block", "Teleport") {
      @Override
      public boolean shouldShow() {
         return Scaffold.this.tower.isEnabled();
      }
   });
   private final CheckboxSetting timerBoost = settingManager.register(new CheckboxSetting(this, "Timer Boost", false) {
      @Override
      public boolean shouldShow() {
         return Scaffold.this.tower.isEnabled();
      }
   });
   private final NumberSetting timerBoostTower = settingManager.register(new NumberSetting(this, "Timer Boost Tower", 1.7F, 1.0F, 2.0F) {
      @Override
      public boolean shouldShow() {
         return Scaffold.this.tower.isEnabled() && Scaffold.this.timerBoost.isEnabled();
      }
   });

   public final MultiComboSetting stopRules = settingManager.register(
      new MultiComboSetting(this, "Stop Rules").add("World Change", true).add("Death", true).add("Game End", true)
   );

   /* Internal State */
   public boolean active;
   public boolean disablingModule;
   private boolean fastEnabled;
   private boolean rightHeld;
   public int keptY = -1;
   private int searchMode;
   public int legitTicks;
   public int delay;
   public int enableTick;
   public int jumpState;
   public int startTick;
   private int jumpTick;
   private int airBelowTicks;
   private int placedCount;
   public int pausedTicks;
   private boolean sneakQueued;
   private boolean smoothSnap;
   private BlockPos lastPlaced;
   private float aimYaw;
   private float aimPitch = 100.0f;
   private float smoothLimit;
   private float legitYaw;
   private float restPitch;
   private double yawOffset;
   private long placedAt;
   public long startedAt;
   public float startPitch;
   private long stoppedAt;
   private FastScaffoldMode lastFastScaffold = FastScaffoldMode.Disabled;
   private final Map<BlockPos, Long> placed = new HashMap<>();
   private int oldSlot = -1;
   private int pickedSlot = -1;

   public long groundTicks;
   public long airTicks;
   public float lastReportedYaw;
   public float lastReportedPitch;

   private int towerAirTicks = 0;
   private int placeCount = 0;
   private boolean miss;

   private static final EnumFacing[] SIDES = {EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.WEST};
   private static final BlockPos.MutableBlockPos SCRATCH = new BlockPos.MutableBlockPos();

   public Scaffold() {
      super(Category.WORLD, "Scaffold", "Places blocks beneath player with RavenXD mechanics and Novoline tower");
   }

   @Override
   protected void onInitialized() {
      this.addAutoDisableTargets(ModuleManager.speed, ModuleManager.longJump, ModuleManager.fly);
      super.onInitialized();
   }

   public RotationMode getRotationMode() {
      try {
         return RotationMode.valueOf(this.rotationSetting.getCurrentElementAsString());
      } catch (Exception e) {
         return RotationMode.Strict;
      }
   }

   public FastScaffoldMode getFastScaffoldMode() {
      try {
         String val = this.rightHeld ? this.fastScaffoldRmbSetting.getCurrentElementAsString() : this.fastScaffoldSetting.getCurrentElementAsString();
         return FastScaffoldMode.valueOf(val.replace(" ", "_"));
      } catch (Exception e) {
         return FastScaffoldMode.Disabled;
      }
   }

   public MultiPlaceMode getMultiPlaceMode() {
      try {
         String val = this.multiPlaceSetting.getCurrentElementAsString();
         if (val.equalsIgnoreCase("1 Extra")) return MultiPlaceMode._1_extra;
         if (val.equalsIgnoreCase("2 Extra")) return MultiPlaceMode._2_extra;
         return MultiPlaceMode.valueOf(val);
      } catch (Exception e) {
         return MultiPlaceMode.Disabled;
      }
   }

   public Precision getPrecision() {
      try {
         String val = this.precisionSetting.getCurrentElementAsString().replace(" ", "_");
         return Precision.valueOf(val);
      } catch (Exception e) {
         return Precision.High;
      }
   }

   public boolean isUsingFastScaffold() {
      if (!this.isEnabled()) return false;
      return getFastScaffoldMode() != FastScaffoldMode.Disabled;
   }

   public boolean canSprint() {
      if (!this.isEnabled() || !this.active) return true;
      if (this.tower.isEnabled() && MoveUtils.jumpKeyDown()) return false;
      FastScaffoldMode fs = getFastScaffoldMode();
      return fs.canSprint(this);
   }

   @Override
   protected void onEnable() {
      this.setSuffix(this.fastScaffoldSetting.getCurrentElementAsString().equalsIgnoreCase("Disabled") ? this.rotationSetting.getCurrentElementAsString() : this.fastScaffoldSetting.getCurrentElementAsString());
      if (minecraft.thePlayer == null) return;
      if (this.autoSwap.isEnabled()) {
         this.oldSlot = minecraft.thePlayer.inventory.currentItem;
      }
      this.restPitch = randomFloat(84.5f, 87.5f);
      this.yawOffset = RotationUtils.randomOffset();
      this.startTick = minecraft.thePlayer.ticksExisted;
      this.startedAt = System.currentTimeMillis();
      this.aimYaw = minecraft.thePlayer.rotationYaw;
      this.aimPitch = 100.0f;
      this.towerAirTicks = 0;
      this.placeCount = 0;
      this.pickedSlot = -1;

      if (!minecraft.thePlayer.onGround) {
         this.delay = 3;
      } else if (MoveUtils.nearEdge(0.2) && BlockUtils.countBlocks() > 0) {
         this.jumpState = 2;
         this.jumpTick = minecraft.thePlayer.ticksExisted;
         this.delay = 4;
      } else {
         this.delay = 1;
      }
   }

   @Override
   protected void onDisable() {
      this.disablingModule = true;
      stop();
      this.legitTicks = 0;
      this.delay = 0;
      this.airBelowTicks = 0;
      this.pausedTicks = 0;
      this.placedCount = 0;
      this.placedAt = 0L;
      this.towerAirTicks = 0;
      this.placeCount = 0;
      minecraft.timer.timerSpeed = 1.0F;
      double d = MoveUtils.speed();
      if (d > 0.34) {
         MoveUtils.multiplyMotion((0.29 + 0.02 * Math.random()) / d);
      }
      this.disablingModule = false;
   }

   private void stop() {
      if (this.active) {
         this.stoppedAt = System.currentTimeMillis();
         getRotationMode().onDisable();
         if (this.oldSlot != -1 && this.autoSwap.isEnabled()) {
            if (this.pickedSlot != -1) {
               sendPacketUnregistered(new C09PacketHeldItemChange(minecraft.thePlayer.inventory.currentItem = this.oldSlot));
            } else {
               minecraft.thePlayer.inventory.currentItem = this.oldSlot;
            }
            this.oldSlot = -1;
            this.pickedSlot = -1;
         }
         if (this.fastEnabled) {
            getFastScaffoldMode().onDisable(this, true);
            this.fastEnabled = false;
         }
         this.sneakQueued = false;
         this.smoothSnap = false;
         this.rightHeld = false;
         this.active = false;
         this.jumpState = 1;
         if (minecraft.thePlayer.onGround && getRotationMode() != RotationMode.Offset) {
            MoveUtils.multiplyMotion(0.75);
         }
      }
      this.keptY = -1;
      this.lastPlaced = null;
      this.aimPitch = 100.0f;
   }

   public void resetKeepY() {
      this.keptY = -1;
   }

   public boolean safeWalk() {
      if (!this.isEnabled() || !this.active) return false;
      if (this.safeWalk.isEnabled()) {
         FastScaffoldMode fast = getFastScaffoldMode();
         if (fast.jump || fast.keepY) {
            return !fast.canFastScaffold(this);
         }
         return true;
      }
      FastScaffoldMode fast = getFastScaffoldMode();
      return (fast == FastScaffoldMode.Float_A || fast == FastScaffoldMode.Float_B) && fast.canFastScaffold(this);
   }

   /* Event Listeners */
   @EventLink
   public final Listener<GameEndEvent> onGameEnd = gameEndEvent -> {
      if (this.stopRules.isSelected(2)) {
         this.setEnabled(false);
         this.showNotification(NotificationType.INFO, "Scaffold", Language.translate("feature.scaffold.disable_rules.notification.game_end"), 2500L);
      }
   };

   @EventLink
   public final Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> {
      if (this.stopRules.isSelected(0)) {
         this.setEnabled(false);
         this.showNotification(NotificationType.INFO, "Scaffold", Language.translate("feature.scaffold.disable_rules.notification.world_change"), 2500L);
      }
   };

   @EventLink
   public final Listener<MoveEntityEvent> onMoveEntity = moveEntityEvent -> {
      if (this.safeWalk()) {
         moveEntityEvent.cancel();
      }
   };

   @EventLink
   public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
      if (!this.cancelKnockback.isEnabled() || !this.active) return;
      if (event.packet instanceof S12PacketEntityVelocity) {
         S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) event.packet;
         if (minecraft.thePlayer != null && s12.getEntityID() == minecraft.thePlayer.getEntityId()) {
            if ((this.tower.isEnabled() && MoveUtils.jumpKeyDown()) || MoveUtils.isMoving()) {
               event.cancel();
            }
         }
      } else if (event.packet instanceof S27PacketExplosion) {
         if ((this.tower.isEnabled() && MoveUtils.jumpKeyDown()) || MoveUtils.isMoving()) {
            event.cancel();
         }
      }
   };

   @EventLink
   public final Listener<PlayerMoveEvent> onPlayerMove = playerMoveEvent -> {
      if (minecraft.thePlayer == null) return;
      // Original Novoline Tower on PlayerMove
      if (this.tower.isEnabled() && this.towerMode.getCurrentElement() == 0 && minecraft.gameSettings.keyBindJump.isKeyDown()) {
         if (PlayerUtils.hasCollisionBelow(0.76)
            && !PlayerUtils.hasCollisionBelow(0.75)
            && minecraft.thePlayer.motionY > 0.23
            && minecraft.thePlayer.motionY < 0.25) {
            playerMoveEvent.y = minecraft.thePlayer.motionY = Math.round(minecraft.thePlayer.posY) - minecraft.thePlayer.posY;
         }

         if (PlayerUtils.hasCollisionBelow(1.0E-4)) {
            playerMoveEvent.y = minecraft.thePlayer.motionY = 0.41999998688698;
         }
      }
   };

   @EventLink
   public final Listener<MoveFlyingEvent> onMoveFlying = moveFlyingEvent -> {
      // Rotation strafe / correct movement
      if (this.active && getRotationMode() != RotationMode.None && this.aimPitch != 100.0f) {
         // optional correction if needed
      }
   };

   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (minecraft.thePlayer == null || minecraft.theWorld == null) return;

      this.setSuffix(this.fastScaffoldSetting.getCurrentElementAsString().equalsIgnoreCase("Disabled") ? this.rotationSetting.getCurrentElementAsString() : this.fastScaffoldSetting.getCurrentElementAsString());

      if (!minecraft.thePlayer.isEntityAlive() && this.stopRules.isSelected(1)) {
         this.setEnabled(false);
         this.showNotification(NotificationType.INFO, "Scaffold", Language.translate("feature.scaffold.disable_rules.notification.death"), 2500L);
         return;
      }

      if (minecraft.thePlayer.onGround) {
         ++this.groundTicks;
         this.airTicks = 0L;
      } else {
         ++this.airTicks;
         this.groundTicks = 0L;
      }
      this.lastReportedYaw = preUpdateEvent.rotationYaw;
      this.lastReportedPitch = preUpdateEvent.rotationPitch;

      if (this.miss) {
         this.miss = false;
         minecraft.timer.timerSpeed = 1.0F;
      }

      // PreMotion updates
      updatePreMotion(preUpdateEvent);

      // Tower PreUpdate execution (Original Novoline Tower)
      if (this.tower.isEnabled() && minecraft.gameSettings.keyBindJump.isKeyDown()) {
         if (this.timerBoost.isEnabled()) {
            minecraft.timer.timerSpeed = this.timerBoostTower.getCurrentValue();
            this.miss = true;
         }

         if (this.towerMode.getCurrentElement() == 0) { // Normal
            double d = MathHelper.floor_double(preUpdateEvent.posY);
            preUpdateEvent.posX = preUpdateEvent.posX
               + (
                  minecraft.thePlayer.ticksExisted % 2 == 0
                     ? ThreadLocalRandom.current().nextDouble(0.03, 0.03125)
                     : -ThreadLocalRandom.current().nextDouble(0.03, 0.03125)
               );
            preUpdateEvent.posZ = preUpdateEvent.posZ
               + (
                  minecraft.thePlayer.ticksExisted % 2 != 0
                     ? ThreadLocalRandom.current().nextDouble(0.03, 0.03125)
                     : -ThreadLocalRandom.current().nextDouble(0.03, 0.03125)
               );
            double d2 = this.roundToFourDecimals(preUpdateEvent.posY % 1.0);
            if (d2 == 0.42 && HypixelState.isServerType(ServerType.HYPIXEL)) {
               this.sendPacketUnregistered(
                  new C06PacketPlayerPosLook(preUpdateEvent.posX, d + 0.015625, preUpdateEvent.posZ, this.aimYaw, this.aimPitch, preUpdateEvent.onGround)
               );
               this.sendPacketUnregistered(
                  new C06PacketPlayerPosLook(preUpdateEvent.posX, d + 0.089899898876, preUpdateEvent.posZ, this.aimYaw, this.aimPitch, preUpdateEvent.onGround)
               );
               this.sendPacketUnregistered(new C06PacketPlayerPosLook(preUpdateEvent.posX, d, preUpdateEvent.posZ, this.aimYaw, this.aimPitch, preUpdateEvent.onGround));
            }

            if (!preUpdateEvent.onGround && preUpdateEvent.posY == d) {
               preUpdateEvent.posY += 0.00133597911216;
            }
         } else if (this.towerMode.getCurrentElement() == 2) { // Teleport
            if (this.towerAirTicks == 2) {
               double d3 = minecraft.thePlayer.ticksExisted % 2 == 0 ? 0.1 : 0.3;
               minecraft.thePlayer.setPositionAndUpdate(minecraft.thePlayer.posX, minecraft.thePlayer.posY + d3, minecraft.thePlayer.posZ);
            }

            if (minecraft.thePlayer.onGround) {
               this.towerAirTicks = 0;
            } else {
               this.towerAirTicks++;
            }
         }
      }

      // Placement & Rotations
      updatePlacement();

      float[] rot = calculateRotations(preUpdateEvent.rotationYaw, preUpdateEvent.rotationPitch);
      if (rot != null) {
         preUpdateEvent.rotationYaw = rot[0];
         preUpdateEvent.rotationPitch = rot[1];
         this.aimYaw = rot[0];
         this.aimPitch = rot[1];
         minecraft.thePlayer.renderYawOffset = rot[0];
         minecraft.thePlayer.rotationYawHead = rot[0];
         minecraft.thePlayer.rotationPitchHead = rot[1];
      }

      updatePostPlayerInput();
   };

   @EventLink
   public final Listener<RenderWorldEvent> onRenderWorld = renderWorldEvent -> {
      if (minecraft.thePlayer == null || minecraft.theWorld == null) return;
      if (!this.highlightBlocks.isEnabled() || this.placed.isEmpty()) return;
      long now = System.currentTimeMillis();
      Iterator<Map.Entry<BlockPos, Long>> it = this.placed.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry<BlockPos, Long> entry = it.next();
         long age = now - Math.abs(entry.getValue());
         float f = (float) age / 500.0f;
         if (f >= 1.0f) {
            it.remove();
            continue;
         }
         float alpha = 1.0f - f;
         int color = ((int)(alpha * 80) << 24) | 0x8A2BE2;
         BlockPos pos = entry.getKey();
         Block block = minecraft.theWorld.getBlockState(pos).getBlock();
         RenderUtils.drawBoxFilledAndOutlined(
            block.getSelectedBoundingBox(minecraft.theWorld, pos),
            color,
            renderWorldEvent.partialTicks
         );
      }
   };

   private void updatePreMotion(PreUpdateEvent e) {
      boolean floatWasSet = FastScaffoldMode.Float_A.state == 1 || FastScaffoldMode.Float_B.state == 1;
      FastScaffoldMode.Float_A.state = 0;
      FastScaffoldMode.Float_B.state = 0;
      FastScaffoldMode fastScaffold = getFastScaffoldMode();
      if (!fastScaffold.canFastScaffold(this)) {
         return;
      }
      if (fastScaffold == FastScaffoldMode.Float_A) {
         if (e.posY % 1.0 == 0.0 && minecraft.thePlayer.motionY == -0.0784000015258789) {
            e.posY += 1.0E-13;
            fastScaffold.state = 1;
            if (MoveUtils.isMoving() && !minecraft.thePlayer.isSneaking()) {
               double d = fastScaffold.sprint ? 0.2 + MoveUtils.perSpeedLevel(MoveUtils.isDiagonal() ? 0.035 : 0.043)
                       : 0.23 + MoveUtils.perSpeedLevel(MoveUtils.isDiagonal() ? 0.042 : 0.05);
               if (MoveUtils.speedLevel() == 0 && MoveUtils.isDiagonal()) {
                  d *= 0.87;
               }
               MoveUtils.strafe(d);
               if (!fastScaffold.sprint) {
                  minecraft.thePlayer.setSprinting(true);
               }
            }
         }
      } else if (fastScaffold == FastScaffoldMode.Float_B) {
         if (e.posY % 1.0 == 0.0 && minecraft.thePlayer.motionY == -0.0784000015258789 && MoveUtils.isMoving() && !minecraft.thePlayer.isSneaking()) {
            double d;
            if (!floatWasSet && (MoveUtils.isDiagonal() || !MoveUtils.isAirBelow())) {
               e.posY += 0.001;
               e.onGround = false;
               fastScaffold.state = 1;
               d = 0.153;
            } else {
               d = 0.245 + MoveUtils.perSpeedLevel(0.025);
            }
            MoveUtils.strafe(d);
         }
      } else if ((fastScaffold.jump || fastScaffold.keepY) && minecraft.thePlayer.onGround && (this.jumpFacingForward.isEnabled() || floatWasSet)) {
         if (fastScaffold.state == 1) {
            fastScaffold.state = 2;
            double d = fastScaffold.sprint && MoveUtils.direction().forward ? 0.25 : (fastScaffold.keepY ? 0.54 : 0.4);
            jumpWith(d);
         }
         this.delay = this.legitTicks;
      }
   }

   private void updatePlacement() {
      if (this.pausedTicks > 0 && --this.pausedTicks != 0) {
         stop();
         return;
      }
      if (minecraft.currentScreen != null) {
         stop();
         return;
      }
      autoSwapTick();
      ItemStack itemStack = minecraft.thePlayer.getHeldItem();
      if (itemStack == null || !isValid(itemStack.getItem())) {
         if (!this.autoSwap.isEnabled() || BlockUtils.countBlocks() == 0) {
            stop();
         }
         return;
      }
      long l = System.currentTimeMillis();
      if (!this.active) {
         this.startTick = minecraft.thePlayer.ticksExisted;
         this.startedAt = l;
         this.startPitch = minecraft.thePlayer.rotationPitch;
         if (minecraft.gameSettings.keyBindAttack.isKeyDown()) {
            KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindAttack.getKeyCode(), false);
         }
         if (minecraft.gameSettings.keyBindUseItem.isKeyDown()) {
            KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindUseItem.getKeyCode(), false);
         }
         if (this.slowOnEnable.isEnabled()) {
            MoveUtils.multiplyMotion(0.2);
         }
      }
      this.active = true;
      fastScaffoldTick();
      if (this.delay > 0) {
         --this.delay;
         return;
      }
      BlockPos below = BlockUtils.below(1.0);
      this.airBelowTicks = BlockUtils.isReplaceable(below) ? ++this.airBelowTicks : 0;
      BlockPos pos = place(null, itemStack);
      if (pos != null) {
         ++this.placedCount;
         int sneakInterval = (int) this.sneakEvery.getCurrentValue();
         if (sneakInterval > 0 && this.placedCount % sneakInterval == 0) {
            this.sneakQueued = true;
         }
         if (itemStack.stackSize > 0) {
            afterPlace(pos, itemStack);
         }
      }
   }

   private void autoSwapTick() {
      if (!this.autoSwap.isEnabled()) return;
      ItemStack held = minecraft.thePlayer.getHeldItem();
      if (held != null && isValid(held.getItem()) && held.stackSize > 0) {
         return;
      }
      int bestSlot = -1;
      int maxCount = 0;
      for (int i = 0; i < 9; ++i) {
         ItemStack stack = minecraft.thePlayer.inventory.getStackInSlot(i);
         if (stack != null && isValid(stack.getItem()) && stack.stackSize > maxCount) {
            maxCount = stack.stackSize;
            bestSlot = i;
         }
      }
      if (bestSlot != -1 && minecraft.thePlayer.inventory.currentItem != bestSlot) {
         if (this.oldSlot == -1) {
            this.oldSlot = minecraft.thePlayer.inventory.currentItem;
         }
         sendPacketUnregistered(new C09PacketHeldItemChange(this.pickedSlot = bestSlot));
         minecraft.thePlayer.inventory.currentItem = bestSlot;
      }
   }

   private void fastScaffoldTick() {
      this.rightHeld = Mouse.isCreated() && Mouse.isButtonDown(1) && minecraft.currentScreen == null;
      FastScaffoldMode fastScaffold = getFastScaffoldMode();
      if (fastScaffold != FastScaffoldMode.Disabled && !fastScaffold.canSprint(this) && minecraft.thePlayer.isSprinting()) {
         minecraft.thePlayer.setSprinting(false);
      }
      boolean moving = MoveUtils.isMoving();
      boolean groundMoving = minecraft.thePlayer.onGround && moving;
      boolean can = fastScaffold.canFastScaffold(this);
      boolean sprintForward = fastScaffold.sprint && MoveUtils.direction().forward;
      boolean applied = false;
      if (fastScaffold == this.lastFastScaffold) {
         if (!can) {
            if (this.fastEnabled) {
               this.fastEnabled = false;
               fastScaffold.onDisable(this, false);
            }
         } else if (!this.fastEnabled) {
            this.fastEnabled = true;
            fastScaffold.onEnable(this);
         }
      } else {
         if (this.fastEnabled && this.lastFastScaffold != null) {
            this.lastFastScaffold.onDisable(this, false);
         }
         if (can) {
            fastScaffold.onEnable(this);
         }
         this.fastEnabled = can;
         this.lastFastScaffold = fastScaffold;
      }
      double d;
      if (!can) {
         if (!(this.tower.isEnabled() && MoveUtils.jumpKeyDown()) && this.legitTicks > 0 && minecraft.thePlayer.onGround) {
            this.delay = 0;
            this.legitTicks = 0;
         }
      } else {
         boolean newY;
         double speed;
         switch (fastScaffold) {
            case Constant_A:
               if (groundMoving) {
                  d = 0.153 + MoveUtils.perSpeedLevel(0.034);
                  if (MoveUtils.speedLevel() == 0 && MoveUtils.isDiagonal()) {
                     d *= 0.87;
                  }
                  MoveUtils.strafe(d);
               }
               break;
            case Constant_B:
               if (groundMoving) {
                  MoveUtils.strafe(0.181);
               }
               break;
            case Jump_A:
            case Jump_B:
            case Jump_C:
            case Jump_D:
               if (!groundMoving) {
                  if (moving && this.jumpFacingForward.isEnabled() && minecraft.thePlayer.motionY < 0.0) {
                     d = Math.floor(minecraft.thePlayer.posY);
                     if (d - 2.0 == (double) this.keptY && minecraft.thePlayer.posY + minecraft.thePlayer.motionY < d) {
                        this.legitTicks = !this.legitMotion.isEnabled() ? 1 : 5;
                     }
                  }
                  break;
               }
               if (fastScaffold.state != 2) {
                  newY = false;
                  if (minecraft.thePlayer.ticksExisted - fastScaffold.ticks > 10) {
                     this.keptY = (int) Math.floor(minecraft.thePlayer.posY) - 1;
                     newY = true;
                  }
                  if ((this.jumpFacingForward.isEnabled() || FastScaffoldMode.Float_A.state == 1 || FastScaffoldMode.Float_B.state == 1) && newY) {
                     if (this.jumpFacingForward.isEnabled()) {
                        this.legitTicks = !this.legitMotion.isEnabled() ? 1 : 5;
                     }
                     fastScaffold.state = 1;
                  }
                  if (fastScaffold.state == 0) {
                     if (fastScaffold != FastScaffoldMode.Jump_A) {
                        if (!sprintForward) {
                           speed = !newY ? 0.545 : 0.4;
                        } else {
                           speed = !newY ? 0.3 : 0.25;
                        }
                     } else {
                        speed = !MoveUtils.isDiagonal() ? 0.53 : 0.37;
                     }
                     jumpWith(speed);
                  }
               } else {
                  fastScaffold.state = 0;
               }
               MoveUtils.multiplyMotion(0.95);
               break;
            case Keep_Y_A:
            case Keep_Y_B:
               if (!groundMoving) {
                  if (moving && this.jumpFacingForward.isEnabled() && minecraft.thePlayer.motionY < 0.0) {
                     d = Math.floor(minecraft.thePlayer.posY);
                     if (d - 1.0 == (double) this.keptY && minecraft.thePlayer.posY + minecraft.thePlayer.motionY < d) {
                        this.legitTicks = !this.legitMotion.isEnabled() ? 1 : 5;
                     }
                  }
               } else {
                  if (fastScaffold.state != 2) {
                     newY = false;
                     if (minecraft.thePlayer.ticksExisted - fastScaffold.ticks > 10) {
                        this.keptY = (int) Math.floor(minecraft.thePlayer.posY) - 1;
                        newY = true;
                     }
                     if ((this.jumpFacingForward.isEnabled() || FastScaffoldMode.Float_A.state == 1 || FastScaffoldMode.Float_B.state == 1) && newY) {
                        if (this.jumpFacingForward.isEnabled() && this.legitTicks == 0) {
                           this.legitTicks = !this.legitMotion.isEnabled() ? 1 : 5;
                        }
                        fastScaffold.state = 1;
                        MoveUtils.multiplyMotion(MoveUtils.speedLevel() <= 0 ? 0.8 : 0.4);
                     }
                     if (fastScaffold.state == 0) {
                        if (!sprintForward) {
                           speed = !newY ? 0.545 : 0.54;
                        } else {
                           speed = !newY ? 0.3 : 0.25;
                        }
                        jumpWith(speed);
                     }
                  } else {
                     fastScaffold.state = 0;
                  }
                  MoveUtils.multiplyMotion(0.95);
               }
               break;
            default:
               break;
         }
         if (!minecraft.thePlayer.onGround && (fastScaffold == FastScaffoldMode.Jump_D || fastScaffold == FastScaffoldMode.Keep_Y_B) && MoveUtils.motionYDecimals() == 30) {
            minecraft.thePlayer.motionY -= 0.14;
         }
         if (groundMoving) {
            d = this.fastScaffoldMotion.getCurrentValue();
            if (!this.legitMotion.isEnabled() && MoveUtils.direction().diagonal) {
               d -= 0.02;
            }
            MoveUtils.multiplyMotion(d);
            applied = true;
         }
      }
      if (!applied && groundMoving) {
         d = this.motion.getCurrentValue();
         if (!this.legitMotion.isEnabled()) {
            if (this.slowSpeedPotion.isEnabled()) {
               d -= MoveUtils.perSpeedLevel(0.17);
            }
            if (MoveUtils.direction().diagonal) {
               d -= 0.02;
            }
            if (minecraft.thePlayer.ticksExisted - this.startTick <= 2) {
               d -= 0.05;
            }
         }
         MoveUtils.multiplyMotion(d);
      }
   }

   private void jumpWith(double d) {
      if (this.legitMotion.isEnabled()) {
         return;
      }
      d += ((double) this.speedBoost.getCurrentValue() / 10.0) + (1.0E-4 * Math.random());
      MoveUtils.jump(d);
   }

   private void updatePostPlayerInput() {
      if (this.sneakQueued && sincePlace() > 90L) {
         this.sneakQueued = false;
         minecraft.thePlayer.movementInput.sneak = true;
         minecraft.thePlayer.movementInput.moveForward *= 0.3f;
         minecraft.thePlayer.movementInput.moveStrafe *= 0.3f;
      }
      if (this.jumpState == 1) {
         if (MoveUtils.jumpKeyDown()) {
            minecraft.thePlayer.movementInput.jump = false;
            this.jumpState = 2;
         } else {
            this.jumpState = 0;
         }
      } else if (this.jumpState == 2) {
         minecraft.thePlayer.movementInput.jump = true;
         this.jumpState = 0;
      }
      FastScaffoldMode fastScaffold = getFastScaffoldMode();
      if (fastScaffold == FastScaffoldMode.Float_A || fastScaffold == FastScaffoldMode.Float_B) {
         if (!fastScaffold.canFastScaffold(this) && fastScaffold.state == 1) {
            fastScaffold.state = 0;
         }
      } else if ((fastScaffold.jump || fastScaffold.keepY) && this.legitMotion.isEnabled() && fastScaffold.canFastScaffold(this) && minecraft.thePlayer.onGround && fastScaffold.state == 0) {
         minecraft.thePlayer.movementInput.jump = true;
      }
   }

   private BlockPos place(BlockPos last, ItemStack itemStack) {
      BlockPos target = search(last);
      if (target == null) {
         return null;
      }
      EnumFacing facing = faceFor(target, null);
      if (facing == null) {
         return null;
      }
      BlockPos against = target.offset(facing.getOpposite());
      if (this.airBelowTicks > 0) {
         float yaw = this.aimYaw;
         float pitch = this.aimPitch;
         MovingObjectPosition hit = aim(against, facing, -170.0f, this.aimYaw);
         if (hit == null) {
            return last == null ? null : place(null, itemStack);
         }
         RotationMode rotation = getRotationMode();
         switch (rotation) {
            case Simple: {
               float f3 = MoveUtils.movementAngle();
               if (Math.abs(f3 - this.aimYaw) < 100.0f) {
                  float f4 = this.aimYaw - f3;
                  f4 = (f4 + 540.0f) % 360.0f - 180.0f;
                  float f6 = 0.0f;
                  if (Math.abs(f4) >= 10.0f) {
                     f6 = Math.max(-10.0f, Math.min(f4, 10.0f));
                  }
                  this.aimYaw = f3 + f6;
                  if (minecraft.thePlayer.onGround) {
                     this.aimPitch = 82.5f;
                  }
               }
               break;
            }
            case Lazy: {
               if (pitch != 100.0f && rayTraceBlock(against, null, (double) minecraft.playerController.getBlockReachDistance(), yaw, pitch) != null) {
                  this.aimYaw = yaw;
                  this.aimPitch = pitch;
               }
               break;
            }
            default:
               break;
         }
         Vec3 vec3 = hit.hitVec;
         if (minecraft.playerController.performRightClick(minecraft.thePlayer, minecraft.theWorld, itemStack, against, facing, vec3)) {
            this.placeCount++;
            if (this.silentSwing.isEnabled()) {
               sendPacketUnregistered(new C0APacketAnimation());
            } else {
               minecraft.thePlayer.swingItemNoPacket();
            }
            FastScaffoldMode fastScaffold = getFastScaffoldMode();
            if (this.searchMode == 1 || (this.searchMode == 2 && fastScaffold.keepY)) {
               fastScaffold.ticks = minecraft.thePlayer.ticksExisted;
            }
            this.lastPlaced = target;
            this.placedAt = System.currentTimeMillis();
            this.placed.put(target, this.placedAt);
            this.airBelowTicks = 0;

            // Original Novoline Double Block Tower jump
            if (this.tower.isEnabled() && this.towerMode.getCurrentElement() == 1
               && this.placeCount % 2 != 0
               && minecraft.gameSettings.keyBindJump.isKeyDown()) {
               minecraft.thePlayer.motionX = minecraft.thePlayer.motionZ = 0.0;
               minecraft.thePlayer.jump();
            }

            return target;
         }
      } else if (MoveUtils.isMoving() && Math.random() > 0.8) {
         sendPacketUnregistered(new C08PacketPlayerBlockPlacement(itemStack));
      }
      return null;
   }

   private BlockPos search(BlockPos last) {
      List<Vec3> list = new ArrayList<>();
      this.searchMode = 0;
      FastScaffoldMode fastScaffold = getFastScaffoldMode();
      if (this.keptY != -1 && fastScaffold.canFastScaffold(this)) {
         int decimals = MoveUtils.posYDecimals();
         if (fastScaffold == FastScaffoldMode.Jump_A) {
            if (decimals == 1661 || (decimals == 2492 && minecraft.thePlayer.motionY > 0.0)) {
               this.searchMode = 1;
            } else {
               this.searchMode = 2;
            }
         } else if (fastScaffold == FastScaffoldMode.Jump_B || fastScaffold == FastScaffoldMode.Jump_C || fastScaffold == FastScaffoldMode.Jump_D) {
            if (minecraft.thePlayer.ticksExisted - fastScaffold.ticks > 4 && minecraft.thePlayer.posY - (double) this.keptY < fastScaffold.height && minecraft.thePlayer.motionY < -0.1) {
               this.searchMode = 1;
               if (fastScaffold.ticks == 0) {
                  MoveUtils.multiplyMotion(0.4);
               }
            } else {
               this.searchMode = 2;
            }
         } else if (fastScaffold.keepY) {
            this.searchMode = 2;
         } else if (fastScaffold == FastScaffoldMode.Float_A) {
            if (minecraft.thePlayer.onGround && minecraft.thePlayer.ticksExisted - this.enableTick > 0) {
               this.keptY = -1;
            } else {
               this.searchMode = 2;
            }
         }
      }
      return candidates(list, this.searchMode, this.keptY) ? firstPlaceable(list, last) : null;
   }

   private boolean candidates(List<Vec3> list, int mode, int y) {
      for (int i = -5; i <= 5; ++i) {
         for (int j = -5; j <= 5; ++j) {
            for (int k = -3; k <= -1; ++k) {
               int n4 = MathHelper.floor_double(minecraft.thePlayer.posY) + k;
               if (mode > 0 && n4 != y) continue;
               double d2 = minecraft.thePlayer.posX + (double) i;
               double d = minecraft.thePlayer.posZ + (double) j;
               SCRATCH.set(MathHelper.floor_double(d2), n4, MathHelper.floor_double(d));
               if (BlockUtils.isReplaceable(SCRATCH)) continue;
               for (int n3 = -1; n3 <= 1; n3 += 2) {
                  switch (mode) {
                     case 0:
                        list.add(new Vec3(d2 + (double) n3, (double) n4, d));
                        list.add(new Vec3(d2, (double) n4, d + (double) n3));
                     case 1:
                        list.add(new Vec3(d2, (double) (n4 + n3), d));
                        continue;
                     case 2:
                        list.add(new Vec3(d2 + (double) n3, (double) n4, d));
                        list.add(new Vec3(d2, (double) n4, d + (double) n3));
                  }
               }
            }
         }
      }
      if (list.isEmpty()) {
         return false;
      }
      Set<Long> seen = new HashSet<>(list.size() * 2);
      List<Vec3> unique = new ArrayList<>(list.size());
      for (Vec3 vec3 : list) {
         long l = Double.doubleToLongBits(vec3.xCoord) ^ Double.doubleToLongBits(vec3.yCoord) * 31L ^ Double.doubleToLongBits(vec3.zCoord) * 31L * 31L;
         if (!seen.add(l)) continue;
         unique.add(vec3);
      }
      list.clear();
      list.addAll(unique);
      final double x = minecraft.thePlayer.posX;
      double y2 = minecraft.thePlayer.posY - 1.0;
      final double z = minecraft.thePlayer.posZ;
      if (!minecraft.thePlayer.onGround && !MoveUtils.jumpKeyDown() && !getFastScaffoldMode().canFastScaffold(this)) {
         y2 -= 1.0;
      }
      final double fy = y2;
      Comparator<Vec3> comparator = Comparator.comparingDouble(p -> distanceSq(x, fy, z, p));
      if (minecraft.thePlayer.ticksExisted - this.startTick <= 3) {
         double yaw = minecraft.thePlayer.rotationYaw * ((float) Math.PI / 180.0f);
         final double dirX = -Math.sin(yaw);
         final double dirZ = Math.cos(yaw);
         comparator = comparator.thenComparingDouble(p -> behind(x, z, dirX, dirZ, p));
      }
      list.sort(comparator);
      return true;
   }

   private BlockPos firstPlaceable(List<Vec3> list, BlockPos last) {
      for (Vec3 vec3 : list) {
         SCRATCH.set(MathHelper.floor_double(vec3.xCoord), MathHelper.floor_double(vec3.yCoord), MathHelper.floor_double(vec3.zCoord));
         if (!BlockUtils.isReplaceable(SCRATCH) || faceFor(SCRATCH, last) == null) continue;
         return new BlockPos(SCRATCH);
      }
      return null;
   }

   public EnumFacing faceFor(BlockPos pos, BlockPos only) {
      BlockPos down = pos.down();
      if (canPlaceAgainst(down) && (only == null || down.equals(only))) {
         return EnumFacing.UP;
      }
      double d = minecraft.thePlayer.posX - ((double) pos.getX() + 0.5);
      double d2 = minecraft.thePlayer.posZ - ((double) pos.getZ() + 0.5);
      EnumFacing facing = Math.abs(d) > Math.abs(d2) ? (d > 0.0 ? EnumFacing.EAST : EnumFacing.WEST) : (d2 > 0.0 ? EnumFacing.SOUTH : EnumFacing.NORTH);
      BlockPos toward = pos.offset(facing);
      if (canPlaceAgainst(toward) && (only == null || toward.equals(only))) {
         return facing.getOpposite();
      }
      for (EnumFacing side : SIDES) {
         if (side == facing) continue;
         BlockPos other = pos.offset(side);
         if (!canPlaceAgainst(other)) continue;
         if (only != null && !other.equals(only)) continue;
         return side.getOpposite();
      }
      return null;
   }

   private boolean canPlaceAgainst(BlockPos pos) {
      if (!this.placed.isEmpty() && !this.placed.containsKey(pos) && sincePlace() < 250L) {
         boolean near = false;
         for (Map.Entry<BlockPos, Long> entry : this.placed.entrySet()) {
            if (!(minecraft.thePlayer.getDistanceSq(entry.getKey()) < 12.0)) continue;
            near = true;
            break;
         }
         if (near) {
            return false;
         }
      }
      return !BlockUtils.isReplaceable(pos) && !BlockUtils.isInteractable(BlockUtils.getBlock(pos));
   }

   private MovingObjectPosition aim(BlockPos pos, EnumFacing facing, float offset, float lastYaw) {
      float[] r = RotationUtils.searchBlockRotation(pos, facing, offset, getPrecision(), lastYaw, false);
      if (r == null) {
         return null;
      }
      this.aimYaw = r[0];
      this.aimPitch = r[1];
      if (getRotationMode() == RotationMode.Smooth && this.lastPlaced != null && this.groundTicks <= 3L) {
         this.smoothLimit = 35.0f + (float) (1.0 * Math.random());
         float f3 = MathHelper.wrapAngleTo180_float(this.aimYaw - lastYaw);
         if (Math.abs(f3) > this.smoothLimit) {
            return null;
         }
      }
      return rayTraceBlock(pos, null, (double) minecraft.playerController.getBlockReachDistance(), this.aimYaw, this.aimPitch);
   }

   private float[] calculateRotations(float f, float f2) {
      if (!this.active || getRotationMode() == RotationMode.None) {
         return null;
      }
      RotationMode rotation = getRotationMode();
      long l = sincePlace();
      float f7;
      float f8;
      float f9;
      float f10;
      if (rotation == RotationMode.Offset) {
         float f6 = (float) MoveUtils.inputAngleSigned();
         f7 = minecraft.thePlayer.rotationYaw + f6;
         f8 = (f7 % 360.0f + 360.0f) % 360.0f;
         f9 = f8 % 90.0f;
         if (!(f9 >= 80.0f) && !(f9 < 10.0f)) {
            f10 = f9 >= 10.0f && f9 < 45.0f ? 45.0f : -45.0f;
         } else {
            f10 = -45.0f;
         }
         if (!minecraft.thePlayer.onGround && rotation.groundSide != 0.0f) {
            f10 = rotation.groundSide;
         } else {
            rotation.groundSide = f10;
         }
         if (f10 != rotation.side) {
            rotation.side = f10;
            rotation.sideJitter = (float) ((double) Math.signum(f10) * (0.02 + Math.random() * 0.06));
         }
         this.aimYaw = MoveUtils.movementAngle() + (f10 + rotation.sideJitter);
      }
      boolean jumpRotation = false;
      FastScaffoldMode fastScaffold = getFastScaffoldMode();
      if (this.legitTicks <= 0) {
         if (fastScaffold == FastScaffoldMode.Float_A && fastScaffold.ticks == 1 && minecraft.thePlayer.ticksExisted == this.enableTick) {
            jumpRotation = true;
         }
      } else {
         --this.legitTicks;
         jumpRotation = true;
      }
      if (jumpRotation) {
         f7 = -180.0f;
         f8 = this.aimPitch == 100.0f ? minecraft.thePlayer.rotationPitch : this.aimPitch;
         if (this.legitMotion.isEnabled()) {
            if (this.legitTicks == 4) {
               if ((double) (minecraft.thePlayer.rotationYaw - this.lastReportedYaw) < -0.7) {
                  f7 = -f7;
               }
               this.legitYaw = f7;
               --this.delay;
               jumpRotation = false;
            } else if (this.legitTicks == 3) {
               if (this.legitYaw > 0.0f) {
                  f7 = -f7;
               }
               if (rotation == RotationMode.Smooth) {
                  this.legitTicks = Math.min(this.legitTicks, 1);
                  if (this.delay > this.legitTicks) {
                     this.delay = this.legitTicks;
                  }
                  this.smoothSnap = true;
               }
               f8 = randomFloat(20.0f, 25.0f);
            } else if (this.legitTicks == 2) {
               this.legitYaw += randomFloat(105.0f, 120.0f) * -Math.signum(this.legitYaw);
               f7 = this.legitYaw;
               f8 = randomFloat(40.0f, 45.0f);
            } else if (this.legitTicks == 1) {
               this.legitYaw += randomFloat(33.0f, 35.0f) * -Math.signum(this.legitYaw);
               f7 = this.legitYaw;
               if (this.aimPitch == 100.0f || rotation == RotationMode.Simple) {
                  f8 = randomFloat(50.0f, 55.0f);
               }
            } else if (this.legitTicks == 0) {
               this.legitYaw += randomFloat(29.0f, 33.0f) * -Math.signum(this.legitYaw);
               f7 = this.legitYaw;
               if (this.aimPitch == 100.0f || rotation == RotationMode.Simple) {
                  f8 = randomFloat(65.0f, 70.0f);
               }
            }
         }
         if (rotation == RotationMode.Offset && rotation.side > 0.0f) {
            f7 = -f7;
         }
         if (jumpRotation) {
            return RotationUtils.fixSensitivity(MoveUtils.movementAngle() + f7 + RotationUtils.randomOffset(), f8, f, f2);
         }
      }
      float yaw;
      float pitch;
      if (rotation == RotationMode.Smooth && this.lastPlaced != null && this.groundTicks <= 3L) {
         yaw = this.aimYaw;
         pitch = this.aimPitch;
         f7 = this.smoothLimit;
         if (this.smoothSnap) {
            this.smoothSnap = false;
            f7 = 81.0f + randomFloat(-2.0f, 2.0f);
         }
         f8 = MathHelper.wrapAngleTo180_float(yaw - f);
         if (Math.abs(f8) > f7) {
            yaw = f + (!(f8 > 0.0f) ? -f7 : f7);
         }
         return RotationUtils.fixSensitivity(yaw, pitch, f, f2);
      }
      if (this.aimPitch != 100.0f && l < holdTime()) {
         yaw = this.aimYaw;
         pitch = this.aimPitch;
         if (l > 5L && MoveUtils.isMoving() && Math.random() > 0.3) {
            yaw = (float) ((double) yaw + (Math.random() * 0.45 + 0.05) * (Math.random() < 0.5 ? -1 : 1));
            pitch += RotationUtils.randomOffset();
         }
         float[] r = RotationUtils.fixSensitivity(yaw, pitch, f, f2);
         yaw = r[0];
         pitch = r[1];
      } else {
         if (this.aimPitch != 100.0f) {
            this.aimPitch = 100.0f;
         }
         int n = minecraft.thePlayer.ticksExisted - this.startTick;
         f8 = MoveUtils.movementAngle();
         f9 = rotation != RotationMode.Simple ? this.restPitch : 82.5f;
         f8 += (float) this.yawOffset;
         f9 += RotationUtils.randomOffset();
         if (n <= 2 && !minecraft.thePlayer.onGround && minecraft.thePlayer.ticksExisted - this.jumpTick > 3) {
            BlockPos target = search(null);
            if (target != null) {
               EnumFacing facing = faceFor(target, null);
               if (facing != null && facing != EnumFacing.UP) {
                  BlockPos against = target.offset(facing.getOpposite());
                  float[] r = RotationUtils.searchBlockRotation(against, facing, -170.0f, getPrecision(), f, false);
                  if (r != null) {
                     f8 = r[0];
                     f9 = r[1];
                  }
               }
            }
         } else if (rotation == RotationMode.Offset) {
            f8 = this.aimYaw;
         }
         yaw = f8;
         pitch = f9;
         if (n <= 2) {
            f10 = n != 0 ? (n != 2 ? 0.8f : 0.55f) : 0.3f;
            f10 = (float) ((double) f10 + (Math.random() * 0.04 - 0.02));
            yaw = f + f10 * (f8 - f);
            pitch = f2 + f10 * (f9 - f2);
            if (Math.abs(yaw - f8) < 5.0f) {
               this.delay = 0;
            }
         }
         float[] r = RotationUtils.fixSensitivity(yaw, pitch, f, f2);
         yaw = r[0];
         if (yaw != f) {
            pitch = r[1];
            this.yawOffset = RotationUtils.randomOffset();
         } else {
            pitch = f2;
         }
      }
      return new float[]{yaw, pitch};
   }

   private void afterPlace(BlockPos pos, ItemStack itemStack) {
      FastScaffoldMode fastScaffold = getFastScaffoldMode();
      if (fastScaffold == FastScaffoldMode.Side && fastScaffold.canFastScaffold(this) && (fastScaffold.ticks == -1 || ++fastScaffold.ticks == 2 || fastScaffold.ticks >= 5)) {
         if (fastScaffold.ticks >= 5) {
            fastScaffold.ticks = 0;
         }
         double cx = (double) pos.getX() + 0.5;
         double cz = (double) pos.getZ() + 0.5;
         EnumFacing facing = null;
         switch (minecraft.thePlayer.getHorizontalFacing()) {
            case NORTH:
            case SOUTH:
               facing = minecraft.thePlayer.posX > cx ? EnumFacing.EAST : EnumFacing.WEST;
               break;
            case WEST:
            case EAST:
               facing = minecraft.thePlayer.posZ > cz ? EnumFacing.SOUTH : EnumFacing.NORTH;
               break;
            default:
               break;
         }
         if (facing != null) {
            Vec3 vec3 = BlockUtils.hitVec(pos, facing);
            MovingObjectPosition hit = new MovingObjectPosition(MovingObjectType.BLOCK, vec3, facing, pos);
            BlockPos placedPos = BlockUtils.placedPos(hit);
            if (placedPos != null && BlockUtils.isReplaceable(placedPos)) {
               placePacket(hit, itemStack);
               if (this.silentSwing.isEnabled()) {
                  sendPacketUnregistered(new C0APacketAnimation());
               } else {
                  minecraft.thePlayer.swingItemNoPacket();
               }
               this.placedAt = System.currentTimeMillis();
               this.placed.put(pos, this.placedAt);
               if (fastScaffold.ticks == -1) {
                  fastScaffold.ticks = 0;
               }
               return;
            }
         }
         fastScaffold.ticks = -1;
      }
      MultiPlaceMode multiPlace = getMultiPlaceMode();
      if (multiPlace != MultiPlaceMode.Disabled && itemStack.stackSize > 0 && ((this.tower.isEnabled() && MoveUtils.jumpKeyDown()) || fastScaffold.canFastScaffold(this)) && placeExtra(itemStack)
              && multiPlace == MultiPlaceMode._2_extra && itemStack.stackSize > 0) {
         placeExtra(itemStack);
      }
   }

   private boolean placeExtra(ItemStack itemStack) {
      BlockPos target = search(this.lastPlaced);
      if (target == null) {
         return false;
      }
      EnumFacing facing = faceFor(target, this.lastPlaced);
      if (facing == null) {
         return false;
      }
      BlockPos against = target.offset(facing.getOpposite());
      float[] r = RotationUtils.searchBlockRotation(against, facing, -170.0f, getPrecision(), this.aimYaw, false);
      if (r == null) {
         return false;
      }
      MovingObjectPosition hit = rayTraceBlock(against, null, (double) minecraft.playerController.getBlockReachDistance(), r[0], r[1]);
      if (hit == null) {
         return false;
      }
      placePacket(hit, itemStack);
      if (this.silentSwing.isEnabled()) {
         sendPacketUnregistered(new C0APacketAnimation());
      } else {
         minecraft.thePlayer.swingItemNoPacket();
      }
      this.lastPlaced = target;
      this.placedAt = System.currentTimeMillis();
      this.placed.put(target, this.placedAt);
      return true;
   }

   private void placePacket(MovingObjectPosition hit, ItemStack itemStack) {
      float fx = (float) (hit.hitVec.xCoord - (double) hit.getBlockPos().getX());
      float fy = (float) (hit.hitVec.yCoord - (double) hit.getBlockPos().getY());
      float fz = (float) (hit.hitVec.zCoord - (double) hit.getBlockPos().getZ());
      sendPacketUnregistered(new C08PacketPlayerBlockPlacement(hit.getBlockPos(), hit.sideHit.getIndex(), itemStack, fx, fy, fz));
      if (!minecraft.thePlayer.capabilities.isCreativeMode) {
         itemStack.onItemUse(minecraft.thePlayer, minecraft.theWorld, hit.getBlockPos(), hit.sideHit, fx, fy, fz);
      }
   }

   public MovingObjectPosition rayTraceBlock(BlockPos pos, EnumFacing facing, double range, float yaw, float pitch) {
      Vec3 from = new Vec3(minecraft.thePlayer.posX, minecraft.thePlayer.posY + (double) minecraft.thePlayer.getEyeHeight(), minecraft.thePlayer.posZ);
      Vec3 dir = RotationUtils.getVectorForRotation(yaw, pitch);
      Vec3 to = from.addVector(dir.xCoord * range, dir.yCoord * range, dir.zCoord * range);
      MovingObjectPosition hit = minecraft.theWorld.rayTraceBlocks(from, to, false, false, false);
      if (hit == null || hit.typeOfHit != MovingObjectType.BLOCK) {
         return null;
      }
      if (!hit.getBlockPos().equals(pos) || (facing != null && hit.sideHit != facing)) {
         return null;
      }
      return hit;
   }

   private long holdTime() {
      if (minecraft.thePlayer.isSprinting()) return 710L;
      if (!MoveUtils.isMoving()) return 460L;
      return 110L;
   }

   private long sincePlace() {
      return Math.abs(System.currentTimeMillis() - this.placedAt);
   }

   private float randomFloat(float a, float b) {
      return (float) ((double) a + (double) (b - a) * Math.random());
   }

   private double roundToFourDecimals(double d) {
      return new BigDecimal(d).setScale(4, RoundingMode.HALF_UP).doubleValue();
   }

   private static double distanceSq(double x, double y, double z, Vec3 p) {
      double d4 = x - p.xCoord;
      double d5 = y - p.yCoord;
      double d6 = z - p.zCoord;
      return d4 * d4 + d5 * d5 + d6 * d6;
   }

   private static double behind(double x, double z, double dirX, double dirZ, Vec3 p) {
      double d5 = p.xCoord - x;
      double d6 = p.zCoord - z;
      return -(d5 * dirX + d6 * dirZ);
   }

   public boolean isValid(Item item) {
      if (!(item instanceof ItemBlock)) {
         return false;
      }
      ItemBlock itemBlock = (ItemBlock) item;
      Block block = itemBlock.getBlock();
      return BlockUtils.isPlaceable(block);
   }
}
