package rat.novoline.script.adapter;

import rat.novoline.module.Module;
import rat.novoline.script.AbstractTypeAdapter;
import rat.novoline.script.MappingUtils;
import rat.novoline.script.ScriptType;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ColorSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.ModuleSetting;
import rat.novoline.setting.MultiComboSetting;
import rat.novoline.setting.NumberSetting;

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
