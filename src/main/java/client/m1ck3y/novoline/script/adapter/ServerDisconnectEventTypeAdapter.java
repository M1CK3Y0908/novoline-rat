package client.m1ck3y.novoline.script.adapter;

import net.minecraft.util.IChatComponent;
import client.m1ck3y.novoline.event.ServerDisconnectEvent;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public class ServerDisconnectEventTypeAdapter extends EventTypeAdapter<ServerDisconnectEvent> {
   public ServerDisconnectEventTypeAdapter(TypeRegistry typeRegistry) {
      super("ServerDisconnectEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, ServerDisconnectEvent serverDisconnectEvent) {
      scriptValue.putObject("message", serverDisconnectEvent.getMessage(), IChatComponent.class);
   }
}
