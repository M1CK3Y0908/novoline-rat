package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.event.GameEndEvent;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public class EventTypeAdapter11 extends EventTypeAdapter<GameEndEvent> {
   public EventTypeAdapter11(TypeRegistry typeRegistry) {
      super("GameEndEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, GameEndEvent gameEndEvent) {
      scriptValue.put("gameType", gameEndEvent.gameType);
      scriptValue.put("win", gameEndEvent.win);
   }
}
