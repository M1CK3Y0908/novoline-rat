package client.m1ck3y.novoline.script.adapter;

import net.minecraft.client.gui.ScaledResolution;
import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public class ScaledResolutionTypeAdapter extends AbstractTypeAdapter<ScaledResolution> {
   public ScaledResolutionTypeAdapter(TypeRegistry typeRegistry) {
      super("ScaledResolution", typeRegistry);
   }

   public ScriptValue put(ScriptValue scriptValue, ScaledResolution scaledResolution) {
      scriptValue.putSupplier("getScaleFactor", scaledResolution::getScaleFactor);
      scriptValue.putSupplier("getScaledWidth", scaledResolution::getScaledWidth);
      scriptValue.putSupplier("getScaledHeight", scaledResolution::getScaledHeight);
      scriptValue.putSupplier("getScaledWidth_double", scaledResolution::getScaledWidth_double);
      scriptValue.putSupplier("getScaledHeight_double", scaledResolution::getScaledHeight_double);
      return scriptValue;
   }
}
