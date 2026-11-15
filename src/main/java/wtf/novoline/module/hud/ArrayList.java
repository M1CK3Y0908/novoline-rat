package wtf.novoline.module.hud;

import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.utils.render.FontAdapter;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;

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
   public final NumberSetting rightLineWidth = settingManager.register(new NumberSetting(this, "Right Line Width", 1, 1, 5) {
      @Override
      public boolean shouldShow() {
         return ArrayList.this.rightLine.isEnabled();
      }
   });
   public final NumberSetting widthPadding = settingManager.register(new NumberSetting(this, "Width Padding", 4, 0, 10));
   public final NumberSetting heightPadding = settingManager.register(new NumberSetting(this, "Height Padding", 5, 0, 10));
   public final CheckboxSetting rightLine = settingManager.register(new CheckboxSetting(this, "Right Line", true));
   public final CheckboxSetting background = settingManager.register(new CheckboxSetting(this, "Background", true));
   public final CheckboxSetting backgroundShadow = settingManager.register(new CheckboxSetting(this, "Background Shadow", true));
   public final ColorSetting status = settingManager.register(new ColorSetting(this, "Status", -1));
   public final ColorSetting backgroundColor = settingManager.register(new ColorSetting(this, "Background", 0x80000000) {
      @Override
      public boolean shouldShow() {
         return ArrayList.this.background.isEnabled();
      }
   });

   public ArrayList() {
      super(Category.VISUAL, "ArrayList", "");
      this.setEnabled(true);
   }
}
