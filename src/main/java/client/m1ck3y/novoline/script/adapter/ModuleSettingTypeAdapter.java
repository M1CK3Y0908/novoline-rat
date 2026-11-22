package client.m1ck3y.novoline.script.adapter;

import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.script.AbstractTypeAdapter;
import client.m1ck3y.novoline.script.MappingUtils;
import client.m1ck3y.novoline.script.ScriptType;
import client.m1ck3y.novoline.script.ScriptValue;
import client.m1ck3y.novoline.script.TypeRegistry;
import client.m1ck3y.novoline.setting.CheckboxSetting;
import client.m1ck3y.novoline.setting.ColorSetting;
import client.m1ck3y.novoline.setting.ComboSetting;
import client.m1ck3y.novoline.setting.ModuleSetting;
import client.m1ck3y.novoline.setting.MultiComboSetting;
import client.m1ck3y.novoline.setting.NumberSetting;

public final class ModuleSettingTypeAdapter extends AbstractTypeAdapter<ModuleSetting> {
   @Override
   public void registerSubAdapters() {
      this.typeRegistry.put(CheckboxSetting.class, new CheckboxSettingTypeAdapter(this.typeRegistry));
      this.typeRegistry.put(ComboSetting.class, new ComboSettingTypeAdapter(this.typeRegistry));
      this.typeRegistry.put(NumberSetting.class, new NumberSettingTypeAdapter(this.typeRegistry));
      this.typeRegistry.put(MultiComboSetting.class, new MultiComboSettingTypeAdapter(this.typeRegistry));
      this.typeRegistry.put(ColorSetting.class, new ColorSettingTypeAdapter(this.typeRegistry));
   }

   public ModuleSettingTypeAdapter(TypeRegistry typeRegistry2) {
      super("FeatureSetting", typeRegistry2);
      this.addSubTypes(
         ScriptType.createScriptType("CheckboxSetting", CheckboxSetting.class),
         ScriptType.createScriptType("ComboSetting", ComboSetting.class),
         ScriptType.createScriptType("SliderSetting", NumberSetting.class),
         ScriptType.createScriptType("MultiComboSetting", MultiComboSetting.class),
         ScriptType.createScriptType("ColorSetting", ColorSetting.class)
      );
   }

   public ScriptValue put(ScriptValue scriptValue, ModuleSetting moduleSetting) {
      scriptValue.putEnum("featureSettingType", moduleSetting.getCheckboxType());
      scriptValue.putSupplier("getReference", MappingUtils.mappingSupplier(Module.class, moduleSetting::getModule));
      scriptValue.putSupplier("shouldShow", moduleSetting::shouldShow);
      scriptValue.putRunnable("interactCallback", moduleSetting::interactCallback);
      return scriptValue;
   }
}
