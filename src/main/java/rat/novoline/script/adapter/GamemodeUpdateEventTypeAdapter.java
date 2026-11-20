package rat.novoline.script.adapter;

import rat.novoline.event.GamemodeUpdateEvent;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public class GamemodeUpdateEventTypeAdapter extends EventTypeAdapter<GamemodeUpdateEvent> {
   public GamemodeUpdateEventTypeAdapter(TypeRegistry typeRegistry) {
      super("GamemodeUpdateEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, GamemodeUpdateEvent gamemodeUpdateEvent) {
      scriptValue.put("newGamemode", gamemodeUpdateEvent.hypixelGameType);
   }
}
