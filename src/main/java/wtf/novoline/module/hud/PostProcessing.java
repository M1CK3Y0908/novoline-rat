package wtf.novoline.module.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL13;
import wtf.novoline.Novoline;
import wtf.novoline.event.ShaderEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.blur.KawaseBloom;
import wtf.novoline.utils.render.blur.KawaseBlur;

public class PostProcessing extends Module {
   public final CheckboxSetting blur = settingManager.register(new CheckboxSetting(this, "Blur", true));
   public final NumberSetting blurIntensity = settingManager.register(new NumberSetting(this, "Blur Intensity", 50, 0, 100, "%") {
      @Override
      public boolean shouldShow() {
         return PostProcessing.this.blur.isEnabled();
      }
   });

   public final CheckboxSetting bloom = settingManager.register(new CheckboxSetting(this, "Bloom", true));
   public final CheckboxSetting glow = settingManager.register(new CheckboxSetting(this, "Glow", false) {
      @Override
      public boolean shouldShow() {
         return PostProcessing.this.bloom.isEnabled();
      }
   });
   public final NumberSetting glowIntensity = settingManager.register(new NumberSetting(this, "Glow Intensity", 50, 0, 100, "%") {
      @Override
      public boolean shouldShow() {
         return PostProcessing.this.bloom.isEnabled();
      }
   });

   private static Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

   public PostProcessing() {
      super(Category.CLIENT, "PostProcessing", "Tenacity Kawase blur and bloom post-processing");
      this.setEnabled(true);
   }

   public boolean isBlurEnabled() {
      return this.isEnabled() && this.blur.isEnabled();
   }

   public boolean isBlurEnabled(String target) {
      return this.isBlurEnabled();
   }

   public boolean isBloomEnabled() {
      return this.isEnabled() && this.bloom.isEnabled();
   }

   public boolean isBloomEnabled(String target) {
      return this.isBloomEnabled();
   }

   public boolean isGlowEnabled() {
      return this.isBloomEnabled() && this.glow.isEnabled();
   }

   public boolean isGlowEnabled(String target) {
      return this.isGlowEnabled();
   }

   public static void runBloom(Runnable runnable, boolean blur, boolean bloom, int bloomRadius) {
      if (bloom) {
         stencilFramebuffer = KawaseBlur.createFrameBuffer(stencilFramebuffer);
         stencilFramebuffer.framebufferClear();
         stencilFramebuffer.bindFramebuffer(false);
         runnable.run();
         stencilFramebuffer.unbindFramebuffer();
         KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, bloomRadius, 1.0F, 1.0F);
      }
   }

   public void stuffToBlur(boolean bloom, ScaledResolution sr) {
      Minecraft mc = Minecraft.getMinecraft();
      float scaleFactor = sr.getScaleFactor();
      boolean glow = this.isGlowEnabled();

      if (!bloom && mc.currentScreen instanceof GuiChat) {
         RenderUtils.drawRect(2.0F, sr.getScaledHeight() - 14.0F, sr.getScaledWidth() - 4.0F, sr.getScaledHeight() - 2.0F, -16777216);
      }

      GlStateManager.pushMatrix();
      GlStateManager.scale(2.0F / scaleFactor, 2.0F / scaleFactor, 2.0F / scaleFactor);

      if (ModuleManager.targetHUD != null && ModuleManager.targetHUD.isEnabled()) {
         TargetHudElement.renderTargetHudEffects(bloom, glow, sr);
      }

      if (ModuleManager.arrayList != null && ModuleManager.arrayList.isEnabled()) {
         ArrayListElement.renderArrayListEffects(bloom, glow, sr);
      }

      if (ModuleManager.watermark != null && ModuleManager.watermark.isEnabled()) {
         WatermarkElement.renderWatermarkEffects(bloom, glow);
      }

      if (ModuleManager.notifications != null && ModuleManager.notifications.isEnabled()) {
         Novoline.getNovoline().getNotificationManager().renderEffects(bloom, glow, sr);
      }

      if (Novoline.getNovoline().getMenuClickGuiScreen() != null) {
         Novoline.getNovoline().getMenuClickGuiScreen().renderEffects(bloom, glow, sr);
      }

      GlStateManager.popMatrix();
   }

   public void blurScreen() {
      if (!this.isEnabled()) return;
      Minecraft mc = Minecraft.getMinecraft();
      if (stencilFramebuffer == null || stencilFramebuffer.framebufferWidth != mc.displayWidth || stencilFramebuffer.framebufferHeight != mc.displayHeight) {
         if (stencilFramebuffer != null) {
            stencilFramebuffer.deleteFramebuffer();
         }
         stencilFramebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
         stencilFramebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      }

      ScaledResolution sr = new ScaledResolution(mc);
      float scaleFactor = sr.getScaleFactor();
      sr.handleDouble2(sr.getScaledWidth_double() / 2.0 * scaleFactor);
      sr.handleDouble(sr.getScaledHeight_double() / 2.0 * scaleFactor);

      if (this.blur.isEnabled()) {
         float bIntensity = this.blurIntensity.getCurrentValue();
         if (bIntensity > 0.0F) {
            float bFactor = bIntensity / 100.0F;
            int blurIterations = Math.max(1, Math.min(8, Math.round(bFactor * 8.0F)));
            float blurOffset = Math.max(1.0F, Math.min(10.0F, bFactor * 10.0F));

            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(false);
            Novoline.getNovoline().getBus().post(new ShaderEvent(false));
            this.stuffToBlur(false, sr);
            stencilFramebuffer.unbindFramebuffer();

            KawaseBlur.renderBlur(stencilFramebuffer.framebufferTexture, blurIterations, blurOffset);
         }
      }

      if (this.bloom.isEnabled()) {
         float gIntensity = this.glowIntensity.getCurrentValue();
         if (gIntensity > 0.0F) {
            float gFactor = gIntensity / 100.0F;
            int bloomIterations = Math.max(1, Math.min(8, Math.round(gFactor * 8.0F)));
            float bloomOffset = Math.max(1.0F, Math.min(10.0F, gFactor * 10.0F));
            float bloomAlpha = MathHelper.clamp_float(gFactor, 0.0F, 1.0F);

            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(false);
            Novoline.getNovoline().getBus().post(new ShaderEvent(true, this.isGlowEnabled()));
            this.stuffToBlur(true, sr);
            stencilFramebuffer.unbindFramebuffer();

            KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, bloomIterations, bloomOffset, bloomAlpha);
         }
      }

      GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
      GlStateManager.bindTexture(0);
      GlStateManager.enableTexture2D();
      GlStateManager.enableAlpha();
      GlStateManager.disableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
