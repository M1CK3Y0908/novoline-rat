package rat.novoline.script.adapter;

import net.minecraft.entity.player.EntityPlayer;
import rat.novoline.event.PlayerKillEvent;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public class EventTypeAdapter9 extends EventTypeAdapter<PlayerKillEvent> {
   public EventTypeAdapter9(TypeRegistry typeRegistry) {
      super("PlayerKillEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, PlayerKillEvent playerKillEvent) {
      scriptValue.putObject("entity", playerKillEvent.entityPlayer, EntityPlayer.class);
   }
}
