package rat.novoline.script.adapter;

import rat.novoline.script.AbstractTypeAdapter;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;
import rat.novoline.setting.ColorSetting;

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
