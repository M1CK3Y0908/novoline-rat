package rat.novoline.module.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import rat.novoline.event.PacketReceiveEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;

public class NoCloseGui extends Module {
   @EventLink
   private final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      if (minecraft.currentScreen != null && packetReceiveEvent.packet instanceof S2EPacketCloseWindow) {
         packetReceiveEvent.cancel();
      }
   };

   public NoCloseGui() {
      super(Category.MISC, "NoCloseGui", "");
   }
}
