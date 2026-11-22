package client.m1ck3y.novoline.script.adapter;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import client.m1ck3y.novoline.event.BlockCollisionEvent;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public class BlockCollisionEventTypeAdapter extends EventTypeAdapter<BlockCollisionEvent> {
   public void putEvent(ScriptValue scriptValue, BlockCollisionEvent blockCollisionEvent) {
      scriptValue.putObject("boundingBox", blockCollisionEvent.axisAlignedBB, AxisAlignedBB.class);
      scriptValue.putObject("pos", blockCollisionEvent.blockPos, BlockPos.class);
      scriptValue.putObject("state", blockCollisionEvent.iBlockState, IBlockState.class);
   }

   public BlockCollisionEvent apply(ScriptValue scriptValue, BlockCollisionEvent blockCollisionEvent) {
      blockCollisionEvent.axisAlignedBB = (AxisAlignedBB)scriptValue.getScriptValue("boundingBox").getJavaObject();
      blockCollisionEvent.blockPos = (BlockPos)scriptValue.getScriptValue("pos").getJavaObject();
      blockCollisionEvent.iBlockState = (IBlockState)scriptValue.getScriptValue("state").getJavaObject();
      return blockCollisionEvent;
   }

   public BlockCollisionEventTypeAdapter(TypeRegistry typeRegistry) {
      super("BlockCollisionEvent", typeRegistry);
   }
}
