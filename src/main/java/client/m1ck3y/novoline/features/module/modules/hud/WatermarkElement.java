package client.m1ck3y.novoline.features.module.modules.hud;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import client.m1ck3y.novoline.Novoline;
import client.m1ck3y.novoline.Novoline.Fonts;
import client.m1ck3y.novoline.features.module.ModuleManager;
import client.m1ck3y.novoline.utils.hypixel.HypixelState;
import client.m1ck3y.novoline.utils.render.FontAdapter;
import client.m1ck3y.novoline.utils.render.FilledPolygonRenderer;
import client.m1ck3y.novoline.utils.render.RenderUtils;

public class WatermarkElement extends HudElement {
   public static WatermarkElement instance;
   private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
   private String watermarkText;
   private float width;
   private float height = 13.0F;

   public WatermarkElement() {
      super("watermark", 5.0F, 5.0F, true, false);
      instance = this;
   }

   @Override
   public float getWidth() {
      return this.width;
   }

   @Override
   public float getHeight() {
      return this.height;
   }

   @Override
   public void draw(ScaledResolution scaledResolution, float f, boolean flag) {
      if (ModuleManager.watermark.isEnabled()) {
         String type = ModuleManager.watermark.watermark.getCurrentElementAsString();
         if ("Classic".equalsIgnoreCase(type)) {
            FontAdapter customFont = ModuleManager.watermark.getFont(false);
            FontAdapter font = customFont != null ? customFont : (Fonts.sfRegularSize20 != null ? Fonts.sfRegularSize20 : Fonts.rubikRegularSize18);
            String name = "Novoline";
            float curX = this.getX();
            float curY = this.getY();

            for (int i = 0; i < name.length(); i++) {
               String ch = String.valueOf(name.charAt(i));
               int color = ModuleManager.themeColor.getColor(curX, curY, i, name.length());
               font.drawStringWithShadow(ch, curX, curY, color);
               curX += (float) font.getStringWidth(ch);
            }

            if (ModuleManager.watermark.showTime.isEnabled()) {
               curX += 2.0F;
               String timeStr = TIME_FORMAT.format(new Date());
               String timeFull = "§7(§f" + timeStr + "§7)";
               font.drawStringWithShadow(timeFull, curX, curY, ModuleManager.themeColor.getColor(curX, curY, -1));
               curX += (float) font.getStringWidth(timeFull);
            }

            this.width = curX - this.getX();
            this.height = (float) font.getHeight() + 2.0F;
         } else if ("OneTap".equalsIgnoreCase(type)) {
            StringBuilder stringBuilder = new StringBuilder("Novoline")
               .append(" | ")
               .append(Novoline.getUserName())
               .append(" | ")
               .append(Minecraft.getDebugFPS())
               .append("fps | ");
            ServerData currentServerData = Minecraft.getMinecraft().getCurrentServerData();
            if (currentServerData != null && !currentServerData.isOnLAN()) {
               stringBuilder.append(currentServerData.serverIP.split(":")[0]);
            } else {
               stringBuilder.append("Singleplayer");
            }

            if (ModuleManager.watermark.showPlayTime.isEnabled()) {
               Duration duration = Duration.between(HypixelState.getSessionStart(), Instant.now());
               String text = String.format(Locale.ENGLISH, "%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
               stringBuilder.append(" | ").append(text);
            }

            this.watermarkText = stringBuilder.toString();
            FontAdapter customFont = ModuleManager.watermark.getFont(false);
            FontAdapter cFontRenderer = customFont != null ? customFont : Fonts.robotoRegularSize14;
            boolean postProcessingActive = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isEnabled();
            if (!postProcessingActive) {
               RenderUtils.drawShadow(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 1.5F, 1610612736);
            }
            int bgColor = ModuleManager.themeColor.getBackgroundColor();
            RenderUtils.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), bgColor);
            RenderUtils.drawThemeGradientBar(
               this.getX(),
               this.getY(),
               this.getX() + this.getWidth(),
               this.getY() + 1.0F
            );
            cFontRenderer.drawStringWithShadow(
               this.watermarkText,
               this.getX() + this.getWidth() / 2.0F - cFontRenderer.getStringWidth(this.watermarkText) / 2.0F,
               this.getY() + this.getHeight() / 2.0F - cFontRenderer.getHeight() / 2.0F + 0.5F,
               -1
            );
            this.width = cFontRenderer.getStringWidth(this.watermarkText) + 5.0F;
            this.height = 13.0F;
         } else if ("NeverLose".equalsIgnoreCase(type)) {
            StringBuilder stringBuilder2 = new StringBuilder("§8|§r ").append(Novoline.getUserName()).append(" §8|§r ").append(Minecraft.getDebugFPS()).append("fps §8|§r ");
            ServerData currentServerData2 = Minecraft.getMinecraft().getCurrentServerData();
            if (currentServerData2 != null && !currentServerData2.isOnLAN()) {
               stringBuilder2.append(currentServerData2.serverIP.split(":")[0]);
            } else {
               stringBuilder2.append("Singleplayer");
            }

            if (ModuleManager.watermark.showPlayTime.isEnabled()) {
               Duration duration2 = Duration.between(HypixelState.getSessionStart(), Instant.now());
               String text2 = String.format(Locale.ENGLISH, "%02d:%02d:%02d", duration2.toHours(), duration2.toMinutesPart(), duration2.toSecondsPart());
               stringBuilder2.append(" §8|§r ").append(text2);
            }

            this.watermarkText = stringBuilder2.toString();
            FontAdapter customBold = ModuleManager.watermark.getFont(true);
            FontAdapter customNorm = ModuleManager.watermark.getFont(false);
            FontAdapter cFontRenderer2 = customBold != null ? customBold : Fonts.museoSans900Size16;
            FontAdapter cFontRenderer3 = customNorm != null ? customNorm : Fonts.museoSans700Size14;
            String text3 = "Novoline".toUpperCase(Locale.ROOT);
            this.width = cFontRenderer2.getStringWidth(text3) + cFontRenderer3.getStringWidth(this.watermarkText) + 9.0F;
            this.height = 14.0F;
            boolean postProcessingActive2 = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isEnabled();
            if (!postProcessingActive2) {
               RenderUtils.drawShadow(
                  this.getX() + 1.0F, this.getY() + 1.0F, this.getX() + this.getWidth() - 1.0F, this.getY() + this.getHeight(), 2.5F, -285212672
               );
            }
            int bgColor = ModuleManager.themeColor.getBackgroundColor();
            FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
            filledPolygonRenderer.putRoundedRect(this.getX(), this.getY(), this.width, 14.0F, 1.0F, bgColor);
            filledPolygonRenderer.draw();
            cFontRenderer2.drawStringWithShadow(text3, this.getX() + 3.0F, this.getY() + 5.0F, -1);
            cFontRenderer3.drawStringWithShadow(this.watermarkText, this.getX() + 3.0F + cFontRenderer2.getStringWidth(text3) + 3.0F, this.getY() + 5.0F + 0.5F, -1);
         }
      }
   }

   public static void renderWatermarkEffects(boolean bloom) {
      boolean glow = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isGlowEnabled("Watermark");
      renderWatermarkEffects(bloom, glow);
   }

   public static void renderWatermarkEffects(boolean bloom, boolean glow) {
      if (instance == null || ModuleManager.watermark == null || !ModuleManager.watermark.isEnabled()) {
         return;
      }
      float x = instance.getX();
      float y = instance.getY();
      float w = instance.getWidth();
      float h = instance.getHeight();
      String type = ModuleManager.watermark.watermark.getCurrentElementAsString();

      if ("Classic".equalsIgnoreCase(type)) {
         if (glow) {
            FontAdapter customFont = ModuleManager.watermark.getFont(false);
            FontAdapter font = customFont != null ? customFont : (Fonts.sfRegularSize20 != null ? Fonts.sfRegularSize20 : Fonts.rubikRegularSize18);
            String name = "Novoline";
            float curX = x;
            float curY = y;

            for (int i = 0; i < name.length(); i++) {
               String ch = String.valueOf(name.charAt(i));
               int color = ModuleManager.themeColor.getColor(curX, curY, i, name.length());
               font.drawString(ch, curX, curY, color);
               curX += (float) font.getStringWidth(ch);
            }

            if (ModuleManager.watermark.showTime.isEnabled()) {
               curX += 2.0F;
               String timeStr = TIME_FORMAT.format(new Date());
               String timeFull = "§7(§f" + timeStr + "§7)";
               font.drawString(timeFull, curX, curY, ModuleManager.themeColor.getColor(curX, curY, -1));
            }
         }
      } else if (!bloom || !glow) {
         if ("OneTap".equalsIgnoreCase(type)) {
            RenderUtils.drawRect(x, y, x + w, y + h, -16777216);
         } else if ("NeverLose".equalsIgnoreCase(type)) {
            FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
            filledPolygonRenderer.putRoundedRect(x, y, w, 14.0F, 1.0F, -16777216);
            filledPolygonRenderer.draw();
         }
      } else {
         if ("OneTap".equalsIgnoreCase(type)) {
            RenderUtils.drawThemeGradientBar(x, y, x + w, y + 1.0F);
         } else if ("NeverLose".equalsIgnoreCase(type)) {
            int cTheme = ModuleManager.themeColor.getColor(x, y);
            FilledPolygonRenderer fpr = new FilledPolygonRenderer();
            fpr.putRoundedRect(x, y, w, 14.0F, 1.0F, cTheme);
            fpr.draw();
         }
      }
   }
}
