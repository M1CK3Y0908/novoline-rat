package rat.novoline.event;

import rat.novoline.module.Module;

public class ModuleToggleEvent extends Event {
   public Module module;
   public ModuleToggleEvent.EnabledType enabledType;

   public enum EnabledType {
      ENABLED,
      DISABLED;
   }
}
