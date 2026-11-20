package rat.novoline.api;

import rat.novoline.Novoline;
import rat.novoline.module.Module;
import rat.novoline.scripting.ScriptEventHandler;
import rat.novoline.setting.Setting;

public interface Updatable {
   void register();

   void unregister();

   record EventHandlerRegistration(ScriptEventHandler scriptEventHandler) implements Updatable {
      public ScriptEventHandler getScriptEventHandler() {
         return this.scriptEventHandler;
      }

      @Override
      public void unregister() {
         Novoline.getNovoline().getScriptManager().getScriptContext().getScriptEventHandlerList().remove(this.scriptEventHandler);
      }

      @Override
      public void register() {
         Novoline.getNovoline().getScriptManager().getScriptContext().getScriptEventHandlerList().add(this.scriptEventHandler);
      }
   }

   record ModuleRegistration(Module module) implements Updatable {
      public Module getModule() {
         return this.module;
      }

      @Override
      public void register() {
         Novoline novoline2 = Novoline.getNovoline();
         novoline2.getModuleManager().updateCachedFeatures();
         novoline2.getModuleManager().register(this.module);
         if (novoline2.getMenuClickGuiScreen() != null) {
            novoline2.getMenuClickGuiScreen().rebuildPanels();
            novoline2.getMenuClickGuiScreen().initGui();
         }
      }

      @Override
      public void unregister() {
         Novoline novoline2 = Novoline.getNovoline();
         novoline2.getModuleManager().unregister(this.module);
         if (novoline2.getMenuClickGuiScreen() != null) {
            novoline2.getMenuClickGuiScreen().rebuildPanels();
            novoline2.getMenuClickGuiScreen().initGui();
         }
      }
   }

   record SettingRegistration(Setting setting) implements Updatable {
      @Override
      public void register() {
         Novoline.getNovoline().getSettingManager().register(this.setting);
      }

      @Override
      public void unregister() {
         Novoline.getNovoline().getSettingManager().unregister(this.setting);
      }

      public Setting getSetting() {
         return this.setting;
      }
   }
}
