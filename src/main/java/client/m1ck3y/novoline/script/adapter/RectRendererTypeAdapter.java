package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;
import client.m1ck3y.novoline.utils.render.shape.RectRenderer;

public class RectRendererTypeAdapter extends AbstractTypeAdapter<RectRenderer> {
   public RectRendererTypeAdapter(TypeRegistry typeRegistry) {
      super("RectRenderer", typeRegistry);
   }

   public ScriptValue put(ScriptValue scriptValue, RectRenderer rectRenderer) {
      scriptValue.putConsumer9(
         "putRect",
         (Double d, Double d2, Double d3, Double d4, Double d5, Double d6, Double d7, Double d8, Integer integer) -> rectRenderer.putRect(
            d.floatValue(),
            d2.floatValue(),
            d3.floatValue(),
            d4.floatValue(),
            d5.floatValue(),
            d6.floatValue(),
            d7.floatValue(),
            d8.floatValue(),
            integer
         )
      );
      scriptValue.putRunnable("draw", rectRenderer::draw);
      return scriptValue;
   }
}
