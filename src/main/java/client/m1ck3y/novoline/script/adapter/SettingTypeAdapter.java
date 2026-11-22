package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.ScriptType;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;
import client.m1ck3y.novoline.setting.ModuleSetting;
import client.m1ck3y.novoline.setting.Setting;
import client.m1ck3y.novoline.setting.MultiComboSetting.Option;

public class SettingTypeAdapter extends AbstractTypeAdapter<Setting> {
   public ScriptValue put(ScriptValue scriptValue, Setting setting2) {
      scriptValue.putSupplier("getName", setting2::getName);
      scriptValue.putSupplier("getSignature", setting2::getSignature);
      scriptValue.putEnum("type", setting2.getSettingType());
      return scriptValue;
   }

   public SettingTypeAdapter(TypeRegistry typeRegistry2) {
      super("Setting", typeRegistry2);
      this.addSubTypes(ScriptType.createScriptType("FeatureSetting", ModuleSetting.class));
   }

   @Override
   public void registerSubAdapters() {
      this.typeRegistry.put(ModuleSetting.class, new ModuleSettingTypeAdapter(this.typeRegistry));
   }

   public static final class AbstractTypeAdapterChild extends AbstractTypeAdapter<Option> {
      public AbstractTypeAdapterChild(TypeRegistry typeRegistry2) {
         super("MultiComboSetting.Element", typeRegistry2);
      }

      public ScriptValue put(ScriptValue scriptValue, Option option) {
         scriptValue.putSupplier("isSelected", option::isSelected);
         scriptValue.putSupplier("isDefaultState", option::isDefaultState);
         scriptValue.putConsumer("setSelected", option::setSelected);
         scriptValue.putSupplier("getName", option::getName);
         scriptValue.putRunnable("toggleSelection", option::toggleSelection);
         return scriptValue;
      }
   }
}
