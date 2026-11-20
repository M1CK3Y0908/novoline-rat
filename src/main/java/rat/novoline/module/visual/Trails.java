package rat.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import rat.novoline.event.RenderWorldEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.RenderUtils;

public class Trails extends Module {
   private final CheckboxSetting applyForAll = settingManager.register(new CheckboxSetting(this, "Apply For All", true));
   private final CheckboxSetting hideInFirstPerson = settingManager.register(new CheckboxSetting(this, "Hide In First Person", false));
   private final NumberSetting maxDistance = settingManager.register(new NumberSetting(this, "Max Distance", 3, 1, 15));
   private static final ResourceLocation SHIELD_BASE_TEXTURE = new ResourceLocation("novoline", "images/trail.png");
   @EventLink
   public final Listener<RenderWorldEvent> onRenderWorld = renderWorldEvent -> {
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

      for (EntityPlayer entityPlayer : minecraft.theWorld.playerEntities) {
         double d = 0.0;
         if (ModuleManager.customModel.isEnabled() && ModuleManager.customModel.appliesTo(entityPlayer)) {
            int currentElement2 = ModuleManager.customModel.customModel.getCurrentElement();
            if (currentElement2 == 0) {
               d = 0.4F;
            }
         }

         if ((this.applyForAll.isEnabled() || entityPlayer == minecraft.thePlayer)
            && !entityPlayer.isInvisible()
            && (entityPlayer != minecraft.thePlayer || minecraft.gameSettings.thirdPersonView != 0 || !this.hideInFirstPerson.isEnabled())) {
            float partialTicks2 = renderWorldEvent.partialTicks;
            double d2 = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * partialTicks2;
            double d3 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * partialTicks2;
            double d4 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * partialTicks2;
            Vec3 vec3 = new Vec3(d2, d3, d4);
            double d5 = entityPlayer.getEntityBoundingBox().maxY - entityPlayer.getEntityBoundingBox().minY - d;
            double currentValue = this.maxDistance.getCurrentValue();
            float f = 0.0F;
            int i = 0;
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.translate(-renderManager.viewerPosX, -renderManager.viewerPosY, -renderManager.viewerPosZ);
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.alphaFunc(516, 0.003921569F);
            GlStateManager.shadeModel(7425);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
            float f2 = 0.703125F;
            minecraft.getTextureManager().bindTexture(SHIELD_BASE_TEXTURE);
            GL11.glBegin(7);

            RenderUtils.setColor(RenderUtils.withAlpha(ModuleManager.themeColor.getColor(), 0));
            GL11.glTexCoord2f(f, 0.703125F);
            GL11.glVertex3d(d2, d3, d4);
            GL11.glTexCoord2f(f, 0.0F);
            GL11.glVertex3d(d2, d3 + d5, d4);
            double d6 = 0.0;
            double d7 = 0.0;

            for (Vec3 vec32 : entityPlayer.arrayDeque) {
               if (++i == entityPlayer.arrayDeque.size()) {
                  Vec3 vec33 = vec32.subtract(vec3);
                  vec33 = new Vec3(vec33.xCoord * partialTicks2, vec33.yCoord * partialTicks2, vec33.zCoord * partialTicks2);
                  vec32 = vec32.subtract(vec33);
               }

               double d8 = vec32.distanceTo(vec3);
               if (d7 + d8 > currentValue) {
                  Vec3 vec34 = vec32.subtract(vec3).normalize();
                  double d9 = currentValue - d7;
                  vec34 = new Vec3(vec34.xCoord * d9, vec34.yCoord * d9, vec34.zCoord * d9);
                  d7 = currentValue;
                  vec32 = vec3.add(vec34);
               } else {
                  d7 += d8;
                  if (entityPlayer != minecraft.thePlayer || minecraft.gameSettings.thirdPersonView != 0) {
                     d6 += d8;
                  } else if (i > 2) {
                     d6 += d8;
                  }
               }

               float f3 = (float)(d7 / currentValue) + f;

               RenderUtils.setColor(
                  RenderUtils.withAlphaFraction(
                     ModuleManager.themeColor.getColor((long)(i * 20L)),
                     (float)(d6 / 0.3)
                  )
               );
               GL11.glTexCoord2f(f3, f2);
               GL11.glVertex3d(vec32.xCoord, vec32.yCoord + d5, vec32.zCoord);
               GL11.glTexCoord2f(f3, 0.0F);
               GL11.glVertex3d(vec32.xCoord, vec32.yCoord, vec32.zCoord);
               if (i != entityPlayer.arrayDeque.size()) {
                  GL11.glTexCoord2f(f3, 0.0F);
                  GL11.glVertex3d(vec32.xCoord, vec32.yCoord, vec32.zCoord);
                  GL11.glTexCoord2f(f3, f2);
                  GL11.glVertex3d(vec32.xCoord, vec32.yCoord + d5, vec32.zCoord);
               }

               vec3 = vec32;
               if (d7 >= currentValue) {
                  break;
               }
            }

            GL11.glEnd();
            RenderUtils.resetColor();
            GlStateManager.shadeModel(7424);
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
         }
      }
   };

   public Trails() {
      super(Category.VISUAL, "Trails", "");
   }
}
