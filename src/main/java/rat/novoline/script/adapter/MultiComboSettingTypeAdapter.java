package rat.novoline.script.adapter;

import rat.novoline.script.AbstractTypeAdapter;
import rat.novoline.script.MappingUtils;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;
import rat.novoline.script.adapter.SettingTypeAdapter.AbstractTypeAdapterChild;
import rat.novoline.setting.MultiComboSetting;
import rat.novoline.setting.MultiComboSetting.Option;

public final class MultiComboSettingTypeAdapter extends AbstractTypeAdapter<MultiComboSetting> {
   public ScriptValue put(ScriptValue scriptValue, MultiComboSetting multiComboSetting) {
      scriptValue.putBiConsumer("add", multiComboSetting::add);
      scriptValue.putFunction("isSelected", (Integer i) -> multiComboSetting.isSelected(i));
      scriptValue.putFunction("byName", MappingUtils.o2mMappingFunction(Option.class, multiComboSetting::byName));
      scriptValue.putSupplier("getElements", MappingUtils.mappingListSupplier(Option.class, multiComboSetting::getElements));
      return scriptValue;
   }

   @Override
   public void registerSubAdapters() {
      this.typeRegistry.put(Option.class, new AbstractTypeAdapterChild(this.typeRegistry));
   }

   public MultiComboSettingTypeAdapter(TypeRegistry typeRegistry2) {
      super("MultiComboSetting", typeRegistry2);
   }
}
