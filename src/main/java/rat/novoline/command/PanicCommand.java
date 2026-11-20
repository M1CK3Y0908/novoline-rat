package rat.novoline.command;

import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;

public class PanicCommand extends Command {
   @Override
   public void execute(String[] texts) {
      ModuleManager.modules.forEach(Module::disable);
   }

   public PanicCommand() {
      super("Disables all features", "panic", "p");
   }
}
