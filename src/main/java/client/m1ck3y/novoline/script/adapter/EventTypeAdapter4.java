package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.event.ServerConnectEvent;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public class EventTypeAdapter4 extends EventTypeAdapter<ServerConnectEvent> {
   public EventTypeAdapter4(TypeRegistry typeRegistry) {
      super("ServerConnectEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, ServerConnectEvent serverConnectEvent) {
      scriptValue.put("connectingHostname", serverConnectEvent.connectingHostname);
      scriptValue.put("connectingIp", serverConnectEvent.connectingIp);
   }
}
