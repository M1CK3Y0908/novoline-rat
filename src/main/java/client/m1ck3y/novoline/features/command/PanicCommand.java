package client.m1ck3y.novoline.features.command;

import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.features.module.ModuleManager;

public class PanicCommand extends Command {
   @Override
   public void execute(String[] texts) {
      ModuleManager.modules.forEach(Module::disable);
   }

   public PanicCommand() {
      super("Disables all features", "panic", "p");
   }
}
