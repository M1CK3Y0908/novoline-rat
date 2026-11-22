package client.m1ck3y.novoline.features.module.modules.hud;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gui.GuiScreen;
import client.m1ck3y.novoline.Novoline;
import client.m1ck3y.novoline.ui.BackgroundManager;
import client.m1ck3y.novoline.ui.BackgroundManager.Entry;
import client.m1ck3y.novoline.ui.BackgroundManager.Record;
import client.m1ck3y.novoline.ui.clickgui.ClickGuiScreen;
import client.m1ck3y.novoline.ui.menu.MenuClickGuiScreen;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.features.module.Module;
import client.m1ck3y.novoline.setting.CheckboxSetting;
import client.m1ck3y.novoline.setting.ColorSetting;
import client.m1ck3y.novoline.setting.ComboSetting;
import client.m1ck3y.novoline.setting.MultiComboSetting;
import client.m1ck3y.novoline.setting.NumberSetting;
import client.m1ck3y.novoline.utils.time.Stopwatch;

public class ClickGui extends Module {
   public Entry entry;
   public final ComboSetting clickGui = settingManager.register(new ComboSetting(this, "Mode", 0, "Dropdown", "NeverLose") {
      @Override
      public void interactCallback() {
         ClickGui.this.onEnable();
      }
   });
   public final CheckboxSetting moduleAnimation = settingManager.register(new CheckboxSetting(this, "Module Animation", true) {
      @Override
      public boolean shouldShow() {
         return ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   });
   public final MultiComboSetting glowSettings = settingManager.register((new MultiComboSetting(this, "Glow settings") {
      @Override
      public boolean shouldShow() {
         return !ClickGui.this.performanceMode.isEnabled() && ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   }).add("Features", true).add("Settings", true).add("Panels", true));
   public final MultiComboSetting panelsSettings = settingManager.register((new MultiComboSetting(this, "Panels settings") {
      @Override
      public boolean shouldShow() {
         return !ClickGui.this.performanceMode.isEnabled() && ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   }).add("Blur", true).add("Shadow", true));
   public final MultiComboSetting backgroundSettings = settingManager.register((new MultiComboSetting(this, "Background settings") {
      @Override
      public boolean shouldShow() {
         return !ClickGui.this.performanceMode.isEnabled() && ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   }).add("Blur", true).add("Desaturation", false).add("Anime", true));
   public NumberSetting imageScale = new NumberSetting(this, "Image Scale", 1.2F, 1.0F, 2.0F) {
      @Override
      public boolean shouldShow() {
         return ClickGui.this.backgroundSettings.isSelected("Anime") && ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   };
   public CheckboxSetting randomImage = new CheckboxSetting(this, "Random Image", false) {
      @Override
      public boolean shouldShow() {
         return ClickGui.this.backgroundSettings.isSelected("Anime") && ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   };
   public final CheckboxSetting performanceMode = settingManager.register(new CheckboxSetting(this, "Performance Mode", false) {
      @Override
      public boolean shouldShow() {
         return ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   });
   private final Stopwatch toggleTimer = new Stopwatch();
   public BackgroundManager backgroundManager;
   public ComboSetting image;

   @Override
   protected void onEnable() {
      this.toggleTimer.reset();

      GuiScreen guiScreen = switch (this.clickGui.getCurrentElement()) {
         case 0 -> {
            MenuClickGuiScreen menuClickGuiScreen = Novoline.getNovoline().getMenuClickGuiScreen();
            menuClickGuiScreen.open();
            yield menuClickGuiScreen;
         }
         case 1 -> {
            ClickGuiScreen clickGuiScreen = Novoline.getNovoline().getClickGuiScreen();
            clickGuiScreen.reset();
            yield clickGuiScreen;
         }
         default -> throw new IllegalArgumentException("Unsupported GUI mode");
      };
      if (this.randomImage.isEnabled()) {
         int i = ThreadLocalRandom.current().nextInt(0, this.backgroundManager.getLinkedHashMap().size() - 1);
         this.entry = this.backgroundManager.getEntryAt(i);
      } else {
         this.entry = this.backgroundManager.byAlias(this.image.getCurrentElementAsString());
      }



      minecraft.displayGuiScreen(guiScreen);
   }

   public ClickGui() {
      super(Category.CLIENT, "ClickGui", "");
      this.setKeyBind(54);

      try {
         this.backgroundManager = new BackgroundManager();
         this.image = new ComboSetting(
            this,
            "Image",
            0,
            this.backgroundManager.getLinkedHashMap().values().stream().map(entry2 -> entry2.getMetadata().getAlias()).toArray(String[]::new)
         ) {
            @Override
            public boolean shouldShow() {
               return ClickGui.this.backgroundSettings.isSelected("Anime")
                  && !ClickGui.this.randomImage.isEnabled()
                  && ClickGui.this.clickGui.getCurrentElement() == 0;
            }
         };
         settingManager.register(this.image);
         settingManager.register(this.imageScale);
         settingManager.register(this.randomImage);
      } catch (Throwable throwable) {
         Novoline.taggedLogger.error(throwable, "Couldn't load anime images");
         throw new RuntimeException(throwable);
      }
   }

   @Override
   protected void onDisable() {
      minecraft.displayGuiScreen(null);
   }
}
