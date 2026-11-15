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
import org.lwjgl.opengl.GL11;

public class ShadowRenderer {
   private final List<Runnable> pendingDraws = new ArrayList<>();
   private final ShaderProgram shaderProgram = new ShaderProgram(new ResourceLocation("novoline", "shaders/shadow.fsh"));
   private final FramebufferManager framebufferManager;

   private Framebuffer targetFbo;
   private Framebuffer hBlurFbo;
   private Framebuffer vBlurFbo;
   private int lastTargetW = -1;
   private int lastTargetH = -1;
   private int lastDownW = -1;
   private int lastDownH = -1;

   private float boundsMinX = Float.POSITIVE_INFINITY;
   private float boundsMinY = Float.POSITIVE_INFINITY;
   private float boundsMaxX = Float.NEGATIVE_INFINITY;
   private float boundsMaxY = Float.NEGATIVE_INFINITY;
   private boolean hasCustomBounds = false;

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

   public ShadowRenderer(FramebufferManager framebufferManager2) {
      this.framebufferManager = framebufferManager2;
   }

   public void submit(float minX, float minY, float maxX, float maxY, Runnable runnable) {
      this.boundsMinX = Math.min(this.boundsMinX, Math.min(minX, maxX));
      this.boundsMinY = Math.min(this.boundsMinY, Math.min(minY, maxY));
      this.boundsMaxX = Math.max(this.boundsMaxX, Math.max(minX, maxX));
      this.boundsMaxY = Math.max(this.boundsMaxY, Math.max(minY, maxY));
      this.hasCustomBounds = true;
      this.pendingDraws.add(runnable);
   }

   public void submit(Runnable runnable) {
      this.pendingDraws.add(runnable);
   }

   private void checkFramebuffers(int fullW, int fullH, int downW, int downH) {
      if (this.targetFbo == null || this.lastTargetW != fullW || this.lastTargetH != fullH) {
         if (this.targetFbo != null) {
            this.targetFbo.deleteFramebuffer();
         }
         this.targetFbo = new Framebuffer(fullW, fullH, false);
         this.targetFbo.setFramebufferFilter(9729);
         this.lastTargetW = fullW;
         this.lastTargetH = fullH;
      }
      if (this.hBlurFbo == null || this.vBlurFbo == null || this.lastDownW != downW || this.lastDownH != downH) {
         if (this.hBlurFbo != null) {
            this.hBlurFbo.deleteFramebuffer();
         }
         if (this.vBlurFbo != null) {
            this.vBlurFbo.deleteFramebuffer();
         }
         this.hBlurFbo = new Framebuffer(downW, downH, false);
         this.hBlurFbo.setFramebufferFilter(9729);
         this.vBlurFbo = new Framebuffer(downW, downH, false);
         this.vBlurFbo.setFramebufferFilter(9729);
         this.lastDownW = downW;
         this.lastDownH = downH;
      }
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

      float dirX = flag ? (1.0F / (float) this.hBlurFbo.framebufferWidth) : 0.0F;
      float dirY = flag ? 0.0F : (1.0F / (float) this.vBlurFbo.framebufferHeight);

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
      if (this.pendingDraws.isEmpty()) {
         return;
      }

      Minecraft mc = Minecraft.getMinecraft();
      int fullW = mc.displayWidth;
      int fullH = mc.displayHeight;
      int downW = Math.max(1, fullW / 2);
      int downH = Math.max(1, fullH / 2);
      this.checkFramebuffers(fullW, fullH, downW, downH);

      boolean useScissor = this.hasCustomBounds && (this.boundsMaxX > this.boundsMinX && this.boundsMaxY > this.boundsMinY);
      int scX = 0, scY = 0, scW = 0, scH = 0;
      int dScX = 0, dScY = 0, dScW = 0, dScH = 0;

      if (useScissor) {
         int sf = scaledResolution.getScaleFactor();
         float pad = f + 10.0F;
         float sMinX = (this.boundsMinX - pad) * sf;
         float sMaxX = (this.boundsMaxX + pad) * sf;
         float sMinY = (this.boundsMinY - pad) * sf;
         float sMaxY = (this.boundsMaxY + pad) * sf;

         scX = (int) Math.max(0, Math.floor(sMinX));
         scY = (int) Math.max(0, Math.floor(fullH - sMaxY));
         scW = (int) Math.min(fullW - scX, Math.ceil(sMaxX - sMinX));
         scH = (int) Math.min(fullH - scY, Math.ceil(sMaxY - sMinY));

         dScX = Math.max(0, scX / 2);
         dScY = Math.max(0, scY / 2);
         dScW = Math.min(downW - dScX, (int) Math.ceil(scW / 2.0));
         dScH = Math.min(downH - dScY, (int) Math.ceil(scH / 2.0));

         if (scW <= 0 || scH <= 0 || dScW <= 0 || dScH <= 0) {
            useScissor = false;
         }
      }

      // Step 1: Draw shapes into targetFbo
      this.targetFbo.bindFramebuffer(true);
      if (useScissor) {
         GL11.glEnable(GL11.GL_SCISSOR_TEST);
         GL11.glScissor(scX, scY, scW, scH);
      }
      this.targetFbo.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.targetFbo.framebufferClear();

      GlStateManager.alphaFunc(516, 0.001F);
      GlStateManager.enableBlend();
      RenderUtils.applyBlendFunc();

      for (Runnable runnable : this.pendingDraws) {
         runnable.run();
      }
      this.pendingDraws.clear();

      // Step 2: Horizontal blur into hBlurFbo (half-res)
      float downRadius = Math.max(1.0F, f * 0.5F);
      float downSigma = Math.max(0.1F, f2 * 0.5F);
      float[] fArray = RenderUtils.unpackColorNormalized(i);

      this.hBlurFbo.bindFramebuffer(true);
      if (useScissor) {
         GL11.glScissor(dScX, dScY, dScW, dScH);
      }
      this.hBlurFbo.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.hBlurFbo.framebufferClear();
      this.targetFbo.bindFramebufferTexture();
      this.blurPass(scaledResolution, true, fArray, downRadius, downSigma, f3);

      // Step 3: Vertical blur into vBlurFbo (half-res)
      this.vBlurFbo.bindFramebuffer(true);
      if (useScissor) {
         GL11.glScissor(dScX, dScY, dScW, dScH);
      }
      this.vBlurFbo.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.vBlurFbo.framebufferClear();
      this.hBlurFbo.bindFramebufferTexture();
      this.blurPass(scaledResolution, false, fArray, downRadius, downSigma, f3);

      // Step 4: Draw shadow to destination framebuffer
      framebuffer.bindFramebuffer(true);
      if (useScissor) {
         GL11.glScissor(scX, scY, scW, scH);
      }
      RenderUtils.resetColor();
      GlStateManager.enableBlend();
      RenderUtils.applyBlendFunc();
      this.vBlurFbo.bindFramebufferTexture();
      RenderUtils.drawFullscreenQuad(scaledResolution);

      if (flag) {
         this.targetFbo.bindFramebufferTexture();
         RenderUtils.drawFullscreenQuad(scaledResolution);
      }

      if (useScissor) {
         GL11.glDisable(GL11.GL_SCISSOR_TEST);
      }

      this.hasCustomBounds = false;
      this.boundsMinX = Float.POSITIVE_INFINITY;
      this.boundsMinY = Float.POSITIVE_INFINITY;
      this.boundsMaxX = Float.NEGATIVE_INFINITY;
      this.boundsMaxY = Float.NEGATIVE_INFINITY;
      GlStateManager.alphaFunc(516, 0.1F);
   }
}
