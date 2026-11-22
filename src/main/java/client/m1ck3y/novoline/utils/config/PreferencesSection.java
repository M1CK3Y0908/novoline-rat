package client.m1ck3y.novoline.utils.config;

import com.google.gson.JsonObject;
import client.m1ck3y.novoline.Novoline;
import client.m1ck3y.novoline.utils.ClientPreferences;

public class PreferencesSection extends ConfigSection {
   @Override
   public JsonObject serialize() {
      ClientPreferences prefs = Novoline.getNovoline().getClientPreferences();
      if (prefs == null) {
         return new JsonObject();
      }
      com.google.gson.JsonElement tree = gson.toJsonTree(prefs);
      return tree.isJsonObject() ? tree.getAsJsonObject() : new JsonObject();
   }

   @Override
   public void deserialize(JsonObject jsonObject) {
      ClientPreferences clientPreferences = gson.fromJson(jsonObject, ClientPreferences.class);
      Novoline.getNovoline().setClientPreferences(clientPreferences);
   }

   public PreferencesSection() {
      super("prefs");
   }
}
