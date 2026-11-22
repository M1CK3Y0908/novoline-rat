package client.m1ck3y.novoline.scripting;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import client.m1ck3y.novoline.event.BlockCollisionEvent;
import client.m1ck3y.novoline.event.DeviceButtonDownEvent;
import client.m1ck3y.novoline.event.Event;
import client.m1ck3y.novoline.event.GameEndEvent;
import client.m1ck3y.novoline.event.GameStartEvent;
import client.m1ck3y.novoline.event.GamemodeUpdateEvent;
import client.m1ck3y.novoline.event.JumpEvent;
import client.m1ck3y.novoline.event.LoadWorldEvent;
import client.m1ck3y.novoline.event.MoveEntityEvent;
import client.m1ck3y.novoline.event.MoveEntityEvent2;
import client.m1ck3y.novoline.event.OnUpdateWalkingPlayerEvent;
import client.m1ck3y.novoline.event.PacketReceiveEvent;
import client.m1ck3y.novoline.event.PacketSendEvent;
import client.m1ck3y.novoline.event.PlayerKillEvent;
import client.m1ck3y.novoline.event.PlayerMoveEvent;
import client.m1ck3y.novoline.event.PreUpdateEvent;
import client.m1ck3y.novoline.event.RenderBlockEvent;
import client.m1ck3y.novoline.event.RenderScreenEvent;
import client.m1ck3y.novoline.event.RenderWorldEvent;
import client.m1ck3y.novoline.event.RunTickEvent;
import client.m1ck3y.novoline.event.ServerConnectEvent;
import client.m1ck3y.novoline.event.ServerDisconnectEvent;
import client.m1ck3y.novoline.event.SlowdownEvent;
import client.m1ck3y.novoline.event.SpawnParticleEvent;
import client.m1ck3y.novoline.event.SyncCurrentSlotEvent;
import client.m1ck3y.novoline.event.UpdateEvent;
import client.m1ck3y.novoline.scripting.ScriptEventHandler.HelperChild;

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
