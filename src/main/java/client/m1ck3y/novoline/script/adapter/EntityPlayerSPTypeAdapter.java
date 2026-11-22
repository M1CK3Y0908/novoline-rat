package client.m1ck3y.novoline.script.adapter;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public final class EntityPlayerSPTypeAdapter extends AbstractTypeAdapter<EntityPlayerSP> {
   public EntityPlayerSP apply(ScriptValue scriptValue, EntityPlayerSP entityPlayerSP) {
      entityPlayerSP.movementInput = (MovementInput)scriptValue.getUnpacked("movementInput", MovementInput.class);
      return entityPlayerSP;
   }

   public ScriptValue put(ScriptValue scriptValue, EntityPlayerSP entityPlayerSP) {
      scriptValue.putObject("movementInput", entityPlayerSP.movementInput, MovementInput.class);
      return scriptValue;
   }

   public EntityPlayerSPTypeAdapter(TypeRegistry typeRegistry) {
      super("EntityPlayerSP", typeRegistry);
   }
}
