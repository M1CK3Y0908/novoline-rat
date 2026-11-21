package rat.novoline.module.hud;

import com.mojang.authlib.GameProfile;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import rat.novoline.module.ModuleManager;
import rat.novoline.module.combat.Killaura;
import rat.novoline.module.misc.NameProtect;
import rat.novoline.utils.animation.Animation;
import rat.novoline.utils.animation.Easing;
import rat.novoline.utils.math.SmoothingUtils;
import rat.novoline.utils.render.RenderUtils;

public class TargetHudElement extends HudElement {
   public static TargetHudElement instance;
   private static final Minecraft minecraft = Minecraft.getMinecraft();
   private ScaledResolution scaledResolution;

   public static class TargetEntry {
      public EntityPlayer player;
      public final UUID uniqueId;
      public final int entityId;
      public final long firstSeenTime;

      public float currentX;
      public float currentY;
      public float targetX;
      public float targetY;
      public float cardWidth;
      public int lastCol = 0;
      public boolean positionInitialized = false;

      public final Animation animation = new Animation();
      public boolean exiting = false;

      public float animatedHealth;
      public float animatedArmor;
      public double lastP = 0.0;
      public double diffP = 0.0;
      public ResourceLocation skin;
      public float nameWidth;

      public TargetEntry(EntityPlayer player) {
         this.player = player;
         this.uniqueId = player.getUniqueID();
         this.entityId = player.getEntityId();
         this.firstSeenTime = System.currentTimeMillis();
         this.animatedHealth = Math.max(0.0F, player.getHealth());
         this.animatedArmor = player.getTotalArmorValue();
         float maxHP = player.getMaxHealth() + player.getAbsorptionAmount() - 0.05F;
         if (maxHP <= 0.0F) {
            maxHP = 20.0F;
         }
         this.lastP = (player.getHealth() / maxHP) * 100.0;
         this.diffP = 0.0;
         this.animation.unlock();
         this.animation.start(0.0, 100.0, 250L, Easing.backInOut);
         this.updateNameWidth();
      }

      public void updateNameWidth() {
         String name = this.player.getName();
         NameProtect nameProtect = ModuleManager.nameProtect;
         if (nameProtect != null && nameProtect.isEnabled() && nameProtect.getHideIn().isSelected(1)) {
            name = nameProtect.protectText(name);
         }
         if (ModuleManager.targetHUD != null) {
            this.nameWidth = ModuleManager.targetHUD.getTitleFont().getStringWidth(name);
         } else {
            this.nameWidth = Minecraft.getMinecraft().fontRendererObj.getInt3(name);
         }
      }

      public void startExit() {
         if (!this.exiting) {
            this.exiting = true;
            this.animation.unlock();
            this.animation.start(this.animation.getValue(), 0.0, 250L, Easing.backInOut);
         }
      }

      public void revive() {
         if (this.exiting) {
            this.exiting = false;
            this.animation.unlock();
            this.animation.start(this.animation.getValue(), 100.0, 250L, Easing.backInOut);
         }
      }
   }

   private static final List<TargetEntry> activeTargets = new ArrayList<>();
   private static final List<TargetEntry> exitingTargets = new ArrayList<>();
   private static EntityPlayer lastAttackedPlayer = null;
   private static long lastAttackedTime = 0L;
   private static boolean inEditorMode = false;
   private static EntityPlayer previewPlayer2 = null;

   public EntityPlayer entityPlayer;

   public static void onAttackEntity(EntityPlayer player) {
      lastAttackedPlayer = player;
      lastAttackedTime = System.currentTimeMillis();
   }

   public static void clearTargets() {
      activeTargets.clear();
      exitingTargets.clear();
   }

   public static float getMinCardWidth() {
      int currentElement = ModuleManager.targetHUD != null ? ModuleManager.targetHUD.targetHUD.getCurrentElement() : 0;
      if (currentElement == 3) {
         return 120.0F;
      } else if (currentElement == 2) {
         return 130.0F;
      } else {
         return 100.0F;
      }
   }

   public static float getPreferredCardWidth(TargetEntry entry) {
      int currentElement = ModuleManager.targetHUD != null ? ModuleManager.targetHUD.targetHUD.getCurrentElement() : 0;
      float nw = entry != null ? entry.nameWidth : 0.0F;
      if (currentElement == 3) {
         return Math.max(120.0F, 35.0F + nw + 34.0F);
      } else if (currentElement == 2) {
         return Math.max(130.0F, 38.0F + nw);
      } else {
         return Math.max(100.0F, 30.0F + nw);
      }
   }

   public static float getSingleCardHeight() {
      int currentElement = ModuleManager.targetHUD != null ? ModuleManager.targetHUD.targetHUD.getCurrentElement() : 0;
      if (currentElement == 0) {
         return 35.5F;
      } else if (currentElement == 1) {
         return 28.0F;
      } else if (currentElement == 2) {
         return 34.0F;
      } else if (currentElement == 3) {
         return 39.0F;
      } else {
         return 35.5F;
      }
   }

   private static float getColumnWidth(int col, int maxPerRow) {
      float maxW = getMinCardWidth();
      for (TargetEntry entry : activeTargets) {
         int idx = activeTargets.indexOf(entry);
         if (idx % maxPerRow == col) {
            maxW = Math.max(maxW, getPreferredCardWidth(entry));
         }
      }
      for (TargetEntry entry : exitingTargets) {
         if (entry.lastCol == col) {
            maxW = Math.max(maxW, getPreferredCardWidth(entry));
         }
      }
      return maxW;
   }

   @Override
   public float getWidth() {
      int maxPerRow = ModuleManager.targetHUD != null ? Math.max(1, Math.min(5, (int)ModuleManager.targetHUD.maxPerRow.getCurrentValue())) : 2;
      int count = Math.max(1, activeTargets.size() + exitingTargets.size());
      int cols = Math.min(count, maxPerRow);
      float totalW = 0.0F;
      for (int c = 0; c < cols; c++) {
         totalW += getColumnWidth(c, maxPerRow);
         if (c > 0) {
            totalW += 5.0F;
         }
      }
      return Math.max(getMinCardWidth(), totalW);
   }

   @Override
   public float getHeight() {
      int maxPerRow = ModuleManager.targetHUD != null ? Math.max(1, Math.min(5, (int)ModuleManager.targetHUD.maxPerRow.getCurrentValue())) : 2;
      int count = Math.max(1, activeTargets.size() + exitingTargets.size());
      int rows = (count + maxPerRow - 1) / maxPerRow;
      float cardH = getSingleCardHeight();
      return Math.max(cardH, rows * cardH + Math.max(0, rows - 1) * 5.0F);
   }

   @Override
   public float getX() {
      ScaledResolution sr = this.scaledResolution != null ? this.scaledResolution : new ScaledResolution(minecraft);
      return this.x + sr.getScaledWidth() / 2.0F - getMinCardWidth() / 2.0F;
   }

   @Override
   public float getY() {
      ScaledResolution sr = this.scaledResolution != null ? this.scaledResolution : new ScaledResolution(minecraft);
      return this.y + sr.getScaledHeight() / 2.0F + getSingleCardHeight();
   }

   private static boolean isValidTargetPlayer(EntityPlayer p) {
      return p != null && p != minecraft.thePlayer && p.isEntityAlive() && !p.isDead && p.getHealth() > 0.0F && p.worldObj == minecraft.theWorld;
   }

   public static void updateTarget(ScaledResolution sr) {
      if (instance == null || ModuleManager.targetHUD == null || !ModuleManager.targetHUD.isEnabled()) {
         return;
      }
      instance.scaledResolution = sr;
      instance.updatePositionsAndAnimations(sr);
   }

   public void updatePositionsAndAnimations(ScaledResolution sr) {
      if (minecraft.theWorld == null || minecraft.thePlayer == null) {
         clearTargets();
         this.entityPlayer = null;
         return;
      }

      List<EntityPlayer> currentPlayers = new ArrayList<>();
      Killaura killaura = ModuleManager.killaura;
      if (killaura != null && killaura.isEnabled()) {
         if (killaura.entityLivingBase instanceof EntityPlayer primary && isValidTargetPlayer(primary)) {
            currentPlayers.add(primary);
         }
         List<Entity> list = killaura.getTargetList();
         if (list != null) {
            for (Entity e : list) {
               if (e instanceof EntityPlayer p && isValidTargetPlayer(p)) {
                  if (!currentPlayers.contains(p)) {
                     currentPlayers.add(p);
                  }
               }
            }
         }
      }

      if (ModuleManager.aimBot != null && ModuleManager.aimBot.isEnabled()) {
         EntityPlayer aimTarget = ModuleManager.aimBot.getEntityPlayer();
         if (isValidTargetPlayer(aimTarget) && !currentPlayers.contains(aimTarget)) {
            currentPlayers.add(aimTarget);
         }
      }

      if (lastAttackedPlayer != null) {
         if (isValidTargetPlayer(lastAttackedPlayer) && System.currentTimeMillis() - lastAttackedTime < 5000L && minecraft.thePlayer.getDistanceToEntity(lastAttackedPlayer) <= 15.0F) {
            if (!currentPlayers.contains(lastAttackedPlayer)) {
               currentPlayers.add(lastAttackedPlayer);
            }
         } else {
            lastAttackedPlayer = null;
         }
      }

      if (minecraft.pointedEntity instanceof EntityPlayer pointed && isValidTargetPlayer(pointed) && !currentPlayers.contains(pointed)) {
         currentPlayers.add(pointed);
      }

      if (currentPlayers.isEmpty() && inEditorMode && minecraft.thePlayer != null) {
         currentPlayers.add(minecraft.thePlayer);
         int maxPerRow = Math.max(1, Math.min(5, (int)ModuleManager.targetHUD.maxPerRow.getCurrentValue()));
         if (maxPerRow >= 2) {
            if (previewPlayer2 == null || previewPlayer2.worldObj != minecraft.theWorld) {
               previewPlayer2 = new EntityOtherPlayerMP(minecraft.theWorld, new GameProfile(UUID.randomUUID(), "Target 2"));
               previewPlayer2.setHealth(14.0F);
            }
            currentPlayers.add(previewPlayer2);
         }
      }

      // Check existing active targets to see if any died or left
      Iterator<TargetEntry> activeIter = activeTargets.iterator();
      while (activeIter.hasNext()) {
         TargetEntry entry = activeIter.next();
         boolean stillPresent = false;
         for (EntityPlayer p : currentPlayers) {
            if (p == entry.player || p.getUniqueID().equals(entry.uniqueId)) {
               entry.player = p;
               entry.updateNameWidth();
               stillPresent = true;
               break;
            }
         }

         if (!stillPresent || !entry.player.isEntityAlive() || entry.player.isDead || entry.player.getHealth() <= 0.0F) {
            activeIter.remove();
            entry.startExit();
            if (!exitingTargets.contains(entry)) {
               exitingTargets.add(entry);
            }
         }
      }

      // Process new incoming targets or revived targets
      for (EntityPlayer p : currentPlayers) {
         boolean foundInActive = false;
         for (TargetEntry entry : activeTargets) {
            if (entry.player == p || entry.uniqueId.equals(p.getUniqueID())) {
               foundInActive = true;
               break;
            }
         }

         if (!foundInActive) {
            TargetEntry revived = null;
            for (TargetEntry exitEntry : exitingTargets) {
               if (exitEntry.player == p || exitEntry.uniqueId.equals(p.getUniqueID())) {
                  revived = exitEntry;
                  break;
               }
            }

            if (revived != null) {
               exitingTargets.remove(revived);
               revived.revive();
               revived.player = p;
               revived.updateNameWidth();
               activeTargets.add(revived);
            } else {
               TargetEntry newEntry = new TargetEntry(p);
               activeTargets.add(newEntry);
            }
         }
      }

      // Maintain chronological order
      activeTargets.sort(Comparator.comparingLong(e -> e.firstSeenTime));

      this.entityPlayer = !activeTargets.isEmpty() ? activeTargets.get(0).player : null;

      // Calculate slot layout and smoothly animate translations
      int maxPerRow = Math.max(1, Math.min(5, (int)ModuleManager.targetHUD.maxPerRow.getCurrentValue()));
      float baseX = this.getX();
      float baseY = this.getY();
      float cardHeight = getSingleCardHeight();
      float[] colWidths = new float[maxPerRow];
      for (int c = 0; c < maxPerRow; c++) {
         colWidths[c] = getColumnWidth(c, maxPerRow);
      }

      for (int i = 0; i < activeTargets.size(); i++) {
         TargetEntry entry = activeTargets.get(i);
         int row = i / maxPerRow;
         int col = i % maxPerRow;
         entry.lastCol = col;
         entry.cardWidth = colWidths[col];

         float sx = baseX;
         for (int c = 0; c < col; c++) {
            sx += colWidths[c] + 5.0F;
         }
         entry.targetX = sx;
         entry.targetY = baseY + row * (cardHeight + 5.0F);

         if (!entry.positionInitialized) {
            entry.currentX = entry.targetX;
            entry.currentY = entry.targetY;
            entry.positionInitialized = true;
         } else if (this.isDragging()) {
            entry.currentX = entry.targetX;
            entry.currentY = entry.targetY;
         } else {
            entry.currentX = SmoothingUtils.approachWithMinimumStep(entry.currentX, entry.targetX, 10.0F, 0.05F);
            entry.currentY = SmoothingUtils.approachWithMinimumStep(entry.currentY, entry.targetY, 10.0F, 0.05F);
         }

         entry.animation.update();
      }

      // Animate and clean up exiting targets
      Iterator<TargetEntry> exitIter = exitingTargets.iterator();
      while (exitIter.hasNext()) {
         TargetEntry entry = exitIter.next();
         entry.animation.update();
         if (!entry.animation.isAnimating() && entry.animation.getValue() <= 0.5) {
            exitIter.remove();
         }
      }
   }

   private static List<TargetEntry> getAllRenderEntries() {
      List<TargetEntry> list = new ArrayList<>(exitingTargets.size() + activeTargets.size());
      list.addAll(exitingTargets);
      list.addAll(activeTargets);
      return list;
   }

   @Override
   public void draw(ScaledResolution scaledResolution2, float f2, boolean flag) {
      this.scaledResolution = scaledResolution2;
      if (!ModuleManager.targetHUD.isEnabled()) {
         return;
      }
      inEditorMode = flag;
      this.updatePositionsAndAnimations(scaledResolution2);

      boolean postProcessingActive = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isEnabled();

      // Render exiting targets first (shrinking down), then active targets
      for (TargetEntry entry : exitingTargets) {
         drawTargetCard(entry, postProcessingActive);
      }
      for (TargetEntry entry : activeTargets) {
         drawTargetCard(entry, postProcessingActive);
      }
   }

   private void drawTargetCard(TargetEntry entry, boolean postProcessingActive) {
      EntityPlayer player = entry.player;
      if (player == null) {
         return;
      }

      double scale = Math.max(0.0, entry.animation.getValue() / 100.0);
      if (scale <= 0.0) {
         return;
      }

      float x = entry.currentX;
      float y = entry.currentY;
      float width = entry.cardWidth;
      float height = getSingleCardHeight();

      GlStateManager.pushMatrix();
      GlStateManager.translate(x + width / 2.0F, y + height / 2.0F, 0.0F);
      GlStateManager.scale(scale, scale, 0.0);
      GlStateManager.translate(-(x + width / 2.0F), -(y + height / 2.0F), 0.0F);

      if (entry.skin == null) {
         if (minecraft.thePlayer != null && minecraft.thePlayer.sendQueue != null) {
            NetworkPlayerInfo playerInfo = minecraft.thePlayer.sendQueue.getPlayerInfo(player.getUniqueID());
            if (playerInfo != null) {
               entry.skin = playerInfo.getLocationSkin();
            } else if (player instanceof AbstractClientPlayer clientPlayer) {
               entry.skin = clientPlayer.getLocationSkin();
            }
         } else if (player instanceof AbstractClientPlayer clientPlayer) {
            entry.skin = clientPlayer.getLocationSkin();
         }
      }

      String name = player.getName();
      NameProtect nameProtect2 = ModuleManager.nameProtect;
      if (nameProtect2 != null && nameProtect2.isEnabled() && nameProtect2.getHideIn().isSelected(1)) {
         name = nameProtect2.protectText(name);
      }

      int currentElement = ModuleManager.targetHUD.targetHUD.getCurrentElement();
      entry.nameWidth = ModuleManager.targetHUD.getTitleFont().getStringWidth(name);

      if (!postProcessingActive && (ModuleManager.targetHUD.addons.isSelected("Shadow") || ModuleManager.targetHUD.addons.isSelected(0))) {
         RenderUtils.drawShadow(x, y, x + width, y + height, 2.0F, 1073741824);
      }

      if (currentElement == 0) {
         float maxHealth = player.getMaxHealth();
         entry.animatedHealth = SmoothingUtils.approachWithMinimumStep(entry.animatedHealth, Math.max(0.0F, player.getHealth()), 10.0F, 0.01F);
         entry.animatedArmor = SmoothingUtils.approachWithMinimumStep(entry.animatedArmor, player.getTotalArmorValue(), 10.0F, 0.01F);
         RenderUtils.drawRect(x, y, x + width, y + height, -2061426399);
         if (entry.skin != null) {
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            minecraft.getTextureManager().bindTexture(entry.skin);
            RenderUtils.drawScaledTexturedRect(x + 2.0F, y + 2.0F, 8.0F, 8.0F, 8.0F, 8.0F, 24.0F, 24.0F, 64.0F, 64.0F);
            if (player.isWearing(EnumPlayerModelParts.HAT)) {
               RenderUtils.drawScaledTexturedRect(x + 2.0F, y + 2.0F, 40.0F, 8.0F, 8.0F, 8.0F, 24.0F, 24.0F, 64.0F, 64.0F);
            }
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
         }

         ModuleManager.targetHUD.getTitleFont().drawStringWithShadow(name, x + 28.0F, y + 4.0F, -1);
         ModuleManager.targetHUD.getInfoFont().drawStringWithShadow(String.format(Locale.ENGLISH, "Health: %.1f", entry.animatedHealth), x + 28.0F, y + 13.5F, -986896);
         float dist = minecraft.thePlayer != null ? player.getDistanceToEntity(minecraft.thePlayer) : 0.0F;
         ModuleManager.targetHUD.getInfoFont().drawStringWithShadow(
            String.format(Locale.ENGLISH, "Distance: %.1fm", dist),
            x + 28.0F,
            y + 21.0F,
            -986896
         );
         float f3 = width - 2.0F - 2.0F;
         float f4 = f3 * MathHelper.clamp_float(entry.animatedHealth / maxHealth, 0.0F, 1.0F);
         float f5 = f3 * MathHelper.clamp_float(entry.animatedArmor / 20.0F, 0.0F, 1.0F);
         RenderUtils.drawRect(
            x + 2.0F - 0.5F,
            y + 29.0F - 0.5F,
            x + width - 2.0F + 0.5F,
            y + 29.0F + 1.5F + 0.5F,
            1342177280
         );
         RenderUtils.drawHorizontalGradientRect(
            x + 2.0F, y + 29.0F, x + 2.0F + f4, y + 29.0F + 1.5F, -16737215, -7405631
         );
         RenderUtils.drawRect(
            x + 2.0F - 0.5F,
            y + 29.0F + 3.0F - 0.5F,
            x + width - 2.0F + 0.5F,
            y + 29.0F + 3.0F + 1.5F + 0.5F,
            1342177280
         );
         RenderUtils.drawHorizontalGradientRect(
            x + 2.0F, y + 29.0F + 3.0F, x + 2.0F + f5, y + 29.0F + 3.0F + 1.5F, -16750672, -12986881
         );
      } else if (currentElement == 1) {
         float maxHealth2 = player.getMaxHealth();
         entry.animatedHealth = SmoothingUtils.approachWithMinimumStep(entry.animatedHealth, Math.max(0.0F, player.getHealth()), 10.0F, 0.01F);
         entry.animatedArmor = SmoothingUtils.approachWithMinimumStep(entry.animatedArmor, player.getTotalArmorValue(), 10.0F, 0.01F);
         RenderUtils.drawRect(x, y, x + width, y + height, -2061426399);
         if (entry.skin != null) {
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            minecraft.getTextureManager().bindTexture(entry.skin);
            RenderUtils.drawScaledTexturedRect(x + 2.0F, y + 2.0F, 8.0F, 8.0F, 8.0F, 8.0F, 24.0F, 24.0F, 64.0F, 64.0F);
            if (player.isWearing(EnumPlayerModelParts.HAT)) {
               RenderUtils.drawScaledTexturedRect(x + 2.0F, y + 2.0F, 40.0F, 8.0F, 8.0F, 8.0F, 24.0F, 24.0F, 64.0F, 64.0F);
            }
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
         }

         ModuleManager.targetHUD.getTitleFont().drawStringWithShadow(name, x + 28.0F, y + 4.0F, -1);
         float dist = minecraft.thePlayer != null ? player.getDistanceToEntity(minecraft.thePlayer) : 0.0F;
         ModuleManager.targetHUD.getInfoFont().drawStringWithShadow(
            String.format(Locale.ENGLISH, "%d HP - %dm", (int)entry.animatedHealth, (int)dist),
            x + 28.0F,
            y + 13.5F,
            -986896
         );
         float f6 = width - 28.0F - 2.0F;
         float f7 = f6 * MathHelper.clamp_float(entry.animatedHealth / maxHealth2, 0.0F, 1.0F);
         RenderUtils.drawRect(
            x + 28.0F - 0.5F,
            y + 21.0F - 0.5F,
            x + width - 2.0F + 0.5F,
            y + 24.0F + 1.5F + 0.5F,
            1342177280
         );
         RenderUtils.drawHorizontalGradientRect(
            x + 28.0F, y + 21.0F, x + 28.0F + f7, y + 24.0F + 1.5F, -3527576, -33608
         );
      } else if (currentElement == 2) {
         float maxHealth3 = player.getMaxHealth();
         entry.animatedHealth = SmoothingUtils.approachWithMinimumStep(entry.animatedHealth, Math.max(0.0F, player.getHealth()), 10.0F, 0.01F);
         entry.animatedArmor = SmoothingUtils.approachWithMinimumStep(entry.animatedArmor, player.getTotalArmorValue(), 10.0F, 0.01F);
         RenderUtils.drawRect(x, y, x + width, y + height, -2061426399);
         if (entry.skin != null) {
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            minecraft.getTextureManager().bindTexture(entry.skin);
            RenderUtils.drawScaledTexturedRect(x + 3.0F, y + 3.0F, 8.0F, 8.0F, 8.0F, 8.0F, 28.0F, 28.0F, 64.0F, 64.0F);
            if (player.isWearing(EnumPlayerModelParts.HAT)) {
               RenderUtils.drawScaledTexturedRect(x + 3.0F, y + 3.0F, 40.0F, 8.0F, 8.0F, 8.0F, 28.0F, 28.0F, 64.0F, 64.0F);
            }
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
         }

         ModuleManager.targetHUD.getTitleFont().drawStringWithShadow(name, x + 34.0F, y + 7.0F, -1);
         float dist = minecraft.thePlayer != null ? player.getDistanceToEntity(minecraft.thePlayer) : 0.0F;
         ModuleManager.targetHUD.getInfoFont().drawStringWithShadow(
            String.format(Locale.ENGLISH, "%d HP - %dm", (int)entry.animatedHealth, (int)dist),
            x + 34.0F,
            y + 16.5F,
            -986896
         );
         float f8 = width - 34.0F - 4.0F;
         float f9 = f8 * MathHelper.clamp_float(entry.animatedHealth / maxHealth3, 0.0F, 1.0F);
         RenderUtils.drawRect(x + 34.0F, y + 24.5F, x + width - 4.0F, y + 26.5F + 1.5F, -1876942816);
         if (!postProcessingActive) {
            RenderUtils.drawShadow(
               x + 34.0F,
               y + 24.5F,
               x + 34.0F + f9,
               y + 26.5F + 1.5F,
               2.0F,
               RenderUtils.withAlphaFraction(-14039553, 0.4F)
            );
         }
         RenderUtils.drawRect(x + 34.0F, y + 24.5F, x + 34.0F + f9, y + 26.5F + 1.5F, -14039553);
      } else if (currentElement == 3) {
         float hp = Math.max(0.0F, player.getHealth() + player.getAbsorptionAmount());
         float maxHP = player.getMaxHealth() + player.getAbsorptionAmount() - 0.05F;
         float health = (float)(Math.round(hp * 100.0) / 100.0);
         if (health > maxHP) {
            health = maxHP;
         }

         float amplifier = 100.0F / maxHP;
         float percent = MathHelper.clamp_float(health * amplifier, 0.0F, 100.0F);
         float rectLength = width + 6.0F;
         float space = (rectLength - 50.0F) / 100.0F;

         if ((double)percent < entry.lastP) {
            entry.diffP += entry.lastP - (double)percent;
         }
         entry.lastP = (double)percent;
         if (entry.diffP > 0.0) {
            entry.diffP += (0.0 - entry.diffP) * 0.05;
            if (entry.diffP < 0.001) {
               entry.diffP = 0.0;
            }
         }
         entry.diffP = MathHelper.clamp_double(entry.diffP, 0.0, (double)(100.0F - percent));

         RenderUtils.drawBorderedRect(x, y, x + width, y + height, 1.0F, new Color(29, 29, 29, 130).getRGB(), new Color(0, 0, 0, 50).getRGB());
         RenderUtils.drawRect(x + 1.0F, y + 1.5F, x + width - 1.0F, y + height - 1.5F, new Color(40, 40, 40, 130).getRGB());

         if (entry.skin != null) {
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            minecraft.getTextureManager().bindTexture(entry.skin);
            RenderUtils.drawScaledTexturedRect(x + 1.5F, y + 1.7F, 8.0F, 8.0F, 8.0F, 8.0F, 35.2F, 35.2F, 64.0F, 64.0F);
            if (player.isWearing(EnumPlayerModelParts.HAT)) {
               RenderUtils.drawScaledTexturedRect(x + 1.5F, y + 1.7F, 40.0F, 8.0F, 8.0F, 8.0F, 35.2F, 35.2F, 64.0F, 64.0F);
            }
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
         }

         int hudColor = ModuleManager.themeColor.getColor();
         int darkerHudColor = new Color(hudColor).darker().getRGB();
         float barStartX = x + 40.0F;
         float barY1 = y + 18.0F;
         float barY2 = y + 28.8F;

         RenderUtils.drawRect(barStartX, barY1, barStartX + 100.0F * space, barY2, new Color(0, 0, 0, 50).getRGB());
         RenderUtils.drawRect(barStartX, barY1, barStartX + percent * space, barY2, hudColor);
         RenderUtils.drawRect(barStartX + percent * space, barY1, barStartX + (float)((double)percent + entry.diffP) * space, barY2, darkerHudColor);

         String text = String.format(Locale.ENGLISH, "%.1f%%", percent);
         float textX = barStartX + 50.0F * space - (float)minecraft.fontRendererObj.getInt3(text) / 2.0F;
         float textY = y + 19.5F;

         minecraft.fontRendererObj.drawStringWithShadow(text, textX, textY, -1);
         minecraft.fontRendererObj.drawStringWithShadow(name, x + 40.0F, y + 5.5F, -1);
      }

      GlStateManager.popMatrix();
   }

   public TargetHudElement() {
      super("target-hud", 0.0F, 0.0F, true);
      instance = this;
   }

   public static void renderTargetHudEffects(boolean bloom, ScaledResolution sr) {
      boolean glow = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isGlowEnabled("TargetHud");
      renderTargetHudEffects(bloom, glow, sr);
   }

   public static void renderTargetHudEffects(boolean bloom, boolean glow, ScaledResolution sr) {
      if (instance == null || ModuleManager.targetHUD == null || !ModuleManager.targetHUD.isEnabled()) {
         return;
      }
      if (sr != null) {
         instance.scaledResolution = sr;
      }

      float cardHeight = getSingleCardHeight();
      for (TargetEntry entry : getAllRenderEntries()) {
         if (entry.player == null) {
            continue;
         }
         double scale = Math.max(0.0, entry.animation.getValue() / 100.0);
         if (scale <= 0.0) {
            continue;
         }

         float x = entry.currentX;
         float y = entry.currentY;
         float w = entry.cardWidth;
         float h = cardHeight;

         GlStateManager.pushMatrix();
         GlStateManager.translate(x + w / 2.0F, y + h / 2.0F, 0.0F);
         GlStateManager.scale(scale, scale, 0.0);
         GlStateManager.translate(-(x + w / 2.0F), -(y + h / 2.0F), 0.0F);

         int hudColor = (!bloom || !glow) ? -16777216 : ModuleManager.themeColor.getColor(x, y);
         RenderUtils.drawRect(x, y, x + w, y + h, hudColor);

         GlStateManager.popMatrix();
      }
   }
}
