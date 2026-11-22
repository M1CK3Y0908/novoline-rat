package client.m1ck3y.novoline.utils;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.Minecraft;
import client.m1ck3y.novoline.Novoline;
import client.m1ck3y.novoline.client.ApiClient;
import client.m1ck3y.novoline.client.PatchUsersSelfPresenceRequest;
import client.m1ck3y.novoline.enums.ClientState;
import client.m1ck3y.novoline.event.LoadWorldEvent;
import client.m1ck3y.novoline.event.ServerConnectEvent;
import client.m1ck3y.novoline.event.ServerDisconnectEvent;

public class PresenceUpdater {
   private final Minecraft minecraft = Minecraft.getMinecraft();
   private final ApiClient apiClient = Novoline.getNovoline().getIrcConnection().getApiClient();
   @EventLink
   private final Listener<ServerConnectEvent> onServerConnect = serverConnectEvent -> {
      try {
         this.apiClient
            .updatePresence(
               PatchUsersSelfPresenceRequest.builder()
                  .presenceType(ClientState.ON_SERVER)
                  .ign(this.minecraft.session.getUsername())
                  .serverIp(serverConnectEvent.connectingHostname)
                  .build()
            );
      } catch (IllegalStateException illegalStateException) {
      }
   };
   @EventLink
   private final Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> {
      if (this.minecraft.isSingleplayer()) {
         try {
            this.apiClient
               .updatePresence(
                  PatchUsersSelfPresenceRequest.builder().presenceType(ClientState.IN_SINGLEPLAYER).ign(this.minecraft.session.getUsername()).build()
               );
         } catch (IllegalStateException illegalStateException) {
         }
      }
   };
   @EventLink
   private final Listener<ServerDisconnectEvent> onServerDisconnect = serverDisconnectEvent -> {
      try {
         this.apiClient.updatePresence(PatchUsersSelfPresenceRequest.builder().presenceType(ClientState.IN_MENU).build());
      } catch (IllegalStateException illegalStateException) {
      }
   };
}
