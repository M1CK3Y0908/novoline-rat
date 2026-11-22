package client.m1ck3y.novoline.script;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import client.m1ck3y.novoline.event.BlockCollisionEvent;
import client.m1ck3y.novoline.event.DeviceButtonDownEvent;
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
import client.m1ck3y.novoline.event.SyncCurrentSlotEvent;
import client.m1ck3y.novoline.event.UpdateEvent;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.script.adapter.AnimationTypeAdapter;
import client.m1ck3y.novoline.script.adapter.AxisAlignedBBTypeAdapter;
import client.m1ck3y.novoline.script.adapter.BlockCollisionEventTypeAdapter;
import client.m1ck3y.novoline.script.adapter.BlockPosTypeAdapter;
import client.m1ck3y.novoline.script.adapter.BlockTypeAdapter;
import client.m1ck3y.novoline.script.adapter.CFontRendererTypeAdapter;
import client.m1ck3y.novoline.script.adapter.EntityTypeAdapter;
import client.m1ck3y.novoline.script.adapter.EnumFacingTypeAdapter;
import client.m1ck3y.novoline.script.adapter.EventTypeAdapter10;
import client.m1ck3y.novoline.script.adapter.EventTypeAdapter11;
import client.m1ck3y.novoline.script.adapter.EventTypeAdapter2;
import client.m1ck3y.novoline.script.adapter.EventTypeAdapter3;
import client.m1ck3y.novoline.script.adapter.EventTypeAdapter4;
import client.m1ck3y.novoline.script.adapter.EventTypeAdapter5;
import client.m1ck3y.novoline.script.adapter.EventTypeAdapter7;
import client.m1ck3y.novoline.script.adapter.EventTypeAdapter8;
import client.m1ck3y.novoline.script.adapter.EventTypeAdapter9;
import client.m1ck3y.novoline.script.adapter.FontRendererTypeAdapter;
import client.m1ck3y.novoline.script.adapter.FramebufferTypeAdapter;
import client.m1ck3y.novoline.script.adapter.GamemodeUpdateEventTypeAdapter;
import client.m1ck3y.novoline.script.adapter.IBlockStateTypeAdapter;
import client.m1ck3y.novoline.script.adapter.IChatComponentTypeAdapter;
import client.m1ck3y.novoline.script.adapter.ItemStackTypeAdapter;
import client.m1ck3y.novoline.script.adapter.MinecraftTypeAdapter;
import client.m1ck3y.novoline.script.adapter.ModuleTypeAdapter;
import client.m1ck3y.novoline.script.adapter.MovementInputTypeAdapter;
import client.m1ck3y.novoline.script.adapter.NotificationTypeAdapter;
import client.m1ck3y.novoline.script.adapter.PacketSendEventTypeAdapter;
import client.m1ck3y.novoline.script.adapter.PlayerCapabilitiesTypeAdapter;
import client.m1ck3y.novoline.script.adapter.PlayerMoveEventTypeAdapter;
import client.m1ck3y.novoline.script.adapter.PolygonRendererTypeAdapter;
import client.m1ck3y.novoline.script.adapter.RectRendererTypeAdapter;
import client.m1ck3y.novoline.script.adapter.RenderWorldEventTypeAdapter;
import client.m1ck3y.novoline.script.adapter.ScaledResolutionTypeAdapter;
import client.m1ck3y.novoline.script.adapter.ServerDataTypeAdapter;
import client.m1ck3y.novoline.script.adapter.ServerDisconnectEventTypeAdapter;
import client.m1ck3y.novoline.script.adapter.SettingTypeAdapter;
import client.m1ck3y.novoline.script.adapter.SlowdownEventTypeAdapter;
import client.m1ck3y.novoline.script.adapter.SyncCurrentSlotEventTypeAdapter;
import client.m1ck3y.novoline.script.adapter.TimerTypeAdapter;
import client.m1ck3y.novoline.script.adapter.Vec3TypeAdapter;
import client.m1ck3y.novoline.script.adapter.Vec3iTypeAdapter;
import client.m1ck3y.novoline.script.adapter.WorldClientTypeAdapter;
import client.m1ck3y.novoline.setting.Setting;
import client.m1ck3y.novoline.util.EventTypeAdapters.EventTypeAdapter12Child;
import client.m1ck3y.novoline.util.EventTypeAdapters.EventTypeAdapter12Child2;
import client.m1ck3y.novoline.util.EventTypeAdapters.EventTypeAdapter12Child3;
import client.m1ck3y.novoline.util.EventTypeAdapters.EventTypeAdapter12Child4;
import client.m1ck3y.novoline.util.EventTypeAdapters.EventTypeAdapter12Child5;
import client.m1ck3y.novoline.util.EventTypeAdapters.EventTypeAdapter12Child6;
import client.m1ck3y.novoline.util.EventTypeAdapters.EventTypeAdapter12Child7;
import client.m1ck3y.novoline.utils.animation.Animation;
import client.m1ck3y.novoline.utils.notification.Notification;
import client.m1ck3y.novoline.utils.render.CFontRenderer;
import client.m1ck3y.novoline.utils.render.PolygonRenderer;
import client.m1ck3y.novoline.utils.render.shape.RectRenderer;

public class TypeRegistry extends UnifiedMap<Class<?>, AbstractTypeAdapter<?>> {
   private final TypeStore typeStore = TypeStore.getTypeStore();
   private final PacketAdapterRegistry packetAdapterRegistry = new PacketAdapterRegistry(this);

   public PacketAdapterRegistry getPacketAdapterRegistry() {
      return this.packetAdapterRegistry;
   }

   public static TypeRegistry getTypeRegistry() {
      return TypeRegistry.InstanceType.INSTANCE.typeRegistry;
   }

   public TypeRegistry() {
      this.put(Module.class, new ModuleTypeAdapter(this));
      this.put(Setting.class, new SettingTypeAdapter(this));
      this.put(CFontRenderer.class, new CFontRendererTypeAdapter(this));
      this.put(Notification.class, new NotificationTypeAdapter(this));
      this.put(PolygonRenderer.class, new PolygonRendererTypeAdapter(this));
      this.put(RectRenderer.class, new RectRendererTypeAdapter(this));
      this.put(Animation.class, new AnimationTypeAdapter(this));
      this.put(BlockCollisionEvent.class, new BlockCollisionEventTypeAdapter(this));
      this.put(DeviceButtonDownEvent.class, new EventTypeAdapter8(this));
      this.put(GameEndEvent.class, new EventTypeAdapter11(this));
      this.put(GamemodeUpdateEvent.class, new GamemodeUpdateEventTypeAdapter(this));
      this.put(GameStartEvent.class, new EventTypeAdapter2(this));
      this.put(LoadWorldEvent.class, new EventTypeAdapter12Child(this));
      this.put(JumpEvent.class, new EventTypeAdapter12Child2(this));
      this.put(OnUpdateWalkingPlayerEvent.class, new EventTypeAdapter12Child3(this));
      this.put(UpdateEvent.class, new EventTypeAdapter12Child4(this));
      this.put(RunTickEvent.class, new EventTypeAdapter12Child5(this));
      this.put(MoveEntityEvent.class, new EventTypeAdapter12Child6(this));
      this.put(MoveEntityEvent2.class, new EventTypeAdapter12Child7(this));
      this.put(PacketSendEvent.class, new PacketSendEventTypeAdapter(this));
      this.put(PacketReceiveEvent.class, new EventTypeAdapter5(this));
      this.put(PlayerKillEvent.class, new EventTypeAdapter9(this));
      this.put(PlayerMoveEvent.class, new PlayerMoveEventTypeAdapter(this));
      this.put(PreUpdateEvent.class, new EventTypeAdapter7(this));
      this.put(RenderBlockEvent.class, new EventTypeAdapter3(this));
      this.put(RenderScreenEvent.class, new EventTypeAdapter10(this));
      this.put(RenderWorldEvent.class, new RenderWorldEventTypeAdapter(this));
      this.put(ServerConnectEvent.class, new EventTypeAdapter4(this));
      this.put(ServerDisconnectEvent.class, new ServerDisconnectEventTypeAdapter(this));
      this.put(SlowdownEvent.class, new SlowdownEventTypeAdapter(this));
      this.put(SyncCurrentSlotEvent.class, new SyncCurrentSlotEventTypeAdapter(this));
      this.put(Vec3.class, new Vec3TypeAdapter(this));
      this.put(Vec3i.class, new Vec3iTypeAdapter(this));
      this.put(BlockPos.class, new BlockPosTypeAdapter(this));
      this.put(EnumFacing.class, new EnumFacingTypeAdapter(this));
      this.put(ItemStack.class, new ItemStackTypeAdapter(this));
      this.put(IChatComponent.class, new IChatComponentTypeAdapter(this));
      this.put(IBlockState.class, new IBlockStateTypeAdapter(this));
      this.put(Block.class, new BlockTypeAdapter(this));
      this.put(Entity.class, new EntityTypeAdapter(this));
      this.put(AxisAlignedBB.class, new AxisAlignedBBTypeAdapter(this));
      this.put(PlayerCapabilities.class, new PlayerCapabilitiesTypeAdapter(this));
      this.put(Minecraft.class, new MinecraftTypeAdapter(this));
      this.put(WorldClient.class, new WorldClientTypeAdapter(this));
      this.put(FontRenderer.class, new FontRendererTypeAdapter(this));
      this.put(Timer.class, new TimerTypeAdapter(this));
      this.put(ServerData.class, new ServerDataTypeAdapter(this));
      this.put(ScaledResolution.class, new ScaledResolutionTypeAdapter(this));
      this.put(MovementInput.class, new MovementInputTypeAdapter(this));
      this.put(Framebuffer.class, new FramebufferTypeAdapter(this));
   }

   public AbstractTypeAdapter put(Class clazz, AbstractTypeAdapter abstractTypeAdapter) {
      ScriptType scriptType = ScriptType.createScriptType(abstractTypeAdapter.getTypeName(), clazz);
      if (!this.typeStore.isRegistered(scriptType.getName())) {
         this.typeStore.add(scriptType);
      }

      for (ScriptType scriptType2 : (Iterable<ScriptType>) abstractTypeAdapter.getSubTypes()) {
         scriptType2.setParentName(scriptType.getName());
         this.typeStore.add(scriptType2);
      }

      super.put(clazz, abstractTypeAdapter);
      abstractTypeAdapter.registerSubAdapters();
      return abstractTypeAdapter;
   }

   public <T> TypeAdapter<T> getTypeAdapter(Class<T> clazz) {
      return (TypeAdapter<T>)this.get(clazz);
   }

   public TypeStore getTypeStore() {
      return this.typeStore;
   }

   enum InstanceType {
      INSTANCE;

      private final TypeRegistry typeRegistry = new TypeRegistry();
   }
}
