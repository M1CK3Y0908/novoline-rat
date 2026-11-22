package client.m1ck3y.novoline.features.module.modules.visual;

import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.setting.CheckboxSetting;

public class NoRender extends Module {
   public final CheckboxSetting noRender = settingManager.register(new CheckboxSetting(this, "Pumpkin", true));
   public final CheckboxSetting bossBar = settingManager.register(new CheckboxSetting(this, "Boss bar", false));
   public final CheckboxSetting title = settingManager.register(new CheckboxSetting(this, "Title", false));
   public final CheckboxSetting fire = settingManager.register(new CheckboxSetting(this, "Fire", true));
   public final CheckboxSetting scoreboard = settingManager.register(new CheckboxSetting(this, "Scoreboard", false));
   public final CheckboxSetting armor = settingManager.register(new CheckboxSetting(this, "Armor", false));

   public NoRender() {
      super(Category.VISUAL, "NoRender", "");
   }
}
