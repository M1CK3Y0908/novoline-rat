package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;
import client.m1ck3y.novoline.setting.ColorSetting;

public final class ColorSettingTypeAdapter extends AbstractTypeAdapter<ColorSetting> {
   public ColorSettingTypeAdapter(TypeRegistry typeRegistry) {
      super("ColorSetting", typeRegistry);
   }

   public ScriptValue put(ScriptValue scriptValue, ColorSetting colorSetting) {
      scriptValue.putConsumer("setColor", colorSetting::setColor);
      scriptValue.putSupplier("getColor", colorSetting::getColor);
      return scriptValue;
   }
}
