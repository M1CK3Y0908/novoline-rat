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

public class KawaseBloom {
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

   private static void initFramebuffers(float iterations) {
      for (Framebuffer fb : framebufferList) {
         fb.deleteFramebuffer();
      }
      framebufferList.clear();

      Minecraft mc = Minecraft.getMinecraft();
      framebufferList.add(framebuffer = KawaseBlur.createFrameBuffer(null, true));

      for (int i = 1; i <= iterations; i++) {
         Framebuffer currentBuffer = new Framebuffer((int)(mc.displayWidth / Math.pow(2.0, i)), (int)(mc.displayHeight / Math.pow(2.0, i)), true);
         currentBuffer.setFramebufferFilter(GL11.GL_LINEAR);

         GlStateManager.bindTexture(currentBuffer.framebufferTexture);
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
         GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT);
         GlStateManager.bindTexture(0);

         framebufferList.add(currentBuffer);
      }
   }

   public static void renderBlur(int framebufferTexture, int iterations, int offset) {
      Minecraft mc = Minecraft.getMinecraft();
      if (!Display.isVisible() || mc.displayWidth <= 1 || mc.displayHeight <= 1) return;
      checkShaders();

      if (currentIterations != iterations || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
         initFramebuffers(iterations);
         currentIterations = iterations;
      }

      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);

      GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      renderFBO(framebufferList.get(1), framebufferTexture, kawaseDown, (float)offset);

      // Downsample
      for (int i = 1; i < iterations; i++) {
         renderFBO(framebufferList.get(i + 1), framebufferList.get(i).framebufferTexture, kawaseDown, (float)offset);
      }

      // Upsample
      for (int i = iterations; i > 1; i--) {
         renderFBO(framebufferList.get(i - 1), framebufferList.get(i).framebufferTexture, kawaseUp, (float)offset);
      }

      Framebuffer lastBuffer = framebufferList.get(0);
      lastBuffer.framebufferClear();
      lastBuffer.bindFramebuffer(false);
      kawaseUp.init();
      kawaseUp.setUniformf("offset", (float)offset, (float)offset);
      kawaseUp.setUniformi("inTexture", 0);
      kawaseUp.setUniformi("check", 1);
      kawaseUp.setUniformi("textureToCheck", 16);
      kawaseUp.setUniformf("halfpixel", 1.0F / (float)lastBuffer.framebufferWidth, 1.0F / (float)lastBuffer.framebufferHeight);
      kawaseUp.setUniformf("iResolution", (float)lastBuffer.framebufferWidth, (float)lastBuffer.framebufferHeight);
      GlStateManager.setActiveTexture(GL13.GL_TEXTURE16);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferTexture);
      GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferList.get(1).framebufferTexture);
      ShaderUtil.drawQuads();
      kawaseUp.unload();

      GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
      mc.getFramebuffer().bindFramebuffer(false);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferList.get(0).framebufferTexture);
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      ShaderUtil.drawQuads();
      GlStateManager.bindTexture(0);
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
   }

   public static void renderBlur(int framebufferTexture, float alpha) {
      renderBlur(framebufferTexture, 3, 1);
   }

   private static void renderFBO(Framebuffer framebuffer, int framebufferTexture, ShaderUtil shader, float offset) {
      framebuffer.framebufferClear();
      framebuffer.bindFramebuffer(false);
      shader.init();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebufferTexture);
      shader.setUniformf("offset", offset, offset);
      shader.setUniformi("inTexture", 0);
      shader.setUniformi("check", 0);
      shader.setUniformf("halfpixel", 1.0F / (float)framebuffer.framebufferWidth, 1.0F / (float)framebuffer.framebufferHeight);
      shader.setUniformf("iResolution", (float)framebuffer.framebufferWidth, (float)framebuffer.framebufferHeight);
      ShaderUtil.drawQuads();
      shader.unload();
   }
}
