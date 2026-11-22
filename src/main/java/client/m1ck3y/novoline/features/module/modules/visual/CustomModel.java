package client.m1ck3y.novoline.features.module.modules.visual;

import io.github.nevalackin.homoBus.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import client.m1ck3y.novoline.event.PreUpdateEvent;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.features.module.ModuleManager;
import client.m1ck3y.novoline.setting.ColorSetting;
import client.m1ck3y.novoline.setting.ComboSetting;
import client.m1ck3y.novoline.setting.MultiComboSetting;
import client.m1ck3y.novoline.setting.NumberSetting;

public class CustomModel extends Module {
   public final ComboSetting customModel = settingManager.register(new ComboSetting(this, "Model", 0, "AmongUs", "Sonic", "Demon"));
   public final MultiComboSetting applyFor = settingManager.register(new MultiComboSetting(this, "Apply for").add("Self", true).add("Others", true));
   public final ComboSetting colorType = settingManager.register(
      new ComboSetting(this, "Color type", 0, "Default", "Colored", "Random", "Rainbow", "Astolfo") {
         @Override
         public boolean shouldShow() {
            return CustomModel.this.customModel.getCurrentElement() == 0;
         }
      }
   );
   public final NumberSetting cycleTime = settingManager.register(
      new NumberSetting(this, "Cycle time", 2000, 1, 20000) {
         @Override
         public boolean shouldShow() {
            return CustomModel.this.customModel.getCurrentElement() == 0 && CustomModel.this.colorType.getCurrentElement() == 3
               || CustomModel.this.colorType.getCurrentElement() == 4;
         }
      }
   );
   public final ColorSetting amongUsColor = settingManager.register(new ColorSetting(this, "AmongUs Color", -14506000) {
      @Override
      public boolean shouldShow() {
         return CustomModel.this.customModel.getCurrentElement() == 0 && CustomModel.this.colorType.getCurrentElement() == 1;
      }
   });
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> this.setSuffix(this.customModel.getCurrentElementAsString());

   public boolean appliesTo(Entity entity2) {
      return entity2 == Minecraft.getMinecraft().thePlayer ? ModuleManager.customModel.applyFor.isSelected(0) : ModuleManager.customModel.applyFor.isSelected(1);
   }

   public CustomModel() {
      super(Category.VISUAL, "CustomModel", "");
   }
}
