package rat.novoline.script.adapter;

import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags;
import rat.novoline.script.MappingUtils;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;
import rat.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import rat.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class S08PacketPlayerPosLookTypeAdapter extends PacketTypeAdapter<S08PacketPlayerPosLook> {
   public S08PacketPlayerPosLookTypeAdapter(TypeRegistry typeRegistry) {
      super(8, HandshakingType.PLAY, ClientboundType.CLIENTBOUND, "S08PacketPlayerPosLook", typeRegistry);
   }

   protected void putPacketFields(ScriptValue scriptValue, S08PacketPlayerPosLook s08PacketPlayerPosLook) {
      scriptValue.putSupplier("getX", s08PacketPlayerPosLook::getX);
      scriptValue.putConsumer("setX", s08PacketPlayerPosLook::setX);
      scriptValue.putSupplier("getY", s08PacketPlayerPosLook::getY);
      scriptValue.putConsumer("setY", s08PacketPlayerPosLook::setY);
      scriptValue.putSupplier("getZ", s08PacketPlayerPosLook::getZ);
      scriptValue.putConsumer("setZ", s08PacketPlayerPosLook::setZ);
      scriptValue.putSupplier("getYaw", s08PacketPlayerPosLook::getYaw);
      scriptValue.putConsumer("setYaw", s08PacketPlayerPosLook::setYaw);
      scriptValue.putSupplier("getPitch", s08PacketPlayerPosLook::getPitch);
      scriptValue.putConsumer("setPitch", s08PacketPlayerPosLook::setPitch);
      scriptValue.putConsumer("setFlags", MappingUtils.mappingEnumSetConsumer(s08PacketPlayerPosLook::setFlags, EnumFlags.class));
      scriptValue.putSupplier("getFlags", MappingUtils.mappingEnumSetSupplier(s08PacketPlayerPosLook::getFlags));
   }
}
