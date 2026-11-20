package rat.novoline.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import rat.novoline.event.PacketSendEvent;
import rat.novoline.event.PreUpdateEvent;
import rat.novoline.event.UpdateEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.utils.player.PlayerUtils;

public class Sprint extends Module {
   private final ComboSetting sprint = settingManager.register(new ComboSetting(this, "Multi-directional", 0, "Disabled", "Normal", "Advanced"));
   private final CheckboxSetting ignoreHunger = settingManager.register(new CheckboxSetting(this, "Ignore hunger", false));
   private final CheckboxSetting ignoreBlindness = settingManager.register(new CheckboxSetting(this, "Ignore blindness", false));
   private final CheckboxSetting cancelStopState = settingManager.register(new CheckboxSetting(this, "Cancel stop state", false));
   @EventLink
   private final Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (ModuleManager.scaffold.isEnabled() && !ModuleManager.scaffold.canSprint()) {
         minecraft.thePlayer.setSprinting(false);
      } else if (!minecraft.thePlayer.isSneaking()
         && !minecraft.thePlayer.isCollidedHorizontally
         && (this.ignoreHunger.isEnabled() || minecraft.thePlayer.getFoodStats().getFoodLevel() > 6)
         && (this.ignoreBlindness.isEnabled() || !minecraft.thePlayer.isPotionActive(Potion.blindness))) {
         if (minecraft.thePlayer.moveForward > 0.0F || this.sprint.getCurrentElement() != 0 && PlayerUtils.isMoving()) {
            minecraft.thePlayer.setSprinting(true);
         }
      }
   };
   @EventLink(1)
   private final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (this.sprint.getCurrentElement() == 2) {
         MovementInput movementInput2 = minecraft.thePlayer.movementInput;
         if (movementInput2.moveStrafe != 0.0F || movementInput2.moveForward < 0.0F) {
            preUpdateEvent.rotationYaw = PlayerUtils.getMoveDirectionDegrees();
         }
      }
   };
   @EventLink
   private final Listener<PacketSendEvent> onPacketSend = packetSendEvent -> {
      Packet packet2 = packetSendEvent.packet;
      if (packet2 instanceof C0BPacketEntityAction && ((C0BPacketEntityAction)packet2).action == Action.STOP_SPRINTING && this.cancelStopState.isEnabled()) {
         packetSendEvent.cancel();
      }
   };

   public Sprint() {
      super(Category.MOVEMENT, "Sprint", "Enables sprint during move start");
   }

   public ComboSetting getSprint() {
      return this.sprint;
   }
}
