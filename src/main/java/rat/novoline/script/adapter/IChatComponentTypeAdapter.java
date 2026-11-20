package rat.novoline.script.adapter;

import net.minecraft.util.IChatComponent;
import rat.novoline.script.AbstractTypeAdapter;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;

public class IChatComponentTypeAdapter extends AbstractTypeAdapter<IChatComponent> {
   public IChatComponentTypeAdapter(TypeRegistry typeRegistry) {
      super("ChatComponent", typeRegistry);
   }

   public ScriptValue put(ScriptValue scriptValue, IChatComponent iChatComponent) {
      scriptValue.putSupplier("getFormattedText", iChatComponent::getFormattedText);
      scriptValue.putSupplier("getUnformattedText", iChatComponent::getUnformattedText);
      scriptValue.putSupplier("getUnformattedTextForChat", iChatComponent::getUnformattedTextForChat);
      return scriptValue;
   }
}
