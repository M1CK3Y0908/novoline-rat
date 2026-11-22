package client.m1ck3y.novoline.features.module.modules.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import client.m1ck3y.novoline.event.PreUpdateEvent;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.setting.NumberSetting;

public class Timer extends Module {
   private final NumberSetting timer = settingManager.register(new NumberSetting(this, "Value", 1.5F, 0.01F, 10.0F));
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      this.setSuffix(String.valueOf(this.timer.getCurrentValue()));
      minecraft.timer.timerSpeed = this.timer.getCurrentValue();
   };

   @Override
   protected void onToggled() {
      minecraft.timer.timerSpeed = 1.0F;
   }

   public Timer() {
      super(Category.PLAYER, "Timer", "");
   }
}
