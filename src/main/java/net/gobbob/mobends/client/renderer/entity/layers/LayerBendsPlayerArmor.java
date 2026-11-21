package net.gobbob.mobends.client.renderer.entity.layers;

import net.gobbob.mobends.client.model.entity.ModelBendsPlayerArmor;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;

public class LayerBendsPlayerArmor extends LayerBipedArmor {
    public LayerBendsPlayerArmor(RendererLivingEntity<?> rendererIn) {
        super(rendererIn);
    }

    @Override
    protected void initArmor() {
        this.modelLeggings = new ModelBendsPlayerArmor(0.5f);
        this.modelArmor = new ModelBendsPlayerArmor(1.0f);
    }

    @Override
    protected void setModelPartVisible(ModelBiped modelBiped, int armorSlot) {
        this.setModelVisible(modelBiped);
        ModelBendsPlayerArmor model = (ModelBendsPlayerArmor)modelBiped;
        switch (armorSlot) {
            case 1: {
                model.bipedRightLeg.showModel = true;
                model.bipedLeftLeg.showModel = true;
                model.bipedRightForeLeg.showModel = true;
                model.bipedLeftForeLeg.showModel = true;
                break;
            }
            case 2: {
                model.bipedBody.showModel = true;
                model.bipedRightLeg.showModel = true;
                model.bipedLeftLeg.showModel = true;
                model.bipedRightForeLeg.showModel = true;
                model.bipedLeftForeLeg.showModel = true;
                break;
            }
            case 3: {
                model.bipedBody.showModel = true;
                model.bipedRightArm.showModel = true;
                model.bipedLeftArm.showModel = true;
                model.bipedRightForeArm.showModel = true;
                model.bipedLeftForeArm.showModel = true;
                break;
            }
            case 4: {
                model.bipedHead.showModel = true;
                model.bipedHeadwear.showModel = true;
                break;
            }
        }
    }
}
