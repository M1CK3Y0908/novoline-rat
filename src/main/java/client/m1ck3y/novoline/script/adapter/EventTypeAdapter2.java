package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.event.GameStartEvent;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public class EventTypeAdapter2 extends EventTypeAdapter<GameStartEvent> {
   protected void putEvent(ScriptValue scriptValue, GameStartEvent gameStartEvent) {
      scriptValue.putEnum("gamemode", gameStartEvent.hypixelGameType);
   }

   public EventTypeAdapter2(TypeRegistry typeRegistry) {
      super("GameStartEvent", typeRegistry);
   }
}
