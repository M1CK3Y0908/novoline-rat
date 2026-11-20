package rat.novoline.script.adapter;

import net.minecraft.client.gui.ScaledResolution;
import rat.novoline.event.RenderGameOverlayEvent;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public final class EventTypeAdapter6 extends EventTypeAdapter<RenderGameOverlayEvent> {
   public EventTypeAdapter6() {
      super((String) null, (TypeRegistry) null);
   }

   protected void putEvent(ScriptValue scriptValue, RenderGameOverlayEvent renderGameOverlayEvent) {
      scriptValue.put("partialTicks", renderGameOverlayEvent.partialTicks);
      scriptValue.putObject("scaledResolution", renderGameOverlayEvent.scaledResolution, ScaledResolution.class);
   }
}
