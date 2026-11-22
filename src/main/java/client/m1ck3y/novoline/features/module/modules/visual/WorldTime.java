package client.m1ck3y.novoline.features.module.modules.visual;

import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.setting.NumberSetting;

public class WorldTime extends Module {
   public final NumberSetting worldTime = settingManager.register(new NumberSetting(this, "Time", 12, 0, 24));

   public WorldTime() {
      super(Category.VISUAL, "WorldTime", "");
   }
}
