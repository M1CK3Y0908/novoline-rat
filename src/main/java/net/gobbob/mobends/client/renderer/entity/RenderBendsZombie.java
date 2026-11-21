package net.gobbob.mobends.client.renderer.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.gobbob.mobends.client.model.entity.ModelBendsZombie;
import net.gobbob.mobends.client.model.entity.ModelBendsZombieVillager;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerVillagerArmor;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderBendsZombie extends RenderBiped<EntityZombie> {
    private static final ResourceLocation zombieTextures = new ResourceLocation("textures/entity/zombie/zombie.png");
    private static final ResourceLocation zombieVillagerTextures = new ResourceLocation("textures/entity/zombie/zombie_villager.png");
    private final ModelBiped zombieModel;
    private final ModelBendsZombieVillager zombieVillagerModel;
    private final List<LayerRenderer<EntityZombie>> zombieVillagerLayerRenderers;
    private final List<LayerRenderer<EntityZombie>> zombieLayerRenderers;

    public RenderBendsZombie(RenderManager p_i46127_1_) {
        super(p_i46127_1_, new ModelBendsZombie(), 0.5f, 1.0f);
        LayerRenderer layerrenderer = this.layerRenderers.get(0);
        this.zombieModel = this.modelBipedMain;
        this.zombieVillagerModel = new ModelBendsZombieVillager();
        this.addLayer(new LayerHeldItem(this));
        LayerBipedArmor layerbipedarmor = new LayerBipedArmor(this) {
            @Override
            protected void initArmor() {
                this.modelLeggings = new ModelBendsZombie(0.5f, true);
                this.modelArmor = new ModelBendsZombie(1.0f, true);
            }
        };
        this.addLayer(layerbipedarmor);
        this.zombieLayerRenderers = Lists.newArrayList(this.layerRenderers);
        if (layerrenderer instanceof LayerCustomHead) {
            this.removeLayer(layerrenderer);
            this.addLayer(new LayerCustomHead(this.zombieVillagerModel.bipedHead));
        }
        this.removeLayer(layerbipedarmor);
        this.addLayer(new LayerVillagerArmor(this));
        this.zombieVillagerLayerRenderers = Lists.newArrayList(this.layerRenderers);
    }

    @Override
    public void doRender2(EntityZombie p_180579_1_, double p_180579_2_, double p_180579_4_, double p_180579_6_, float p_180579_8_, float p_180579_9_) {
        this.setModelForZombieType(p_180579_1_);
        super.doRender2(p_180579_1_, p_180579_2_, p_180579_4_, p_180579_6_, p_180579_8_, p_180579_9_);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityZombie p_180578_1_) {
        return p_180578_1_.isVillager() ? zombieVillagerTextures : zombieTextures;
    }

    private void setModelForZombieType(EntityZombie p_82427_1_) {
        if (p_82427_1_.isVillager()) {
            this.mainModel = this.zombieVillagerModel;
            this.layerRenderers = this.zombieVillagerLayerRenderers;
        } else {
            this.mainModel = this.zombieModel;
            this.layerRenderers = this.zombieLayerRenderers;
        }
        this.modelBipedMain = (ModelBiped)this.mainModel;
    }

    @Override
    protected void rotateCorpse(EntityZombie p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
        if (p_77043_1_.isConverting()) {
            p_77043_3_ += (float)(Math.cos((double)p_77043_1_.ticksExisted * 3.25) * Math.PI * 0.25);
        }
        super.rotateCorpse(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
    }
}
