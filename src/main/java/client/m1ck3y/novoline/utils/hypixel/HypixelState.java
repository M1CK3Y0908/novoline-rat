package client.m1ck3y.novoline.utils.hypixel;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.time.Instant;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import client.m1ck3y.novoline.Novoline;
import client.m1ck3y.novoline.enums.HypixelGameType;
import client.m1ck3y.novoline.enums.ServerType;
import client.m1ck3y.novoline.event.GamemodeUpdateEvent;
import client.m1ck3y.novoline.event.LoadWorldEvent;
import client.m1ck3y.novoline.event.PacketReceiveEvent;
import client.m1ck3y.novoline.event.ServerConnectEvent;
import client.m1ck3y.novoline.event.ServerDisconnectEvent;
import client.m1ck3y.novoline.loader.HostsChecker;
import client.m1ck3y.novoline.loader.HostsChecker.Context;

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
