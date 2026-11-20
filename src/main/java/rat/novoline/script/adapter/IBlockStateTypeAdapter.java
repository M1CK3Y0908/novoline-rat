package rat.novoline.script.adapter;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import rat.novoline.script.AbstractTypeAdapter;
import rat.novoline.script.MappingUtils;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public class IBlockStateTypeAdapter extends AbstractTypeAdapter<IBlockState> {
   public IBlockStateTypeAdapter(TypeRegistry typeRegistry) {
      super("IBlockState", typeRegistry);
   }

   public ScriptValue put(ScriptValue scriptValue, IBlockState iBlockState) {
      scriptValue.putSupplier("getBlock", MappingUtils.mappingSupplier(Block.class, iBlockState::getBlock));
      return scriptValue;
   }
}
