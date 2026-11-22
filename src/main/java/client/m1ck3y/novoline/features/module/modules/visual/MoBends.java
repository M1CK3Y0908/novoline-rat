package client.m1ck3y.novoline.features.module.modules.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.gobbob.mobends.AnimatedEntity;
import net.gobbob.mobends.data.Data_Player;
import net.gobbob.mobends.data.Data_Spider;
import net.gobbob.mobends.data.Data_Zombie;
import net.gobbob.mobends.event.EventHandler_DataUpdate;
import net.gobbob.mobends.event.EventHandler_RenderPlayer;
import net.gobbob.mobends.pack.BendsPack;
import client.m1ck3y.novoline.event.RenderWorldEvent;
import client.m1ck3y.novoline.event.RunTickEvent;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.setting.CheckboxSetting;

public class MoBends extends Module {
    public final CheckboxSetting swordTrail = settingManager.register(new CheckboxSetting(this, "Sword Trail", true));
    public final CheckboxSetting spinAttack = settingManager.register(new CheckboxSetting(this, "Spin Attack", true));
    public final CheckboxSetting player = settingManager.register(new CheckboxSetting(this, "Player", true));
    public final CheckboxSetting zombie = settingManager.register(new CheckboxSetting(this, "Zombie", true));
    public final CheckboxSetting spider = settingManager.register(new CheckboxSetting(this, "Spider", true));

    private final EventHandler_DataUpdate dataUpdate = new EventHandler_DataUpdate();

    @EventLink
    private final Listener<RenderWorldEvent> onRenderWorld = event -> {
        EventHandler_RenderPlayer.partialTicks = event.partialTicks;
        this.dataUpdate.updateAnimations(event.partialTicks);
    };

    @EventLink
    private final Listener<RunTickEvent> onRunTick = event -> {
        this.dataUpdate.onClientTick();
    };

    public MoBends() {
        super(Category.VISUAL, "MoBends", "Mo' Bends realistic player and mob animations");
    }

    @Override
    public void onInitialized() {
        BendsPack.preInit();
        AnimatedEntity.checkInit();
    }

    @Override
    public void onDisable() {
        Data_Player.dataList.clear();
        Data_Zombie.dataList.clear();
        Data_Spider.dataList.clear();
    }
}
