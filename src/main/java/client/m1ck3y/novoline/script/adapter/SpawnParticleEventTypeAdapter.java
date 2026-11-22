package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.event.SpawnParticleEvent;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

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
