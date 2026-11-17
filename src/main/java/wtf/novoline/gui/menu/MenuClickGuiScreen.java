package wtf.novoline.gui.menu;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.enums.WidgetType;
import wtf.novoline.gui.BackgroundManager;
import wtf.novoline.gui.DashboardScreen;
import wtf.novoline.gui.TextSelection;
import wtf.novoline.gui.BackgroundManager.Entry;
import wtf.novoline.gui.clickgui.ClickGuiTheme;
import wtf.novoline.module.Category;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.hud.ClickGui;
import wtf.novoline.utils.animation.Animation;
import wtf.novoline.utils.animation.Easing;
import wtf.novoline.utils.render.BloomRenderer;
import wtf.novoline.utils.render.BlurFramebuffer;
import wtf.novoline.utils.render.BlurRenderer;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.DesaturationShader;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.shape.GradientRectRenderer;
import wtf.novoline.utils.render.shape.OutlineRectRenderer;
import wtf.novoline.utils.render.shape.RoundedRectRenderer;
import wtf.novoline.utils.render.shape.ShaderRoundedRectRenderer;

public class MenuClickGuiScreen extends GuiScreen {
   private Framebuffer framebuffer = null;
   private final ClickGuiTheme clickGuiTheme = new ClickGuiTheme();
   private final MenuLayout menuLayout = new MenuLayout();
   private long tickCount = 0L;
   private boolean redrawPending = false;
   private boolean closing = true;
   public boolean panelBlurActive;
   public static final ResourceLocation NOISE_TEXTURE = new ResourceLocation("novoline", "images/noise.png");
   private boolean resourcesReleased = false;
   private final Animation animation = new Animation();
   private float visibility;
   private final Animation animation2 = new Animation();
   private boolean mouseHeld;
   private TextSelection textSelection;
   private final ClickGui clickGui;
   private final BackgroundManager backgroundManager;
   public DashboardScreen dashboardScreen;
   public static final ResourceLocation DASHBOARD_ICON = new ResourceLocation("novoline", "gui/script.png");
   public boolean dashboardOpen;
   private Framebuffer framebuffer2;
   public BlurRenderer blurRenderer;
   public boolean returnToClickGui;
   public static boolean shadersEnabled = OpenGlHelper.shadersSupported;
   public BloomRenderer bloomRenderer;
   public final List<MenuCategoryPanel> buttons;
   private ScaledResolution scaledResolution;

   public Framebuffer getFramebuffer() {
      return this.framebuffer == null ? this.framebuffer2 : this.framebuffer;
   }

   private void handleCFontRenderer(CFontRenderer cFontRenderer, WidgetType widgetType, float f) {
      ShaderRoundedRectRenderer shaderRoundedRectRenderer = new ShaderRoundedRectRenderer(cFontRenderer);
      Iterator iterator2 = this.buttons.iterator();

      while (iterator2.hasNext()) {
         ((MenuCategoryPanel)iterator2.next()).renderText(shaderRoundedRectRenderer, widgetType, f, true);
      }

      shaderRoundedRectRenderer.draw();
   }

   public ClickGuiTheme getClickGuiTheme() {
      return this.clickGuiTheme;
   }

   @Override
   public void updateScreen() {
      Iterator iterator2 = this.buttons.iterator();

      while (iterator2.hasNext()) {
         ((MenuCategoryPanel)iterator2.next()).tick();
      }

      this.tickCount++;
      if (this.tickCount % 2L == 1L) {
         this.redrawPending = true;
      }
   }

   private void releaseResources() {
      if (this.blurRenderer != null) {
         this.blurRenderer.releaseResult();
      }

      if (this.framebuffer != null) {
         this.framebuffer.deleteFramebuffer();
         this.framebuffer = null;
      }

      Novoline.framebufferManager.deleteFreeFramebuffers();
      Iterator iterator2 = this.buttons.iterator();

      while (iterator2.hasNext()) {
         ((MenuCategoryPanel)iterator2.next()).reset();
      }

      Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
      RenderUtils.resetColor();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableTexture2D();
      ARBShaderObjects.glUseProgramObjectARB(0);
   }

   public void handleFramebuffer(Framebuffer framebuffer3, float f) {
      ClickGui clickGui2 = ModuleManager.clickGui;
      if (OpenGlHelper.shadersSupported) {
         shadersEnabled = !clickGui2.performanceMode.isEnabled();
      }

      this.animation.update();
      this.animation2.update();
      this.visibility = MathHelper.clamp_float((float)this.animation.getValue(), 0.0F, 100.0F);
      ScaledResolution scaledResolution2 = new ScaledResolution(Minecraft.getMinecraft());
      if (this.visibility > 0.0F && clickGui2.backgroundSettings.isSelected(3)) {
         RenderUtils.resetColor();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         Entry entry2 = clickGui2.entry;
         if (entry2 != null && entry2.getMetadata() != null) {
            this.backgroundManager.loadTexture(entry2);
            float width = entry2.getMetadata().getWidth();
            float height = entry2.getMetadata().getHeight();
            double d = 0.45 + clickGui2.imageScale.getCurrentValue();
            double d2 = width / d / Display.getWidth();
            double d3 = height / d / Display.getHeight();
            int i = (int)(scaledResolution2.getScaledWidth() * d2);
            int i2 = (int)(scaledResolution2.getScaledHeight() * d3);
            GlStateManager.enableBlend();
            RenderUtils.applyBlendFunc();
            GlStateManager.color(1.0F, 1.0F, 1.0F, this.visibility / 100.0F);
            Gui.drawModalRectWithCustomSizedTexture(
               scaledResolution2.getScaledWidth() - i + (int)this.animation2.getValue(), scaledResolution2.getScaledHeight() - i2 + 5, 1.0F, 1.0F, i, i2, i, i2
            );
            RenderUtils.resetColor();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      if (this.closing) {
         if (this.visibility > 0.0F && (this.animation.isAnimating() || !this.resourcesReleased)) {
            this.renderLayers(framebuffer3, 0, 0, f);
         } else if (!this.animation.isAnimating() && !this.resourcesReleased) {
            this.releaseResources();
            this.resourcesReleased = true;
         }
      } else if (this.mc.currentScreen == this || this.clickGui.isEnabled()) {
         int scaledWidth = scaledResolution2.getScaledWidth();
         int scaledHeight = scaledResolution2.getScaledHeight();
         int i3 = Mouse.getX() * scaledWidth / this.mc.displayWidth;
         int i4 = scaledHeight - Mouse.getY() * scaledHeight / this.mc.displayHeight - 1;
         this.renderLayers(framebuffer3, i3, i4, f);
      }
   }

   @Override
   public void handleMouseInput() throws IOException {
      if (this.dashboardOpen) {
         super.handleMouseInput();
      } else {
         if (Mouse.getEventDWheel() != 0) {
            int i = MathHelper.clamp_int(Mouse.getEventDWheel(), -1, 1);

            for (MenuCategoryPanel menuCategoryPanel : this.buttons) {
               float f = menuCategoryPanel.getY() + i * 10;
               menuCategoryPanel.setY(f);
            }
         }

         super.handleMouseInput();
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   @Override
   protected void keyTyped(char c, int i) throws IOException {
      if (this.dashboardOpen) {
         this.dashboardScreen.keyTyped(c, i);
         super.keyTyped(c, i);
      } else if (i == 33 && isCtrlKeyDown()) {
         this.textSelection.getTextBuffer().deleteRange(0, this.textSelection.getTextBuffer().length());
         this.textSelection.setVisible(!this.textSelection.isVisible());
         this.textSelection.focus();
      } else if (i == 1 && this.textSelection.isVisible()) {
         this.textSelection.setVisible(false);
      } else if (this.textSelection.isVisible()) {
         this.textSelection.keyTyped(c, i);
      } else {
         Iterator iterator2 = this.buttons.iterator();

         while (iterator2.hasNext()) {
            if (!((MenuCategoryPanel)iterator2.next()).keyTyped(c, i)) {
               return;
            }
         }

         if (i == this.clickGui.getKeyBind() || i == 1) {
            this.mc.displayGuiScreen(null);
         } else {
            super.keyTyped(c, i);
         }
      }
   }

   public MenuLayout getMenuLayout() {
      return this.menuLayout;
   }

   public TextSelection getTextSelection() {
      return this.textSelection;
   }

   public void updateAnimations() {
      this.animation.update();
      this.animation2.update();
      this.visibility = MathHelper.clamp_float((float)this.animation.getValue(), 0.0F, 100.0F);
   }

   public void renderEffects(boolean bloom, ScaledResolution sr) {
      this.renderEffects(bloom, false, sr);
   }

   public void renderEffects(boolean bloom, boolean glow, ScaledResolution sr) {
      this.updateAnimations();

      if (this.visibility <= 0.01F || (this.closing && (!this.animation.isAnimating() || this.resourcesReleased))) {
         return;
      }
      if (this.mc.currentScreen != this && !this.clickGui.isEnabled() && !this.animation.isAnimating()) {
         return;
      }

      float alpha = MathHelper.clamp_float(this.visibility / 100.0F, 0.0F, 1.0F);
      if (alpha <= 0.005F) {
         return;
      }

      double d = sr.getScaledWidth_double();
      double d2 = sr.getScaledHeight_double();
      double midX = d / 2.0;
      double midY = d2 / 2.0;

      GlStateManager.pushMatrix();
      double d3 = MathHelper.clamp_double(this.animation.getValue() / 100.0, 0.0, 1.0) * 0.2 + 0.8;
      GlStateManager.translate(midX, midY, 0.0);
      GlStateManager.scale(d3, d3, 1.0);
      GlStateManager.translate(-midX, -midY, 0.0);

      int drawColor = RenderUtils.withAlpha(-16777216, (int)(255.0F * alpha));

      if (!this.dashboardOpen) {
         for (MenuCategoryPanel panel : this.buttons) {
            float px = panel.getX();
            float py = panel.getY();
            float pw = MenuCategoryPanel.getWidth();
            float headerH = MenuCategoryPanel.getHeaderHeight();
            float bottomY = panel.getBottomY();

            // Header rect (width 114, x - 2 to x + 112)
            RenderUtils.drawRect(px - 2.0F, py, px + pw + 2.0F, py + headerH, drawColor);

            // Body rect (width 110, x to x + 110)
            if (bottomY > py + headerH) {
               RenderUtils.drawRect(px, py + headerH, px + pw, bottomY, drawColor);
            }
         }
      }

      GlStateManager.popMatrix();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
   }

   private void renderLayers(Framebuffer framebuffer3, int i, int i2, float f) {
      this.framebuffer2 = framebuffer3;
      GlStateManager.pushMatrix();
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      float f2 = Math.min(this.visibility / 100.0F, 1.0F);
      RenderUtils.resetColor();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableTexture2D();
      boolean isPanelAnimating = this.buttons.stream().anyMatch(MenuCategoryPanel::isAnimating);
      boolean flag = this.mouseHeld || this.tickCount < 60L || this.redrawPending || this.dashboardOpen || isPanelAnimating;
      this.redrawPending = false;
      boolean usePostProcessing = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isEnabled();
      boolean flag2 = !usePostProcessing && this.menuLayout.getBloomRadius() > 0 && this.menuLayout.getBloomSigma() > 0.0;
      if (shadersEnabled) {
         if (ModuleManager.postProcessing != null && !ModuleManager.postProcessing.isBloomEnabled("ClickGui")) {
            flag2 = false;
         }
         this.bloomRenderer.setEnabled(flag2);
      }

      float backgroundDimAmount = this.menuLayout.getBackgroundDimAmount();
      boolean flag3 = backgroundDimAmount != 0.0;
      ScaledResolution scaledResolution2 = new ScaledResolution(this.mc);
      int backgroundBlurRadius = this.menuLayout.getBackgroundBlurRadius();
      float backgroundBlurSigma = this.menuLayout.getBackgroundBlurSigma();
      int effectiveBlurRadius = (int)(f2 * backgroundBlurRadius);
      boolean clickGuiBlurAllowed = !usePostProcessing && (ModuleManager.postProcessing == null || ModuleManager.postProcessing.isBlurEnabled());
      boolean flag4 = effectiveBlurRadius > 0 && backgroundBlurSigma > 0.0 && shadersEnabled && clickGuiBlurAllowed && this.clickGui.backgroundSettings.getElements().get(0).isSelected();
      if (flag4) {
         this.blurRenderer.blur("Background", framebuffer3, scaledResolution2, effectiveBlurRadius, backgroundBlurSigma, 1);
         framebuffer3.bindFramebuffer(true);
      }

      int desaturationColor = this.menuLayout.getDesaturationColor();
      float desaturationFactor = this.menuLayout.getDesaturationFactor();
      float effectiveDesaturation = f2 < 0.02F ? 0.0F : desaturationFactor * f2;
      boolean flag5 = effectiveDesaturation > 0.0F && shadersEnabled && this.clickGui.backgroundSettings.getElements().get(1).isSelected();
      if (flag5) {
         BlurFramebuffer blurFramebuffer = null;
         if (!flag4) {
            blurFramebuffer = Novoline.framebufferManager.acquire("Desaturation");
            blurFramebuffer.bindFramebuffer();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            RenderUtils.drawFramebuffer(framebuffer3, scaledResolution2);
            framebuffer3.bindFramebuffer(true);
         }

         float[] fArray = RenderUtils.unpackColorNormalized(desaturationColor);
         DesaturationShader desaturationShader2 = DesaturationShader.desaturationShader;
         desaturationShader2.use();
         ARBShaderObjects.glUniform1iARB(desaturationShader2.getUniformLocation("u_texture"), 0);
         ARBShaderObjects.glUniform3fARB(desaturationShader2.getUniformLocation("u_color"), fArray[0], fArray[1], fArray[2]);
         ARBShaderObjects.glUniform1fARB(desaturationShader2.getUniformLocation("u_factor"), effectiveDesaturation);
         if (blurFramebuffer != null) {
            blurFramebuffer.drawFullscreen(scaledResolution2);
            blurFramebuffer.release();
         }
      }

      if (flag4) {
         this.blurRenderer.drawResult(scaledResolution2);
         this.blurRenderer.releaseResult();
      }

      if (flag5) {
         ARBShaderObjects.glUseProgramObjectARB(0);
      }

      if (flag3 && f2 >= 0.02F && !usePostProcessing && this.clickGui.backgroundSettings.getElements().get(2).isSelected()) {
         int i3 = RenderUtils.withAlpha(0, (int)(255.0F * backgroundDimAmount * f2));
         RenderUtils.drawRect(0.0F, 0.0F, scaledResolution2.getScaledWidth(), scaledResolution2.getScaledHeight(), i3);
      }

      float panelOpacity = this.menuLayout.getPanelOpacity();
      if (shadersEnabled) {
         boolean flag6 = panelOpacity < 1.0;
         this.panelBlurActive = flag6 && (flag4 || flag3) && flag && clickGuiBlurAllowed && this.clickGui.panelsSettings.getElements().get(0).isSelected();
         if (this.panelBlurActive) {
            this.blurRenderer.blur("Panel", framebuffer3, scaledResolution2, this.menuLayout.getPanelBlurRadius(), this.menuLayout.getPanelBlurSigma(), 1);
            framebuffer3.bindFramebuffer(true);
         } else {
            this.blurRenderer.releaseResult();
         }
      }

      if (flag) {
         this.framebuffer = RenderUtils.ensureFramebuffer(this.framebuffer);
         this.framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
         this.framebuffer.framebufferClear();
         this.framebuffer.bindFramebuffer(true);
         GlStateManager.enableBlend();
         float f3 = (float)i * scaledResolution2.getScaleFactor() / 2.0F;
         float f4 = (float)i2 * scaledResolution2.getScaleFactor() / 2.0F;
         float scaleFactor = scaledResolution2.getScaleFactor();
         GlStateManager.scale(2.0F / scaleFactor, 2.0F / scaleFactor, 2.0F / scaleFactor);
         double d = scaledResolution2.getScaledWidth_double();
         double d2 = scaledResolution2.getScaledHeight_double();
         this.scaledResolution = scaledResolution2;
         scaledResolution2.handleDouble2(d / 2.0 * scaleFactor);
         scaledResolution2.handleDouble(d2 / 2.0 * scaleFactor);
         if (this.dashboardOpen) {
            this.dashboardScreen.drawScreen(i, i2, f);
            if (shadersEnabled) {
               this.blurRenderer.releaseResult();
            }
         } else {
            for (MenuCategoryPanel menuCategoryPanel : this.buttons) {
               menuCategoryPanel.updateLayout(f3, f4, f);
            }

            if (shadersEnabled && this.panelBlurActive) {
               this.blurRenderer.bindResultTexture();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GradientRectRenderer gradientRectRenderer = new GradientRectRenderer(this.scaledResolution.getFloat(), this.scaledResolution.getFloat2());

               for (MenuCategoryPanel menuCategoryPanel2 : this.buttons) {
                  menuCategoryPanel2.addRects(gradientRectRenderer, f);
               }

               gradientRectRenderer.draw();
            }

            if (this.textSelection.isVisible()) {
               this.textSelection.draw(f);
            }

            if (shadersEnabled) {
               this.blurRenderer.releaseResult();
            }

            if (panelOpacity > 0.0) {
               OutlineRectRenderer outlineRectRenderer = new OutlineRectRenderer(panelOpacity);

               for (MenuCategoryPanel menuCategoryPanel3 : this.buttons) {
                  menuCategoryPanel3.addRects(outlineRectRenderer, f);
               }

               outlineRectRenderer.draw();
            }

            float panelTextureAlpha = this.menuLayout.getPanelTextureAlpha();
            if (panelTextureAlpha > 0.0 && this.clickGui.panelsSettings.getElements().get(0).isSelected() && !this.clickGui.performanceMode.isEnabled()) {
               this.mc.getTextureManager().bindTexture(NOISE_TEXTURE);
               GlStateManager.alphaFunc(516, 0.001F);
               GlStateManager.color(1.0F, 1.0F, 1.0F, panelTextureAlpha);
               RoundedRectRenderer roundedRectRenderer = new RoundedRectRenderer(256.0F, 256.0F);

               for (MenuCategoryPanel menuCategoryPanel4 : this.buttons) {
                  menuCategoryPanel4.addRects(roundedRectRenderer, f);
               }

               roundedRectRenderer.draw();
               GlStateManager.alphaFunc(516, 0.1F);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }

            for (MenuCategoryPanel menuCategoryPanel5 : this.buttons) {
               menuCategoryPanel5.render(f3, f4, f);
               float bottomY = menuCategoryPanel5.getBottomY();
               if (!usePostProcessing && this.clickGui.panelsSettings.getElements().get(1).isSelected()) {
                  RenderUtils.drawShadow(
                     menuCategoryPanel5.getX(),
                     menuCategoryPanel5.getY() + MenuCategoryPanel.getHeaderHeight(),
                     menuCategoryPanel5.getX() + MenuCategoryPanel.getWidth(),
                     bottomY,
                     4.0F,
                     1375731712,
                     false
                  );
               }
            }

            this.renderText(Fonts.rubikRegularSize18, WidgetType.HEADER, f, false);
            this.renderText(Fonts.rubikRegularSize16, WidgetType.FEATURE, f, this.clickGui.glowSettings.getElements().get(0).isSelected());
            this.renderText(Fonts.rubikRegularSize14, WidgetType.REGULAR, f, this.clickGui.glowSettings.getElements().get(1).isSelected());
            if (flag2 && shadersEnabled) {
               this.bloomRenderer
                  .applyBloom(
                     this.getFramebuffer(),
                     scaledResolution2,
                     "Neon",
                     this.clickGuiTheme.getSecondaryColor(true),
                     this.menuLayout.getBloomRadius(),
                     this.menuLayout.getBloomSigma(),
                     1.0F,
                     this.menuLayout.getBloomExposure(),
                     this.menuLayout.getBloomGamma()
                  );
            }
         }

         RenderUtils.drawTextureWithAlpha(10.0F, scaledResolution2.getScaledHeight() - 20.0F - 10.0F, 20.0F, 20.0F, 1.0F, DASHBOARD_ICON);
      }

      if (this.framebuffer != null) {
         framebuffer3.bindFramebuffer(true);
         GlStateManager.color(1.0F, 1.0F, 1.0F, f2);
         GlStateManager.alphaFunc(516, 0.001F);
         RenderUtils.applyBlendFunc();
         GlStateManager.enableBlend();
         GlStateManager.enableTexture2D();
         this.framebuffer.bindFramebufferTexture();
         GlStateManager.pushMatrix();
         double d3 = MathHelper.clamp_double(this.animation.getValue() / 100.0, 0.0, 1.0) * 0.2 + 0.8;
         GlStateManager.translate(scaledResolution2.getScaledWidth_double() / 2.0, scaledResolution2.getScaledHeight_double() / 2.0, 0.0);
         GlStateManager.scale(d3, d3, 1.0);
         GlStateManager.translate(-scaledResolution2.getScaledWidth_double() / 2.0, -scaledResolution2.getScaledHeight_double() / 2.0, 0.0);
         RenderUtils.drawFullscreenQuad(scaledResolution2);
         GlStateManager.popMatrix();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.disableBlend();
         RenderUtils.applyBlendFunc();
         RenderUtils.resetColor();
      }

      Novoline.framebufferManager.assertAllReleased();
      GlStateManager.popMatrix();
      ARBShaderObjects.glUseProgramObjectARB(0);
      RenderUtils.resetColor();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableTexture2D();
   }

   public void rebuildPanels() {
      this.buttons.clear();
      float f = 10.0F;

      for (Category category : Category.values()) {
         this.buttons.add(new MenuCategoryPanel(this, category, f, 10.0F));
         f += MenuCategoryPanel.getWidth() + 10.0F;
      }
   }

   public void open() {
      this.closing = this.mouseHeld = this.resourcesReleased = false;
      this.visibility = (float)(this.tickCount = 0L);
      this.animation.unlock();
      this.animation.start(this.animation.getValue(), 100.0, 500L, Easing.backOut);
      this.animation2.unlock();
      this.animation2.start(this.animation2.getValue(), 0.0, 400L, Easing.cubicInOut);
   }

   @Override
   public void startClosing() {
      if (this.closing) {
         return;
      }
      this.closing = true;
      if (this.clickGui.isEnabled()) {
         this.clickGui.setEnabled(false);
      }
      this.animation.unlock();
      this.animation.start(this.animation.getValue(), 0.0, 300L, Easing.cubicInOut);
      Entry entry2 = this.clickGui.entry;
      if (entry2 != null && entry2.getMetadata() != null) {
         this.animation2.unlock();
         this.animation2.start(this.animation2.getValue(), entry2.getMetadata().getWidth(), 300L, Easing.cubicInOut);
      }
   }

   @Override
   protected void mouseReleased(int i, int i2, int i3) {
      if (this.dashboardOpen) {
         this.dashboardScreen.mouseReleased(i, i2, i3);
      } else {
         this.mouseHeld = false;
         ScaledResolution scaledResolution2 = new ScaledResolution(this.mc);
         float f = (float)i * scaledResolution2.getScaleFactor() / 2.0F;
         float f2 = (float)i2 * scaledResolution2.getScaleFactor() / 2.0F;
         Iterator iterator2 = this.buttons.iterator();

         while (iterator2.hasNext()) {
            ((MenuCategoryPanel)iterator2.next()).handleMouseRelease(f, f2, i3);
         }
      }
   }

   @Override
   protected void mouseClicked(int i, int i2, int i3) throws IOException {
      if (this.dashboardOpen) {
         this.dashboardScreen.mouseClicked(i, i2, i3);
      } else {
         this.onGuiClosed();
         this.mouseHeld = true;
         ScaledResolution scaledResolution2 = new ScaledResolution(this.mc);
         float f = (float)i * scaledResolution2.getScaleFactor() / 2.0F;
         float f2 = (float)i2 * scaledResolution2.getScaleFactor() / 2.0F;
         if (i3 == 0
            && f >= 10.0F
            && f <= 30.0F
            && f2 >= (float)scaledResolution2.getScaledHeight() * scaledResolution2.getScaleFactor() / 2.0F - 20.0F - 10.0F
            && f2 <= (float)scaledResolution2.getScaledHeight() * scaledResolution2.getScaleFactor() / 2.0F - 20.0F - 10.0F + 20.0F) {
            this.dashboardOpen = !this.dashboardOpen;
         }

         for (int i4 = this.buttons.size() - 1; i4 >= 0; i4--) {
            MenuCategoryPanel menuCategoryPanel = this.buttons.get(i4);
            if (f >= menuCategoryPanel.getX() && f <= menuCategoryPanel.getX() + MenuCategoryPanel.getWidth()) {
               float bottomY = menuCategoryPanel.getBottomY();
               if (f2 >= menuCategoryPanel.getY() && f2 <= bottomY) {
                  menuCategoryPanel.handleMouseClick(f, f2, i3);
                  return;
               }
            }
         }

         if (this.textSelection.isVisible()) {
            this.textSelection.updateFocus((int)f, (int)f2);
         }
      }
   }

   public MenuClickGuiScreen() {
      this.clickGui = ModuleManager.clickGui;
      this.backgroundManager = this.clickGui.backgroundManager;
      this.dashboardScreen = new DashboardScreen();
      if (shadersEnabled) {
         this.blurRenderer = new BlurRenderer(Novoline.framebufferManager);
         this.bloomRenderer = new BloomRenderer(Novoline.framebufferManager);
      } else {
         Novoline.getNovoline().sendChatMessage("Warning! Shaders are not supported!");
      }

      this.buttons = new CopyOnWriteArrayList<>();
      this.rebuildPanels();
   }

   @Override
   public void onResize(Minecraft minecraft2, int i, int i2) {
      super.onResize(minecraft2, i, i2);
      this.initGui();
      this.onGuiClosed();
   }

   private void renderText(CFontRenderer cFontRenderer, WidgetType widgetType, float f, boolean flag) {
      ShaderRoundedRectRenderer shaderRoundedRectRenderer = new ShaderRoundedRectRenderer(cFontRenderer);
      Iterator iterator2 = this.buttons.iterator();

      while (iterator2.hasNext()) {
         ((MenuCategoryPanel)iterator2.next()).renderText(shaderRoundedRectRenderer, widgetType, f, false);
      }

      shaderRoundedRectRenderer.draw();
      if (flag && shadersEnabled) {
         this.bloomRenderer.submit(() -> this.handleCFontRenderer(cFontRenderer, widgetType, f));
      } else {
         this.handleCFontRenderer(cFontRenderer, widgetType, f);
      }
   }

   private void onGuiClosed() {
      this.tickCount = 0L;
   }

   @Override
   public void initGui() {
      float f = 10.0F;
      float f2 = 10.0F;
      ScaledResolution scaledResolution2 = this.scaledResolution == null ? new ScaledResolution(this.mc) : this.scaledResolution;
      float scaledWidth = scaledResolution2.getScaledWidth();
      float scaledHeight = scaledResolution2.getScaledHeight();
      float f3 = MenuCategoryPanel.getWidth() + 10.0F;

      for (int i = 0; i < this.buttons.size(); i++) {
         MenuCategoryPanel menuCategoryPanel = this.buttons.get(i);
         if (menuCategoryPanel.getX() > scaledWidth - f3) {
            float f4 = (MenuCategoryPanel.getHeaderHeight() + 5.0F) * (this.buttons.size() - i);
            menuCategoryPanel.setExpanded(false);
            menuCategoryPanel.setX(10.0F);
            menuCategoryPanel.setY(scaledHeight - 5.0F - f4);
         } else if (f <= scaledWidth - f3) {
            menuCategoryPanel.setExpanded(true);
            menuCategoryPanel.setX(f);
            menuCategoryPanel.setY(f2);
            f += f3;
         }
      }

      this.textSelection = new TextSelection(Fonts.rubikRegularSize16, this.blurRenderer, scaledResolution2.getScaledWidth() / 2 - 100, scaledResolution2.getScaledHeight() - 50);
      this.textSelection.setVisible(false);
   }
}
