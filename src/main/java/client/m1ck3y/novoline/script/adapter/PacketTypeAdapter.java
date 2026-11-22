package client.m1ck3y.novoline.script.adapter;

import net.minecraft.network.Packet;
import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public abstract class PacketTypeAdapter<T extends Packet<?>> extends AbstractTypeAdapter<T> {
   private final int packetId;
   private final PacketTypeAdapter.HandshakingType handshakingType;
   private final PacketTypeAdapter.ClientboundType clientboundType;

   public PacketTypeAdapter(int i, PacketTypeAdapter.HandshakingType handshakingType2, PacketTypeAdapter.ClientboundType clientboundType2, String text, TypeRegistry typeRegistry) {
      super(text, typeRegistry);
      this.packetId = i;
      this.handshakingType = handshakingType2;
      this.clientboundType = clientboundType2;
   }

   public ScriptValue put(ScriptValue scriptValue, T value) {
      scriptValue.putEnum("packetType", this.getHandshakingType());
      scriptValue.putEnum("packetDirection", this.getClientboundType());
      scriptValue.put("packetId", this.getPacketId());
      this.putPacketFields(scriptValue, (T)value);
      return scriptValue;
   }

   protected void putPacketFields(ScriptValue scriptValue, T value) {
   }

   public int getPacketId() {
      return this.packetId;
   }

   public PacketTypeAdapter.HandshakingType getHandshakingType() {
      return this.handshakingType;
   }

   public PacketTypeAdapter.ClientboundType getClientboundType() {
      return this.clientboundType;
   }

   public enum ClientboundType {
      CLIENTBOUND,
      SERVERBOUND;
   }

   public enum HandshakingType {
      HANDSHAKING,
      PLAY,
      LOGIN,
      STATUS;
   }
}
