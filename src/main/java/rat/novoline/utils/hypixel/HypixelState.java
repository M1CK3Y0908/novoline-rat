package rat.novoline.utils.hypixel;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.time.Instant;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import rat.novoline.Novoline;
import rat.novoline.enums.HypixelGameType;
import rat.novoline.enums.ServerType;
import rat.novoline.event.GamemodeUpdateEvent;
import rat.novoline.event.LoadWorldEvent;
import rat.novoline.event.PacketReceiveEvent;
import rat.novoline.event.ServerConnectEvent;
import rat.novoline.event.ServerDisconnectEvent;
import rat.novoline.loader.HostsChecker;
import rat.novoline.loader.HostsChecker.Context;

public class HypixelState {
   @EventLink(3)
   public Listener<ServerConnectEvent> onServerConnect = serverConnectEvent -> HostsChecker.detectServer(
      Context.builder().connectingHostname(serverConnectEvent.connectingHostname).connectingIp(serverConnectEvent.connectingIp).getContext()
   );
   @EventLink(3)
   public Listener<ServerDisconnectEvent> onServerDisconnect = serverDisconnectEvent -> reset();
   private static Instant sessionStart;
   @EventLink(3)
   public Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> {
      if (Minecraft.getMinecraft().isSingleplayer()) {
         reset();
      }
   };
   private static HypixelGameType hypixelGameType;
   private static ServerType serverType;
   @EventLink(3)
   public Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      if (packetReceiveEvent.packet instanceof S3DPacketDisplayScoreboard s3DPacketDisplayScoreboard) {
         if (serverType == null) {
            return;
         }

         switch (serverType) {
            case HYPIXEL:
               HypixelGameType byScoreName = HypixelGameType.getByScoreName(s3DPacketDisplayScoreboard.getScoreName());
               if (byScoreName != null) {
                  setGameType(byScoreName);
                  Novoline.getNovoline().getBus().post(new GamemodeUpdateEvent(hypixelGameType));
               }
         }
      }
   };

   public static boolean isServerType(ServerType serverType2) {
      return serverType == serverType2;
   }

   public static Instant getSessionStart() {
      if (sessionStart == null) {
         sessionStart = Instant.now();
      }

      return sessionStart;
   }

   public static void reset() {
      serverType = null;
      hypixelGameType = null;
      sessionStart = Instant.now();
   }

   public static void setGameType(HypixelGameType hypixelGameType2) {
      hypixelGameType = hypixelGameType2;
   }

   public static ServerType getServerType() {
      return serverType;
   }

   public static void setServerType(ServerType serverType2) {
      serverType = serverType2;
   }

   public static HypixelGameType getHypixelGameType() {
      return hypixelGameType;
   }

   public static boolean isGameType(HypixelGameType hypixelGameType2) {
      return hypixelGameType == hypixelGameType2;
   }
}
