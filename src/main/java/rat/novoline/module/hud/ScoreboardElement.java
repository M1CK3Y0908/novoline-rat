package rat.novoline.module.hud;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import rat.novoline.module.ModuleManager;
import rat.novoline.module.misc.NameProtect;
import rat.novoline.utils.math.SmoothingUtils;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.utils.render.RenderUtils;

public class ScoreboardElement extends HudElement {
   public static ScoreboardElement instance;
   private static final Minecraft mc = Minecraft.getMinecraft();

   private float animatedY = -1.0F;
   private boolean hasUserMoved = false;
   private float width = 120.0F;
   private float height = 80.0F;

   // Cached render state for draw and post-processing
   private static float cachedX = 0.0F;
   private static float cachedY = 0.0F;
   private static float cachedWidth = 0.0F;
   private static float cachedHeight = 0.0F;
   private static boolean hasActiveScoreboard = false;

   public static class ScoreLine {
      public String text;
      public String scoreStr;
      public boolean isWatermark;
   }

   private static final List<ScoreLine> cachedLines = new ArrayList<>();
   private static String cachedTitle = "";

   public ScoreboardElement() {
      super("scoreboard", 0.0F, 0.0F, true);
      instance = this;
   }

   @Override
   public float getX() {
      return this.x;
   }

   @Override
   public float getY() {
      return this.animatedY >= 0.0F ? this.animatedY : this.y;
   }

   @Override
   public float getWidth() {
      return this.width > 0.0F ? this.width : 120.0F;
   }

   @Override
   public float getHeight() {
      return this.height > 0.0F ? this.height : 80.0F;
   }

   @Override
   public void updateDragging(ScaledResolution scaledResolution, float f, float f2) {
      super.updateDragging(scaledResolution, f, f2);
      if (this.isDragging()) {
         this.hasUserMoved = true;
         this.animatedY = this.y;
      }
   }

   public static ScoreObjective getScoreObjective() {
      if (mc.theWorld == null || mc.thePlayer == null) {
         return null;
      }

      net.minecraft.scoreboard.Scoreboard scoreboard = mc.theWorld.getScoreboard();
      if (scoreboard == null) {
         return null;
      }

      ScoreObjective scoreObjective = null;
      ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(mc.thePlayer.getName());

      if (scorePlayerTeam != null) {
         int colorIndex = scorePlayerTeam.getChatFormat().getColorIndex();
         if (colorIndex >= 0) {
            scoreObjective = scoreboard.getObjectiveInDisplaySlot(3 + colorIndex);
         }
      }

      return scoreObjective != null ? scoreObjective : scoreboard.getObjectiveInDisplaySlot(1);
   }

   private float calculateTargetY(ScaledResolution sr) {
      Scoreboard scoreboardMod = ModuleManager.scoreboard;
      if (scoreboardMod == null || !scoreboardMod.avoidArrayList.isEnabled()) {
         return this.y;
      }
      if (ModuleManager.arrayList == null || !ModuleManager.arrayList.isEnabled() || ArrayListElement.instance == null) {
         return this.y;
      }
      if (ArrayListElement.getEntryCount() <= 0) {
         return this.y;
      }

      float alX = ArrayListElement.instance.getX();
      float alWidth = ArrayListElement.instance.getWidth();
      float alRight = alX + alWidth;
      float alY = ArrayListElement.instance.getY();
      float alBottom = alY + ArrayListElement.instance.getHeight();

      float sbX = this.getX();
      float sbRight = sbX + this.getWidth();

      // Check horizontal overlap between Scoreboard and ArrayList
      if (sbX < alRight && sbRight > alX) {
         boolean isAlBottom = sr.getFloat2() / 2.0F < alY;
         if (isAlBottom) {
            // ArrayList at bottom growing upwards: avoid by moving above ArrayList
            float safeY = alY - this.height - 8.0F;
            if (safeY < this.y) {
               return Math.max(4.0F, safeY);
            }
         } else {
            // ArrayList at top growing downwards: avoid by moving below ArrayList
            float safeY = alBottom + 8.0F;
            if (safeY > this.y) {
               float maxY = sr.getFloat2() - this.height - 4.0F;
               return Math.min(safeY, maxY);
            }
         }
      }
      return this.y;
   }

   public static void updateScoreboard(ScaledResolution sr) {
      if (instance == null) return;
      Scoreboard scoreboardMod = ModuleManager.scoreboard;
      if (scoreboardMod == null || !scoreboardMod.isEnabled()) {
         hasActiveScoreboard = false;
         return;
      }

      ScoreObjective objective = getScoreObjective();
      if (objective == null) {
         hasActiveScoreboard = false;
         return;
      }

      hasActiveScoreboard = true;
      net.minecraft.scoreboard.Scoreboard scoreboard = objective.getScoreboard();
      Collection<Score> collection = scoreboard.getSortedScores(objective);
      List<Score> list = Lists.newArrayList(Iterables.filter(collection, s -> s.getPlayerName() != null && !s.getPlayerName().startsWith("#")));

      String serverIpMode = scoreboardMod.serverIp.getCurrentElementAsString();
      if (!list.isEmpty() && "Novoline".equalsIgnoreCase(serverIpMode)) {
         Score score = list.get(0);
         ScorePlayerTeam playersTeam = scoreboard.getPlayersTeam(score.getPlayerName());
         String text = ScorePlayerTeam.formatPlayerName(playersTeam, score.getPlayerName());
         if (!text.contains(".")) {
            list.add(0, new Score(scoreboard, objective, "www.novoline.rat"));
            list.add(1, new Score(scoreboard, objective, "§a§l"));
         }
      } else if ("None".equalsIgnoreCase(serverIpMode)) {
         list.removeIf(s -> {
            String name = s.getPlayerName();
            return name != null && (name.contains(".net") || name.contains(".com") || name.contains(".org") || name.contains(".io") || name.contains(".rat"));
         });
      }

      if (list.size() > 15) {
         list = Lists.newArrayList(Iterables.skip(list, list.size() - 15));
      }

      FontAdapter font = scoreboardMod.getFont();
      FontAdapter titleFont = scoreboardMod.getTitleFont();
      float fontHeight = font.getHeight();
      float rowHeight = fontHeight + 3.0F;
      float headerHeight = titleFont.getHeight() + 4.0F;

      cachedTitle = objective.getDisplayName();
      float maxTextWidth = titleFont.getStringWidth(cachedTitle);
      float maxScoreWidth = 0.0F;
      boolean hideNumbers = scoreboardMod.hideNumbers.isEnabled();
      NameProtect nameProtect = ModuleManager.nameProtect;

      cachedLines.clear();
      List<Score> displayList = Lists.newArrayList(list);
      Collections.reverse(displayList);

      for (int i = 0; i < displayList.size(); i++) {
         Score score = displayList.get(i);
         ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
         String text = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());
         if (nameProtect != null && nameProtect.isEnabled() && nameProtect.getHideIn().isSelected(3)) {
            text = nameProtect.protectText(text);
         }

         ScoreLine line = new ScoreLine();
         line.text = text;
         line.isWatermark = (i == displayList.size() - 1) && text.contains("novoline.rat");

         maxTextWidth = Math.max(maxTextWidth, font.getStringWidth(text));
         if (!hideNumbers) {
            line.scoreStr = EnumChatFormatting.RED + String.valueOf(score.getScorePoints());
            maxScoreWidth = Math.max(maxScoreWidth, font.getStringWidth(line.scoreStr));
         } else {
            line.scoreStr = "";
         }
         cachedLines.add(line);
      }

      float padX = 6.0F;
      float gap = hideNumbers ? 0.0F : 8.0F;
      float contentWidth = maxTextWidth + (hideNumbers ? 0.0F : maxScoreWidth + gap);
      float totalWidth = contentWidth + padX * 2.0F;
      float totalHeight = headerHeight + cachedLines.size() * rowHeight + 3.0F;

      instance.width = totalWidth;
      instance.height = totalHeight;

      if (!instance.hasUserMoved) {
         instance.x = sr.getFloat() - totalWidth - 4.0F;
         instance.y = sr.getFloat2() / 2.0F - totalHeight / 2.0F;
      }

      float targetY = instance.calculateTargetY(sr);
      if (instance.animatedY < 0.0F) {
         instance.animatedY = targetY;
      } else if (instance.isDragging()) {
         instance.animatedY = instance.y;
      } else {
         instance.animatedY = SmoothingUtils.approachWithMinimumStep(instance.animatedY, targetY, 12.0F, 0.05F);
      }

      cachedX = instance.getX();
      cachedY = instance.getY();
      cachedWidth = instance.width;
      cachedHeight = instance.height;
   }

   @Override
   public void draw(ScaledResolution scaledResolution, float partialTicks, boolean inEditor) {
      if (Minecraft.getMinecraft().gameSettings.showDebugInfo || ModuleManager.scoreboard == null || !ModuleManager.scoreboard.isEnabled()) {
         return;
      }
      if (ModuleManager.noRender != null && ModuleManager.noRender.isEnabled() && ModuleManager.noRender.scoreboard.isEnabled()) {
         return;
      }

      if (!hasActiveScoreboard) {
         if (inEditor) {
            this.drawSampleScoreboard(scaledResolution);
         }
         return;
      }

      Scoreboard scoreboardMod = ModuleManager.scoreboard;
      FontAdapter font = scoreboardMod.getFont();
      FontAdapter titleFont = scoreboardMod.getTitleFont();
      float fontHeight = font.getHeight();
      float rowHeight = fontHeight + 3.0F;
      float headerHeight = titleFont.getHeight() + 4.0F;

      float x = this.getX();
      float y = this.getY();
      float w = this.width;
      float h = this.height;
      float padX = 6.0F;

      // 1. Background
      if (scoreboardMod.background.isEnabled()) {
         int bgColor = scoreboardMod.backgroundColor.getColor();
         RenderUtils.drawRect(x, y, x + w, y + h, bgColor);

         // Header bar (slightly darker tint)
         int headerBg = (bgColor & 0x00FFFFFF) | (Math.min(255, ((bgColor >>> 24) * 3) / 2) << 24);
         RenderUtils.drawRect(x, y, x + w, y + headerHeight, headerBg);
         RenderUtils.drawRect(x, y + headerHeight - 1.0F, x + w, y + headerHeight, 0x1FFFFFFF);
      }

      // 2. Custom Outline (Identical to ArrayList)
      if (scoreboardMod.hasOutline()) {
         float lw = scoreboardMod.lineWidth.getCurrentValue();
         boolean drawTop = scoreboardMod.outline.isSelected("Top");
         boolean drawBottom = scoreboardMod.outline.isSelected("Bottom");
         boolean drawLeft = scoreboardMod.outline.isSelected("Left");
         boolean drawRight = scoreboardMod.outline.isSelected("Right");

         int outlineColor = ModuleManager.themeColor.getColor(x, y);

         if (drawTop) {
            RenderUtils.drawRect(x, y, x + w, y + lw, outlineColor);
         }
         if (drawBottom) {
            RenderUtils.drawRect(x, y + h - lw, x + w, y + h, outlineColor);
         }
         if (drawLeft) {
            RenderUtils.drawRect(x, y, x + lw, y + h, outlineColor);
         }
         if (drawRight) {
            RenderUtils.drawRect(x + w - lw, y, x + w, y + h, outlineColor);
         }
      }

      // 3. Header Title
      float titleWidth = titleFont.getStringWidth(cachedTitle);
      float titleX = x + (w - titleWidth) / 2.0F;
      float titleY = y + (headerHeight - titleFont.getHeight()) / 2.0F;
      titleFont.drawStringWithShadow(cachedTitle, titleX, titleY, -1);

      // 4. Score Lines
      boolean hideNumbers = scoreboardMod.hideNumbers.isEnabled();
      float lineY = y + headerHeight + 2.0F;

      for (ScoreLine line : cachedLines) {
         float textY = lineY + (rowHeight - fontHeight) / 2.0F;
         if (line.isWatermark) {
            font.drawStringWithShadow(line.text, x + padX, textY, ModuleManager.themeColor.getColor(x + padX, textY));
         } else {
            font.drawStringWithShadow(line.text, x + padX, textY, -1);
         }

         if (!hideNumbers && line.scoreStr != null && !line.scoreStr.isEmpty()) {
            float scoreX = x + w - padX - font.getStringWidth(line.scoreStr);
            font.drawStringWithShadow(line.scoreStr, scoreX, textY, -1);
         }

         lineY += rowHeight;
      }
   }

   public static void renderScoreboardEffects(boolean bloom, boolean glow, ScaledResolution sr) {
      if (Minecraft.getMinecraft().gameSettings.showDebugInfo || ModuleManager.scoreboard == null || !ModuleManager.scoreboard.isEnabled()) {
         return;
      }
      if (ModuleManager.noRender != null && ModuleManager.noRender.isEnabled() && ModuleManager.noRender.scoreboard.isEnabled()) {
         return;
      }
      if (!hasActiveScoreboard || cachedWidth <= 0.0F || cachedHeight <= 0.0F) {
         return;
      }

      Scoreboard scoreboardMod = ModuleManager.scoreboard;
      boolean bg = scoreboardMod.background.isEnabled();
      boolean drawTop = scoreboardMod.outline.isSelected("Top");
      boolean drawBottom = scoreboardMod.outline.isSelected("Bottom");
      boolean drawLeft = scoreboardMod.outline.isSelected("Left");
      boolean drawRight = scoreboardMod.outline.isSelected("Right");
      boolean hasOutline = drawTop || drawBottom || drawLeft || drawRight;

      if (!bg && !hasOutline) {
         return;
      }

      float x = cachedX;
      float y = cachedY;
      float w = cachedWidth;
      float h = cachedHeight;
      float lw = scoreboardMod.lineWidth.getCurrentValue();

      if (bg) {
         if (!bloom || !glow) {
            // Blur & Shadow pass: solid black background
            RenderUtils.drawRect(x, y, x + w, y + h, -16777216);
         }
      }

      if (hasOutline) {
         int outlineColor = (!bloom || !glow) ? -16777216 : ModuleManager.themeColor.getColor(x, y);
         if (drawTop) {
            RenderUtils.drawRect(x, y, x + w, y + lw, outlineColor);
         }
         if (drawBottom) {
            RenderUtils.drawRect(x, y + h - lw, x + w, y + h, outlineColor);
         }
         if (drawLeft) {
            RenderUtils.drawRect(x, y, x + lw, y + h, outlineColor);
         }
         if (drawRight) {
            RenderUtils.drawRect(x + w - lw, y, x + w, y + h, outlineColor);
         }
      }
   }

   private void drawSampleScoreboard(ScaledResolution sr) {
      Scoreboard scoreboardMod = ModuleManager.scoreboard;
      FontAdapter font = scoreboardMod.getFont();
      FontAdapter titleFont = scoreboardMod.getTitleFont();
      float fontHeight = font.getHeight();
      float rowHeight = fontHeight + 3.0F;
      float headerHeight = titleFont.getHeight() + 4.0F;

      String sampleTitle = "§lNovoline";
      String[] sampleLines = new String[] {
         "www.novoline.rat",
         "Rank: §bUser",
         "Coins: §61,234",
         "Kills: §a12",
         "Ping: §735ms"
      };
      int[] sampleScores = new int[] { 5, 4, 3, 2, 1 };

      float maxTextWidth = titleFont.getStringWidth(sampleTitle);
      float maxScoreWidth = 0.0F;
      boolean hideNumbers = scoreboardMod.hideNumbers.isEnabled();

      for (int i = 0; i < sampleLines.length; i++) {
         maxTextWidth = Math.max(maxTextWidth, font.getStringWidth(sampleLines[i]));
         if (!hideNumbers) {
            String s = EnumChatFormatting.RED + String.valueOf(sampleScores[i]);
            maxScoreWidth = Math.max(maxScoreWidth, font.getStringWidth(s));
         }
      }

      float padX = 6.0F;
      float gap = hideNumbers ? 0.0F : 8.0F;
      float contentWidth = maxTextWidth + (hideNumbers ? 0.0F : maxScoreWidth + gap);
      float totalWidth = contentWidth + padX * 2.0F;
      float totalHeight = headerHeight + sampleLines.length * rowHeight + 3.0F;

      this.width = totalWidth;
      this.height = totalHeight;

      if (!this.hasUserMoved) {
         this.x = sr.getFloat() - totalWidth - 4.0F;
         this.y = sr.getFloat2() / 2.0F - totalHeight / 2.0F;
      }

      float x = this.getX();
      float y = this.getY();
      float w = this.width;
      float h = this.height;

      if (scoreboardMod.background.isEnabled()) {
         int bgColor = scoreboardMod.backgroundColor.getColor();
         RenderUtils.drawRect(x, y, x + w, y + h, bgColor);
         int headerBg = (bgColor & 0x00FFFFFF) | (Math.min(255, ((bgColor >>> 24) * 3) / 2) << 24);
         RenderUtils.drawRect(x, y, x + w, y + headerHeight, headerBg);
         RenderUtils.drawRect(x, y + headerHeight - 1.0F, x + w, y + headerHeight, 0x1FFFFFFF);
      }

      if (scoreboardMod.hasOutline()) {
         float lw = scoreboardMod.lineWidth.getCurrentValue();
         boolean drawTop = scoreboardMod.outline.isSelected("Top");
         boolean drawBottom = scoreboardMod.outline.isSelected("Bottom");
         boolean drawLeft = scoreboardMod.outline.isSelected("Left");
         boolean drawRight = scoreboardMod.outline.isSelected("Right");
         int outlineColor = ModuleManager.themeColor.getColor(x, y);
         if (drawTop) RenderUtils.drawRect(x, y, x + w, y + lw, outlineColor);
         if (drawBottom) RenderUtils.drawRect(x, y + h - lw, x + w, y + h, outlineColor);
         if (drawLeft) RenderUtils.drawRect(x, y, x + lw, y + h, outlineColor);
         if (drawRight) RenderUtils.drawRect(x + w - lw, y, x + w, y + h, outlineColor);
      }

      float titleX = x + (w - titleFont.getStringWidth(sampleTitle)) / 2.0F;
      float titleY = y + (headerHeight - titleFont.getHeight()) / 2.0F;
      titleFont.drawStringWithShadow(sampleTitle, titleX, titleY, -1);

      float lineY = y + headerHeight + 2.0F;
      for (int i = 0; i < sampleLines.length; i++) {
         float textY = lineY + (rowHeight - fontHeight) / 2.0F;
         if (i == 0) {
            font.drawStringWithShadow(sampleLines[i], x + padX, textY, ModuleManager.themeColor.getColor(x + padX, textY));
         } else {
            font.drawStringWithShadow(sampleLines[i], x + padX, textY, -1);
         }
         if (!hideNumbers) {
            String s = EnumChatFormatting.RED + String.valueOf(sampleScores[i]);
            float scoreX = x + w - padX - font.getStringWidth(s);
            font.drawStringWithShadow(s, scoreX, textY, -1);
         }
         lineY += rowHeight;
      }
   }
}
