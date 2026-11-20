package rat.novoline.script.adapter;

import rat.novoline.event.DeviceButtonDownEvent;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public class EventTypeAdapter8 extends EventTypeAdapter<DeviceButtonDownEvent> {
   public void putEvent(ScriptValue scriptValue, DeviceButtonDownEvent deviceButtonDownEvent) {
      scriptValue.put("keyId", deviceButtonDownEvent.keyId);
      scriptValue.putEnum("device", deviceButtonDownEvent.mouseType);
   }

   public EventTypeAdapter8(TypeRegistry typeRegistry) {
      super("DeviceButtonDownEvent", typeRegistry);
   }
}
