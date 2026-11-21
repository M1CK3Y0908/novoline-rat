package rat.novoline.module.hud;

import rat.novoline.Novoline;
import rat.novoline.Novoline.Fonts;
import rat.novoline.setting.ComboSetting;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ColorSetting;
import rat.novoline.setting.MultiComboSetting;
import rat.novoline.setting.NumberSetting;

public class ArrayList extends Module {
   public final ComboSetting font = settingManager.register(new ComboSetting(this, "Font", 0, "Rubik", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Minecraft") {
      @Override
      public void interactCallback() {
         Novoline.getNovoline().getModuleManager().updateCachedFeatures();
      }
   });
   public final NumberSetting fontSize = settingManager.register(new NumberSetting(this, "Font Size", 16, 10, 24) {
      @Override
      public void interactCallback() {
         Novoline.getNovoline().getModuleManager().updateCachedFeatures();
      }
   });

   public FontAdapter getFont() {
      return Fonts.getFont(this.font.getCurrentElementAsString(), this.fontSize.getCurrentValue());
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
   public final ColorSetting backgroundColor = settingManager.register(new ColorSetting(this, "Background", 0x32000000) {
      @Override
      public boolean shouldShow() {
         return ArrayList.this.background.isEnabled();
      }
   });

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
