package rat.novoline.script.adapter;

import rat.novoline.event.GameEndEvent;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public class EventTypeAdapter11 extends EventTypeAdapter<GameEndEvent> {
   public EventTypeAdapter11(TypeRegistry typeRegistry) {
      super("GameEndEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, GameEndEvent gameEndEvent) {
      scriptValue.put("gameType", gameEndEvent.gameType);
      scriptValue.put("win", gameEndEvent.win);
   }
}
