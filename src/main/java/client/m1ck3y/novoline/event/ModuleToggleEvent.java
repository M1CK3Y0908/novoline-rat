package client.m1ck3y.novoline.event;

import client.m1ck3y.novoline.features.module.Module;

public class ModuleToggleEvent extends Event {
   public Module module;
   public ModuleToggleEvent.EnabledType enabledType;

   public enum EnabledType {
      ENABLED,
      DISABLED;
   }
}
