package rat.novoline.utils.render.blur;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

public class KawaseBlur {
   private static final List<Framebuffer> framebufferList = new ArrayList<>();
   public static ShaderUtil kawaseDown;
   public static ShaderUtil kawaseUp;
   public static Framebuffer framebuffer = new Framebuffer(1, 1, false);
   private static int currentIterations;

   public static void checkShaders() {
      if (kawaseDown == null) {
         kawaseDown = new ShaderUtil("kawaseDown");
      }
      if (kawaseUp == null) {
         kawaseUp = new ShaderUtil("kawaseUp");
      }
   }

   public static void setupUniforms(float offset) {
      checkShaders();
      kawaseDown.setUniformf("offset", offset, offset);
      kawaseUp.setUniformf("offset", offset, offset);
   }

   public static Framebuffer createFrameBuffer(Framebuffer fb) {
      return createFrameBuffer(fb, false);
   }

   public static Framebuffer createFrameBuffer(Framebuffer fb, boolean depth) {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.displayWidth <= 1 || mc.displayHeight <= 1) {
         return fb;
      }
      if (needsNewFramebuffer(fb)) {
         if (fb != null) {
            fb.deleteFramebuffer();
         }
         Framebuffer newFb = new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
         newFb.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
         return newFb;
      }
      return fb;
   }

   public static boolean needsNewFramebuffer(Framebuffer fb) {
      Minecraft mc = Minecraft.getMinecraft();
      return fb == null || fb.framebufferWidth != mc.displayWidth || fb.framebufferHeight != mc.displayHeight;
   }

   private static void initFramebuffers(float iterations) {
      for (Framebuffer fb : framebufferList) {
         fb.deleteFramebuffer();
      }
      framebufferList.clear();

      Minecraft mc = Minecraft.getMinecraft();
      framebufferList.add(framebuffer = createFrameBuffer(null, false));
      framebuffer.setFramebufferFilter(GL11.GL_LINEAR);
      framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);

      for (int i = 1; i <= iterations; i++) {
         int w = Math.max(1, (int)(mc.displayWidth / Math.pow(2.0, i)));
         int h = Math.max(1, (int)(mc.displayHeight / Math.pow(2.0, i)));
         Framebuffer currentBuffer = new Framebuffer(w, h, false);
         currentBuffer.setFramebufferFilter(GL11.GL_LINEAR);
         currentBuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
         GlStateManager.bindTexture(currentBuffer.framebufferTexture);
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
         GlStateManager.bindTexture(0);

         framebufferList.add(currentBuffer);
      }
   }

   public static void renderBlur(int stencilFrameBufferTexture, int iterations, int offset) {
      renderBlur(stencilFrameBufferTexture, iterations, (float)offset);
   }

   public static void renderBlur(int stencilFrameBufferTexture, int iterations, float offset) {
      if (!Display.isVisible()) return;
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.displayWidth <= 1 || mc.displayHeight <= 1) return;
      checkShaders();
      if (currentIterations != iterations || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
         initFramebuffers(iterations);
         currentIterations = iterations;
      }

      renderFBO(framebufferList.get(1), mc.getFramebuffer().framebufferTexture, kawaseDown, offset);

      // Downsample
      for (int i = 1; i < iterations; i++) {
         renderFBO(framebufferList.get(i + 1), framebufferList.get(i).framebufferTexture, kawaseDown, offset);
      }

      // Upsample
      for (int i = iterations; i > 1; i--) {
         renderFBO(framebufferList.get(i - 1), framebufferList.get(i).framebufferTexture, kawaseUp, offset);
      }

      Framebuffer lastBuffer = framebufferList.get(0);
      lastBuffer.framebufferClear();
      lastBuffer.bindFramebuffer(false);
      GlStateManager.enableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      kawaseUp.init();
      kawaseUp.setUniformf("offset", offset, offset);
      kawaseUp.setUniformi("inTexture", 0);
      kawaseUp.setUniformi("check", 1);
      kawaseUp.setUniformi("textureToCheck", 16);
      kawaseUp.setUniformf("halfpixel", 1.0F / (float)lastBuffer.framebufferWidth, 1.0F / (float)lastBuffer.framebufferHeight);
      kawaseUp.setUniformf("iResolution", (float)lastBuffer.framebufferWidth, (float)lastBuffer.framebufferHeight);

      GlStateManager.setActiveTexture(GL13.GL_TEXTURE16);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, stencilFrameBufferTexture);
      GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferList.get(1).framebufferTexture);
      ShaderUtil.drawQuads();
      kawaseUp.unload();

      GlStateManager.setActiveTexture(GL13.GL_TEXTURE16);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
      GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

      mc.getFramebuffer().bindFramebuffer(true);
      GlStateManager.enableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferList.get(0).framebufferTexture);
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      ShaderUtil.drawQuads();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private static void renderFBO(Framebuffer fb, int framebufferTexture, ShaderUtil shader, float offset) {
      fb.framebufferClear();
      fb.bindFramebuffer(false);
      GlStateManager.enableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      shader.init();
      GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferTexture);
      shader.setUniformf("offset", offset, offset);
      shader.setUniformi("inTexture", 0);
      shader.setUniformi("check", 0);
      shader.setUniformf("halfpixel", 1.0F / (float)fb.framebufferWidth, 1.0F / (float)fb.framebufferHeight);
      shader.setUniformf("iResolution", (float)fb.framebufferWidth, (float)fb.framebufferHeight);
      ShaderUtil.drawQuads();
      shader.unload();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
   }
}
