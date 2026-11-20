package rat.novoline.script.adapter;

import rat.novoline.event.SpawnParticleEvent;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public class SpawnParticleEventTypeAdapter extends EventTypeAdapter<SpawnParticleEvent> {
   public SpawnParticleEventTypeAdapter() {
      super((String) null, (TypeRegistry) null);
   }

   public SpawnParticleEvent apply(ScriptValue scriptValue, SpawnParticleEvent spawnParticleEvent) {
      spawnParticleEvent.id = scriptValue.getInt("id");
      return spawnParticleEvent;
   }

   protected void putEvent(ScriptValue scriptValue, SpawnParticleEvent spawnParticleEvent) {
      scriptValue.put("id", spawnParticleEvent.id);
   }
}
