package wtf.novoline.utils.render.blur;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

public class KawaseBloom {
   public static final int SHADOW_ITERATIONS = 4;
   public static final float SHADOW_OFFSET = 50.0F / 60.0F;

   private static final List<Framebuffer> framebufferList = new ArrayList<>();
   public static ShaderUtil kawaseDown;
   public static ShaderUtil kawaseUp;
   public static Framebuffer framebuffer = new Framebuffer(1, 1, true);
   private static int currentIterations;

   public static void checkShaders() {
      if (kawaseDown == null) {
         kawaseDown = new ShaderUtil("kawaseDownBloom");
      }
      if (kawaseUp == null) {
         kawaseUp = new ShaderUtil("kawaseUpBloom");
      }
   }

   private static void initFramebuffers(int iterations) {
      Minecraft mc = Minecraft.getMinecraft();
      if (framebufferList.size() >= iterations + 1
            && framebuffer.framebufferWidth == mc.displayWidth
            && framebuffer.framebufferHeight == mc.displayHeight) {
         return;
      }
      for (Framebuffer fb : framebufferList) {
         fb.deleteFramebuffer();
      }
      framebufferList.clear();

      framebufferList.add(framebuffer = KawaseBlur.createFrameBuffer(null, true));
      framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);

      for (int i = 1; i <= iterations; i++) {
         int w = Math.max(1, (int)(mc.displayWidth / Math.pow(2.0, i)));
         int h = Math.max(1, (int)(mc.displayHeight / Math.pow(2.0, i)));
         Framebuffer currentBuffer = new Framebuffer(w, h, true);
         currentBuffer.setFramebufferFilter(GL11.GL_LINEAR);
         currentBuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
         GlStateManager.bindTexture(currentBuffer.framebufferTexture);
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
         GlStateManager.bindTexture(0);

         framebufferList.add(currentBuffer);
      }
      currentIterations = iterations;
   }

   public static void renderBlur(int framebufferTexture, float alpha) {
      renderBlur(framebufferTexture, SHADOW_ITERATIONS, SHADOW_OFFSET, alpha);
   }

   public static void renderBlur(int framebufferTexture, int iterations, int offset) {
      renderBlur(framebufferTexture, iterations, (float)offset, 1.0F);
   }

   public static void renderBlur(int framebufferTexture, int iterations, float offset, float alpha) {
      checkShaders();
      Minecraft mc = Minecraft.getMinecraft();
      if (framebufferList.size() < iterations + 1 || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
         initFramebuffers(Math.max(SHADOW_ITERATIONS, iterations));
      }

      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);

      GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      renderFBO(framebufferList.get(1), framebufferTexture, kawaseDown, offset, 0);

      // Downsample
      for (int i = 1; i < iterations; i++) {
         renderFBO(framebufferList.get(i + 1), framebufferList.get(i).framebufferTexture, kawaseDown, offset, 0);
      }

      // Upsample
      for (int i = iterations; i > 1; i--) {
         renderFBO(framebufferList.get(i - 1), framebufferList.get(i).framebufferTexture, kawaseUp, offset, 0);
      }

      Framebuffer lastBuffer = framebufferList.get(0);
      lastBuffer.framebufferClear();
      lastBuffer.bindFramebuffer(false);
      kawaseUp.init();
      kawaseUp.setUniformf("offset", offset, offset);
      kawaseUp.setUniformi("inTexture", 0);
      kawaseUp.setUniformi("check", 1);
      kawaseUp.setUniformf("alpha", MathHelper.clamp_float(alpha, 0.0F, 1.0F));
      kawaseUp.setUniformi("textureToCheck", 16);
      kawaseUp.setUniformf("halfpixel", 1.0F / (float)lastBuffer.framebufferWidth, 1.0F / (float)lastBuffer.framebufferHeight);
      kawaseUp.setUniformf("iResolution", (float)lastBuffer.framebufferWidth, (float)lastBuffer.framebufferHeight);

      GlStateManager.setActiveTexture(GL13.GL_TEXTURE16);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferTexture);
      GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferList.get(1).framebufferTexture);
      ShaderUtil.drawQuads();
      kawaseUp.unload();

      GlStateManager.setActiveTexture(GL13.GL_TEXTURE16);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
      GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

      GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
      mc.getFramebuffer().bindFramebuffer(true);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferList.get(0).framebufferTexture);
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      ShaderUtil.drawQuads();
      GlStateManager.bindTexture(0);
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void renderExposureGlow(int uiTexture, float alpha) {
      if (alpha <= 0.001F) return;
      checkShaders();
      Minecraft mc = Minecraft.getMinecraft();
      int iterations = 3;
      float offset = 1.8F;

      if (framebufferList.size() < Math.max(SHADOW_ITERATIONS, iterations) + 1
            || framebuffer.framebufferWidth != mc.displayWidth
            || framebuffer.framebufferHeight != mc.displayHeight) {
         initFramebuffers(Math.max(SHADOW_ITERATIONS, iterations));
      }

      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);

      GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);

      // Downsample pass 1 (with content color extraction: check = 2)
      renderFBO(framebufferList.get(1), uiTexture, kawaseDown, offset, 2);

      // Subsequent downsample passes (check = 0)
      for (int i = 1; i < iterations; i++) {
         renderFBO(framebufferList.get(i + 1), framebufferList.get(i).framebufferTexture, kawaseDown, offset, 0);
      }

      // Upsample passes (check = 0)
      for (int i = iterations; i > 1; i--) {
         renderFBO(framebufferList.get(i - 1), framebufferList.get(i).framebufferTexture, kawaseUp, offset, 0);
      }

      // Final upsample into framebufferList.get(0) with content color saturation and falloff (check = 2)
      Framebuffer lastBuffer = framebufferList.get(0);
      lastBuffer.framebufferClear();
      lastBuffer.bindFramebuffer(false);
      kawaseUp.init();
      kawaseUp.setUniformf("offset", offset, offset);
      kawaseUp.setUniformi("inTexture", 0);
      kawaseUp.setUniformi("check", 2);
      kawaseUp.setUniformf("alpha", MathHelper.clamp_float(alpha, 0.0F, 1.0F));
      kawaseUp.setUniformi("textureToCheck", 16);
      kawaseUp.setUniformf("halfpixel", 1.0F / (float)lastBuffer.framebufferWidth, 1.0F / (float)lastBuffer.framebufferHeight);
      kawaseUp.setUniformf("iResolution", (float)lastBuffer.framebufferWidth, (float)lastBuffer.framebufferHeight);

      GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferList.get(1).framebufferTexture);
      ShaderUtil.drawQuads();
      kawaseUp.unload();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

      // Composite colored glow onto mc.getFramebuffer() with alpha blending
      mc.getFramebuffer().bindFramebuffer(true);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferList.get(0).framebufferTexture);
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      ShaderUtil.drawQuads();
      GlStateManager.bindTexture(0);
   }

   public static void renderExposureGlow(int uiTexture, float exposure, float alpha) {
      renderExposureGlow(uiTexture, alpha);
   }

   private static void renderFBO(Framebuffer fb, int framebufferTexture, ShaderUtil shader, float offset) {
      renderFBO(fb, framebufferTexture, shader, offset, 0);
   }

   private static void renderFBO(Framebuffer fb, int framebufferTexture, ShaderUtil shader, float offset, int check) {
      fb.framebufferClear();
      fb.bindFramebuffer(false);
      shader.init();
      GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferTexture);
      shader.setUniformf("offset", offset, offset);
      shader.setUniformi("inTexture", 0);
      shader.setUniformi("check", check);
      shader.setUniformf("alpha", 1.0F);
      shader.setUniformf("halfpixel", 1.0F / (float)fb.framebufferWidth, 1.0F / (float)fb.framebufferHeight);
      shader.setUniformf("iResolution", (float)fb.framebufferWidth, (float)fb.framebufferHeight);
      ShaderUtil.drawQuads();
      shader.unload();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
   }
}
