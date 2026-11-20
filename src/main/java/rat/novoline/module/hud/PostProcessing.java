package rat.novoline.module.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;
import rat.novoline.Novoline;
import rat.novoline.event.ShaderEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.MultiComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.RenderUtils;
import rat.novoline.utils.render.blur.KawaseBloom;
import rat.novoline.utils.render.blur.KawaseBlur;

public class PostProcessing extends Module {
   public static MultiComboSetting glowOptions;

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

   public final CheckboxSetting bloom = this.settingManager.register(new CheckboxSetting(this, "Bloom", true));
   public final MultiComboSetting glowOptionsSetting = this.settingManager.register(
      glowOptions = new MultiComboSetting(this, "Glow Options") {
         @Override
         public boolean shouldShow() {
            return PostProcessing.this.bloom.isEnabled();
         }
      }
      .add("Arraylist", true)
      .add("ClickGui", false)
      .add("Watermark", true)
      .add("Statistics", true)
      .add("Radar", true)
      .add("TargetHud", true)
      .add("Spotify", true)
      .add("Notifications", false)
      .add("Keystrokes", false)
   );
   public final NumberSetting shadowRadius = this.settingManager.register(new NumberSetting(this, "Bloom Iterations", 3, 1, 8) {
      @Override
      public boolean shouldShow() {
         return PostProcessing.this.bloom.isEnabled();
      }
   });
   public final NumberSetting shadowOffset = this.settingManager.register(new NumberSetting(this, "Bloom Offset", 1, 1, 10) {
      @Override
      public boolean shouldShow() {
         return PostProcessing.this.bloom.isEnabled();
      }
   });

   private static Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

   public PostProcessing() {
      super(Category.CLIENT, "PostProcessing", "blurs shit");
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
      return this.isBloomEnabled();
   }

   public boolean isGlowEnabled(String target) {
      return this.isBloomEnabled() && this.glowOptionsSetting != null && this.glowOptionsSetting.isSelected(target);
   }

   public MultiComboSetting getGlowOptions() {
      return this.glowOptionsSetting;
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

   public void stuffToBlur(boolean bloom, ScaledResolution sr) {
      Minecraft mc = Minecraft.getMinecraft();
      float scaleFactor = sr.getScaleFactor();

      if (!bloom && mc.currentScreen instanceof GuiChat) {
         RenderUtils.drawRect(2.0F, sr.getScaledHeight() - 14.0F, sr.getScaledWidth() - 4.0F, sr.getScaledHeight() - 2.0F, -16777216);
      }

      GlStateManager.pushMatrix();
      GlStateManager.scale(2.0F / scaleFactor, 2.0F / scaleFactor, 2.0F / scaleFactor);

      boolean arraylistGlow = this.glowOptionsSetting.isSelected("Arraylist");
      boolean targetHudGlow = this.glowOptionsSetting.isSelected("TargetHud");
      boolean watermarkGlow = this.glowOptionsSetting.isSelected("Watermark");
      boolean notifGlow = this.glowOptionsSetting.isSelected("Notifications");
      boolean clickGuiGlow = this.glowOptionsSetting.isSelected("ClickGui");

      if (ModuleManager.targetHUD != null && ModuleManager.targetHUD.isEnabled()) {
         TargetHudElement.renderTargetHudEffects(bloom, targetHudGlow, sr);
      }

      if (ModuleManager.arrayList != null && ModuleManager.arrayList.isEnabled()) {
         ArrayListElement.renderArrayListEffects(bloom, arraylistGlow, sr);
      }

      if (ModuleManager.watermark != null && ModuleManager.watermark.isEnabled()) {
         WatermarkElement.renderWatermarkEffects(bloom, watermarkGlow);
      }

      if (ModuleManager.notifications != null && ModuleManager.notifications.isEnabled()) {
         Novoline.getNovoline().getNotificationManager().renderEffects(bloom, notifGlow, sr);
      }

      if (Novoline.getNovoline().getMenuClickGuiScreen() != null) {
         Novoline.getNovoline().getMenuClickGuiScreen().renderEffects(bloom, clickGuiGlow, sr);
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

      if (this.blur.isEnabled()) {
         stencilFramebuffer = KawaseBlur.createFrameBuffer(stencilFramebuffer);
         stencilFramebuffer.framebufferClear();
         stencilFramebuffer.bindFramebuffer(false);
         Novoline.getNovoline().getBus().post(new ShaderEvent(false, this.glowOptionsSetting));
         this.stuffToBlur(false, sr);
         stencilFramebuffer.unbindFramebuffer();

         KawaseBlur.renderBlur(stencilFramebuffer.framebufferTexture, (int)this.iterations.getCurrentValue(), (int)this.offset.getCurrentValue());
      }

      if (this.bloom.isEnabled()) {
         stencilFramebuffer = KawaseBlur.createFrameBuffer(stencilFramebuffer);
         stencilFramebuffer.framebufferClear();
         stencilFramebuffer.bindFramebuffer(false);

         Novoline.getNovoline().getBus().post(new ShaderEvent(true, this.glowOptionsSetting));
         this.stuffToBlur(true, sr);

         stencilFramebuffer.unbindFramebuffer();

         KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, (int)this.shadowRadius.getCurrentValue(), (int)this.shadowOffset.getCurrentValue());
      }
   }
}
