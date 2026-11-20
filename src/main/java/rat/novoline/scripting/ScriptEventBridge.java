package rat.novoline.scripting;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import rat.novoline.event.BlockCollisionEvent;
import rat.novoline.event.DeviceButtonDownEvent;
import rat.novoline.event.Event;
import rat.novoline.event.GameEndEvent;
import rat.novoline.event.GameStartEvent;
import rat.novoline.event.GamemodeUpdateEvent;
import rat.novoline.event.JumpEvent;
import rat.novoline.event.LoadWorldEvent;
import rat.novoline.event.MoveEntityEvent;
import rat.novoline.event.MoveEntityEvent2;
import rat.novoline.event.OnUpdateWalkingPlayerEvent;
import rat.novoline.event.PacketReceiveEvent;
import rat.novoline.event.PacketSendEvent;
import rat.novoline.event.PlayerKillEvent;
import rat.novoline.event.PlayerMoveEvent;
import rat.novoline.event.PreUpdateEvent;
import rat.novoline.event.RenderBlockEvent;
import rat.novoline.event.RenderScreenEvent;
import rat.novoline.event.RenderWorldEvent;
import rat.novoline.event.RunTickEvent;
import rat.novoline.event.ServerConnectEvent;
import rat.novoline.event.ServerDisconnectEvent;
import rat.novoline.event.SlowdownEvent;
import rat.novoline.event.SpawnParticleEvent;
import rat.novoline.event.SyncCurrentSlotEvent;
import rat.novoline.event.UpdateEvent;
import rat.novoline.scripting.ScriptEventHandler.HelperChild;

public class ScriptEventBridge {
   @EventLink
   private final Listener<BlockCollisionEvent> onBlockCollision = this::dispatch;
   @EventLink
   private final Listener<DeviceButtonDownEvent> onDeviceButtonDown = this::dispatch;
   @EventLink
   private final Listener<GameEndEvent> onGameEnd = this::dispatch;
   @EventLink
   private final Listener<GamemodeUpdateEvent> onGamemodeUpdate = this::dispatch;
   @EventLink
   private final Listener<GameStartEvent> onGameStart = this::dispatch;
   @EventLink
   private final Listener<LoadWorldEvent> onLoadWorld = this::dispatch;
   @EventLink
   private final Listener<JumpEvent> onJump = this::dispatch;
   @EventLink
   private final Listener<OnUpdateWalkingPlayerEvent> onOnUpdateWalkingPlayer = this::dispatch;
   @EventLink
   private final Listener<MoveEntityEvent> onMoveEntity = this::dispatch;
   @EventLink
   private final Listener<MoveEntityEvent2> onMoveEntityEvent2 = this::dispatch;
   @EventLink
   private final Listener<RunTickEvent> onRunTick = this::dispatch;
   @EventLink
   private final Listener<UpdateEvent> onUpdate = this::dispatch;
   @EventLink
   private final Listener<PacketSendEvent> onPacketSend = this::dispatch;
   @EventLink
   private final Listener<PacketReceiveEvent> onPacketReceive = this::dispatch;
   @EventLink
   private final Listener<SpawnParticleEvent> onSpawnParticle = this::dispatch;
   @EventLink
   private final Listener<PlayerKillEvent> onPlayerKill = this::dispatch;
   @EventLink
   private final Listener<PlayerMoveEvent> onPlayerMove = this::dispatch;
   @EventLink
   private final Listener<PreUpdateEvent> onPreUpdate = this::dispatch;
   @EventLink
   private final Listener<RenderBlockEvent> onRenderBlock = this::dispatch;
   @EventLink
   private final Listener<RenderScreenEvent> onRenderScreen = this::dispatch;
   @EventLink
   private final Listener<RenderWorldEvent> onRenderWorld = this::dispatch;
   @EventLink
   private final Listener<ServerConnectEvent> onServerConnect = this::dispatch;
   @EventLink
   private final Listener<ServerDisconnectEvent> onServerDisconnect = this::dispatch;
   @EventLink
   private final Listener<SlowdownEvent> onSlowdown = this::dispatch;
   @EventLink
   private final Listener<SyncCurrentSlotEvent> onSyncCurrentSlot = this::dispatch;
   private final ScriptEventDispatcher scriptEventDispatcher;
   private final ScriptEventHandlerList scriptEventHandlerList;

   public ScriptEventBridge(ScriptEventDispatcher scriptEventDispatcher2, ScriptEventHandlerList scriptEventHandlerList2) {
      this.scriptEventDispatcher = scriptEventDispatcher2;
      this.scriptEventHandlerList = scriptEventHandlerList2;
   }

   private void dispatch(Event event2) {
      for (ScriptEventHandler scriptEventHandler : this.scriptEventHandlerList
         .sortByPriority(this.scriptEventHandlerList.findAllEnabledByEvent(HelperChild.forClass(event2.getClass())))) {
         this.scriptEventDispatcher.dispatchEvent(scriptEventHandler, event2);
         if (event2.isCancelled()) {
            break;
         }
      }
   }
}
