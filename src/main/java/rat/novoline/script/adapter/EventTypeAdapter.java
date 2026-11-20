package rat.novoline.script.adapter;

import rat.novoline.event.Event;
import rat.novoline.script.AbstractTypeAdapter;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public abstract class EventTypeAdapter<T extends Event> extends AbstractTypeAdapter<T> {
   protected abstract void putEvent(ScriptValue scriptValue, T value);

   public EventTypeAdapter(String text, TypeRegistry typeRegistry) {
      super(text, typeRegistry);
   }

   public ScriptValue put(ScriptValue scriptValue, Event event2) {
      scriptValue.putRunnable("cancel", event2::cancel);
      scriptValue.putSupplier("isCancelled", event2::isCancelled);
      this.putEvent(scriptValue, (T)event2);
      return scriptValue;
   }
}
