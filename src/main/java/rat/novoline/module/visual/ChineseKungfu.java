package rat.novoline.module.visual;

import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.CheckboxSetting;

public class ChineseKungfu extends Module {
    public final CheckboxSetting hideHeldItem = settingManager.register(new CheckboxSetting(this, "Hide Held Item", true));
    public final CheckboxSetting hideArmor = settingManager.register(new CheckboxSetting(this, "Hide Armor", true));
    public final CheckboxSetting firstPersonEffects = settingManager.register(new CheckboxSetting(this, "First Person Effects", true));

    public ChineseKungfu() {
        super(Category.VISUAL, "ChineseKungfu", "Hides held items and armor for Kung Fu combat with Mo' Bends effects");
    }
}
