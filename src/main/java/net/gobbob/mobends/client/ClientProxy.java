package net.gobbob.mobends.client;

import net.gobbob.mobends.AnimatedEntity;
import net.gobbob.mobends.CommonProxy;
import net.gobbob.mobends.pack.BendsPack;
import net.minecraft.util.ResourceLocation;

public class ClientProxy extends CommonProxy {
    public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public static final ResourceLocation texture_NULL = new ResourceLocation("mobends", "textures/white.png");
    public static final ResourceLocation GOBLIN_CAPE = new ResourceLocation("mobends", "textures/goblinCape.png");

    @Override
    public void preinit() {
        BendsPack.preInit();
    }

    @Override
    public void init() {
        AnimatedEntity.register();
    }
}
