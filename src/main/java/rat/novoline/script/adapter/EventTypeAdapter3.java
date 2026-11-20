package rat.novoline.script.adapter;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import rat.novoline.event.RenderBlockEvent;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public class EventTypeAdapter3 extends EventTypeAdapter<RenderBlockEvent> {
   public EventTypeAdapter3(TypeRegistry typeRegistry) {
      super("RenderBlockEvent", typeRegistry);
   }

   public RenderBlockEvent apply(ScriptValue scriptValue, RenderBlockEvent renderBlockEvent) {
      renderBlockEvent.blockPos = (BlockPos)scriptValue.getScriptValue("pos").getJavaObject();
      renderBlockEvent.iBlockState = (IBlockState)scriptValue.getScriptValue("state").getJavaObject();
      renderBlockEvent.shouldRender = scriptValue.checkString("shouldRender");
      return renderBlockEvent;
   }

   protected void putEvent(ScriptValue scriptValue, RenderBlockEvent renderBlockEvent) {
      scriptValue.putObject("pos", renderBlockEvent.blockPos, BlockPos.class);
      scriptValue.putObject("state", renderBlockEvent.iBlockState, IBlockState.class);
      scriptValue.put("shouldRender", renderBlockEvent.shouldRender);
   }
}
