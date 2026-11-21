package rat.novoline.module.hud;

import io.github.nevalackin.homoBus.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import rat.novoline.Novoline.Fonts;
import rat.novoline.event.PacketSendEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.MultiComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.FontAdapter;

public class TargetHUD extends Module {
   public final ComboSetting targetHUD = settingManager.register(new ComboSetting(this, "Mode", 0, "Type 1", "Type 2", "Type 3", "Classic"));
   public final ComboSetting font = settingManager.register(new ComboSetting(this, "Font", 0, "SF", "Rubik", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Tahoma", "Minecraft"));
   public final MultiComboSetting addons = settingManager.register(new MultiComboSetting(this, "Addons").add("Shadow", true));
   public final NumberSetting maxPerRow = settingManager.register(new NumberSetting(this, "Max Per Row", 2, 1, 5));

   public FontAdapter getTitleFont() {
      return Fonts.getFont(this.font.getCurrentElementAsString(), 18.0F, true);
   }

   public FontAdapter getInfoFont() {
      return Fonts.getFont(this.font.getCurrentElementAsString(), 14.0F, false);
   }

   public final Listener<PacketSendEvent> onPacketSend = event -> {
      if (event.packet instanceof C02PacketUseEntity packet && packet.getAction() == C02PacketUseEntity.Action.ATTACK) {
         if (minecraft.theWorld != null) {
            Entity entity = minecraft.theWorld.getEntityByID(packet.entityId);
            if (entity instanceof EntityPlayer player && player != minecraft.thePlayer) {
               TargetHudElement.onAttackEntity(player);
            }
         }
      }
   };

   public TargetHUD() {
      super(Category.VISUAL, "TargetHUD", "");
      this.setEnabled(true);
   }
}
