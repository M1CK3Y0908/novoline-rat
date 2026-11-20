package rat.novoline.module.hud;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.WorldSettings.GameType;
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
   public final ComboSetting font = settingManager.register(new ComboSetting(this, "Font", 0, "Rubik", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Minecraft"));
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
         .add("Armor Status", true)
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
      if (this.hud.isSelected(3)) {
         this.drawArmorStatus(renderScreenEvent);
      }

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

   private static Collection getArmorStacks(EntityPlayerSP entityPlayerSP) {
      java.util.ArrayList arrayList = new java.util.ArrayList();

      for (int i = 3; i >= 0; i--) {
         ItemStack itemStack = entityPlayerSP.inventory.armorInventory[i];
         if (itemStack != null) {
            arrayList.add(itemStack);
         }
      }

      ItemStack currentEquippedItem = entityPlayerSP.getCurrentEquippedItem();
      if (currentEquippedItem != null) {
         arrayList.add(currentEquippedItem);
      }

      return arrayList;
   }

   private static void drawColoredQuad(WorldRenderer worldRenderer, int i, int i2, int i3, int i4, int i5, int i6) {
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(i, i2, 0.0).color(i5, i6, 0, 255).endVertex();
      worldRenderer.pos(i, i2 + i4, 0.0).color(i5, i6, 0, 255).endVertex();
      worldRenderer.pos(i + i3, i2 + i4, 0.0).color(i5, i6, 0, 255).endVertex();
      worldRenderer.pos(i + i3, i2, 0.0).color(i5, i6, 0, 255).endVertex();
      Tessellator.getInstance().draw();
   }

   public HUD() {
      super(Category.CLIENT, "HUD", "Various useful elements");
   }

   private void drawArmorStatus(RenderScreenEvent renderScreenEvent2) {
      EntityPlayerSP thePlayer2 = minecraft.thePlayer;
      Collection armorStacks = getArmorStacks(thePlayer2);
      float scaledWidth = renderScreenEvent2.scaledResolution.getScaledWidth();
      float scaledHeight = renderScreenEvent2.scaledResolution.getScaledHeight();
      int i = 0;
      boolean flag = thePlayer2.isEntityAlive() && thePlayer2.isInsideOfMaterial(Material.water);
      int i2 = MathHelper.ceiling_float_int((thePlayer2.getMaxHealth() + thePlayer2.getAbsorptionAmount()) / 20.0F);
      int i3 = (i2 - 1) * Math.max(10 - (i2 - 2), 3);
      int i4 = flag ? 10 : 0;
      GameType currentGameType = minecraft.playerController.getCurrentGameType();
      float f2 = scaledWidth / 2.0F - 91.0F;
      float f3 = scaledHeight - 39.0F;
      if (currentGameType == GameType.CREATIVE) {
         f2 += (182 - armorStacks.size() * 16) / 2.0F;
      } else {
         f3 -= i3 + i4;
      }

      for (ItemStack itemStack : (Iterable<ItemStack>) armorStacks) {
         float f4;
         float f5;
         if (currentGameType == GameType.CREATIVE) {
            f4 = f2 + i;
            f5 = f3;
         } else {
            f4 = f2 + 101.0F + i;
            f5 = f3 - 17.0F;
         }

         boolean itemStackDamageable = itemStack.isItemStackDamageable();
         if (itemStackDamageable || itemStack.getItem().getItemStackLimit() != 1 || itemStack.stackSize != 1) {
            String text = String.valueOf(itemStackDamageable ? itemStack.getMaxDamage() - itemStack.getItemDamage() : itemStack.stackSize);
            drawCenteredText(text, f4, f5);
         }

         RenderItem renderItem = minecraft.getRenderItem();
         renderItem.zLevel = 100.0F;
         RenderHelper.enableGUIStandardItemLighting();
         renderItem.renderItemAndEffectIntoGUI(itemStack, (int)f4, (int)f5);
         RenderHelper.disableStandardItemLighting();
         renderItem.zLevel = 0.0F;
         if (itemStack.isItemDamaged()) {
            int i5 = (int)Math.round(13.0 - itemStack.getItemDamage() * 13.0 / itemStack.getMaxDamage());
            int i6 = (int)Math.round(255.0 - itemStack.getItemDamage() * 255.0 / itemStack.getMaxDamage());
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.disableBlend();
            WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
            drawColoredQuad(worldRenderer, (int)f4 + 2, (int)f5 + 13, 13, 2, 0, 0);
            drawColoredQuad(worldRenderer, (int)f4 + 2, (int)f5 + 13, 12, 1, (255 - i6) / 4, 64);
            drawColoredQuad(worldRenderer, (int)f4 + 2, (int)f5 + 13, i5, 1, 255 - i6, i6);
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
         }

         i += 16;
      }
   }

   private static double getSpeedPerTick(Entity entity2) {
      return Math.hypot(entity2.posX - entity2.prevPosX, entity2.posZ - entity2.prevPosZ);
   }

   private void drawCornerTexts(RenderScreenEvent renderScreenEvent2) {
      ScaledResolution scaledResolution2 = renderScreenEvent2.scaledResolution;
      int i = 2;
      if (this.hud.isSelected(5)) {
         i = 2 + (int) this.getFont().getHeight();
         String text = "UID: " + EnumChatFormatting.GRAY + Novoline.userId;
         this.getFont()
            .drawStringWithShadow(
               text, scaledResolution2.getScaledWidth() - 2 - (int) this.getFont().getStringWidth(text), scaledResolution2.getScaledHeight() - i, -1
            );
         i += 7;
      }

      if (this.hud.isSelected(4)) {
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

   private void drawCenteredText(String text, float f2, float f3) {
      float f4 = f2 + (16 - this.getFont().getStringWidth(text)) / 2.0F;
      this.getFont().drawStringWithShadow(text, f4, f3 - 4.0F, -1);
   }
}
