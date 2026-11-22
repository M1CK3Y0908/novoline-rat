package client.m1ck3y.novoline.script.adapter;

import net.minecraft.entity.player.EntityPlayer;
import client.m1ck3y.novoline.event.PlayerKillEvent;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public class EventTypeAdapter9 extends EventTypeAdapter<PlayerKillEvent> {
   public EventTypeAdapter9(TypeRegistry typeRegistry) {
      super("PlayerKillEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, PlayerKillEvent playerKillEvent) {
      scriptValue.putObject("entity", playerKillEvent.entityPlayer, EntityPlayer.class);
   }
}
