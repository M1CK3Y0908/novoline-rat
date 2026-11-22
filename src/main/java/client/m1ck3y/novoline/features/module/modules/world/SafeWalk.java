package client.m1ck3y.novoline.features.module.modules.world;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import client.m1ck3y.novoline.event.MoveEntityEvent;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.setting.CheckboxSetting;

public class SafeWalk extends Module {
   private final CheckboxSetting safeWalk = settingManager.register(new CheckboxSetting(this, "Only on ground", true));
   @EventLink
   private final Listener<MoveEntityEvent> onMoveEntity = moveEntityEvent -> {
      if (!this.safeWalk.isEnabled() || minecraft.thePlayer.onGround) {
         moveEntityEvent.cancel();
      }
   };

   public SafeWalk() {
      super(Category.WORLD, "SafeWalk", "Prevents you from falling from block edges");
   }
}
