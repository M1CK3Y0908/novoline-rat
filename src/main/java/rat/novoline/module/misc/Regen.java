package rat.novoline.module.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import rat.novoline.event.UpdateEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.player.MoveUtils;
import rat.novoline.utils.time.Stopwatch;

public class Regen extends Module {
   private final ComboSetting mode = settingManager.register(
      new ComboSetting(this, "Mode", 0, "Vanilla", "Spartan")
   );
   private final NumberSetting speed = settingManager.register(
      new NumberSetting(this, "Speed", 100, 1, 100) {
         @Override
         public boolean shouldShow() {
            return Regen.this.mode.getCurrentElement() == 0;
         }
      }
   );
   private final NumberSetting delay = settingManager.register(
      new NumberSetting(this, "Delay", 0, 0, 10000)
   );
   private final NumberSetting health = settingManager.register(
      new NumberSetting(this, "Health", 18, 0, 20)
   );
   private final NumberSetting food = settingManager.register(
      new NumberSetting(this, "Food", 18, 0, 20)
   );
   private final CheckboxSetting noAir = settingManager.register(
      new CheckboxSetting(this, "NoAir", false)
   );
   private final CheckboxSetting potionEffect = settingManager.register(
      new CheckboxSetting(this, "PotionEffect", false)
   );

   private final Stopwatch timer = new Stopwatch();
   private boolean resetTimer = false;

   @EventLink
   private final Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (this.resetTimer) {
         minecraft.timer.timerSpeed = 1.0F;
         this.resetTimer = false;
      }

      EntityPlayerSP thePlayer = minecraft.thePlayer;
      if (thePlayer == null || minecraft.theWorld == null) {
         return;
      }

      this.setSuffix(this.mode.getCurrentElementAsString());

      if (minecraft.playerController != null && !minecraft.playerController.gameIsSurvivalOrAdventure()) {
         return;
      }
      if (this.noAir.isEnabled() && !thePlayer.onGround) {
         return;
      }
      if (thePlayer.getFoodStats().getFoodLevel() <= (int) this.food.getCurrentValue()) {
         return;
      }
      if (!thePlayer.isEntityAlive()) {
         return;
      }
      if (thePlayer.getHealth() >= this.health.getCurrentValue()) {
         return;
      }
      if (this.potionEffect.isEnabled() && !thePlayer.isPotionActive(Potion.regeneration)) {
         return;
      }
      if (!this.timer.hasElapsed((long) this.delay.getCurrentValue())) {
         return;
      }

      int currentMode = this.mode.getCurrentElement();
      if (currentMode == 0) { // Vanilla
         int packetCount = (int) this.speed.getCurrentValue();
         for (int i = 0; i < packetCount; i++) {
            this.sendPacketUnregistered(new C03PacketPlayer(thePlayer.onGround));
         }
      } else if (currentMode == 1) { // Spartan
         if (!MoveUtils.isMoving() && thePlayer.onGround) {
            for (int i = 0; i < 9; i++) {
               this.sendPacketUnregistered(new C03PacketPlayer(thePlayer.onGround));
            }
            minecraft.timer.timerSpeed = 0.45F;
            this.resetTimer = true;
         }
      }

      this.timer.reset();
   };

   @Override
   protected void onDisable() {
      if (this.resetTimer) {
         minecraft.timer.timerSpeed = 1.0F;
         this.resetTimer = false;
      }
   }

   public Regen() {
      super(Category.MISC, "Regen", "Regenerates health by sending packets");
      this.getAliases().add("Renge");
   }
}
