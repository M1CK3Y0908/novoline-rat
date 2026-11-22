package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.event.GamemodeUpdateEvent;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public class GamemodeUpdateEventTypeAdapter extends EventTypeAdapter<GamemodeUpdateEvent> {
   public GamemodeUpdateEventTypeAdapter(TypeRegistry typeRegistry) {
      super("GamemodeUpdateEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, GamemodeUpdateEvent gamemodeUpdateEvent) {
      scriptValue.put("newGamemode", gamemodeUpdateEvent.hypixelGameType);
   }
}
