package rat.novoline.script.adapter;

import rat.novoline.event.RenderWorldEvent;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public class RenderWorldEventTypeAdapter extends EventTypeAdapter<RenderWorldEvent> {
   protected void putEvent(ScriptValue scriptValue, RenderWorldEvent renderWorldEvent) {
      scriptValue.put("partialTicks", renderWorldEvent.partialTicks);
   }

   public RenderWorldEventTypeAdapter(TypeRegistry typeRegistry) {
      super("RenderWorldEvent", typeRegistry);
   }
}
