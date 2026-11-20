package rat.novoline.script.adapter;

import rat.novoline.script.AbstractTypeAdapter;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;
import rat.novoline.setting.CheckboxSetting;

public final class CheckboxSettingTypeAdapter extends AbstractTypeAdapter<CheckboxSetting> {
   public ScriptValue put(ScriptValue scriptValue, CheckboxSetting checkboxSetting) {
      scriptValue.putRunnable("toggle", checkboxSetting::toggle);
      scriptValue.putSupplier("isEnabled", checkboxSetting::isEnabled);
      scriptValue.putConsumer("setEnabled", checkboxSetting::setEnabled);
      scriptValue.putSupplier("isDefaultState", checkboxSetting::isDefaultState);
      return scriptValue;
   }

   public CheckboxSettingTypeAdapter(TypeRegistry typeRegistry) {
      super("CheckboxSetting", typeRegistry);
   }
}
