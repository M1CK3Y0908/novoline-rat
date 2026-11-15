package wtf.novoline.utils.render;

import java.nio.FloatBuffer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;

public class BlurRenderer {
   private BlurFramebuffer blurFramebuffer;
   private final ShaderProgram shaderProgram = new ShaderProgram(new ResourceLocation("novoline", "shaders/blur.fsh"));
   private final FramebufferManager framebufferManager;

   private int uTextureLoc = -1;
   private int uDirLoc = -1;
   private int uSampleCountLoc = -1;
   private int uOffsetsLoc = -1;
   private int uWeightsLoc = -1;
   private boolean uniformsInitialized = false;

   private final FloatBuffer offsetsBuffer = BufferUtils.createFloatBuffer(32);
   private final FloatBuffer weightsBuffer = BufferUtils.createFloatBuffer(32);

   private int lastRadius = -1;
   private float lastSigma = -1.0F;
   private int lastStep = -1;
   private int cachedSampleCount = 0;

   public Framebuffer getFramebuffer() {
      return this.blurFramebuffer.getFramebuffer();
   }

   public BlurRenderer(FramebufferManager framebufferManager2) {
      this.framebufferManager = framebufferManager2;
   }

   private void updateKernel(int radius, float sigma, int step) {
      if (radius == this.lastRadius && sigma == this.lastSigma && step == this.lastStep) {
         return;
      }
      this.lastRadius = radius;
      this.lastSigma = sigma;
      this.lastStep = step;

      int r = Math.max(1, Math.min(radius, 40));
      int s = Math.max(1, step);
      float effSigma = Math.max(0.001F, sigma * 0.5F);
      float twoEffSigma = 2.0F * effSigma;
      float gaussNorm = (float)(1.0 / (2.0 * 3.14157 * effSigma));

      float[] dWeights = new float[41];
      int[] dIndices = new int[41];
      dWeights[0] = 1.0F;
      dIndices[0] = 0;
      float totalWeight = dWeights[0];

      int idx = 1;
      for (int i = 1; i <= r; i += s) {
         float x = (float) i / (float) r;
         float weight = (float)(s * (Math.exp(-(x * x) / twoEffSigma) * gaussNorm));
         if (weight < 1.0F / 255.0F) {
            break;
         }
         dWeights[idx] = weight;
         dIndices[idx] = i;
         totalWeight += 2.0F * weight;
         idx++;
      }

      this.offsetsBuffer.clear();
      this.weightsBuffer.clear();

      this.offsetsBuffer.put(0.0F);
      this.weightsBuffer.put(dWeights[0] / totalWeight);
      int count = 1;

      for (int j = 1; j < idx; j += 2) {
         if (j + 1 < idx) {
            float w1 = dWeights[j];
            float w2 = dWeights[j + 1];
            int i1 = dIndices[j];
            int i2 = dIndices[j + 1];
            float wSum = w1 + w2;
            float offset = (wSum > 0.0F) ? ((i1 * w1 + i2 * w2) / wSum) : (float) i1;
            this.offsetsBuffer.put(offset);
            this.weightsBuffer.put(wSum / totalWeight);
         } else {
            this.offsetsBuffer.put((float) dIndices[j]);
            this.weightsBuffer.put(dWeights[j] / totalWeight);
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

   private void blurPass(Framebuffer framebuffer, ScaledResolution scaledResolution, int dirIndex, int radius, float sigma, int step) {
      this.updateKernel(radius, sigma, step);

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
         this.uniformsInitialized = true;
      }

      float dirX = (dirIndex == 1 ? 0.0F : 1.0F) / (float) framebuffer.framebufferWidth;
      float dirY = (dirIndex == 1 ? 1.0F : 0.0F) / (float) framebuffer.framebufferHeight;

      ARBShaderObjects.glUniform1iARB(this.uTextureLoc, 0);
      ARBShaderObjects.glUniform2fARB(this.uDirLoc, dirX, dirY);
      ARBShaderObjects.glUniform1iARB(this.uSampleCountLoc, this.cachedSampleCount);

      this.offsetsBuffer.position(0);
      OpenGlHelper.glUniform1(this.uOffsetsLoc, this.offsetsBuffer);
      this.weightsBuffer.position(0);
      OpenGlHelper.glUniform1(this.uWeightsLoc, this.weightsBuffer);

      RenderUtils.applyBlendFunc();
      this.shaderProgram.drawFullscreenQuad(scaledResolution);
   }

   public void blur(String text, BlurFramebuffer blurFramebuffer2, ScaledResolution scaledResolution, int radius, float sigma) {
      if (radius < 1) {
         this.blurFramebuffer = this.framebufferManager.reuseOrAcquire(this.blurFramebuffer, text + " no blur fallback");
         this.blurFramebuffer.bindFramebuffer();
         blurFramebuffer2.bindTexture();
         RenderUtils.drawFullscreenQuad(scaledResolution);
         blurFramebuffer2.release();
         return;
      }

      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      BlurFramebuffer blurFramebuffer3 = this.framebufferManager.acquire(text + " horizontal blur");
      blurFramebuffer3.bindFramebuffer();
      blurFramebuffer2.bindTexture();
      this.blurPass(blurFramebuffer3.getFramebuffer(), scaledResolution, 0, radius, sigma, 1);
      blurFramebuffer2.release();
      this.blurFramebuffer = this.framebufferManager.reuseOrAcquire(this.blurFramebuffer, text + " vertical blur");
      this.blurFramebuffer.bindFramebuffer();
      blurFramebuffer3.bindTexture();
      this.blurPass(this.blurFramebuffer.getFramebuffer(), scaledResolution, 1, radius, sigma, 1);
      blurFramebuffer3.release();
   }

   public void blur(String text, Framebuffer framebuffer, ScaledResolution scaledResolution, int radius, float sigma, int step) {
      if (radius < 1) {
         this.blurFramebuffer = this.framebufferManager.reuseOrAcquire(this.blurFramebuffer, text + " no blur fallback");
         this.blurFramebuffer.bindFramebuffer();
         framebuffer.bindFramebufferTexture();
         RenderUtils.drawFullscreenQuad(scaledResolution);
         return;
      }

      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      BlurFramebuffer blurFramebuffer2 = this.framebufferManager.acquire(text + " horizontal blur");
      blurFramebuffer2.bindFramebuffer();
      framebuffer.bindFramebufferTexture();
      this.blurPass(blurFramebuffer2.getFramebuffer(), scaledResolution, 0, radius, sigma, step);
      this.blurFramebuffer = this.framebufferManager.reuseOrAcquire(this.blurFramebuffer, text + " vertical blur");
      this.blurFramebuffer.bindFramebuffer();
      blurFramebuffer2.bindTexture();
      this.blurPass(this.blurFramebuffer.getFramebuffer(), scaledResolution, 1, radius, sigma, step);
      blurFramebuffer2.release();
   }

   public void drawResult(ScaledResolution scaledResolution) {
      this.bindResultTexture();
      RenderUtils.drawFullscreenQuad(scaledResolution);
   }

   public void drawResultRegion(ScaledResolution scaledResolution, double d, double d2, double d3, double d4) {
      this.bindResultTexture();
      RenderUtils.drawFlippedTexturedQuad(scaledResolution, d, d2, d3, d4);
   }

   public void releaseResult() {
      if (this.blurFramebuffer != null) {
         this.blurFramebuffer.release();
      }

      this.blurFramebuffer = null;
   }

   public int bindResultTexture() {
      this.getFramebuffer().bindFramebufferTexture();
      return 0;
   }

   public boolean hasNoResult() {
      return this.blurFramebuffer == null;
   }
}
