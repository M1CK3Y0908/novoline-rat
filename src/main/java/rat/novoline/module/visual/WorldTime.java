package rat.novoline.module.visual;

import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.NumberSetting;

public class WorldTime extends Module {
   public final NumberSetting worldTime = settingManager.register(new NumberSetting(this, "Time", 12, 0, 24));

   public WorldTime() {
      super(Category.VISUAL, "WorldTime", "");
   }
}
