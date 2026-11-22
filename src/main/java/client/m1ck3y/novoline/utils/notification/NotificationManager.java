package client.m1ck3y.novoline.utils.notification;

import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import client.m1ck3y.novoline.Novoline;
import client.m1ck3y.novoline.Novoline.Fonts;
import client.m1ck3y.novoline.features.module.ModuleManager;
import client.m1ck3y.novoline.features.module.modules.hud.Notifications;
import client.m1ck3y.novoline.features.module.modules.hud.PostProcessing;
import client.m1ck3y.novoline.utils.math.SmoothingUtils;
import client.m1ck3y.novoline.utils.render.BloomRenderer;
import client.m1ck3y.novoline.utils.render.BlurRenderer;
import client.m1ck3y.novoline.utils.render.CFontRenderer;
import client.m1ck3y.novoline.utils.render.FontAdapter;
import client.m1ck3y.novoline.utils.render.FilledPolygonRenderer;
import client.m1ck3y.novoline.utils.render.RenderUtils;
import client.m1ck3y.novoline.utils.render.RoundedPolygonRenderer;

public class NotificationManager extends CopyOnWriteArrayList<Notification> {
   private FontAdapter getTitleFont() {
      return ModuleManager.notifications != null ? ModuleManager.notifications.getTitleFont() : Fonts.rubikRegularSize18;
   }

   private FontAdapter getSubTextFont() {
      return ModuleManager.notifications != null ? ModuleManager.notifications.getSubTextFont() : Fonts.rubikRegularSize16;
   }

   public void show(Notification notification2) {
      this.add(notification2);
   }

   public void dismiss(Notification notification2) {
      this.remove(notification2);
   }

   private void drawNotificationContent(Notification notification2, float f, float f2, float f3, ResourceLocation resourceLocation) {
      RenderUtils.drawTextureWithAlpha(f + 4.0F, f2 + f3 / 2.0F - 8.0F, 16.0F, 16.0F, 1.0F, resourceLocation);
      FontAdapter titleFont = this.getTitleFont();
      FontAdapter subFont = this.getSubTextFont();
      titleFont.drawStringWithShadow(notification2.getTitle(), f + 28.0F, f2 + f3 / 2.0F - titleFont.getHeight() / 2.0F - 3.0F, -1);
      subFont.drawStringWithShadow(notification2.getSubText(), f + 28.0F, f2 + f3 / 2.0F - subFont.getHeight() / 2.0F + 6.0F, -2039584);
   }

   public void updatePositions(ScaledResolution scaledResolution) {
      if (!this.isEmpty()) {
         this.removeIf(Notification::hasExpired);
         float f = 5.0F;
         float f2 = 44.0F;
         float f3 = 30.0F;

         for (int i = 0; i < Math.min(this.size(), 15); i++) {
            Notification notification2 = this.get(i);
            float f4 = Math.max(170.0F, this.getSubTextFont().getStringWidth(notification2.getSubText()) + 36.0F);
            notification2.offsetY = SmoothingUtils.approach(notification2.offsetY, f2, 10.0F);
            if (notification2.isFinished()) {
               notification2.offsetX = SmoothingUtils.approach(notification2.offsetX, -f, 10.0F);
            } else {
               notification2.offsetX = SmoothingUtils.approach(notification2.offsetX, f4 + f, 10.0F);
               f2 += f3 + 5.0F;
            }
         }
      }
   }

   public void renderEffects(boolean bloom, ScaledResolution scaledResolution) {
      boolean glow = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isGlowEnabled("Notifications");
      this.renderEffects(bloom, glow, scaledResolution);
   }

   public void renderEffects(boolean bloom, boolean glow, ScaledResolution scaledResolution) {
      if (!this.isEmpty()) {
         float scaledWidth = scaledResolution.getScaledWidth();
         float scaledHeight = scaledResolution.getScaledHeight();
         for (int i = 0; i < Math.min(this.size(), 15); i++) {
            Notification notification2 = this.get(i);
            float f4 = Math.max(170.0F, this.getSubTextFont().getStringWidth(notification2.getSubText()) + 36.0F);
            float f3 = 30.0F;
            float f5 = scaledWidth - notification2.offsetX;
            float f6 = scaledHeight - notification2.offsetY;
            int color = (!bloom || !glow) ? -16777216 : notification2.getType().getAccentColor();
            FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
            filledPolygonRenderer.putRoundedRect(f5, f6, f4, f3, 3.0F, color);
            filledPolygonRenderer.draw();
         }
      }
   }

   public void renderAll(ScaledResolution scaledResolution) {
      Notifications notifications2 = ModuleManager.notifications;
      if (notifications2.isEnabled() && !this.isEmpty()) {
         boolean shadowEnabled = notifications2.shadow.isEnabled();
         boolean postProcessingActive = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isEnabled();
         float scaledWidth = scaledResolution.getScaledWidth();
         float scaledHeight = scaledResolution.getScaledHeight();
         float f3 = 30.0F;

         for (int i = 0; i < Math.min(this.size(), 15); i++) {
            Notification notification2 = this.get(i);
            float f4 = Math.max(170.0F, this.getSubTextFont().getStringWidth(notification2.getSubText()) + 36.0F);
            NotificationType type = notification2.getType();
            ResourceLocation resourceLocation = type.getResourceLocation();
            int backgroundColor = type.getBackgroundColor();
            int accentColor = type.getAccentColor();

            float f5 = scaledWidth - notification2.offsetX;
            float f6 = scaledHeight - notification2.offsetY;
            if (shadowEnabled && !postProcessingActive) {
               RenderUtils.drawShadow(f5 + 1.0F, f6 + 1.0F, f5 + f4 - 1.0F, f6 + f3 - 1.0F, 3.0F, 1879048192);
            }

            FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
            filledPolygonRenderer.putRoundedRect(f5, f6, f4, f3, 3.0F, backgroundColor);
            filledPolygonRenderer.draw();
            filledPolygonRenderer = new FilledPolygonRenderer();
            filledPolygonRenderer.putCustomRoundedRect(f5, f6, 24.0F, f3, 3.0F, 3.0F, 0.0F, 0.0F, accentColor);
            filledPolygonRenderer.draw();
            this.drawNotificationContent(notification2, f5, f6, f3, resourceLocation);
         }
      }
   }
}
