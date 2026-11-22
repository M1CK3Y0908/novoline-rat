package client.m1ck3y.novoline.utils.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import client.m1ck3y.novoline.Novoline;
import client.m1ck3y.novoline.features.module.modules.hud.HudElement;
import client.m1ck3y.novoline.features.module.modules.hud.HudManager;

public class OverlaySection extends ConfigSection {
   private final HudManager hudManager = Novoline.getNovoline().getHudManager();

   public OverlaySection() {
      super("overlay");
   }

   @Override
   public JsonObject serialize() {
      JsonObject jsonObject = new JsonObject();

      for (HudElement hudElement : this.hudManager.getElements()) {
         JsonObject jsonObject2 = new JsonObject();
         jsonObject2.addProperty("x", hudElement.getLeft());
         jsonObject2.addProperty("y", hudElement.getTop());
         jsonObject.add(hudElement.getName(), jsonObject2);
      }

      return jsonObject;
   }

   @Override
   public void deserialize(JsonObject jsonObject) {
      if (jsonObject.size() > 0) {
         for (java.util.Map.Entry entry : jsonObject.entrySet()) {
            JsonObject asJsonObject = ((JsonElement)entry.getValue()).getAsJsonObject();
            HudElement hudElement = this.hudManager.getHudElement((String)entry.getKey());
            if (hudElement != null && hudElement.isDraggable()) {
               hudElement.setX(asJsonObject.get("x").getAsInt());
               hudElement.setY(asJsonObject.get("y").getAsInt());
            }
         }
      }
   }
}
