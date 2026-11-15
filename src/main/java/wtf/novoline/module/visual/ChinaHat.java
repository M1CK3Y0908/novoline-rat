package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import wtf.novoline.event.RenderWorldEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.render.RenderUtils;

public class ChinaHat extends Module {
   private final ComboSetting chinaHat = settingManager.register(new ComboSetting(this, "Mode", 0, "Theme", "Colored", "Rainbow", "Astolfo", "Fade", "Gradient"));
   private final CheckboxSetting applyForAll = settingManager.register(new CheckboxSetting(this, "Apply For All", false));
   private final NumberSetting cycleTime = settingManager.register(new NumberSetting(this, "Cycle Time", 3000, 1, 20000) {
      @Override
      public boolean shouldShow() {
         String m = ChinaHat.this.chinaHat.getCurrentElementAsString();
         return m.equals("Rainbow") || m.equals("Astolfo") || m.equals("Fade") || m.equals("Gradient");
      }
   });
   private final ColorSetting upColor = settingManager.register(new ColorSetting(this, "Up Color", -1441155586) {
      @Override
      public boolean shouldShow() {
         return ChinaHat.this.chinaHat.getCurrentElementAsString().equals("Colored");
      }
   });
   private final ColorSetting downColor = settingManager.register(new ColorSetting(this, "Down Color", -1442809920) {
      @Override
      public boolean shouldShow() {
         return ChinaHat.this.chinaHat.getCurrentElementAsString().equals("Colored");
      }
   });
   private final ColorSetting fadeColor = settingManager.register(new ColorSetting(this, "Fade Color", -14039553) {
      @Override
      public boolean shouldShow() {
         return ChinaHat.this.chinaHat.getCurrentElementAsString().equals("Fade");
      }
   });
   private final ColorSetting gradientFirst = settingManager.register(new ColorSetting(this, "Gradient First", -14039553) {
      @Override
      public boolean shouldShow() {
         return ChinaHat.this.chinaHat.getCurrentElementAsString().equals("Gradient");
      }
   });
   private final ColorSetting gradientSecond = settingManager.register(new ColorSetting(this, "Gradient Second", -12469) {
      @Override
      public boolean shouldShow() {
         return ChinaHat.this.chinaHat.getCurrentElementAsString().equals("Gradient");
      }
   });
   private final CheckboxSetting hideInFirstPerson = settingManager.register(new CheckboxSetting(this, "Hide In First Person", true));
   @EventLink
   public final Listener<RenderWorldEvent> onRenderWorld = renderWorldEvent -> {
      for (EntityPlayer entityPlayer : minecraft.theWorld.playerEntities) {
         if (!entityPlayer.isInvisible()
            && (this.applyForAll.isEnabled() || entityPlayer == minecraft.thePlayer)
            && (!this.hideInFirstPerson.isEnabled() || entityPlayer != minecraft.thePlayer || minecraft.gameSettings.thirdPersonView != 0)) {
            String mode = this.chinaHat.getCurrentElementAsString();
            RenderManager renderManager = minecraft.getRenderManager();
            double d = entityPlayer.prevPosX + (entityPlayer.posX - entityPlayer.prevPosX) * renderWorldEvent.partialTicks;
            double d2 = entityPlayer.prevPosY + (entityPlayer.posY - entityPlayer.prevPosY) * renderWorldEvent.partialTicks;
            double d3 = entityPlayer.prevPosZ + (entityPlayer.posZ - entityPlayer.prevPosZ) * renderWorldEvent.partialTicks;
            d -= renderManager.viewerPosX;
            d2 -= renderManager.viewerPosY;
            d3 -= renderManager.viewerPosZ;
            d2 += entityPlayer.getEyeHeight() + 0.53;
            if (entityPlayer.isSneaking()) {
               d2 -= 0.3;
            }

            double d4 = 0.0;
            if (ModuleManager.customModel.isEnabled()
               && ModuleManager.customModel.appliesTo(entityPlayer)
               && ModuleManager.customModel.customModel.getCurrentElement() == 0) {
               d4 = 0.4F;
               if (entityPlayer.isSneaking()) {
                  d4 = 0.15F;
               }
            }

            d2 -= d4;
            double d5 = 90.0 / 32;
            float f = 0.7F;
            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            RenderUtils.applyBlendFunc();
            GlStateManager.shadeModel(7425);
            GlStateManager.alphaFunc(516, 0.003921569F);
            GL11.glBegin(6);
            int i = -1441155586;
            if (mode.equals("Theme")) {
               i = RenderUtils.withAlphaFraction(ModuleManager.themeColor.getColor(0), 0.8F);
            } else if (mode.equals("Colored")) {
               i = this.upColor.getColor();
            } else if (mode.equals("Fade")) {
               i = RenderUtils.pulseBrightness(this.fadeColor.getColor(), 0.4F, 1.0F, 1, 360, (long)this.cycleTime.getCurrentValue());
            } else if (mode.equals("Gradient")) {
               i = RenderUtils.pulseBetweenColors(
                  this.gradientFirst.getColor(), this.gradientSecond.getColor(), 1, 360, (long)this.cycleTime.getCurrentValue()
               );
            }

            RenderUtils.setColor(i);
            GL11.glVertex3d(d, d2, d3);

            for (int i2 = 0; i2 <= 128; i2++) {
               int color2 = this.downColor.getColor();
               if (mode.equals("Theme")) {
                  color2 = RenderUtils.withAlphaFraction(ModuleManager.themeColor.getColor(i2 * 2.8), 0.6F);
               } else if (mode.equals("Rainbow")) {
                  color2 = RenderUtils.withAlphaFraction(RenderUtils.indexedHsbColor(i2, 128, 0.6F, 1.0F, (long)this.cycleTime.getCurrentValue()), 0.6F);
               } else if (mode.equals("Astolfo")) {
                  color2 = RenderUtils.withAlphaFraction(
                     RenderUtils.indexedHsbColorMirrored(i2, 128, 0.6F, 1.0F, (long)this.cycleTime.getCurrentValue()), 0.6F
                  );
               } else if (mode.equals("Fade")) {
                  color2 = RenderUtils.pulseBrightness(this.fadeColor.getColor(), 0.4F, 1.0F, i2, 64, (long)this.cycleTime.getCurrentValue());
               } else if (mode.equals("Gradient")) {
                  color2 = RenderUtils.pulseBetweenColors(this.gradientFirst.getColor(), this.gradientSecond.getColor(), i2, 64, 1000L);
               }

               RenderUtils.setColor(color2);
               double d6 = i2 * d5 * Math.PI / 180.0;
               GL11.glVertex3d(d + Math.sin(d6) * f, d2 - 0.35, d3 + Math.cos(d6) * f);
            }

            GL11.glEnd();
            GL11.glLineWidth(2.0F);
            GL11.glBegin(3);

            for (int i3 = 0; i3 <= 360; i3++) {
               int i4 = 0;
               if (mode.equals("Theme")) {
                  i4 = RenderUtils.withAlphaFraction(ModuleManager.themeColor.getColor(i3), 0.9F);
               } else if (mode.equals("Rainbow")) {
                  i4 = RenderUtils.withAlphaFraction(RenderUtils.indexedHsbColor(i3, 360, 0.6F, 1.0F, (long)this.cycleTime.getCurrentValue()), 0.9F);
               } else if (mode.equals("Astolfo")) {
                  i4 = RenderUtils.withAlphaFraction(
                     RenderUtils.indexedHsbColorMirrored(i3, 360, 0.6F, 1.0F, (long)this.cycleTime.getCurrentValue()), 0.9F
                  );
               } else if (mode.equals("Fade")) {
                  i4 = RenderUtils.pulseBrightness(this.fadeColor.getColor(), 0.4F, 1.0F, i3, 360, (long)this.cycleTime.getCurrentValue());
               } else if (mode.equals("Gradient")) {
                  i4 = RenderUtils.pulseBetweenColors(
                     this.gradientFirst.getColor(), this.gradientSecond.getColor(), i3, 360, (long)this.cycleTime.getCurrentValue()
                  );
               }

               RenderUtils.setColor(i4);
               GL11.glVertex3d(d + Math.sin(i3 * Math.PI / 180.0) * f, d2 - 0.35, d3 + Math.cos(i3 * Math.PI / 180.0) * f);
            }

            GL11.glEnd();
            RenderUtils.resetColor();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.shadeModel(7424);
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
         }
      }
   };

   public ChinaHat() {
      super(Category.VISUAL, "ChinaHat", "");
   }
}