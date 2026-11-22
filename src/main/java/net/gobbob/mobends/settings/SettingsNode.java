package net.gobbob.mobends.settings;

import client.m1ck3y.novoline.features.module.ModuleManager;

public abstract class SettingsNode {
    public static SettingsNode[] settings = new SettingsNode[]{
        new SettingsBoolean("swordTrail", "Sword Trail").setupDefault(true),
        new SettingsBoolean("thirdSwordAttack", "Spin Attack").setupDefault(true)
    };
    public String id;
    public String displayName;

    public SettingsNode() {
        this.id = "NULL";
        this.displayName = "NULL";
    }

    public SettingsNode(String argID, String argDisplayName) {
        this.id = argID;
        this.displayName = argDisplayName;
    }

    public static SettingsNode getSetting(String argID) {
        for (SettingsNode setting : settings) {
            if (setting.id.equalsIgnoreCase(argID)) {
                if (ModuleManager.moBends != null && ModuleManager.moBends.isEnabled()) {
                    if ("swordTrail".equalsIgnoreCase(argID)) {
                        ((SettingsBoolean)setting).data = ModuleManager.moBends.swordTrail.isEnabled();
                    } else if ("thirdSwordAttack".equalsIgnoreCase(argID)) {
                        ((SettingsBoolean)setting).data = ModuleManager.moBends.spinAttack.isEnabled();
                    }
                }
                return setting;
            }
        }
        return null;
    }

    public boolean getBoolean() {
        if (ModuleManager.moBends != null && ModuleManager.moBends.isEnabled()) {
            if ("swordTrail".equalsIgnoreCase(this.id)) {
                return ModuleManager.moBends.swordTrail.isEnabled();
            }
            if ("thirdSwordAttack".equalsIgnoreCase(this.id)) {
                return ModuleManager.moBends.spinAttack.isEnabled();
            }
        }
        return ((SettingsBoolean)this).data;
    }
}
