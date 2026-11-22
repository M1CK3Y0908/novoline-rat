package client.m1ck3y.novoline.script.adapter;

import net.minecraft.item.ItemStack;
import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

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
