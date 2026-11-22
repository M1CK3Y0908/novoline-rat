package client.m1ck3y.novoline.client.discord;

import java.util.Collection;
import client.m1ck3y.novoline.util.Profile;

public class PersistenceProfileLoadedEvent implements GatewayEvent {
   private final Collection<Profile> collection;

   public Collection<Profile> getCollection() {
      return this.collection;
   }

   public static PersistenceProfileLoadedEvent createPersistenceProfileLoadedEvent(Collection<Profile> collection2) {
      return new PersistenceProfileLoadedEvent(collection2);
   }

   private PersistenceProfileLoadedEvent(Collection<Profile> collection2) {
      this.collection = collection2;
   }
}
