package client.m1ck3y.novoline.features.module.modules.hud;

import client.m1ck3y.novoline.Novoline;
import client.m1ck3y.novoline.Novoline.Fonts;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.features.module.ModuleManager;
import client.m1ck3y.novoline.setting.CheckboxSetting;
import client.m1ck3y.novoline.setting.ColorSetting;
import client.m1ck3y.novoline.setting.MultiComboSetting;
import client.m1ck3y.novoline.setting.NumberSetting;
import client.m1ck3y.novoline.utils.render.FontAdapter;

public class ArrayList extends Module {
   public FontAdapter getFont() {
      return ModuleManager.font != null ? ModuleManager.font.getFont() : Fonts.rubikRegularSize16;
   }

   public final MultiComboSetting arrayList = settingManager.register((new MultiComboSetting(this, "Limitations") {
      @Override
      public void interactCallback() {
         Novoline.getNovoline().getModuleManager().updateCachedFeatures();
      }
   }).add("Hide status", false).add("Hide visuals", false).add("Bound only", false));
   public final MultiComboSetting outline = settingManager.register(
      new MultiComboSetting(this, "Outline")
         .add("Top", false)
         .add("Bottom", false)
         .add("Left", false)
         .add("Right", true)
   );
   public final NumberSetting lineWidth = settingManager.register(new NumberSetting(this, "Line Width", 1, 1, 5) {
      @Override
      public boolean shouldShow() {
         return ArrayList.this.hasOutline();
      }
   });
   public final NumberSetting widthPadding = settingManager.register(new NumberSetting(this, "Width Padding", 4, 0, 10));
   public final NumberSetting heightPadding = settingManager.register(new NumberSetting(this, "Height Padding", 5, 0, 10));
   public final CheckboxSetting background = settingManager.register(new CheckboxSetting(this, "Background", true));
   public final CheckboxSetting backgroundShadow = settingManager.register(new CheckboxSetting(this, "Background Shadow", true));
   public final ColorSetting status = settingManager.register(new ColorSetting(this, "Status", -1));

   public boolean hasOutline() {
      for (MultiComboSetting.Option option : this.outline.getElements()) {
         if (option.isSelected()) {
            return true;
         }
      }
      return false;
   }

   public ArrayList() {
      super(Category.VISUAL, "ArrayList", "");
      this.setEnabled(true);
   }
}
