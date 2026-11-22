package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.event.Event;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public class EventTypeAdapter12<T extends Event> extends EventTypeAdapter<T> {
   @Override
   protected void putEvent(ScriptValue scriptValue, Event event2) {
   }

   public EventTypeAdapter12(String text, TypeRegistry typeRegistry) {
      super(text, typeRegistry);
   }
}
