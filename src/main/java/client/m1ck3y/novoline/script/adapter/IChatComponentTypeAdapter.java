package client.m1ck3y.novoline.script.adapter;

import net.minecraft.util.IChatComponent;
import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;

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
