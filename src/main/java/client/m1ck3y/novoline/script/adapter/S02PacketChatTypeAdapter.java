package client.m1ck3y.novoline.script.adapter;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.IChatComponent;
import client.m1ck3y.novoline.script.MappingUtils;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;
import client.m1ck3y.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import client.m1ck3y.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class S02PacketChatTypeAdapter extends PacketTypeAdapter<S02PacketChat> {
   public S02PacketChatTypeAdapter(TypeRegistry typeRegistry) {
      super(2, HandshakingType.PLAY, ClientboundType.CLIENTBOUND, "S02PacketChat", typeRegistry);
   }

   protected void putPacketFields(ScriptValue scriptValue, S02PacketChat s02PacketChat) {
      scriptValue.putSupplier("getChatComponent", MappingUtils.mappingSupplier(IChatComponent.class, s02PacketChat::getChatComponent));
      scriptValue.putConsumer("setChatComponent", MappingUtils.mappingConsumer(IChatComponent.class, s02PacketChat::setChatComponent));
      scriptValue.putSupplier("getType", s02PacketChat::getType);
      scriptValue.putConsumer("setType", s02PacketChat::setType);
   }
}
