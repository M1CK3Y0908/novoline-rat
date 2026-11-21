package net.gobbob.mobends.event;

import net.gobbob.mobends.data.Data_Player;
import net.gobbob.mobends.data.Data_Spider;
import net.gobbob.mobends.data.Data_Zombie;
import net.gobbob.mobends.data.EntityData;
import net.gobbob.mobends.util.BendsLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;

public class EventHandler_DataUpdate {
    public void updateAnimations(float partialTicks) {
        if (Minecraft.getMinecraft().theWorld == null) {
            return;
        }
        for (int i = 0; i < Data_Player.dataList.size(); ++i) {
            Data_Player.dataList.get(i).update(partialTicks);
        }
        for (int i = 0; i < Data_Zombie.dataList.size(); ++i) {
            Data_Zombie.dataList.get(i).update(partialTicks);
        }
        for (int i = 0; i < Data_Spider.dataList.size(); ++i) {
            Data_Spider.dataList.get(i).update(partialTicks);
        }
    }

    public void onClientTick() {
        if (Minecraft.getMinecraft().theWorld == null) {
            return;
        }
        for (int i = 0; i < Data_Player.dataList.size(); ++i) {
            EntityData data = Data_Player.dataList.get(i);
            Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(data.entityID);
            if (entity != null) {
                if (!data.entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Player.dataList.remove(data);
                    Data_Player.add(new Data_Player(entity.getEntityId()));
                    BendsLogger.log("Reset entity", BendsLogger.DEBUG);
                    continue;
                }
                data.motion_prev.set((ReadableVector3f)data.motion);
                data.motion.x = (float)entity.posX - data.position.x;
                data.motion.y = (float)entity.posY - data.position.y;
                data.motion.z = (float)entity.posZ - data.position.z;
                data.position = new Vector3f((float)entity.posX, (float)entity.posY, (float)entity.posZ);
                continue;
            }
            Data_Player.dataList.remove(data);
            BendsLogger.log("No entity", BendsLogger.DEBUG);
        }
        for (int i = 0; i < Data_Zombie.dataList.size(); ++i) {
            EntityData data = Data_Zombie.dataList.get(i);
            Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(((Data_Zombie)data).entityID);
            if (entity != null) {
                if (!((Data_Zombie)data).entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Zombie.dataList.remove(data);
                    Data_Zombie.add(new Data_Zombie(entity.getEntityId()));
                    BendsLogger.log("Reset entity", BendsLogger.DEBUG);
                    continue;
                }
                ((Data_Zombie)data).motion_prev.set((ReadableVector3f)((Data_Zombie)data).motion);
                ((Data_Zombie)data).motion.x = (float)entity.posX - ((Data_Zombie)data).position.x;
                ((Data_Zombie)data).motion.y = (float)entity.posY - ((Data_Zombie)data).position.y;
                ((Data_Zombie)data).motion.z = (float)entity.posZ - ((Data_Zombie)data).position.z;
                ((Data_Zombie)data).position = new Vector3f((float)entity.posX, (float)entity.posY, (float)entity.posZ);
                continue;
            }
            Data_Zombie.dataList.remove(data);
            BendsLogger.log("No entity", BendsLogger.DEBUG);
        }
        for (int i = 0; i < Data_Spider.dataList.size(); ++i) {
            EntityData data = Data_Spider.dataList.get(i);
            Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(((Data_Spider)data).entityID);
            if (entity != null) {
                if (!((Data_Spider)data).entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Spider.dataList.remove(data);
                    Data_Spider.add(new Data_Spider(entity.getEntityId()));
                    BendsLogger.log("Reset entity", BendsLogger.DEBUG);
                    continue;
                }
                ((Data_Spider)data).motion_prev.set((ReadableVector3f)((Data_Spider)data).motion);
                ((Data_Spider)data).motion.x = (float)entity.posX - ((Data_Spider)data).position.x;
                ((Data_Spider)data).motion.y = (float)entity.posY - ((Data_Spider)data).position.y;
                ((Data_Spider)data).motion.z = (float)entity.posZ - ((Data_Spider)data).position.z;
                ((Data_Spider)data).position = new Vector3f((float)entity.posX, (float)entity.posY, (float)entity.posZ);
                continue;
            }
            Data_Spider.dataList.remove(data);
            BendsLogger.log("No entity", BendsLogger.DEBUG);
        }
    }
}
