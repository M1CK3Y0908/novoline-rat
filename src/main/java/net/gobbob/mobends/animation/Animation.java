/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.entity.EntityLivingBase
 */
package net.gobbob.mobends.animation;

import net.gobbob.mobends.data.EntityData;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;

public abstract class Animation {
    public abstract void animate(EntityLivingBase var1, ModelBase var2, EntityData var3);

    public abstract String getName();
}
