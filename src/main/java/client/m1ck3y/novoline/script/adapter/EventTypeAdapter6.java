package client.m1ck3y.novoline.script.adapter;

import net.minecraft.client.gui.ScaledResolution;
import client.m1ck3y.novoline.event.RenderGameOverlayEvent;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public final class EventTypeAdapter6 extends EventTypeAdapter<RenderGameOverlayEvent> {
   public EventTypeAdapter6() {
      super((String) null, (TypeRegistry) null);
   }

   protected void putEvent(ScriptValue scriptValue, RenderGameOverlayEvent renderGameOverlayEvent) {
      scriptValue.put("partialTicks", renderGameOverlayEvent.partialTicks);
      scriptValue.putObject("scaledResolution", renderGameOverlayEvent.scaledResolution, ScaledResolution.class);
   }
}
