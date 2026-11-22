package client.m1ck3y.novoline.script.adapter;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.MappingUtils;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

public class IBlockStateTypeAdapter extends AbstractTypeAdapter<IBlockState> {
   public IBlockStateTypeAdapter(TypeRegistry typeRegistry) {
      super("IBlockState", typeRegistry);
   }

   public ScriptValue put(ScriptValue scriptValue, IBlockState iBlockState) {
      scriptValue.putSupplier("getBlock", MappingUtils.mappingSupplier(Block.class, iBlockState::getBlock));
      return scriptValue;
   }
}
