package rat.novoline.module.hud;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import rat.novoline.Novoline.Fonts;
import rat.novoline.event.LoadWorldEvent;
import rat.novoline.event.PacketReceiveEvent;
import rat.novoline.event.PacketSendEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.module.combat.Killaura;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.MultiComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.utils.time.Stopwatch;

public class Information extends Module {
   public final CheckboxSetting background = settingManager.register(new CheckboxSetting(this, "Background", false));
   public final MultiComboSetting elements = settingManager.register(
      new MultiComboSetting(this, "Elements")
         .add("FPS", true)
         .add("TPS", false)
         .add("Speed", false)
         .add("Reach", false)
         .add("Coordinates", true)
         .add("Ping", true)
         .add("User Info", true)
   );

   private final Stopwatch reachDecayTimer = new Stopwatch();
   private final Stopwatch reachDecayStepTimer = new Stopwatch();
   private final Stopwatch tpsTimer = new Stopwatch();
   private final List<Float> tpsSamples = new CopyOnWriteArrayList<>();
   private int tpsSampleIndex = 0;
   private double reach;

   public FontAdapter getFont() {
      return ModuleManager.font != null ? ModuleManager.font.getFont() : Fonts.rubikRegularSize16;
   }

   public double getReach() {
      if (this.reach > 0.0 && this.reachDecayTimer.hasElapsed(500L) && this.reachDecayStepTimer.hasElapsed(100L)) {
         this.reach = Math.max(0.0, this.reach / 2.0);
         this.reachDecayStepTimer.reset();
      }
      return this.reach;
   }

   public float getAverageTps() {
      float f2 = 0.0F;
      for (float f3 : this.tpsSamples) {
         f2 += f3;
      }
      return MathHelper.clamp_float(f2 / Math.max(1, this.tpsSamples.size()), 0.0F, 20.0F);
   }

   @EventLink
   private final Listener<PacketSendEvent> onPacketSend = packetSendEvent -> {
      Packet packet2 = packetSendEvent.packet;
      if (packet2 instanceof C02PacketUseEntity && ((C02PacketUseEntity) packet2).action == Action.ATTACK) {
         MovingObjectPosition objectMouseOver2 = minecraft.objectMouseOver;
         Killaura killaura2 = ModuleManager.killaura;
         Vec3 vec3 = new Vec3(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ);
         if (killaura2 != null && killaura2.entityLivingBase != null && killaura2.isEnabled()) {
            this.reach = vec3.distanceTo(killaura2.entityLivingBase.getPositionVector());
            this.reachDecayTimer.reset();
         } else if (objectMouseOver2 != null && objectMouseOver2.typeOfHit == MovingObjectType.ENTITY) {
            this.reach = vec3.distanceTo(objectMouseOver2.hitVec);
            this.reachDecayTimer.reset();
         }
      }
   };

   @EventLink
   private final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      if (packetReceiveEvent.packet instanceof S03PacketTimeUpdate) {
         float f2 = (float) this.tpsTimer.getElapsedMillis() / 1000.0F;
         float f3 = MathHelper.clamp_float(20.0F / f2, 0.0F, 20.0F);
         this.tpsSampleIndex %= 5;
         if (this.tpsSamples.size() <= this.tpsSampleIndex) {
            this.tpsSamples.add(f3);
         } else {
            this.tpsSamples.set(this.tpsSampleIndex, f3);
         }
         this.tpsTimer.reset();
         this.tpsSampleIndex = (this.tpsSampleIndex + 1) % 5;
      }
   };

   @EventLink
   private final Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> {
      this.tpsSamples.clear();
      this.tpsSampleIndex = 0;
   };

   public Information() {
      super(Category.VISUAL, "Information", "Displays game and client status information");
      this.setEnabled(true);
   }
}
