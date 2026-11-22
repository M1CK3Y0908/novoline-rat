package client.m1ck3y.novoline.api;

import net.minecraft.util.ResourceLocation;

public interface IResourceHolder<T> {
   Class<T> getType();

   ResourceLocation getResourceLocation();

   T getObject();
}
