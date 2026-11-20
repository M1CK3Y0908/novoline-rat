package rat.novoline.module.visual;

import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.NumberSetting;

public class CameraClip extends Module {
   public final CheckboxSetting cameraClip = settingManager.register(new CheckboxSetting(this, "Override distance", false));
   public final NumberSetting distance = settingManager.register(new NumberSetting(this, "Distance", 10, 1, 100));

   public CameraClip() {
      super(Category.VISUAL, "CameraClip", "");
   }
}
