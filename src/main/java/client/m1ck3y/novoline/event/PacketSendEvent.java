package client.m1ck3y.novoline.event;

import net.minecraft.network.Packet;

public class PacketSendEvent extends Event {
   public Packet<?> packet;

   public PacketSendEvent(Packet packet2) {
      super(23992540611656L, true);
      this.packet = packet2;
   }

   public Packet<?> getPacket() {
      return this.packet;
   }

   public void setPacket(Packet<?> packet) {
      this.packet = packet;
   }
}
