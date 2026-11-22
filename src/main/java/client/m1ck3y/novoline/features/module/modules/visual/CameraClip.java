package client.m1ck3y.novoline.features.module.modules.visual;

import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.setting.CheckboxSetting;
import client.m1ck3y.novoline.setting.NumberSetting;

public class CameraClip extends Module {
   public final CheckboxSetting cameraClip = settingManager.register(new CheckboxSetting(this, "Override distance", false));
   public final NumberSetting distance = settingManager.register(new NumberSetting(this, "Distance", 10, 1, 100));

   public CameraClip() {
      super(Category.VISUAL, "CameraClip", "");
   }
}
