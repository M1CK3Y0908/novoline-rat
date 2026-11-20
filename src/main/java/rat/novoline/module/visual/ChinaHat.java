package rat.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import rat.novoline.event.RenderWorldEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.utils.render.RenderUtils;

public class ChinaHat extends Module {
   private final CheckboxSetting applyForAll = settingManager.register(new CheckboxSetting(this, "Apply For All", false));
   private final CheckboxSetting hideInFirstPerson = settingManager.register(new CheckboxSetting(this, "Hide In First Person", true));
   @EventLink
   public final Listener<RenderWorldEvent> onRenderWorld = renderWorldEvent -> {
      for (EntityPlayer entityPlayer : minecraft.theWorld.playerEntities) {
         if (!entityPlayer.isInvisible()
            && (this.applyForAll.isEnabled() || entityPlayer == minecraft.thePlayer)
            && (!this.hideInFirstPerson.isEnabled() || entityPlayer != minecraft.thePlayer || minecraft.gameSettings.thirdPersonView != 0)) {
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
            int i = RenderUtils.withAlphaFraction(ModuleManager.themeColor.getColor(0), 0.8F);
            RenderUtils.setColor(i);
            GL11.glVertex3d(d, d2, d3);

            for (int i2 = 0; i2 <= 128; i2++) {
               int color2 = RenderUtils.withAlphaFraction(ModuleManager.themeColor.getColor(i2 * 2.8), 0.6F);
               RenderUtils.setColor(color2);
               double d6 = i2 * d5 * Math.PI / 180.0;
               GL11.glVertex3d(d + Math.sin(d6) * f, d2 - 0.35, d3 + Math.cos(d6) * f);
            }

            GL11.glEnd();
            GL11.glLineWidth(2.0F);
            GL11.glBegin(3);

            for (int i3 = 0; i3 <= 360; i3++) {
               int i4 = RenderUtils.withAlphaFraction(ModuleManager.themeColor.getColor(i3), 0.9F);
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
