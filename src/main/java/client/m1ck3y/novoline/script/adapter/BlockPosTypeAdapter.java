package client.m1ck3y.novoline.script.adapter;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.MappingUtils;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public class BlockPosTypeAdapter extends AbstractTypeAdapter<BlockPos> {
   public ScriptValue put(ScriptValue scriptValue, BlockPos blockPos) {
      scriptValue.putBiFunction("offset", MappingUtils.mo2mMappingFunction(EnumFacing.class, Integer.class, BlockPos.class, blockPos::offset));
      scriptValue.putFunction("add", MappingUtils.m2mMappingFunction(BlockPos.class, BlockPos.class, blockPos::add));
      scriptValue.putFunction("subtract", MappingUtils.m2mMappingFunction(BlockPos.class, BlockPos.class, blockPos::subtract));
      scriptValue.putSupplier("toLong", blockPos::toLong);
      return scriptValue;
   }

   public BlockPosTypeAdapter(TypeRegistry typeRegistry) {
      super("BlockPos", typeRegistry);
   }
}
