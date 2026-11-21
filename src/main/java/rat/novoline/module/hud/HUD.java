package rat.novoline.module.hud;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import rat.novoline.Novoline;
import rat.novoline.Novoline.Fonts;
import rat.novoline.event.LoadWorldEvent;
import rat.novoline.event.PacketReceiveEvent;
import rat.novoline.event.PacketSendEvent;
import rat.novoline.event.RenderScreenEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.module.combat.Killaura;
import rat.novoline.setting.MultiComboSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.utils.time.Stopwatch;

public class HUD extends Module {
   public final ComboSetting font = settingManager.register(new ComboSetting(this, "Font", 0, "SF", "Rubik", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Tahoma", "Minecraft"));
   public final NumberSetting fontSize = settingManager.register(new NumberSetting(this, "Font Size", 16, 10, 22));

   public FontAdapter getFont() {
      return Fonts.getFont(this.font.getCurrentElementAsString(), this.fontSize.getCurrentValue());
   }

   private final MultiComboSetting hud = settingManager.register(
      new MultiComboSetting(this, "Elements")
         .add("FPS", true)
         .add("TPS", false)
         .add("Speed", false)
         .add("Reach", false)
         .add("Coordinates", true)
         .add("Potion Effects", true)
         .add("User Info", true)
   );
   private final Stopwatch reachDecayTimer = new Stopwatch();
   private final Stopwatch reachDecayStepTimer = new Stopwatch();
   private final Stopwatch tpsTimer = new Stopwatch();
   private final List<Float> tpsSamples = new CopyOnWriteArrayList<>();
   private int tpsSampleIndex = 0;
   @EventLink
   private final Listener<RenderScreenEvent> onRenderScreen = renderScreenEvent -> {
      this.drawInfoLines(renderScreenEvent);
      this.drawCornerTexts(renderScreenEvent);

      if (this.reach > 0.0 && this.reachDecayTimer.hasElapsed(500L) && this.reachDecayStepTimer.hasElapsed(100L)) {
         this.reach = Math.max(0.0, this.reach / 2.0);
         this.reachDecayStepTimer.reset();
      }
   };
   private double reach;
   @EventLink
   private final Listener<PacketSendEvent> onPacketSend = packetSendEvent -> {
      Packet packet2 = packetSendEvent.packet;
      if (packet2 instanceof C02PacketUseEntity && ((C02PacketUseEntity)packet2).action == Action.ATTACK) {
         MovingObjectPosition objectMouseOver2 = minecraft.objectMouseOver;
         Killaura killaura2 = ModuleManager.killaura;
         Vec3 vec3 = new Vec3(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ);
         if (killaura2.entityLivingBase != null && killaura2.isEnabled()) {
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
         float f2 = (float)this.tpsTimer.getElapsedMillis() / 1000.0F;
         float f3 = MathHelper.clamp_float(20.0F / f2, 0.0F, 20.0F);
         this.tpsSampleIndex %= 5;
         this.tpsSamples.add(this.tpsSampleIndex % 5, f3);
         this.tpsTimer.reset();
         this.tpsSampleIndex++;
      }
   };
   @EventLink
   private final Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> {
      this.tpsSamples.clear();
      this.tpsSampleIndex = 0;
   };

   private void drawInfoLines(RenderScreenEvent renderScreenEvent2) {
      java.util.ArrayList arrayList = new java.util.ArrayList();
      arrayList.add("FPS: " + EnumChatFormatting.GREEN + Minecraft.getDebugFPS());
      arrayList.add("TPS: " + this.getTpsText());
      arrayList.add("Blocks/s: " + EnumChatFormatting.GRAY + getSpeedText());
      arrayList.add("Reach: " + EnumChatFormatting.GRAY + String.format(Locale.ENGLISH, "%.2f", this.reach));
      arrayList.add(
         "X: "
            + EnumChatFormatting.GREEN
            + (int)minecraft.thePlayer.posX
            + EnumChatFormatting.RESET
            + " Y: "
            + EnumChatFormatting.GREEN
            + (int)minecraft.thePlayer.posY
            + EnumChatFormatting.RESET
            + " Z: "
            + EnumChatFormatting.GREEN
            + (int)minecraft.thePlayer.posZ
      );
      int i = minecraft.currentScreen instanceof GuiChat ? 15 : 2;

      for (int i2 = arrayList.size() - 1; i2 >= 0; i2--) {
         if (this.hud.isSelected(i2)) {
            int height = (int) this.getFont().getHeight();
            int i3 = renderScreenEvent2.scaledResolution.getScaledHeight() - height - i;
            this.getFont().drawStringWithShadow((String)arrayList.get(i2), 2.0F, (float) i3, -1);
            i += height + 3;
         }
      }
   }

   private static String getSpeedText() {
      return String.format(
         Locale.ENGLISH, "%.2f", getSpeedPerTick(minecraft.thePlayer) * 20.0 * (minecraft.timer.timerSpeed < 1.0F ? 1.0F : minecraft.timer.timerSpeed)
      );
   }

   public HUD() {
      super(Category.CLIENT, "HUD", "Various useful elements");
   }

   private static double getSpeedPerTick(Entity entity2) {
      return Math.hypot(entity2.posX - entity2.prevPosX, entity2.posZ - entity2.prevPosZ);
   }

   private void drawCornerTexts(RenderScreenEvent renderScreenEvent2) {
      ScaledResolution scaledResolution2 = renderScreenEvent2.scaledResolution;
      int i = 2;
      if (this.hud.isSelected("User Info")) {
         i = 2 + (int) this.getFont().getHeight();
         String text = Novoline.getUserRole() + ": " + EnumChatFormatting.GRAY + Novoline.getUserName();
         this.getFont()
            .drawStringWithShadow(
               text, scaledResolution2.getScaledWidth() - 2 - (int) this.getFont().getStringWidth(text), scaledResolution2.getScaledHeight() - i, -1
            );
         i += 7;
      }

      if (this.hud.isSelected("Potion Effects")) {
         for (PotionEffect potionEffect : minecraft.thePlayer.getActivePotionEffects()) {
            Potion potion2 = Potion.potionTypes[potionEffect.getPotionID()];
            StringBuilder stringBuilder = new StringBuilder();
            if (potion2 != null) {
               stringBuilder.append(I18n.format(potion2.getName()));

               stringBuilder.append(switch (potionEffect.getAmplifier()) {
                  case 1 -> " II";
                  case 2 -> " III";
                  case 3 -> " IV";
                  case 4 -> " V";
                  case 5 -> " VI";
                  case 6 -> " VII";
                  case 7 -> " VIII";
                  case 8 -> " IX";
                  case 9 -> " X";
                  default -> "";
               });
               int duration = potionEffect.getDuration();
               if (duration > 600) {
                  stringBuilder.append(EnumChatFormatting.GRAY);
               } else if (duration < 600 && duration > 300) {
                  stringBuilder.append(EnumChatFormatting.GOLD);
               } else {
                  stringBuilder.append(EnumChatFormatting.RED);
               }

               stringBuilder.append(" ").append(Potion.getDurationString(potionEffect));
               String text2 = stringBuilder.toString();
               this.getFont()
                  .drawStringWithShadow(
                     stringBuilder.toString(), scaledResolution2.getScaledWidth() - 2 - (int) this.getFont().getStringWidth(text2), scaledResolution2.getScaledHeight() - i, -1
                  );
               i += (int) this.getFont().getHeight() + 2;
            }
         }
      }
   }

   private String getTpsText() {
      float averageTps = this.getAverageTps();
      EnumChatFormatting enumChatFormatting = EnumChatFormatting.GREEN;
      if (averageTps < 10.0F) {
         enumChatFormatting = EnumChatFormatting.RED;
      } else if (averageTps >= 10.0F && averageTps <= 17.0F) {
         enumChatFormatting = EnumChatFormatting.GOLD;
      }

      return enumChatFormatting + String.format(Locale.ENGLISH, "%.1f", averageTps);
   }

   private float getAverageTps() {
      float f2 = 0.0F;

      for (float f3 : this.tpsSamples) {
         f2 += f3;
      }

      return MathHelper.clamp_float(f2 / Math.max(1, this.tpsSamples.size()), 0.0F, 20.0F);
   }
}
