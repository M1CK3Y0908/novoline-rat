package rat.novoline.script.adapter;

import rat.novoline.event.Event;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public class EventTypeAdapter12<T extends Event> extends EventTypeAdapter<T> {
   @Override
   protected void putEvent(ScriptValue scriptValue, Event event2) {
   }

   public EventTypeAdapter12(String text, TypeRegistry typeRegistry) {
      super(text, typeRegistry);
   }
}
