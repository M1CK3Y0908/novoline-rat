package wtf.novoline.module.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import wtf.novoline.Novoline;
import wtf.novoline.event.RenderScreenEvent;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.blur.KawaseBloom;
import wtf.novoline.utils.render.blur.ShaderUtil;

public class HudRenderer {
   private final HudManager hudManager;

   public void renderElements(ScaledResolution scaledResolution, float f) {
      this.renderElements(scaledResolution, f, false, 0.0F, 0.0F);
   }

   public void renderForEditor(float f, float f2, float f3) {
      this.renderElements(new ScaledResolution(Minecraft.getMinecraft()), f3, true, f, f2);
   }

   private void renderElements(ScaledResolution scaledResolution, float f, boolean flag, float f2, float f3) {
      SmoothingUtils.updateDelta();
      if (Minecraft.getMinecraft().thePlayer != null) {
         float scaleFactor = scaledResolution.getScaleFactor();
         double d = scaledResolution.getScaledWidth_double();
         double d2 = scaledResolution.getScaledHeight_double();
         scaledResolution.handleDouble2(d / 2.0 * scaleFactor);
         scaledResolution.handleDouble(d2 / 2.0 * scaleFactor);

         if (ModuleManager.arrayList != null && ModuleManager.arrayList.isEnabled()) {
            ArrayListElement.updatePositions(scaledResolution);
         }
         Novoline.getNovoline().getNotificationManager().updatePositions(scaledResolution);
         if (Novoline.getNovoline().getMenuClickGuiScreen() != null) {
            Novoline.getNovoline().getMenuClickGuiScreen().updateAnimations();
         }
         if (ModuleManager.targetHUD != null && ModuleManager.targetHUD.isEnabled()) {
            TargetHudElement.updateTarget(scaledResolution);
         }

         if (ModuleManager.postProcessing != null && ModuleManager.postProcessing.isEnabled()) {
            ModuleManager.postProcessing.blurScreen();
         }

         PostProcessing postProcessing = ModuleManager.postProcessing;
         boolean glow = postProcessing != null && postProcessing.isGlowEnabled();
         Framebuffer uiFb = glow ? PostProcessing.getUiFramebuffer() : null;

         if (glow && uiFb != null) {
            uiFb.framebufferClear();
            uiFb.bindFramebuffer(false);
         }

         GlStateManager.pushMatrix();
         GlStateManager.scale(2.0F / scaleFactor, 2.0F / scaleFactor, 2.0F / scaleFactor);
         Novoline.getNovoline().getBus().post(new RenderScreenEvent(scaledResolution, f));

         for (HudElement hudElement : this.hudManager.getElements()) {
            if (flag) {
               float f4 = f2 * scaleFactor / 2.0F;
               float f5 = f3 * scaleFactor / 2.0F;
               hudElement.updateDragging(scaledResolution, f4, f5);
            }

            if (hudElement.isVisibleInDebugScreen() || !Minecraft.getMinecraft().gameSettings.showDebugInfo) {
               hudElement.draw(scaledResolution, f, flag);
            }
         }

         GlStateManager.popMatrix();
         Novoline.getNovoline()
            .getMenuClickGuiScreen()
            .handleFramebuffer(glow && uiFb != null ? uiFb : Minecraft.getMinecraft().getFramebuffer(), Minecraft.getMinecraft().timer.renderPartialTicks);
         GlStateManager.pushMatrix();
         GlStateManager.scale(2.0F / scaleFactor, 2.0F / scaleFactor, 2.0F / scaleFactor);
         Novoline.getNovoline().getNotificationManager().renderAll(scaledResolution);
         GlStateManager.popMatrix();

         if (glow && uiFb != null) {
            uiFb.unbindFramebuffer();
            Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);

            // Blit UI onto screen
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, uiFb.framebufferTexture);
            ShaderUtil.drawQuads();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

            // Additive Exposure Glow
            float gIntensity = postProcessing.glowIntensity.getCurrentValue();
            if (gIntensity > 0.0F) {
               float gFactor = gIntensity / 100.0F;
               float exposure = 1.0F + gFactor * 1.5F;
               float alpha = gFactor;
               KawaseBloom.renderExposureGlow(uiFb.framebufferTexture, exposure, alpha);
            }
         }
      }
   }

   public HudRenderer(HudManager hudManager2) {
      this.hudManager = hudManager2;
   }
}
