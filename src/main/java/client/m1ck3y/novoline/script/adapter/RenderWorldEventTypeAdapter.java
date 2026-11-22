package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.event.RenderWorldEvent;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public class RenderWorldEventTypeAdapter extends EventTypeAdapter<RenderWorldEvent> {
   protected void putEvent(ScriptValue scriptValue, RenderWorldEvent renderWorldEvent) {
      scriptValue.put("partialTicks", renderWorldEvent.partialTicks);
   }

   public RenderWorldEventTypeAdapter(TypeRegistry typeRegistry) {
      super("RenderWorldEvent", typeRegistry);
   }
}
