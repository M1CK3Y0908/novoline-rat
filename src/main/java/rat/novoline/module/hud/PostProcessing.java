package rat.novoline.module.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import rat.novoline.Novoline;
import rat.novoline.event.ShaderEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.RenderUtils;
import rat.novoline.utils.render.blur.KawaseBloom;
import rat.novoline.utils.render.blur.KawaseBlur;

public class PostProcessing extends Module {
   public final CheckboxSetting blur = this.settingManager.register(new CheckboxSetting(this, "Blur", true));
   public final NumberSetting iterations = this.settingManager.register(new NumberSetting(this, "Blur Iterations", 2, 1, 8) {
      @Override
      public boolean shouldShow() {
         return PostProcessing.this.blur.isEnabled();
      }
   });
   public final NumberSetting offset = this.settingManager.register(new NumberSetting(this, "Blur Offset", 3, 1, 10) {
      @Override
      public boolean shouldShow() {
         return PostProcessing.this.blur.isEnabled();
      }
   });

   public final CheckboxSetting shadow = this.settingManager.register(new CheckboxSetting(this, "Shadow", true));
   public final NumberSetting shadowRadius = this.settingManager.register(new NumberSetting(this, "Shadow Iterations", 3, 1, 8) {
      @Override
      public boolean shouldShow() {
         return PostProcessing.this.shadow.isEnabled();
      }
   });
   public final NumberSetting shadowOffset = this.settingManager.register(new NumberSetting(this, "Shadow Offset", 1, 1, 10) {
      @Override
      public boolean shouldShow() {
         return PostProcessing.this.shadow.isEnabled();
      }
   });

   public final CheckboxSetting glow = this.settingManager.register(new CheckboxSetting(this, "Glow", true));
   public final NumberSetting glowRadius = this.settingManager.register(new NumberSetting(this, "Glow Iterations", 3, 1, 8) {
      @Override
      public boolean shouldShow() {
         return PostProcessing.this.glow.isEnabled();
      }
   });
   public final NumberSetting glowOffset = this.settingManager.register(new NumberSetting(this, "Glow Offset", 1, 1, 10) {
      @Override
      public boolean shouldShow() {
         return PostProcessing.this.glow.isEnabled();
      }
   });

   private static Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

   public PostProcessing() {
      super(Category.CLIENT, "PostProcessing", "Tenacity Kawase blur, shadow, and dynamic glow post-processing");
      this.setEnabled(true);
   }

   public boolean isBlurEnabled() {
      return this.isEnabled() && this.blur.isEnabled();
   }

   public boolean isBlurEnabled(String target) {
      return this.isBlurEnabled();
   }

   public boolean isShadowEnabled() {
      return this.isEnabled() && this.shadow.isEnabled();
   }

   public boolean isBloomEnabled() {
      return this.isShadowEnabled() || this.isGlowEnabled();
   }

   public boolean isBloomEnabled(String target) {
      return this.isBloomEnabled();
   }

   public boolean isGlowEnabled() {
      return this.isEnabled() && this.glow.isEnabled();
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
         KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, bloomRadius, 1);
      }
   }

   public void stuffToBlur(boolean bloom, boolean glow, ScaledResolution sr) {
      Minecraft mc = Minecraft.getMinecraft();
      float scaleFactor = sr.getScaleFactor();

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

      if (ModuleManager.scoreboard != null && ModuleManager.scoreboard.isEnabled()) {
         ScoreboardElement.renderScoreboardEffects(bloom, glow, sr);
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
      if (!Display.isVisible() || mc.displayWidth <= 1 || mc.displayHeight <= 1) {
         return;
      }

      ScaledResolution sr = new ScaledResolution(mc);

      // Pass 1: Blur
      if (this.blur.isEnabled()) {
         stencilFramebuffer = KawaseBlur.createFrameBuffer(stencilFramebuffer);
         stencilFramebuffer.setFramebufferFilter(GL11.GL_LINEAR);
         stencilFramebuffer.framebufferClear();
         stencilFramebuffer.bindFramebuffer(false);
         Novoline.getNovoline().getBus().post(new ShaderEvent(false, false));
         this.stuffToBlur(false, false, sr);
         stencilFramebuffer.unbindFramebuffer();

         KawaseBlur.renderBlur(stencilFramebuffer.framebufferTexture, (int)this.iterations.getCurrentValue(), (int)this.offset.getCurrentValue());
      }

      // Pass 2: Shadow (Dark shadow bloom)
      if (this.shadow.isEnabled()) {
         stencilFramebuffer = KawaseBlur.createFrameBuffer(stencilFramebuffer);
         stencilFramebuffer.setFramebufferFilter(GL11.GL_LINEAR);
         stencilFramebuffer.framebufferClear();
         stencilFramebuffer.bindFramebuffer(false);

         Novoline.getNovoline().getBus().post(new ShaderEvent(true, false));
         this.stuffToBlur(true, false, sr);

         stencilFramebuffer.unbindFramebuffer();

         KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, (int)this.shadowRadius.getCurrentValue(), (int)this.shadowOffset.getCurrentValue(), true);
      }

      // Pass 3: Glow (Dynamic colored neon bloom)
      if (this.glow.isEnabled()) {
         stencilFramebuffer = KawaseBlur.createFrameBuffer(stencilFramebuffer);
         stencilFramebuffer.setFramebufferFilter(GL11.GL_LINEAR);
         stencilFramebuffer.framebufferClear();
         stencilFramebuffer.bindFramebuffer(false);

         Novoline.getNovoline().getBus().post(new ShaderEvent(true, true));
         this.stuffToBlur(true, true, sr);

         stencilFramebuffer.unbindFramebuffer();

         KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, (int)this.glowRadius.getCurrentValue(), (int)this.glowOffset.getCurrentValue(), false);
      }
   }
}
