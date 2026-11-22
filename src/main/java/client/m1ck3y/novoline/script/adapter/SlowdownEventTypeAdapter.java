package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.event.SlowdownEvent;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public class SlowdownEventTypeAdapter extends EventTypeAdapter<SlowdownEvent> {
   public SlowdownEvent apply(ScriptValue scriptValue, SlowdownEvent slowdownEvent) {
      slowdownEvent.forwardMultiplier = scriptValue.getFloat("forwardMultiplier");
      slowdownEvent.strafeMultiplier = scriptValue.getFloat("strafeMultiplier");
      return slowdownEvent;
   }

   protected void putEvent(ScriptValue scriptValue, SlowdownEvent slowdownEvent) {
      scriptValue.put("forwardMultiplier", slowdownEvent.forwardMultiplier);
      scriptValue.put("strafeMultiplier", slowdownEvent.strafeMultiplier);
   }

   public SlowdownEventTypeAdapter(TypeRegistry typeRegistry) {
      super("SlowdownEvent", typeRegistry);
   }
}
