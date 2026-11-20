package rat.novoline.script;

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
import rat.novoline.event.BlockCollisionEvent;
import rat.novoline.event.DeviceButtonDownEvent;
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
import rat.novoline.event.SyncCurrentSlotEvent;
import rat.novoline.event.UpdateEvent;
import rat.novoline.module.Module;
import rat.novoline.script.adapter.AnimationTypeAdapter;
import rat.novoline.script.adapter.AxisAlignedBBTypeAdapter;
import rat.novoline.script.adapter.BlockCollisionEventTypeAdapter;
import rat.novoline.script.adapter.BlockPosTypeAdapter;
import rat.novoline.script.adapter.BlockTypeAdapter;
import rat.novoline.script.adapter.CFontRendererTypeAdapter;
import rat.novoline.script.adapter.EntityTypeAdapter;
import rat.novoline.script.adapter.EnumFacingTypeAdapter;
import rat.novoline.script.adapter.EventTypeAdapter10;
import rat.novoline.script.adapter.EventTypeAdapter11;
import rat.novoline.script.adapter.EventTypeAdapter2;
import rat.novoline.script.adapter.EventTypeAdapter3;
import rat.novoline.script.adapter.EventTypeAdapter4;
import rat.novoline.script.adapter.EventTypeAdapter5;
import rat.novoline.script.adapter.EventTypeAdapter7;
import rat.novoline.script.adapter.EventTypeAdapter8;
import rat.novoline.script.adapter.EventTypeAdapter9;
import rat.novoline.script.adapter.FontRendererTypeAdapter;
import rat.novoline.script.adapter.FramebufferTypeAdapter;
import rat.novoline.script.adapter.GamemodeUpdateEventTypeAdapter;
import rat.novoline.script.adapter.IBlockStateTypeAdapter;
import rat.novoline.script.adapter.IChatComponentTypeAdapter;
import rat.novoline.script.adapter.ItemStackTypeAdapter;
import rat.novoline.script.adapter.MinecraftTypeAdapter;
import rat.novoline.script.adapter.ModuleTypeAdapter;
import rat.novoline.script.adapter.MovementInputTypeAdapter;
import rat.novoline.script.adapter.NotificationTypeAdapter;
import rat.novoline.script.adapter.PacketSendEventTypeAdapter;
import rat.novoline.script.adapter.PlayerCapabilitiesTypeAdapter;
import rat.novoline.script.adapter.PlayerMoveEventTypeAdapter;
import rat.novoline.script.adapter.PolygonRendererTypeAdapter;
import rat.novoline.script.adapter.RectRendererTypeAdapter;
import rat.novoline.script.adapter.RenderWorldEventTypeAdapter;
import rat.novoline.script.adapter.ScaledResolutionTypeAdapter;
import rat.novoline.script.adapter.ServerDataTypeAdapter;
import rat.novoline.script.adapter.ServerDisconnectEventTypeAdapter;
import rat.novoline.script.adapter.SettingTypeAdapter;
import rat.novoline.script.adapter.SlowdownEventTypeAdapter;
import rat.novoline.script.adapter.SyncCurrentSlotEventTypeAdapter;
import rat.novoline.script.adapter.TimerTypeAdapter;
import rat.novoline.script.adapter.Vec3TypeAdapter;
import rat.novoline.script.adapter.Vec3iTypeAdapter;
import rat.novoline.script.adapter.WorldClientTypeAdapter;
import rat.novoline.setting.Setting;
import rat.novoline.util.EventTypeAdapters.EventTypeAdapter12Child;
import rat.novoline.util.EventTypeAdapters.EventTypeAdapter12Child2;
import rat.novoline.util.EventTypeAdapters.EventTypeAdapter12Child3;
import rat.novoline.util.EventTypeAdapters.EventTypeAdapter12Child4;
import rat.novoline.util.EventTypeAdapters.EventTypeAdapter12Child5;
import rat.novoline.util.EventTypeAdapters.EventTypeAdapter12Child6;
import rat.novoline.util.EventTypeAdapters.EventTypeAdapter12Child7;
import rat.novoline.utils.animation.Animation;
import rat.novoline.utils.notification.Notification;
import rat.novoline.utils.render.CFontRenderer;
import rat.novoline.utils.render.PolygonRenderer;
import rat.novoline.utils.render.shape.RectRenderer;

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
