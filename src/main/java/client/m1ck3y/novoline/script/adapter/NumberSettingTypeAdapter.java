package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;
import client.m1ck3y.novoline.setting.NumberSetting;

public final class NumberSettingTypeAdapter extends AbstractTypeAdapter<NumberSetting> {
   public ScriptValue put(ScriptValue scriptValue, NumberSetting numberSetting) {
      scriptValue.putSupplier("getMinValue", numberSetting::getMinValue);
      scriptValue.putSupplier("getMaxValue", numberSetting::getMaxValue);
      scriptValue.putSupplier("getCurrentValue", numberSetting::getCurrentValue);
      scriptValue.putSupplier("getDefaultState", numberSetting::getDefaultState);
      scriptValue.putConsumer("setCurrentValue", numberSetting::setCurrentValue);
      scriptValue.putConsumer("setCurrentValueIgnoreNormalize", numberSetting::setCurrentValueIgnoreNormalize);
      scriptValue.putSupplier("getUnit", numberSetting::getUnit);
      scriptValue.putEnum("type", numberSetting.getType());
      return scriptValue;
   }

   public NumberSettingTypeAdapter(TypeRegistry typeRegistry) {
      super("SliderSetting", typeRegistry);
   }
}
