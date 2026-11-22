package client.m1ck3y.novoline.features.module.modules.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import client.m1ck3y.novoline.event.PacketReceiveEvent;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;

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
