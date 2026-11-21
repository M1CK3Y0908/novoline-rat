package net.gobbob.mobends;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.gobbob.mobends.animation.Animation;
import net.gobbob.mobends.animation.player.Animation_Attack;
import net.gobbob.mobends.animation.player.Animation_Axe;
import net.gobbob.mobends.animation.player.Animation_Bow;
import net.gobbob.mobends.animation.player.Animation_Jump;
import net.gobbob.mobends.animation.player.Animation_Mining;
import net.gobbob.mobends.animation.player.Animation_Riding;
import net.gobbob.mobends.animation.player.Animation_Sneak;
import net.gobbob.mobends.animation.player.Animation_Sprint;
import net.gobbob.mobends.animation.player.Animation_Swimming;
import net.gobbob.mobends.animation.spider.Animation_OnGround;
import net.gobbob.mobends.animation.spider.Animation_WallClimb;
import net.gobbob.mobends.animation.zombie.Animation_Stand;
import net.gobbob.mobends.animation.zombie.Animation_Walk;
import net.gobbob.mobends.client.renderer.entity.RenderBendsPlayer;
import net.gobbob.mobends.client.renderer.entity.RenderBendsSpider;
import net.gobbob.mobends.client.renderer.entity.RenderBendsZombie;
import net.gobbob.mobends.util.BendsLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;

public class AnimatedEntity {
    public static List<AnimatedEntity> animatedEntities = new ArrayList<AnimatedEntity>();
    public static Map<String, RenderBendsPlayer> skinMap = Maps.newHashMap();
    public static RenderBendsPlayer playerRenderer;
    public String id;
    public String displayName;
    public Entity entity;
    public Class<? extends Entity> entityClass;
    public Render renderer;
    public List<Animation> animations = new ArrayList<Animation>();
    public boolean animate = true;

    public AnimatedEntity(String argID, String argDisplayName, Entity argEntity, Class<? extends Entity> argClass, Render argRenderer) {
        this.id = argID;
        this.displayName = argDisplayName;
        this.entityClass = argClass;
        this.renderer = argRenderer;
        this.entity = argEntity;
        this.animate = true;
    }

    public AnimatedEntity add(Animation argGroup) {
        this.animations.add(argGroup);
        return this;
    }

    public static void checkInit() {
        if (playerRenderer == null && Minecraft.getMinecraft().getRenderManager() != null) {
            register();
        }
    }

    public static void register() {
        RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        if (rm == null) return;
        BendsLogger.log("Registering Animated Entities...", BendsLogger.INFO);
        animatedEntities.clear();
        AnimatedEntity.registerEntity(new AnimatedEntity("player", "Player", Minecraft.getMinecraft().thePlayer, EntityPlayer.class, new RenderBendsPlayer(rm)).add(new net.gobbob.mobends.animation.player.Animation_Stand()).add(new net.gobbob.mobends.animation.player.Animation_Walk()).add(new Animation_Sneak()).add(new Animation_Sprint()).add(new Animation_Jump()).add(new Animation_Attack()).add(new Animation_Swimming()).add(new Animation_Bow()).add(new Animation_Riding()).add(new Animation_Mining()).add(new Animation_Axe()));
        AnimatedEntity.registerEntity(new AnimatedEntity("zombie", "Zombie", new EntityZombie(null), EntityZombie.class, new RenderBendsZombie(rm)).add(new Animation_Stand()).add(new Animation_Walk()));
        AnimatedEntity.registerEntity(new AnimatedEntity("spider", "Spider", new EntitySpider(null), EntitySpider.class, new RenderBendsSpider(rm)).add(new Animation_OnGround()).add(new net.gobbob.mobends.animation.spider.Animation_Jump()).add(new Animation_WallClimb()));
        
        playerRenderer = new RenderBendsPlayer(rm);
        skinMap.put("default", playerRenderer);
        skinMap.put("slim", new RenderBendsPlayer(rm, true));
    }

    public static void registerEntity(AnimatedEntity argEntity) {
        BendsLogger.log("Registering " + argEntity.displayName, BendsLogger.INFO);
        animatedEntities.add(argEntity);
    }

    public Animation get(String argName) {
        for (int i = 0; i < this.animations.size(); ++i) {
            if (!this.animations.get(i).getName().equalsIgnoreCase(argName)) continue;
            return this.animations.get(i);
        }
        return null;
    }

    public static AnimatedEntity getByEntity(Entity argEntity) {
        checkInit();
        for (int i = 0; i < animatedEntities.size(); ++i) {
            if (!AnimatedEntity.animatedEntities.get((int)i).entityClass.isInstance(argEntity)) continue;
            return animatedEntities.get(i);
        }
        return null;
    }

    public static RenderBendsPlayer getPlayerRenderer(AbstractClientPlayer player) {
        checkInit();
        String s = player.getSkinType();
        RenderBendsPlayer renderplayer = skinMap.get(s);
        return renderplayer != null ? renderplayer : playerRenderer;
    }
}
