package rat.novoline.script.adapter;

import rat.novoline.event.ServerConnectEvent;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public class EventTypeAdapter4 extends EventTypeAdapter<ServerConnectEvent> {
   public EventTypeAdapter4(TypeRegistry typeRegistry) {
      super("ServerConnectEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, ServerConnectEvent serverConnectEvent) {
      scriptValue.put("connectingHostname", serverConnectEvent.connectingHostname);
      scriptValue.put("connectingIp", serverConnectEvent.connectingIp);
   }
}
