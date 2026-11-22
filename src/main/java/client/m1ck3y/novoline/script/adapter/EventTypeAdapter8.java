package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.event.DeviceButtonDownEvent;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public class EventTypeAdapter8 extends EventTypeAdapter<DeviceButtonDownEvent> {
   public void putEvent(ScriptValue scriptValue, DeviceButtonDownEvent deviceButtonDownEvent) {
      scriptValue.put("keyId", deviceButtonDownEvent.keyId);
      scriptValue.putEnum("device", deviceButtonDownEvent.mouseType);
   }

   public EventTypeAdapter8(TypeRegistry typeRegistry) {
      super("DeviceButtonDownEvent", typeRegistry);
   }
}
