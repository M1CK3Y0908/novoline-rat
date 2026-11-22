package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;
import client.m1ck3y.novoline.setting.ComboSetting;

public final class ComboSettingTypeAdapter extends AbstractTypeAdapter<ComboSetting> {
   public ScriptValue put(ScriptValue scriptValue, ComboSetting comboSetting) {
      scriptValue.putSupplier("getElements", comboSetting::getElements);
      scriptValue.putSupplier("getCurrentElement", comboSetting::getCurrentElement);
      scriptValue.putSupplier("getCurrentElementAsString", comboSetting::getCurrentElementAsString);
      scriptValue.putSupplier("getDefaultState", comboSetting::getDefaultState);
      scriptValue.putConsumer("setCurrentElement", comboSetting::setCurrentElement);
      scriptValue.putConsumer("setCurrentElementByName", comboSetting::setCurrentElementByName);
      scriptValue.putSupplier("isExtended", comboSetting::isExtended);
      scriptValue.putConsumer("setExtended", comboSetting::setExtended);
      return scriptValue;
   }

   public ComboSettingTypeAdapter(TypeRegistry typeRegistry) {
      super("ComboSetting", typeRegistry);
   }
}
