package wtf.novoline.utils.render;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;

public class ShadowRenderer {
   private final List<Runnable> pendingDraws = new ArrayList<>();
   private final ShaderProgram shaderProgram = new ShaderProgram(new ResourceLocation("novoline", "shaders/shadow.fsh"));
   private final FramebufferManager framebufferManager;

   private int uTextureLoc = -1;
   private int uDirLoc = -1;
   private int uSampleCountLoc = -1;
   private int uOffsetsLoc = -1;
   private int uWeightsLoc = -1;
   private int uGammaLoc = -1;
   private int uColorLoc = -1;
   private boolean uniformsInitialized = false;

   private final FloatBuffer offsetsBuffer = BufferUtils.createFloatBuffer(32);
   private final FloatBuffer weightsBuffer = BufferUtils.createFloatBuffer(32);

   private float lastRadius = -1.0F;
   private float lastSigma = -1.0F;
   private int cachedSampleCount = 0;

   public void submit(Runnable runnable) {
      this.pendingDraws.add(runnable);
   }

   public ShadowRenderer(FramebufferManager framebufferManager2) {
      this.framebufferManager = framebufferManager2;
   }

   private void updateKernel(float radiusF, float sigmaF) {
      if (radiusF == this.lastRadius && sigmaF == this.lastSigma) {
         return;
      }
      this.lastRadius = radiusF;
      this.lastSigma = sigmaF;

      int r = Math.max(1, Math.min((int) Math.ceil(radiusF), 30));
      float sigma = Math.max(0.1F, sigmaF);
      float twoSigmaSq = 2.0F * sigma * sigma;
      float norm = (float) (1.0 / (Math.abs(sigma) * 2.5066282746310002));

      float[] dWeights = new float[r + 1];
      dWeights[0] = norm;
      float totalSum = dWeights[0];

      for (int x = 1; x <= r; x++) {
         float w = (float) (norm * Math.exp(-(x * x) / twoSigmaSq));
         dWeights[x] = w;
         totalSum += 2.0F * w;
      }

      this.offsetsBuffer.clear();
      this.weightsBuffer.clear();

      this.offsetsBuffer.put(0.0F);
      this.weightsBuffer.put(dWeights[0] / totalSum);
      int count = 1;

      for (int x = 1; x <= r; x += 2) {
         if (x + 1 <= r) {
            float w1 = dWeights[x];
            float w2 = dWeights[x + 1];
            float wSum = w1 + w2;
            float offset = (wSum > 0.0F) ? ((x * w1 + (x + 1) * w2) / wSum) : (float) x;
            this.offsetsBuffer.put(offset);
            this.weightsBuffer.put(wSum / totalSum);
         } else {
            this.offsetsBuffer.put((float) x);
            this.weightsBuffer.put(dWeights[x] / totalSum);
         }
         count++;
      }

      while (this.offsetsBuffer.position() < 32) {
         this.offsetsBuffer.put(0.0F);
         this.weightsBuffer.put(0.0F);
      }

      this.offsetsBuffer.flip();
      this.weightsBuffer.flip();
      this.cachedSampleCount = count;
   }

   private void blurPass(ScaledResolution scaledResolution, boolean flag, float[] fArray, float f, float f2, float f3) {
      this.updateKernel(f, f2);

      this.shaderProgram.use();
      if (!this.uniformsInitialized) {
         this.uTextureLoc = this.shaderProgram.getUniformLocation("u_texture");
         this.uDirLoc = this.shaderProgram.getUniformLocation("u_dir");
         this.uSampleCountLoc = this.shaderProgram.getUniformLocation("u_sampleCount");
         int locOffsets = this.shaderProgram.getUniformLocation("u_offsets[0]");
         if (locOffsets == -1) {
            locOffsets = this.shaderProgram.getUniformLocation("u_offsets");
         }
         this.uOffsetsLoc = locOffsets;
         int locWeights = this.shaderProgram.getUniformLocation("u_weights[0]");
         if (locWeights == -1) {
            locWeights = this.shaderProgram.getUniformLocation("u_weights");
         }
         this.uWeightsLoc = locWeights;
         this.uGammaLoc = this.shaderProgram.getUniformLocation("u_gamma");
         this.uColorLoc = this.shaderProgram.getUniformLocation("u_color");
         this.uniformsInitialized = true;
      }

      Minecraft mc = Minecraft.getMinecraft();
      float dirX = flag ? (1.0F / (float) mc.displayWidth) : 0.0F;
      float dirY = flag ? 0.0F : (1.0F / (float) mc.displayHeight);

      ARBShaderObjects.glUniform1iARB(this.uTextureLoc, 0);
      ARBShaderObjects.glUniform2fARB(this.uDirLoc, dirX, dirY);
      ARBShaderObjects.glUniform1iARB(this.uSampleCountLoc, this.cachedSampleCount);

      this.offsetsBuffer.position(0);
      OpenGlHelper.glUniform1(this.uOffsetsLoc, this.offsetsBuffer);
      this.weightsBuffer.position(0);
      OpenGlHelper.glUniform1(this.uWeightsLoc, this.weightsBuffer);

      ARBShaderObjects.glUniform1fARB(this.uGammaLoc, f3);
      ARBShaderObjects.glUniform3fARB(this.uColorLoc, fArray[0], fArray[1], fArray[2]);

      this.shaderProgram.drawFullscreenQuad(scaledResolution);
   }

   public void render(Framebuffer framebuffer, ScaledResolution scaledResolution, int i, float f, float f2, float f3, boolean flag) {
      if (!this.pendingDraws.isEmpty()) {
         float[] fArray = RenderUtils.unpackColorNormalized(i);
         int i2 = RenderUtils.withAlpha(i, 0);
         GlStateManager.alphaFunc(516, 0.001F);
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         BlurFramebuffer blurFramebuffer = this.framebufferManager.acquire("screen-shadow-target", i2);
         blurFramebuffer.bindFramebuffer();

         for (Runnable runnable : this.pendingDraws) {
            runnable.run();
         }

         this.pendingDraws.clear();
         BlurFramebuffer blurFramebuffer2 = this.framebufferManager.acquire("screen-shadow-h-blur", i2);
         blurFramebuffer2.bindFramebuffer();
         blurFramebuffer.bindTexture();
         this.blurPass(scaledResolution, true, fArray, f, f2, f3);
         BlurFramebuffer blurFramebuffer3 = this.framebufferManager.acquire("screen-shadow-v-blur", i2);
         blurFramebuffer3.bindFramebuffer();
         blurFramebuffer2.bindTexture();
         this.blurPass(scaledResolution, false, fArray, f, f2, f3);
         framebuffer.bindFramebuffer(true);
         RenderUtils.resetColor();
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         blurFramebuffer3.bindTexture();
         RenderUtils.drawFullscreenQuad(scaledResolution);
         if (flag) {
            blurFramebuffer.bindTexture();
            RenderUtils.drawFullscreenQuad(scaledResolution);
         }

         GlStateManager.alphaFunc(516, 0.1F);
         blurFramebuffer.release();
         blurFramebuffer2.release();
         blurFramebuffer3.release();
      }
   }
}
