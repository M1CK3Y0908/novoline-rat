package rat.novoline.script.adapter;

import rat.novoline.script.AbstractTypeAdapter;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;
import rat.novoline.setting.ComboSetting;

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
