package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.event.PacketReceiveEvent;
import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;
import client.m1ck3y.novoline.script.PacketAdapterRegistry.PacketKey;
import client.m1ck3y.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import client.m1ck3y.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class EventTypeAdapter5 extends EventTypeAdapter<PacketReceiveEvent> {
   protected void putEvent(ScriptValue scriptValue, PacketReceiveEvent packetReceiveEvent) {
      Class classValue = packetReceiveEvent.packet.getClass();
      if (this.typeRegistry.containsKey(classValue)) {
         scriptValue.putObject("packet", packetReceiveEvent.packet, classValue);
      }
   }

   public EventTypeAdapter5(TypeRegistry typeRegistry2) {
      super("PacketReceiveEvent", typeRegistry2);
   }

   public PacketReceiveEvent apply(ScriptValue scriptValue, PacketReceiveEvent packetReceiveEvent) {
      if (scriptValue.containsKey("packet")) {
         ScriptValue scriptValue2 = scriptValue.getScriptValue("packet");
         PacketKey packetKey = new PacketKey(
            scriptValue2.getInt("packetId"),
            (HandshakingType)scriptValue2.getEnum("packetType", HandshakingType.class),
            (ClientboundType)scriptValue2.getEnum("packetDirection", ClientboundType.class)
         );
         AbstractTypeAdapter abstractTypeAdapter = this.typeRegistry.getPacketAdapterRegistry().get(packetKey);
         if (abstractTypeAdapter != null) {
            abstractTypeAdapter.apply(scriptValue2, packetReceiveEvent.packet);
         }
      }

      return packetReceiveEvent;
   }
}
