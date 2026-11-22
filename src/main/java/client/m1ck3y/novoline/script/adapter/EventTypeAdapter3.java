package client.m1ck3y.novoline.script.adapter;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import client.m1ck3y.novoline.event.RenderBlockEvent;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

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
