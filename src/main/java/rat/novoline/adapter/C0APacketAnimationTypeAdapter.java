package rat.novoline.adapter;

import java.util.Map;
import net.minecraft.network.play.client.C0APacketAnimation;
import rat.novoline.script.TypeRegistry;
import rat.novoline.script.adapter.PacketTypeAdapter;
import rat.novoline.script.adapter.PacketTypeAdapter.ClientboundType;
import rat.novoline.script.adapter.PacketTypeAdapter.HandshakingType;

public class C0APacketAnimationTypeAdapter extends PacketTypeAdapter<C0APacketAnimation> {
   public C0APacketAnimation unpack(Map<String, Object> map) {
      return new C0APacketAnimation();
   }

   public C0APacketAnimationTypeAdapter(TypeRegistry typeRegistry) {
      super(10, HandshakingType.PLAY, ClientboundType.SERVERBOUND, "C0APacketAnimation", typeRegistry);
   }
}
