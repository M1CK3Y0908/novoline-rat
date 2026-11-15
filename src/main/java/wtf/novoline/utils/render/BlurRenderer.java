package wtf.novoline.utils.render;

import java.nio.FloatBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;

public class BlurRenderer {
   private final ShaderProgram shaderProgram = new ShaderProgram(new ResourceLocation("novoline", "shaders/blur.fsh"));
   private final FramebufferManager framebufferManager;

   private Framebuffer fboH;
   private Framebuffer fboV;
   private int lastTargetWidth = -1;
   private int lastTargetHeight = -1;

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

   public BlurRenderer(FramebufferManager framebufferManager2) {
      this.framebufferManager = framebufferManager2;
   }

   public Framebuffer getFramebuffer() {
      return this.fboV;
   }

   private void checkFramebuffers(int width, int height) {
      if (this.fboH == null || this.fboV == null || this.lastTargetWidth != width || this.lastTargetHeight != height) {
         if (this.fboH != null) {
            this.fboH.deleteFramebuffer();
         }
         if (this.fboV != null) {
            this.fboV.deleteFramebuffer();
         }
         this.fboH = new Framebuffer(width, height, false);
         this.fboH.setFramebufferFilter(9729);
         this.fboV = new Framebuffer(width, height, false);
         this.fboV.setFramebufferFilter(9729);
         this.lastTargetWidth = width;
         this.lastTargetHeight = height;
      }
   }

   private void updateKernel(int radius, float sigma, int step) {
      if (radius == this.lastRadius && sigma == this.lastSigma && step == this.lastStep) {
         return;
      }
      this.lastRadius = radius;
      this.lastSigma = sigma;
      this.lastStep = step;

      int r = Math.max(1, Math.min(radius, 30));
      int s = Math.max(1, step);
      float effSigma = Math.max(0.001F, sigma * 0.5F);
      float twoEffSigma = 2.0F * effSigma;
      float gaussNorm = (float)(1.0 / (2.0 * 3.14157 * effSigma));

      float[] dWeights = new float[32];
      int[] dIndices = new int[32];
      dWeights[0] = 1.0F;
      dIndices[0] = 0;
      float totalWeight = dWeights[0];

      int idx = 1;
      for (int i = 1; i <= r && idx < 32; i += s) {
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
      if (blurFramebuffer2 != null) {
         this.blur(text, blurFramebuffer2.getFramebuffer(), scaledResolution, radius, sigma, 1);
         blurFramebuffer2.release();
      }
   }

   public void blur(String text, Framebuffer framebuffer, ScaledResolution scaledResolution, int radius, float sigma, int step) {
      this.blur(text, framebuffer, scaledResolution, radius, sigma, step, -1.0F, -1.0F, -1.0F, -1.0F);
   }

   public void blur(
      String text,
      Framebuffer framebuffer,
      ScaledResolution scaledResolution,
      int radius,
      float sigma,
      int step,
      float minX,
      float minY,
      float maxX,
      float maxY
   ) {
      Minecraft mc = Minecraft.getMinecraft();
      int downW = Math.max(1, mc.displayWidth / 2);
      int downH = Math.max(1, mc.displayHeight / 2);
      this.checkFramebuffers(downW, downH);

      if (radius < 1) {
         this.fboV.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
         this.fboV.framebufferClear();
         this.fboV.bindFramebuffer(true);
         framebuffer.bindFramebufferTexture();
         RenderUtils.drawFullscreenQuad(scaledResolution);
         return;
      }

      int downRadius = Math.max(1, (int) Math.round(radius / 2.0));
      float downSigma = Math.max(0.05F, sigma * 0.5F);
      int downStep = Math.max(1, step);

      boolean useScissor = minX >= 0.0F && minY >= 0.0F && maxX > minX && maxY > minY;
      int scX = 0, scY = 0, scW = 0, scH = 0;
      if (useScissor) {
         int sf = scaledResolution.getScaleFactor();
         float pad = (float) (radius + 6);
         float sMinX = (minX - pad) * sf * 0.5F;
         float sMaxX = (maxX + pad) * sf * 0.5F;
         float sMinY = (minY - pad) * sf * 0.5F;
         float sMaxY = (maxY + pad) * sf * 0.5F;

         scX = (int) Math.max(0, Math.floor(sMinX));
         scY = (int) Math.max(0, Math.floor(downH - sMaxY));
         scW = (int) Math.min(downW - scX, Math.ceil(sMaxX - sMinX));
         scH = (int) Math.min(downH - scY, Math.ceil(sMaxY - sMinY));

         if (scW <= 0 || scH <= 0) {
            useScissor = false;
         }
      }

      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

      // Pass 1: Horizontal blur from source into fboH
      this.fboH.bindFramebuffer(true);
      if (useScissor) {
         GL11.glEnable(GL11.GL_SCISSOR_TEST);
         GL11.glScissor(scX, scY, scW, scH);
      }
      this.fboH.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.fboH.framebufferClear();
      framebuffer.bindFramebufferTexture();
      this.blurPass(this.fboH, scaledResolution, 0, downRadius, downSigma, downStep);

      // Pass 2: Vertical blur from fboH into fboV
      this.fboV.bindFramebuffer(true);
      if (useScissor) {
         GL11.glScissor(scX, scY, scW, scH);
      }
      this.fboV.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.fboV.framebufferClear();
      this.fboH.bindFramebufferTexture();
      this.blurPass(this.fboV, scaledResolution, 1, downRadius, downSigma, downStep);

      if (useScissor) {
         GL11.glDisable(GL11.GL_SCISSOR_TEST);
      }
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
   }

   public int bindResultTexture() {
      if (this.fboV != null) {
         this.fboV.bindFramebufferTexture();
      }
      return 0;
   }

   public boolean hasNoResult() {
      return this.fboV == null;
   }
}
