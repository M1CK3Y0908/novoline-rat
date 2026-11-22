package client.m1ck3y.novoline.features.module.modules.visual;

import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.setting.ColorSetting;
import client.m1ck3y.novoline.setting.ComboSetting;
import client.m1ck3y.novoline.setting.MultiComboSetting;

public class WorldColor extends Module {
   public final MultiComboSetting worldColor = settingManager.register(
      new MultiComboSetting(this, "Elements").add("Fog", true).add("Sky", true).add("World", true)
   );
   public final ComboSetting colorType = settingManager.register(new ComboSetting(this, "Color type", 0, "Colored", "Astolfo"));
   public final ColorSetting fogColor = settingManager.register(new ColorSetting(this, "Fog color", -16754784) {
      @Override
      public boolean shouldShow() {
         return WorldColor.this.worldColor.isSelected(0) && WorldColor.this.colorType.getCurrentElement() == 0;
      }
   });
   public final ColorSetting skyColor = settingManager.register(new ColorSetting(this, "Sky color", -16754784) {
      @Override
      public boolean shouldShow() {
         return WorldColor.this.worldColor.isSelected(1) && WorldColor.this.colorType.getCurrentElement() == 0;
      }
   });
   public final ColorSetting worldColorValue = settingManager.register(new ColorSetting(this, "World color", -16754784) {
      @Override
      public boolean shouldShow() {
         return WorldColor.this.worldColor.isSelected(2) && WorldColor.this.colorType.getCurrentElement() == 0;
      }
   });

   public WorldColor() {
      super(Category.VISUAL, "WorldColor", "Изменяет цвет мира");
   }
}
