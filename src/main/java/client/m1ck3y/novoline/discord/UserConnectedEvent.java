package client.m1ck3y.novoline.discord;

import client.m1ck3y.novoline.client.discord.GatewayEvent;
import client.m1ck3y.novoline.client.discord.GatewayUser;

public class UserConnectedEvent implements GatewayEvent {
   private final GatewayUser gatewayUser;

   private UserConnectedEvent(GatewayUser gatewayUser2) {
      this.gatewayUser = gatewayUser2;
   }

   public static UserConnectedEvent createUserConnectedEvent(GatewayUser gatewayUser2) {
      return new UserConnectedEvent(gatewayUser2);
   }

   public GatewayUser getGatewayUser() {
      return this.gatewayUser;
   }
}
