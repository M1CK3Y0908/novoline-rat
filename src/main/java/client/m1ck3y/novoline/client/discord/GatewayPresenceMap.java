package client.m1ck3y.novoline.client.discord;

import java.util.HashMap;
import client.m1ck3y.novoline.util.Profile;

public class GatewayPresenceMap extends HashMap<Long, Profile> {
   public static GatewayPresenceMap getGatewayPresenceMap() {
      return new GatewayPresenceMap();
   }

   private GatewayPresenceMap() {
   }

   public Profile getProfile(GatewayUser gatewayUser) {
      return this.get(gatewayUser.getId());
   }
}
