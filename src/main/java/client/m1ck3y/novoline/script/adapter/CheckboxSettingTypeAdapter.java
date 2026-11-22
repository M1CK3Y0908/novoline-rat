package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;
import client.m1ck3y.novoline.setting.CheckboxSetting;

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
