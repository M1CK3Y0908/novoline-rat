package net.gobbob.mobends.client.renderer.entity;

import net.gobbob.mobends.client.model.ModelRendererBends;
import net.gobbob.mobends.client.model.entity.ModelBendsPlayer;
import net.gobbob.mobends.client.renderer.entity.layers.LayerBendsCape;
import net.gobbob.mobends.client.renderer.entity.layers.LayerBendsCustomHead;
import net.gobbob.mobends.client.renderer.entity.layers.LayerBendsPlayerArmor;
import net.gobbob.mobends.data.Data_Player;
import net.gobbob.mobends.settings.SettingsBoolean;
import net.gobbob.mobends.settings.SettingsNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderBendsPlayer extends RenderPlayer {
    private final boolean smallArms;

    public RenderBendsPlayer(RenderManager renderManager) {
        this(renderManager, false);
    }

    public RenderBendsPlayer(RenderManager renderManager, boolean useSmallArms) {
        super(renderManager, useSmallArms);
        this.smallArms = useSmallArms;
        this.mainModel = new ModelBendsPlayer(0.0f, useSmallArms);
        this.layerRenderers.clear();
        this.addLayer(new LayerBendsPlayerArmor(this));
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerBendsCape(this));
        this.addLayer(new LayerBendsCustomHead((ModelBendsPlayer)this.getMainModel()));
    }

    @Override
    public ModelPlayer getMainModel() {
        if (!(this.mainModel instanceof ModelBendsPlayer)) {
            this.mainModel = new ModelBendsPlayer(0.0f, this.smallArms);
        }
        return (ModelBendsPlayer)this.mainModel;
    }

    @Override
    protected void rotateCorpse(AbstractClientPlayer bat, float f, float f2, float partialTicks) {
        super.rotateCorpse(bat, f, f2, partialTicks);
    }

    private void setModelVisibilities(AbstractClientPlayer p_177137_1_) {
        ModelBendsPlayer modelplayer = (ModelBendsPlayer)this.getMainModel();
        if (p_177137_1_.isSpectator()) {
            modelplayer.setInvisible(false);
            modelplayer.bipedHead.showModel = true;
            modelplayer.bipedHeadwear.showModel = true;
        } else {
            ItemStack itemstack = p_177137_1_.inventory.getCurrentItem();
            modelplayer.setInvisible(true);
            modelplayer.bipedHeadwear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.HAT);
            modelplayer.bipedBodyWear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.JACKET);
            modelplayer.bipedLeftLegwear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
            modelplayer.bipedRightLegwear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
            modelplayer.bipedLeftArmwear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
            modelplayer.bipedRightArmwear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
            modelplayer.bipedLeftForeArmwear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
            modelplayer.bipedRightForeArmwear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
            modelplayer.bipedLeftForeLegwear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
            modelplayer.bipedRightForeLegwear.showModel = p_177137_1_.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
            modelplayer.heldItemLeft = 0;
            modelplayer.aimedBow = false;
            modelplayer.isSneak = p_177137_1_.isSneaking();
            if (itemstack == null) {
                modelplayer.heldItemRight = 0;
            } else {
                modelplayer.heldItemRight = 1;
                if (p_177137_1_.getItemInUseCount() > 0) {
                    EnumAction enumaction = itemstack.getItemUseAction();
                    if (enumaction == EnumAction.BLOCK) {
                        modelplayer.heldItemRight = 3;
                    } else if (enumaction == EnumAction.BOW) {
                        modelplayer.aimedBow = true;
                    }
                }
            }
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(AbstractClientPlayer p_180594_1_) {
        return p_180594_1_.getLocationSkin();
    }

    @Override
    public void transformHeldFull3DItemLayer() {
        GlStateManager.translate(0.0f, 0.1875f, 0.0f);
    }

    @Override
    protected void preRenderCallback(AbstractClientPlayer p_77041_1_, float p_77041_2_) {
        float f1 = 0.9375f;
        GlStateManager.scale(f1, f1, f1);
        ((ModelBendsPlayer)this.mainModel).updateWithEntityData(p_77041_1_);
        ((ModelBendsPlayer)this.mainModel).postRenderTranslate(0.0625f);
        Data_Player data = Data_Player.get(p_77041_1_.getEntityId());
        if (((SettingsBoolean)SettingsNode.getSetting("swordTrail")).data) {
            GL11.glPushMatrix();
            float f5 = 0.0625f;
            GL11.glScalef(-f5, -f5, f5);
            data.swordTrail.render((ModelBendsPlayer)this.mainModel);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
        ((ModelBendsPlayer)this.mainModel).postRenderRotate(0.0625f);
    }

    @Override
    public void renderRightArm(AbstractClientPlayer p_177138_1_) {
        float f = 1.0f;
        GlStateManager.color(f, f, f);
        ModelBendsPlayer modelplayer = (ModelBendsPlayer)this.getMainModel();
        this.setModelVisibilities(p_177138_1_);
        modelplayer.swingProgress = 0.0f;
        modelplayer.isSneak = false;

        ModelRendererBends rightArm = (ModelRendererBends)modelplayer.bipedRightArm;

        float origY = rightArm.rotationPointY;
        float origX = rightArm.rotationPointX;
        float origZ = rightArm.rotationPointZ;
        float origRotX = rightArm.rotateAngleX;
        float origRotY = rightArm.rotateAngleY;
        float origRotZ = rightArm.rotateAngleZ;
        float origPreX = rightArm.pre_rotation.getX();
        float origPreY = rightArm.pre_rotation.getY();
        float origPreZ = rightArm.pre_rotation.getZ();

        float origForeY = modelplayer.bipedRightForeArm.rotationPointY;
        float origForeX = modelplayer.bipedRightForeArm.rotationPointX;
        float origForeZ = modelplayer.bipedRightForeArm.rotationPointZ;
        float origForeRotX = modelplayer.bipedRightForeArm.rotateAngleX;
        float origForeRotY = modelplayer.bipedRightForeArm.rotateAngleY;
        float origForeRotZ = modelplayer.bipedRightForeArm.rotateAngleZ;
        float origForePreX = modelplayer.bipedRightForeArm.pre_rotation.getX();
        float origForePreY = modelplayer.bipedRightForeArm.pre_rotation.getY();
        float origForePreZ = modelplayer.bipedRightForeArm.pre_rotation.getZ();

        rightArm.rotationPointX = -5.0f;
        rightArm.rotationPointY = 2.0f;
        rightArm.rotationPointZ = 0.0f;
        rightArm.rotateAngleX = 0.0f;
        rightArm.rotateAngleY = 0.0f;
        rightArm.rotateAngleZ = 0.0f;
        rightArm.pre_rotation.set(0.0f, 0.0f, 0.0f);

        float partialTicks = Minecraft.getMinecraft().timer != null ? Minecraft.getMinecraft().timer.renderPartialTicks : 1.0f;
        float swing = p_177138_1_.getSwingProgress(partialTicks);
        float swingSin = MathHelper.sin(MathHelper.sqrt_float(swing) * (float)Math.PI);

        modelplayer.bipedRightForeArm.rotationPointX = 0.0f;
        modelplayer.bipedRightForeArm.rotationPointY = 4.0f;
        modelplayer.bipedRightForeArm.rotationPointZ = 2.0f;
        modelplayer.bipedRightForeArm.rotateAngleX = (float)Math.toRadians(-12.0f - swingSin * 35.0f);
        modelplayer.bipedRightForeArm.rotateAngleY = (float)Math.toRadians(-swingSin * 8.0f);
        modelplayer.bipedRightForeArm.rotateAngleZ = (float)Math.toRadians(swingSin * 4.0f);
        modelplayer.bipedRightForeArm.pre_rotation.set(0.0f, 0.0f, 0.0f);

        GlStateManager.pushMatrix();
        modelplayer.renderRightArm();
        GlStateManager.popMatrix();

        rightArm.rotationPointX = origX;
        rightArm.rotationPointY = origY;
        rightArm.rotationPointZ = origZ;
        rightArm.rotateAngleX = origRotX;
        rightArm.rotateAngleY = origRotY;
        rightArm.rotateAngleZ = origRotZ;
        rightArm.pre_rotation.set(origPreX, origPreY, origPreZ);

        modelplayer.bipedRightForeArm.rotationPointX = origForeX;
        modelplayer.bipedRightForeArm.rotationPointY = origForeY;
        modelplayer.bipedRightForeArm.rotationPointZ = origForeZ;
        modelplayer.bipedRightForeArm.rotateAngleX = origForeRotX;
        modelplayer.bipedRightForeArm.rotateAngleY = origForeRotY;
        modelplayer.bipedRightForeArm.rotateAngleZ = origForeRotZ;
        modelplayer.bipedRightForeArm.pre_rotation.set(origForePreX, origForePreY, origForePreZ);
    }

    @Override
    public void renderLeftArm(AbstractClientPlayer p_177139_1_) {
        float f = 1.0f;
        GlStateManager.color(f, f, f);
        ModelBendsPlayer modelplayer = (ModelBendsPlayer)this.getMainModel();
        this.setModelVisibilities(p_177139_1_);
        modelplayer.isSneak = false;
        modelplayer.swingProgress = 0.0f;

        ModelRendererBends leftArm = (ModelRendererBends)modelplayer.bipedLeftArm;

        float origY = leftArm.rotationPointY;
        float origX = leftArm.rotationPointX;
        float origZ = leftArm.rotationPointZ;
        float origRotX = leftArm.rotateAngleX;
        float origRotY = leftArm.rotateAngleY;
        float origRotZ = leftArm.rotateAngleZ;
        float origPreX = leftArm.pre_rotation.getX();
        float origPreY = leftArm.pre_rotation.getY();
        float origPreZ = leftArm.pre_rotation.getZ();

        float origForeY = modelplayer.bipedLeftForeArm.rotationPointY;
        float origForeX = modelplayer.bipedLeftForeArm.rotationPointX;
        float origForeZ = modelplayer.bipedLeftForeArm.rotationPointZ;
        float origForeRotX = modelplayer.bipedLeftForeArm.rotateAngleX;
        float origForeRotY = modelplayer.bipedLeftForeArm.rotateAngleY;
        float origForeRotZ = modelplayer.bipedLeftForeArm.rotateAngleZ;
        float origForePreX = modelplayer.bipedLeftForeArm.pre_rotation.getX();
        float origForePreY = modelplayer.bipedLeftForeArm.pre_rotation.getY();
        float origForePreZ = modelplayer.bipedLeftForeArm.pre_rotation.getZ();

        leftArm.rotationPointX = 5.0f;
        leftArm.rotationPointY = 2.0f;
        leftArm.rotationPointZ = 0.0f;
        leftArm.rotateAngleX = 0.0f;
        leftArm.rotateAngleY = 0.0f;
        leftArm.rotateAngleZ = 0.0f;
        leftArm.pre_rotation.set(0.0f, 0.0f, 0.0f);

        modelplayer.bipedLeftForeArm.rotationPointX = 0.0f;
        modelplayer.bipedLeftForeArm.rotationPointY = 4.0f;
        modelplayer.bipedLeftForeArm.rotationPointZ = 2.0f;
        modelplayer.bipedLeftForeArm.rotateAngleX = (float)Math.toRadians(-12.0f);
        modelplayer.bipedLeftForeArm.rotateAngleY = 0.0f;
        modelplayer.bipedLeftForeArm.rotateAngleZ = 0.0f;
        modelplayer.bipedLeftForeArm.pre_rotation.set(0.0f, 0.0f, 0.0f);

        GlStateManager.pushMatrix();
        modelplayer.renderLeftArm();
        GlStateManager.popMatrix();

        leftArm.rotationPointX = origX;
        leftArm.rotationPointY = origY;
        leftArm.rotationPointZ = origZ;
        leftArm.rotateAngleX = origRotX;
        leftArm.rotateAngleY = origRotY;
        leftArm.rotateAngleZ = origRotZ;
        leftArm.pre_rotation.set(origPreX, origPreY, origPreZ);

        modelplayer.bipedLeftForeArm.rotationPointX = origForeX;
        modelplayer.bipedLeftForeArm.rotationPointY = origForeY;
        modelplayer.bipedLeftForeArm.rotationPointZ = origForeZ;
        modelplayer.bipedLeftForeArm.rotateAngleX = origForeRotX;
        modelplayer.bipedLeftForeArm.rotateAngleY = origForeRotY;
        modelplayer.bipedLeftForeArm.rotateAngleZ = origForeRotZ;
        modelplayer.bipedLeftForeArm.pre_rotation.set(origForePreX, origForePreY, origForePreZ);
    }

    @Override
    protected void renderLivingAt(AbstractClientPlayer p_77039_1_, double p_77039_2_, double p_77039_4_, double p_77039_6_) {
        super.renderLivingAt(p_77039_1_, p_77039_2_, p_77039_4_, p_77039_6_);
    }

    @Override
    public void doRender2(AbstractClientPlayer entity, double x, double y, double z, float p_76986_8_, float partialTicks) {
        super.doRender2(entity, x, y, z, p_76986_8_, partialTicks);
    }
}
