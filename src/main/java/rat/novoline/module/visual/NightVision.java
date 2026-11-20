package rat.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import rat.novoline.event.PreUpdateEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;

public class NightVision extends Module {
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> minecraft.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 16345));

   public NightVision() {
      super(Category.VISUAL, "NightVision", "");
   }

   @Override
   protected void onDisable() {
      minecraft.thePlayer.removePotionEffect(Potion.nightVision.id);
   }
}
