package client.m1ck3y.novoline.script.adapter;

import net.minecraft.client.gui.ScaledResolution;
import client.m1ck3y.novoline.event.RenderScreenEvent;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public class EventTypeAdapter10 extends EventTypeAdapter<RenderScreenEvent> {
   public EventTypeAdapter10(TypeRegistry typeRegistry) {
      super("RenderScreenEvent", typeRegistry);
   }

   protected void putEvent(ScriptValue scriptValue, RenderScreenEvent renderScreenEvent) {
      scriptValue.put("partialTicks", renderScreenEvent.partialTicks);
      scriptValue.putObject("scaledResolution", renderScreenEvent.scaledResolution, ScaledResolution.class);
   }
}
