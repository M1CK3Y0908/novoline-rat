package rat.novoline.script.adapter;

import net.minecraft.network.Packet;
import rat.novoline.event.PacketSendEvent;
import rat.novoline.script.AbstractTypeAdapter;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;
import rat.novoline.script.PacketAdapterRegistry.PacketKey;
import rat.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import rat.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class PacketSendEventTypeAdapter extends EventTypeAdapter<PacketSendEvent> {
   protected void putEvent(ScriptValue scriptValue, PacketSendEvent packetSendEvent) {
      Class classValue = packetSendEvent.packet.getClass();
      if (this.typeRegistry.containsKey(classValue)) {
         scriptValue.putObject("packet", packetSendEvent.packet, classValue);
      }
   }

   public PacketSendEvent apply(ScriptValue scriptValue, PacketSendEvent packetSendEvent) {
      ScriptValue scriptValue2 = scriptValue.getScriptValue("packet");
      if (scriptValue2 == null) {
         return packetSendEvent;
      }

      PacketKey packetKey = new PacketKey(
         scriptValue2.getInt("packetId"),
         (HandshakingType)scriptValue2.getEnum("packetType", HandshakingType.class),
         (ClientboundType)scriptValue2.getEnum("packetDirection", ClientboundType.class)
      );
      if (!this.typeRegistry.getPacketAdapterRegistry().containsKey(packetKey)) {
         throw new IllegalArgumentException("Couldn't find packet mapper by ID " + packetKey);
      }

      if (scriptValue2.containsKey("_jObject")) {
         packetSendEvent.packet = (Packet<?>)scriptValue2.getJavaObject();
         AbstractTypeAdapter abstractTypeAdapter = this.typeRegistry.getPacketAdapterRegistry().get(packetKey);
         abstractTypeAdapter.apply(scriptValue2, packetSendEvent.packet);
      } else {
         AbstractTypeAdapter abstractTypeAdapter2 = this.typeRegistry.getPacketAdapterRegistry().get(packetKey);
         packetSendEvent.packet = (Packet<?>)abstractTypeAdapter2.unpack(scriptValue2.asMap());
      }

      return packetSendEvent;
   }

   public PacketSendEventTypeAdapter(TypeRegistry typeRegistry2) {
      super("PacketSendEvent", typeRegistry2);
   }
}
