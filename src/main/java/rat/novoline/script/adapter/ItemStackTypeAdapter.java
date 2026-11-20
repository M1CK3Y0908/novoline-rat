package rat.novoline.script.adapter;

import net.minecraft.item.ItemStack;
import rat.novoline.script.AbstractTypeAdapter;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public class ItemStackTypeAdapter extends AbstractTypeAdapter<ItemStack> {
   public ScriptValue put(ScriptValue scriptValue, ItemStack itemStack) {
      scriptValue.putSupplier("getDisplayName", itemStack::getDisplayName);
      scriptValue.putSupplier("getUnlocalizedName", itemStack::getUnlocalizedName);
      return scriptValue;
   }

   public ItemStackTypeAdapter(TypeRegistry typeRegistry) {
      super("ItemStack", typeRegistry);
   }
}
