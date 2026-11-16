package wtf.novoline.module.hud;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.render.FontAdapter;
import wtf.novoline.utils.render.FilledPolygonRenderer;
import wtf.novoline.utils.render.RenderUtils;

public class WatermarkElement extends HudElement {
   public static WatermarkElement instance;
   private String watermarkText;
   private float width;

   public WatermarkElement() {
      super("watermark", 5.0F, 5.0F, true, false);
      instance = this;
   }

   @Override
   public float getWidth() {
      return this.width;
   }

   @Override
   public void draw(ScaledResolution scaledResolution, float f, boolean flag) {
      if (ModuleManager.watermark.isEnabled()) {
         int currentElement = ModuleManager.watermark.watermark.getCurrentElement();
         if (currentElement == 0) {
            StringBuilder stringBuilder = new StringBuilder("Novoline")
               .append(" ")
               .append("3.0")
               .append(" | ")
               .append(Novoline.username)
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
            int c1 = ModuleManager.themeColor.getColor(this.getX(), this.getY());
            int c2 = ModuleManager.themeColor.getColor(this.getX() + this.getWidth(), this.getY());
            boolean postProcessingActive = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isEnabled();
            if (!postProcessingActive) {
               RenderUtils.drawShadow(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 1.5F, 1610612736);
            }
            RenderUtils.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), -14606047);
            RenderUtils.drawGradientLine(
               this.getX(),
               this.getY(),
               this.getX() + this.getWidth(),
               this.getY(),
               3.0F,
               c1,
               c2,
               false
            );
            cFontRenderer.drawStringWithShadow(
               this.watermarkText,
               this.getX() + this.getWidth() / 2.0F - cFontRenderer.getStringWidth(this.watermarkText) / 2.0F,
               this.getY() + this.getHeight() / 2.0F - cFontRenderer.getHeight() / 2.0F + 1.0F,
               -1
            );
            this.width = cFontRenderer.getStringWidth(this.watermarkText) + 5.0F;
         } else if (currentElement == 1) {
            StringBuilder stringBuilder2 = new StringBuilder("§8|§r ").append(Novoline.username).append(" §8|§r ").append(Minecraft.getDebugFPS()).append("fps §8|§r ");
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
            boolean postProcessingActive2 = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isEnabled();
            if (!postProcessingActive2) {
               RenderUtils.drawShadow(
                  this.getX() + 1.0F, this.getY() + 1.0F, this.getX() + this.getWidth() - 1.0F, this.getY() + this.getHeight(), 2.5F, -285212672
               );
            }
            FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
            filledPolygonRenderer.putRoundedRect(this.getX(), this.getY(), this.width, 14.0F, 1.0F, -15395563);
            filledPolygonRenderer.draw();
            cFontRenderer2.drawStringWithShadow(text3, this.getX() + 3.0F, this.getY() + 5.0F, -1);
            cFontRenderer3.drawStringWithShadow(this.watermarkText, this.getX() + 3.0F + cFontRenderer2.getStringWidth(text3) + 3.0F, this.getY() + 5.0F + 0.5F, -1);
         }
      }
   }

   public static void renderWatermarkEffects(boolean bloom) {
      boolean glow = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isGlowEnabled();
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
      int currentElement = ModuleManager.watermark.watermark.getCurrentElement();

      int hudColor = (!bloom || !glow) ? -16777216 : ModuleManager.themeColor.getColor(x, y);
      if (currentElement == 0) {
         RenderUtils.drawRect(x, y, x + w, y + h, hudColor);
      } else if (currentElement == 1) {
         FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
         filledPolygonRenderer.putRoundedRect(x, y, w, 14.0F, 1.0F, hudColor);
         filledPolygonRenderer.draw();
      }
   }

   @Override
   public float getHeight() {
      return 13.0F;
   }
}
