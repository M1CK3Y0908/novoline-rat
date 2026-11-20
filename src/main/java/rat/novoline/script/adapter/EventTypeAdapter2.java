package rat.novoline.script.adapter;

import rat.novoline.event.GameStartEvent;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public class EventTypeAdapter2 extends EventTypeAdapter<GameStartEvent> {
   protected void putEvent(ScriptValue scriptValue, GameStartEvent gameStartEvent) {
      scriptValue.putEnum("gamemode", gameStartEvent.hypixelGameType);
   }

   public EventTypeAdapter2(TypeRegistry typeRegistry) {
      super("GameStartEvent", typeRegistry);
   }
}
