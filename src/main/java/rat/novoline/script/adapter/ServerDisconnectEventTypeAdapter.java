package rat.novoline.script.adapter;

import net.minecraft.util.IChatComponent;
import rat.novoline.event.ServerDisconnectEvent;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public class ServerDisconnectEventTypeAdapter extends EventTypeAdapter<ServerDisconnectEvent> {
   public ServerDisconnectEventTypeAdapter(TypeRegistry typeRegistry) {
      super("ServerDisconnectEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, ServerDisconnectEvent serverDisconnectEvent) {
      scriptValue.putObject("message", serverDisconnectEvent.getMessage(), IChatComponent.class);
   }
}
