/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.AbstractClientPlayer
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelPlayer
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemAxe
 *  net.minecraft.item.ItemPickaxe
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.util.vector.Vector3f
 */
package net.gobbob.mobends.client.model.entity;

import net.gobbob.mobends.AnimatedEntity;
import net.gobbob.mobends.client.model.ModelRendererBends;
import net.gobbob.mobends.client.model.ModelRendererBends_SeperatedChild;
import net.gobbob.mobends.client.renderer.SwordTrail;
import net.gobbob.mobends.data.Data_Player;
import net.gobbob.mobends.pack.BendsPack;
import net.gobbob.mobends.pack.BendsVar;
import net.gobbob.mobends.util.SmoothVector3f;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class ModelBendsPlayer
extends ModelPlayer {
    public ModelRendererBends bipedRightForeArm;
    public ModelRendererBends bipedLeftForeArm;
    public ModelRendererBends bipedRightForeLeg;
    public ModelRendererBends bipedLeftForeLeg;
    public ModelRendererBends bipedRightForeArmwear;
    public ModelRendererBends bipedLeftForeArmwear;
    public ModelRendererBends bipedRightForeLegwear;
    public ModelRendererBends bipedLeftForeLegwear;
    public ModelRendererBends bipedCloak;
    public ModelRendererBends bipedEars;
    public boolean smallArms;
    public SmoothVector3f renderOffset = new SmoothVector3f();
    public SmoothVector3f renderRotation = new SmoothVector3f();
    public SmoothVector3f renderItemRotation = new SmoothVector3f();
    public SwordTrail swordTrail = new SwordTrail();
    public float headRotationX;
    public float headRotationY;
    public float armSwing;
    public float armSwingAmount;

    public ModelBendsPlayer(float p_i46304_1_, boolean p_i46304_2) {
        this(p_i46304_1_, p_i46304_2, true);
    }

    public ModelBendsPlayer(float p_i46304_1_, boolean p_i46304_2_, boolean bigTexture) {
        super(p_i46304_1_, p_i46304_2_);
        this.textureWidth = 64;
        this.textureHeight = bigTexture ? 64 : 32;
        this.smallArms = p_i46304_2_;
        this.bipedEars = new ModelRendererBends((ModelBase)this, 24, 0);
        this.bipedEars.addBox(-3.0f, -6.0f, -1.0f, 6, 6, 1, p_i46304_1_);
        this.bipedCloak = new ModelRendererBends((ModelBase)this, 0, 0);
        this.bipedCloak.setTextureSize(64, 32);
        this.bipedCloak.addBox(-5.0f, 0.0f, -1.0f, 10, 16, 1, p_i46304_1_);
        this.bipedHeadwear = new ModelRendererBends((ModelBase)this, 32, 0);
        this.bipedHeadwear.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, p_i46304_1_ + 0.5f);
        this.bipedHeadwear.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.bipedBody = new ModelRendererBends((ModelBase)this, 16, 16);
        this.bipedBody.addBox(-4.0f, -12.0f, -2.0f, 8, 12, 4, p_i46304_1_);
        this.bipedBody.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.bipedHead = new ModelRendererBends((ModelBase)this, 0, 0).setShowChildIfHidden(true);
        this.bipedHead.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, p_i46304_1_);
        this.bipedHead.setRotationPoint(0.0f, -12.0f, 0.0f);
        if (p_i46304_2_) {
            this.bipedLeftArm = new ModelRendererBends_SeperatedChild((ModelBase)this, 32, 48).setMother((ModelRendererBends)this.bipedBody).setShowChildIfHidden(true);
            this.bipedLeftArm.addBox(-1.0f, -2.0f, -2.0f, 3, 6, 4, p_i46304_1_);
            this.bipedLeftArm.setRotationPoint(5.0f, -9.5f, 0.0f);
            this.bipedRightArm = new ModelRendererBends_SeperatedChild((ModelBase)this, 40, 16).setMother((ModelRendererBends)this.bipedBody).setShowChildIfHidden(true);
            this.bipedRightArm.addBox(-2.0f, -2.0f, -2.0f, 3, 6, 4, p_i46304_1_);
            this.bipedRightArm.setRotationPoint(-5.0f, -9.5f, 0.0f);
            this.bipedLeftArmwear = new ModelRendererBends((ModelBase)this, 48, 48);
            this.bipedLeftArmwear.addBox(-1.0f, -2.0f, -2.0f, 3, 6, 4, p_i46304_1_ + 0.25f);
            ((ModelRendererBends)this.bipedLeftArmwear).getBox().resY -= 0.25f;
            ((ModelRendererBends)this.bipedLeftArmwear).getBox().updateVertexPositions(this.bipedLeftArmwear);
            this.bipedLeftArmwear.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.bipedRightArmwear = new ModelRendererBends((ModelBase)this, 40, 32);
            this.bipedRightArmwear.addBox(-2.0f, -2.0f, -2.0f, 3, 6, 4, p_i46304_1_ + 0.25f);
            ((ModelRendererBends)this.bipedRightArmwear).getBox().resY -= 0.25f;
            ((ModelRendererBends)this.bipedRightArmwear).getBox().updateVertexPositions(this.bipedRightArmwear);
            this.bipedRightArmwear.setRotationPoint(0.0f, 0.0f, 0.0f);
            ((ModelRendererBends)this.bipedRightArm).offsetBox_Add(-0.01f, 0.0f, -0.01f).resizeBox(3.02f, 6.0f, 4.02f).updateVertices();
            ((ModelRendererBends)this.bipedLeftArm).offsetBox_Add(-0.01f, 0.0f, -0.01f).resizeBox(3.02f, 6.0f, 4.02f).updateVertices();
            this.bipedLeftForeArm = new ModelRendererBends((ModelBase)this, 32, 54);
            this.bipedLeftForeArm.addBox(-1.0f, 0.0f, -4.0f, 3, 6, 4, p_i46304_1_);
            this.bipedLeftForeArm.setRotationPoint(0.0f, 4.0f, 2.0f);
            this.bipedLeftForeArm.getBox().offsetTextureQuad(this.bipedLeftForeArm, 3, 0.0f, -6.0f);
            this.bipedRightForeArm = new ModelRendererBends((ModelBase)this, 40, 22);
            this.bipedRightForeArm.addBox(-2.0f, 0.0f, -4.0f, 3, 6, 4, p_i46304_1_);
            this.bipedRightForeArm.setRotationPoint(0.0f, 4.0f, 2.0f);
            this.bipedRightForeArm.getBox().offsetTextureQuad(this.bipedRightForeArm, 3, 0.0f, -6.0f);
            this.bipedLeftForeArmwear = new ModelRendererBends((ModelBase)this, 48, 54);
            this.bipedLeftForeArmwear.addBox(-1.0f, 0.0f, -4.0f, 3, 6, 4, p_i46304_1_ + 0.25f);
            this.bipedLeftForeArmwear.getBox().resY -= 0.25f;
            this.bipedLeftForeArmwear.getBox().offsetY += 0.25f;
            this.bipedLeftForeArmwear.getBox().updateVertexPositions(this.bipedLeftForeArmwear);
            this.bipedLeftForeArmwear.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.bipedLeftForeArmwear.getBox().offsetTextureQuad(this.bipedLeftForeArmwear, 3, 0.0f, -6.0f);
            this.bipedRightForeArmwear = new ModelRendererBends((ModelBase)this, 40, 38);
            this.bipedRightForeArmwear.addBox(-2.0f, 0.0f, -4.0f, 3, 6, 4, p_i46304_1_ + 0.25f);
            this.bipedRightForeArmwear.getBox().resY -= 0.25f;
            this.bipedRightForeArmwear.getBox().offsetY += 0.25f;
            this.bipedRightForeArmwear.getBox().updateVertexPositions(this.bipedRightForeArmwear);
            this.bipedRightForeArmwear.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.bipedRightForeArmwear.getBox().offsetTextureQuad(this.bipedRightForeArmwear, 3, 0.0f, -6.0f);
        } else {
            this.bipedLeftArm = new ModelRendererBends_SeperatedChild((ModelBase)this, 32, 48).setMother((ModelRendererBends)this.bipedBody).setShowChildIfHidden(true);
            this.bipedLeftArm.addBox(-1.0f, -2.0f, -2.0f, 4, 6, 4, p_i46304_1_);
            this.bipedLeftArm.setRotationPoint(5.0f, -10.0f, 0.0f);
            this.bipedRightArm = new ModelRendererBends_SeperatedChild((ModelBase)this, 40, 16).setMother((ModelRendererBends)this.bipedBody).setShowChildIfHidden(true);
            this.bipedRightArm.addBox(-3.0f, -2.0f, -2.0f, 4, 6, 4, p_i46304_1_);
            this.bipedRightArm.setRotationPoint(-5.0f, -10.0f, 0.0f);
            this.bipedLeftArmwear = new ModelRendererBends((ModelBase)this, 48, 48);
            this.bipedLeftArmwear.addBox(-1.0f, -2.0f, -2.0f, 4, 6, 4, p_i46304_1_ + 0.25f);
            ((ModelRendererBends)this.bipedLeftArmwear).getBox().resY -= 0.25f;
            ((ModelRendererBends)this.bipedLeftArmwear).getBox().updateVertexPositions(this.bipedLeftArmwear);
            this.bipedLeftArmwear.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.bipedRightArmwear = new ModelRendererBends((ModelBase)this, 40, 32);
            this.bipedRightArmwear.addBox(-3.0f, -2.0f, -2.0f, 4, 6, 4, p_i46304_1_ + 0.25f);
            this.bipedRightArmwear.setRotationPoint(0.0f, 0.0f, 0.0f);
            ((ModelRendererBends)this.bipedRightArm).offsetBox_Add(-0.01f, 0.0f, -0.01f).resizeBox(4.02f, 6.0f, 4.02f).updateVertices();
            ((ModelRendererBends)this.bipedLeftArm).offsetBox_Add(-0.01f, 0.0f, -0.01f).resizeBox(4.02f, 6.0f, 4.02f).updateVertices();
            this.bipedLeftForeArm = new ModelRendererBends((ModelBase)this, 32, 54);
            this.bipedLeftForeArm.addBox(-1.0f, 0.0f, -4.0f, 4, 6, 4, p_i46304_1_);
            this.bipedLeftForeArm.setRotationPoint(0.0f, 4.0f, 2.0f);
            this.bipedLeftForeArm.getBox().offsetTextureQuad(this.bipedLeftForeArm, 3, 0.0f, -6.0f);
            this.bipedRightForeArm = new ModelRendererBends((ModelBase)this, 40, 22);
            this.bipedRightForeArm.addBox(-3.0f, 0.0f, -4.0f, 4, 6, 4, p_i46304_1_);
            this.bipedRightForeArm.setRotationPoint(0.0f, 4.0f, 2.0f);
            this.bipedRightForeArm.getBox().offsetTextureQuad(this.bipedRightForeArm, 3, 0.0f, -6.0f);
            this.bipedLeftForeArmwear = new ModelRendererBends((ModelBase)this, 48, 54);
            this.bipedLeftForeArmwear.addBox(-1.0f, 0.0f, -4.0f, 4, 6, 4, p_i46304_1_ + 0.25f);
            this.bipedLeftForeArmwear.getBox().resY -= 0.25f;
            this.bipedLeftForeArmwear.getBox().offsetY += 0.25f;
            this.bipedLeftForeArmwear.getBox().updateVertexPositions(this.bipedLeftForeArmwear);
            this.bipedLeftForeArmwear.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.bipedLeftForeArmwear.getBox().offsetTextureQuad(this.bipedLeftForeArmwear, 3, 0.0f, -6.0f);
            this.bipedRightForeArmwear = new ModelRendererBends((ModelBase)this, 40, 38);
            this.bipedRightForeArmwear.addBox(-3.0f, 0.0f, -4.0f, 4, 6, 4, p_i46304_1_ + 0.25f);
            this.bipedRightForeArmwear.setRotationPoint(0.0f, 0.0f, 0.0f);
            this.bipedRightForeArmwear.getBox().offsetTextureQuad(this.bipedRightForeArmwear, 3, 0.0f, -6.0f);
        }
        this.bipedRightLeg = new ModelRendererBends((ModelBase)this, 0, 16);
        this.bipedRightLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, p_i46304_1_);
        this.bipedRightLeg.setRotationPoint(-1.9f, 12.0f, 0.0f);
        this.bipedLeftLeg = new ModelRendererBends((ModelBase)this, 16, 48);
        this.bipedLeftLeg.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, p_i46304_1_);
        this.bipedLeftLeg.setRotationPoint(1.9f, 12.0f, 0.0f);
        this.bipedLeftLegwear = new ModelRendererBends((ModelBase)this, 0, 48);
        this.bipedLeftLegwear.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, p_i46304_1_ + 0.25f);
        ((ModelRendererBends)this.bipedLeftLegwear).getBox().resY -= 0.25f;
        ((ModelRendererBends)this.bipedLeftLegwear).getBox().updateVertexPositions(this.bipedLeftLegwear);
        this.bipedLeftLegwear.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.bipedRightLegwear = new ModelRendererBends((ModelBase)this, 0, 32);
        this.bipedRightLegwear.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, p_i46304_1_ + 0.25f);
        ((ModelRendererBends)this.bipedRightLegwear).getBox().resY -= 0.25f;
        ((ModelRendererBends)this.bipedRightLegwear).getBox().updateVertexPositions(this.bipedRightLegwear);
        this.bipedRightLegwear.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.bipedBodyWear = new ModelRendererBends((ModelBase)this, 16, 32);
        this.bipedBodyWear.addBox(-4.0f, -12.0f, -2.0f, 8, 12, 4, p_i46304_1_ + 0.25f);
        this.bipedBodyWear.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.bipedRightForeLeg = new ModelRendererBends((ModelBase)this, 0, 22);
        this.bipedRightForeLeg.addBox(-2.0f, 0.0f, 0.0f, 4, 6, 4, p_i46304_1_);
        this.bipedRightForeLeg.setRotationPoint(0.0f, 6.0f, -2.0f);
        this.bipedRightForeLeg.getBox().offsetTextureQuad(this.bipedRightForeLeg, 3, 0.0f, -6.0f);
        this.bipedLeftForeLeg = new ModelRendererBends((ModelBase)this, 16, 54);
        this.bipedLeftForeLeg.addBox(-2.0f, 0.0f, 0.0f, 4, 6, 4, p_i46304_1_);
        this.bipedLeftForeLeg.setRotationPoint(0.0f, 6.0f, -2.0f);
        this.bipedLeftForeLeg.getBox().offsetTextureQuad(this.bipedLeftForeLeg, 3, 0.0f, -6.0f);
        this.bipedRightForeLegwear = new ModelRendererBends((ModelBase)this, 0, 38);
        this.bipedRightForeLegwear.addBox(-2.0f, 0.0f, 0.0f, 4, 6, 4, p_i46304_1_ + 0.25f);
        this.bipedRightForeLegwear.getBox().resY -= 0.25f;
        this.bipedRightForeLegwear.getBox().offsetY += 0.25f;
        this.bipedRightForeLegwear.getBox().updateVertexPositions(this.bipedRightForeLegwear);
        this.bipedRightForeLegwear.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.bipedRightForeLegwear.getBox().offsetTextureQuad(this.bipedRightForeLegwear, 3, 0.0f, -6.0f);
        this.bipedLeftForeLegwear = new ModelRendererBends((ModelBase)this, 0, 54);
        this.bipedLeftForeLegwear.addBox(-2.0f, 0.0f, 0.0f, 4, 6, 4, p_i46304_1_ + 0.25f);
        this.bipedLeftForeLegwear.getBox().resY -= 0.25f;
        this.bipedLeftForeLegwear.getBox().offsetY += 0.25f;
        this.bipedLeftForeLegwear.getBox().updateVertexPositions(this.bipedLeftForeLegwear);
        this.bipedLeftForeLegwear.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.bipedLeftForeLegwear.getBox().offsetTextureQuad(this.bipedLeftForeLegwear, 3, 0.0f, -6.0f);
        this.bipedBody.addChild(this.bipedRightArm);
        this.bipedBody.addChild(this.bipedLeftArm);
        this.bipedBody.addChild(this.bipedHead);
        this.bipedBody.addChild(this.bipedBodyWear);
        this.bipedHead.addChild(this.bipedHeadwear);
        this.bipedRightArm.addChild((ModelRenderer)this.bipedRightForeArm);
        this.bipedLeftArm.addChild((ModelRenderer)this.bipedLeftForeArm);
        this.bipedRightArm.addChild(this.bipedRightArmwear);
        this.bipedLeftArm.addChild(this.bipedLeftArmwear);
        this.bipedRightForeArm.addChild(this.bipedRightForeArmwear);
        this.bipedLeftForeArm.addChild(this.bipedLeftForeArmwear);
        this.bipedRightLeg.addChild((ModelRenderer)this.bipedRightForeLeg);
        this.bipedLeftLeg.addChild((ModelRenderer)this.bipedLeftForeLeg);
        this.bipedRightLeg.addChild(this.bipedRightLegwear);
        this.bipedLeftLeg.addChild(this.bipedLeftLegwear);
        this.bipedRightForeLeg.addChild(this.bipedRightForeLegwear);
        this.bipedLeftForeLeg.addChild(this.bipedLeftForeLegwear);
        ((ModelRendererBends_SeperatedChild)this.bipedRightArm).setSeperatedPart(this.bipedRightForeArm);
        ((ModelRendererBends_SeperatedChild)this.bipedLeftArm).setSeperatedPart(this.bipedLeftForeArm);
        ((ModelRendererBends)this.bipedRightLeg).offsetBox_Add(-0.01f, 0.0f, -0.01f).resizeBox(4.02f, 6.0f, 4.02f).updateVertices();
        ((ModelRendererBends)this.bipedLeftLeg).offsetBox_Add(-0.01f, 0.0f, -0.01f).resizeBox(4.02f, 6.0f, 4.02f).updateVertices();
    }

    public void render(Entity argEntity, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, argEntity);
        GL11.glPushMatrix();
        if (this.isChild) {
            float f6 = 2.0f;
            GL11.glPushMatrix();
            GL11.glScalef((float)(1.5f / f6), (float)(1.5f / f6), (float)(1.5f / f6));
            GL11.glTranslatef((float)0.0f, (float)(16.0f * p_78088_7_), (float)0.0f);
            this.bipedHead.render(p_78088_7_);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef((float)(1.0f / f6), (float)(1.0f / f6), (float)(1.0f / f6));
            GL11.glTranslatef((float)0.0f, (float)(24.0f * p_78088_7_), (float)0.0f);
            this.bipedBody.render(p_78088_7_);
            this.bipedRightArm.render(p_78088_7_);
            this.bipedLeftArm.render(p_78088_7_);
            this.bipedRightLeg.render(p_78088_7_);
            this.bipedLeftLeg.render(p_78088_7_);
            this.bipedHeadwear.render(p_78088_7_);
            GL11.glPopMatrix();
        } else {
            this.bipedBody.render(p_78088_7_);
            this.bipedRightLeg.render(p_78088_7_);
            this.bipedLeftLeg.render(p_78088_7_);
        }
        GL11.glPopMatrix();
    }

    public void setRotationAngles(float argSwingTime, float argSwingAmount, float argArmSway, float argHeadY, float argHeadX, float argNr6, Entity argEntity) {
        if (Minecraft.getMinecraft().theWorld == null) {
            return;
        }
        if (Minecraft.getMinecraft().theWorld.isRemote && Minecraft.getMinecraft().isGamePaused()) {
            return;
        }
        Data_Player data = Data_Player.get(argEntity.getEntityId());
        this.armSwing = argSwingTime;
        this.armSwingAmount = argSwingAmount;
        this.headRotationX = argHeadX;
        this.headRotationY = argHeadY;
        if (Minecraft.getMinecraft().currentScreen != null) {
            this.headRotationY = 0.0f;
        }
        ((ModelRendererBends)this.bipedHead).sync(data.head);
        ((ModelRendererBends)this.bipedHeadwear).sync(data.headwear);
        ((ModelRendererBends)this.bipedBody).sync(data.body);
        ((ModelRendererBends)this.bipedRightArm).sync(data.rightArm);
        ((ModelRendererBends)this.bipedLeftArm).sync(data.leftArm);
        ((ModelRendererBends)this.bipedRightLeg).sync(data.rightLeg);
        ((ModelRendererBends)this.bipedLeftLeg).sync(data.leftLeg);
        this.bipedRightForeArm.sync(data.rightForeArm);
        this.bipedLeftForeArm.sync(data.leftForeArm);
        this.bipedRightForeLeg.sync(data.rightForeLeg);
        this.bipedLeftForeLeg.sync(data.leftForeLeg);
        this.renderOffset.set(data.renderOffset);
        this.renderRotation.set(data.renderRotation);
        this.renderItemRotation.set(data.renderItemRotation);
        this.swordTrail = data.swordTrail;
        if (Data_Player.get(argEntity.getEntityId()).canBeUpdated()) {
            this.renderOffset.setSmooth(new Vector3f(0.0f, -1.0f, 0.0f), 0.5f);
            this.renderRotation.setSmooth(new Vector3f(0.0f, 0.0f, 0.0f), 0.5f);
            this.renderItemRotation.setSmooth(new Vector3f(0.0f, 0.0f, 0.0f), 0.5f);
            ((ModelRendererBends)this.bipedHead).resetScale();
            ((ModelRendererBends)this.bipedHeadwear).resetScale();
            ((ModelRendererBends)this.bipedBody).resetScale();
            ((ModelRendererBends)this.bipedRightArm).resetScale();
            ((ModelRendererBends)this.bipedLeftArm).resetScale();
            ((ModelRendererBends)this.bipedRightLeg).resetScale();
            ((ModelRendererBends)this.bipedLeftLeg).resetScale();
            this.bipedRightForeArm.resetScale();
            this.bipedLeftForeArm.resetScale();
            this.bipedRightForeLeg.resetScale();
            this.bipedLeftForeLeg.resetScale();
            BendsVar.tempData = Data_Player.get(argEntity.getEntityId());
            if (argEntity.isRiding()) {
                AnimatedEntity.getByEntity(argEntity).get("riding").animate((EntityLivingBase)argEntity, (ModelBase)this, Data_Player.get(argEntity.getEntityId()));
                BendsPack.animate(this, "player", "riding");
            } else if (argEntity.isInWater()) {
                AnimatedEntity.getByEntity(argEntity).get("swimming").animate((EntityLivingBase)argEntity, (ModelBase)this, Data_Player.get(argEntity.getEntityId()));
                BendsPack.animate(this, "player", "swimming");
            } else if (!Data_Player.get(argEntity.getEntityId()).isOnGround() | Data_Player.get((int)argEntity.getEntityId()).ticksAfterTouchdown < 2.0f) {
                AnimatedEntity.getByEntity(argEntity).get("jump").animate((EntityLivingBase)argEntity, (ModelBase)this, Data_Player.get(argEntity.getEntityId()));
                BendsPack.animate(this, "player", "jump");
            } else {
                if (Data_Player.get((int)argEntity.getEntityId()).motion.x == 0.0f & Data_Player.get((int)argEntity.getEntityId()).motion.z == 0.0f) {
                    AnimatedEntity.getByEntity(argEntity).get("stand").animate((EntityLivingBase)argEntity, (ModelBase)this, Data_Player.get(argEntity.getEntityId()));
                    BendsPack.animate(this, "player", "stand");
                } else if (argEntity.isSprinting()) {
                    AnimatedEntity.getByEntity(argEntity).get("sprint").animate((EntityLivingBase)argEntity, (ModelBase)this, Data_Player.get(argEntity.getEntityId()));
                    BendsPack.animate(this, "player", "sprint");
                } else {
                    AnimatedEntity.getByEntity(argEntity).get("walk").animate((EntityLivingBase)argEntity, (ModelBase)this, Data_Player.get(argEntity.getEntityId()));
                    BendsPack.animate(this, "player", "walk");
                }
                if (argEntity.isSneaking()) {
                    AnimatedEntity.getByEntity(argEntity).get("sneak").animate((EntityLivingBase)argEntity, (ModelBase)this, Data_Player.get(argEntity.getEntityId()));
                    BendsPack.animate(this, "player", "sneak");
                }
            }
            if (this.aimedBow) {
                AnimatedEntity.getByEntity(argEntity).get("bow").animate((EntityLivingBase)argEntity, (ModelBase)this, Data_Player.get(argEntity.getEntityId()));
                BendsPack.animate(this, "player", "bow");
            } else if (((EntityPlayer)argEntity).getCurrentEquippedItem() != null && ((EntityPlayer)argEntity).getCurrentEquippedItem().getItem() instanceof ItemPickaxe || ((EntityPlayer)argEntity).getCurrentEquippedItem() != null && Block.getBlockFromItem((Item)((EntityPlayer)argEntity).getCurrentEquippedItem().getItem()) != null) {
                AnimatedEntity.getByEntity(argEntity).get("mining").animate((EntityLivingBase)argEntity, (ModelBase)this, Data_Player.get(argEntity.getEntityId()));
                BendsPack.animate(this, "player", "mining");
            } else if (((EntityPlayer)argEntity).getCurrentEquippedItem() != null && ((EntityPlayer)argEntity).getCurrentEquippedItem().getItem() instanceof ItemAxe) {
                AnimatedEntity.getByEntity(argEntity).get("axe").animate((EntityLivingBase)argEntity, (ModelBase)this, Data_Player.get(argEntity.getEntityId()));
                BendsPack.animate(this, "player", "axe");
            } else {
                AnimatedEntity.getByEntity(argEntity).get("attack").animate((EntityLivingBase)argEntity, (ModelBase)this, Data_Player.get(argEntity.getEntityId()));
            }
            ((ModelRendererBends)this.bipedHead).update(data.ticksPerFrame);
            ((ModelRendererBends)this.bipedHeadwear).update(data.ticksPerFrame);
            ((ModelRendererBends)this.bipedBody).update(data.ticksPerFrame);
            ((ModelRendererBends)this.bipedLeftArm).update(data.ticksPerFrame);
            ((ModelRendererBends)this.bipedRightArm).update(data.ticksPerFrame);
            ((ModelRendererBends)this.bipedLeftLeg).update(data.ticksPerFrame);
            ((ModelRendererBends)this.bipedRightLeg).update(data.ticksPerFrame);
            this.bipedLeftForeArm.update(data.ticksPerFrame);
            this.bipedRightForeArm.update(data.ticksPerFrame);
            this.bipedLeftForeLeg.update(data.ticksPerFrame);
            this.bipedRightForeLeg.update(data.ticksPerFrame);
            this.renderOffset.update(data.ticksPerFrame);
            this.renderRotation.update(data.ticksPerFrame);
            this.renderItemRotation.update(data.ticksPerFrame);
            this.swordTrail.update(data.ticksPerFrame);
            data.updatedThisFrame = true;
        }
        Data_Player.get(argEntity.getEntityId()).syncModelInfo(this);
    }

    public void postRender(float argScale) {
        GlStateManager.translate((float)(this.renderOffset.vSmooth.x * argScale), (float)(-this.renderOffset.vSmooth.y * argScale), (float)(this.renderOffset.vSmooth.z * argScale));
        GlStateManager.rotate((float)this.renderRotation.getX(), (float)1.0f, (float)0.0f, (float)0.0f);
        GlStateManager.rotate((float)this.renderRotation.getY(), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)this.renderRotation.getZ(), (float)0.0f, (float)0.0f, (float)1.0f);
    }

    public void postRenderTranslate(float argScale) {
        GlStateManager.translate((float)(this.renderOffset.vSmooth.x * argScale), (float)(-this.renderOffset.vSmooth.y * argScale), (float)(this.renderOffset.vSmooth.z * argScale));
    }

    public void postRenderRotate(float argScale) {
        GlStateManager.rotate((float)this.renderRotation.getX(), (float)1.0f, (float)0.0f, (float)0.0f);
        GlStateManager.rotate((float)this.renderRotation.getY(), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)this.renderRotation.getZ(), (float)0.0f, (float)0.0f, (float)1.0f);
    }

    public void postRenderArm(float argScale) {
        this.bipedRightArm.postRender(argScale);
        this.bipedRightForeArm.postRender(argScale);
        GL11.glTranslatef((float)(0.0f * argScale), (float)(4.0f * argScale), (float)(-2.0f * argScale));
        GL11.glRotatef((float)this.renderItemRotation.vSmooth.x, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glRotatef((float)this.renderItemRotation.vSmooth.y, (float)0.0f, (float)-1.0f, (float)0.0f);
        GL11.glRotatef((float)this.renderItemRotation.vSmooth.z, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glTranslatef((float)(0.0f * argScale), (float)(-8.0f * argScale), (float)(0.0f * argScale));
    }

    public void updateWithEntityData(AbstractClientPlayer argPlayer) {
        Data_Player data = Data_Player.get(argPlayer.getEntityId());
        if (data != null) {
            this.renderOffset.set(data.renderOffset);
            this.renderRotation.set(data.renderRotation);
            this.renderItemRotation.set(data.renderItemRotation);
        }
    }
}
