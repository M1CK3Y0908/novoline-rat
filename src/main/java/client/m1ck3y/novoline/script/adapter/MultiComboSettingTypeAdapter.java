package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.MappingUtils;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;
import client.m1ck3y.novoline.script.adapter.SettingTypeAdapter.AbstractTypeAdapterChild;
import client.m1ck3y.novoline.setting.MultiComboSetting;
import client.m1ck3y.novoline.setting.MultiComboSetting.Option;

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
