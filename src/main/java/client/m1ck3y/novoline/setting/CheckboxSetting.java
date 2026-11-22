package client.m1ck3y.novoline.setting;

import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.setting.ModuleSetting.CheckboxType;

public class CheckboxSetting extends ModuleSetting {
   private boolean enabled;
   private final boolean defaultState;

   public CheckboxSetting(Module module2, String text, boolean flag) {
      super(module2, text, "checkbox", CheckboxType.CHECKBOX);
      this.enabled = flag;
      this.defaultState = flag;
   }

   public void toggle() {
      this.setEnabled(!this.isEnabled());
   }

   public void setEnabled(boolean flag) {
      this.enabled = flag;
      this.interactCallback();
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean isDefaultState() {
      return this.defaultState;
   }
}
