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
import rat.novoline.utils.time.Stopwatch;

public class ScoreboardElement extends HudElement {
   public static ScoreboardElement instance;
   private static final Minecraft mc = Minecraft.getMinecraft();

   private float animatedY = -1.0F;
   private boolean hasUserMoved = false;
   private float width = 120.0F;
   private float height = 80.0F;

   private final Stopwatch avoidTimer = new Stopwatch();
   private boolean wasAvoiding = false;

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
         this.wasAvoiding = false;
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
         this.wasAvoiding = false;
         return this.y;
      }
      if (ModuleManager.arrayList == null || !ModuleManager.arrayList.isEnabled() || ArrayListElement.instance == null) {
         this.wasAvoiding = false;
         return this.y;
      }
      if (ArrayListElement.getEntryCount() <= 0) {
         this.wasAvoiding = false;
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
         float safeY = this.y;
         boolean needsAvoid = false;

         if (isAlBottom) {
            // ArrayList at bottom growing upwards: avoid by moving above ArrayList
            float candidateY = alY - this.height - 8.0F;
            if (candidateY < this.y) {
               safeY = Math.max(4.0F, candidateY);
               needsAvoid = true;
            }
         } else {
            // ArrayList at top growing downwards: avoid by moving below ArrayList
            float candidateY = alBottom + 8.0F;
            if (candidateY > this.y) {
               float maxY = sr.getFloat2() - this.height - 4.0F;
               safeY = Math.min(candidateY, maxY);
               needsAvoid = true;
            }
         }

         if (needsAvoid) {
            if (!this.wasAvoiding) {
               this.wasAvoiding = true;
               this.avoidTimer.reset();
            }
            // 0.3s (300ms) delay before starting to avoid
            if (!this.avoidTimer.hasElapsed(300L)) {
               return this.y;
            }
            return safeY;
         }
      }

      this.wasAvoiding = false;
      return this.y;
   }

   public static boolean isServerIp(String text) {
      if (text == null || text.trim().isEmpty()) {
         return false;
      }
      String clean = EnumChatFormatting.getTextWithoutFormattingCodes(text).trim().toLowerCase();
      if (clean.contains("novoline.rat") || clean.contains("hypixel.net") || clean.contains("minemen.club")
            || clean.contains("blocksmc.com") || clean.contains("loyisa.cn")) {
         return true;
      }
      if (clean.startsWith("www.") || clean.startsWith("play.") || clean.startsWith("mc.") || clean.startsWith("pvp.") || clean.startsWith("store.")) {
         return true;
      }
      String[] tlds = new String[] {
         ".net", ".com", ".org", ".io", ".club", ".gg", ".cc", ".ru", ".cn",
         ".de", ".me", ".tv", ".rip", ".top", ".xyz", ".fun", ".games", ".lol",
         ".vip", ".pro", ".co", ".us", ".uk", ".eu", ".rat"
      };
      for (String tld : tlds) {
         if (clean.contains(tld)) {
            return true;
         }
      }
      return clean.matches(".*\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.*");
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
      String serverIpMode = scoreboardMod.serverIp.getCurrentElementAsString();

      cachedLines.clear();
      List<Score> displayList = Lists.newArrayList(list);
      Collections.reverse(displayList);

      // Identify server IP line
      int ipIndex = -1;
      for (int i = displayList.size() - 1; i >= 0; i--) {
         Score score = displayList.get(i);
         ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
         String raw = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());
         if (isServerIp(raw)) {
            ipIndex = i;
            break;
         }
      }
      if (ipIndex == -1 && !displayList.isEmpty()) {
         Score score = displayList.get(displayList.size() - 1);
         ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
         String raw = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());
         String clean = EnumChatFormatting.getTextWithoutFormattingCodes(raw).trim();
         if (clean.contains(".")) {
            ipIndex = displayList.size() - 1;
         }
      }

      boolean replacedAny = false;
      for (int i = 0; i < displayList.size(); i++) {
         Score score = displayList.get(i);
         ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
         String text = ScorePlayerTeam.formatPlayerName(team, score.getPlayerName());
         if (nameProtect != null && nameProtect.isEnabled() && nameProtect.getHideIn().isSelected(3)) {
            text = nameProtect.protectText(text);
         }

         boolean isThisIp = (i == ipIndex) || isServerIp(text);
         if (isThisIp && "None".equalsIgnoreCase(serverIpMode)) {
            continue;
         }

         ScoreLine line = new ScoreLine();
         if (isThisIp && "Novoline".equalsIgnoreCase(serverIpMode)) {
            line.text = "www.novoline.rat";
            line.isWatermark = true;
            line.scoreStr = "";
            replacedAny = true;
         } else {
            line.text = text;
            line.isWatermark = false;
            if (!hideNumbers) {
               line.scoreStr = EnumChatFormatting.RED + String.valueOf(score.getScorePoints());
               maxScoreWidth = Math.max(maxScoreWidth, font.getStringWidth(line.scoreStr));
            } else {
               line.scoreStr = "";
            }
         }

         maxTextWidth = Math.max(maxTextWidth, font.getStringWidth(line.text));
         cachedLines.add(line);
      }

      // If "Novoline" mode is selected but no server IP line was replaced, append watermark at bottom
      if ("Novoline".equalsIgnoreCase(serverIpMode) && !replacedAny) {
         ScoreLine wm = new ScoreLine();
         wm.text = "www.novoline.rat";
         wm.isWatermark = true;
         wm.scoreStr = "";
         maxTextWidth = Math.max(maxTextWidth, font.getStringWidth(wm.text));
         cachedLines.add(wm);
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

   private static void drawWatermarkText(FontAdapter font, String text, float startX, float textY) {
      float curCharX = startX;
      int len = Math.max(1, text.length());
      for (int ci = 0; ci < text.length(); ci++) {
         String ch = String.valueOf(text.charAt(ci));
         int charColor = ModuleManager.themeColor.getColor(curCharX, textY, ci, len);
         font.drawStringWithShadow(ch, curCharX, textY, charColor);
         curCharX += (float) font.getStringWidth(ch);
      }
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

      // 1. Unified Background (Title and Content together with the same color)
      if (scoreboardMod.background.isEnabled()) {
         int bgColor = ModuleManager.themeColor.getBackgroundColor();
         RenderUtils.drawRect(x, y, x + w, y + h, bgColor);
      }

      // 2. Custom Outer Outline (Outside bounds to avoid obstructing text)
      if (scoreboardMod.hasOutline()) {
         float lw = scoreboardMod.lineWidth.getCurrentValue();
         boolean drawTop = scoreboardMod.outline.isSelected("Top");
         boolean drawBottom = scoreboardMod.outline.isSelected("Bottom");
         boolean drawLeft = scoreboardMod.outline.isSelected("Left");
         boolean drawRight = scoreboardMod.outline.isSelected("Right");

         float topX1 = x - (drawLeft ? lw : 0.0F);
         float topX2 = x + w + (drawRight ? lw : 0.0F);

         if (drawTop) {
            RenderUtils.drawThemeGradientBar(topX1, y - lw, topX2, y);
         }
         if (drawBottom) {
            RenderUtils.drawThemeGradientBar(topX1, y + h, topX2, y + h + lw);
         }
         if (drawLeft) {
            RenderUtils.drawThemeGradientBar(x - lw, y, x, y + h);
         }
         if (drawRight) {
            RenderUtils.drawThemeGradientBar(x + w, y, x + w + lw, y + h);
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
            drawWatermarkText(font, line.text, x + padX, textY);
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
         float topX1 = x - (drawLeft ? lw : 0.0F);
         float topX2 = x + w + (drawRight ? lw : 0.0F);

         if (!bloom || !glow) {
            if (drawTop) {
               RenderUtils.drawRect(topX1, y - lw, topX2, y, -16777216);
            }
            if (drawBottom) {
               RenderUtils.drawRect(topX1, y + h, topX2, y + h + lw, -16777216);
            }
            if (drawLeft) {
               RenderUtils.drawRect(x - lw, y, x, y + h, -16777216);
            }
            if (drawRight) {
               RenderUtils.drawRect(x + w, y, x + w + lw, y + h, -16777216);
            }
         } else {
            if (drawTop) {
               RenderUtils.drawThemeGradientBar(topX1, y - lw, topX2, y);
            }
            if (drawBottom) {
               RenderUtils.drawThemeGradientBar(topX1, y + h, topX2, y + h + lw);
            }
            if (drawLeft) {
               RenderUtils.drawThemeGradientBar(x - lw, y, x, y + h);
            }
            if (drawRight) {
               RenderUtils.drawThemeGradientBar(x + w, y, x + w + lw, y + h);
            }
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
         "Rank: §bUser",
         "Coins: §61,234",
         "Kills: §a12",
         "Ping: §735ms",
         "www.novoline.rat"
      };
      int[] sampleScores = new int[] { 4, 3, 2, 1, 0 };

      float maxTextWidth = titleFont.getStringWidth(sampleTitle);
      float maxScoreWidth = 0.0F;
      boolean hideNumbers = scoreboardMod.hideNumbers.isEnabled();

      for (int i = 0; i < sampleLines.length; i++) {
         maxTextWidth = Math.max(maxTextWidth, font.getStringWidth(sampleLines[i]));
         if (!hideNumbers && i != sampleLines.length - 1) {
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
         int bgColor = ModuleManager.themeColor.getBackgroundColor();
         RenderUtils.drawRect(x, y, x + w, y + h, bgColor);
      }

      if (scoreboardMod.hasOutline()) {
         float lw = scoreboardMod.lineWidth.getCurrentValue();
         boolean drawTop = scoreboardMod.outline.isSelected("Top");
         boolean drawBottom = scoreboardMod.outline.isSelected("Bottom");
         boolean drawLeft = scoreboardMod.outline.isSelected("Left");
         boolean drawRight = scoreboardMod.outline.isSelected("Right");
         float topX1 = x - (drawLeft ? lw : 0.0F);
         float topX2 = x + w + (drawRight ? lw : 0.0F);

         if (drawTop) {
            RenderUtils.drawThemeGradientBar(topX1, y - lw, topX2, y);
         }
         if (drawBottom) {
            RenderUtils.drawThemeGradientBar(topX1, y + h, topX2, y + h + lw);
         }
         if (drawLeft) {
            RenderUtils.drawThemeGradientBar(x - lw, y, x, y + h);
         }
         if (drawRight) {
            RenderUtils.drawThemeGradientBar(x + w, y, x + w + lw, y + h);
         }
      }

      float titleX = x + (w - titleFont.getStringWidth(sampleTitle)) / 2.0F;
      float titleY = y + (headerHeight - titleFont.getHeight()) / 2.0F;
      titleFont.drawStringWithShadow(sampleTitle, titleX, titleY, -1);

      float lineY = y + headerHeight + 2.0F;
      for (int i = 0; i < sampleLines.length; i++) {
         float textY = lineY + (rowHeight - fontHeight) / 2.0F;
         if (i == sampleLines.length - 1) {
            drawWatermarkText(font, sampleLines[i], x + padX, textY);
         } else {
            font.drawStringWithShadow(sampleLines[i], x + padX, textY, -1);
         }
         if (!hideNumbers && i != sampleLines.length - 1) {
            String s = EnumChatFormatting.RED + String.valueOf(sampleScores[i]);
            float scoreX = x + w - padX - font.getStringWidth(s);
            font.drawStringWithShadow(s, scoreX, textY, -1);
         }
         lineY += rowHeight;
      }
   }
}
