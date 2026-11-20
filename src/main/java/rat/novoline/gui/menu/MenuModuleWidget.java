package rat.novoline.gui.menu;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import rat.novoline.Novoline;
import rat.novoline.enums.WidgetType;
import rat.novoline.gui.TextSelection;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ColorSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.ModuleSetting;
import rat.novoline.setting.MultiComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.setting.Setting.FeatureType;
import rat.novoline.utils.math.InterpolationUtils;
import rat.novoline.utils.math.SmoothingUtils;
import rat.novoline.utils.notification.Notification;
import rat.novoline.utils.notification.NotificationType;
import rat.novoline.utils.render.RenderUtils;
import rat.novoline.utils.render.shader.FloatSupplier;
import rat.novoline.utils.render.shape.IRectRenderer;

public class MenuModuleWidget implements MenuWidget, ExpandableWidget {
   private boolean awaitingKeyBind;
   private MenuClickGuiScreen menuClickGuiScreen;
   public Module module;
   private float bottomY;
   private float x;
   private float y;
   private boolean expanded;
   private float expandProgress = 0.0F;

   public boolean isAnimating() {
      if (!ModuleManager.clickGui.moduleAnimation.isEnabled()) {
         return false;
      }
      float target = this.expanded ? 1.0F : 0.0F;
      if (this.expandProgress != target) {
         return true;
      }
      for (MenuSettingWidget menuSettingWidget : this.settingWidgets) {
         if (menuSettingWidget.getModuleSetting().shouldShow() && menuSettingWidget.isAnimating()) {
            return true;
         }
      }
      return false;
   }
   private static final Minecraft minecraft = Minecraft.getMinecraft();
   public final List<MenuSettingWidget> settingWidgets;
   private int labelColor;
   private float arrowOffset;
   private static final Novoline novoline = Novoline.getNovoline();

   public MenuModuleWidget(MenuClickGuiScreen menuClickGuiScreen2, Module module2, float f, float f2) {
      this.menuClickGuiScreen = menuClickGuiScreen2;
      this.module = module2;
      this.x = f;
      this.y = f2;
      this.settingWidgets = new ArrayList<>();
      this.labelColor = -2039584;
      Novoline.getNovoline()
         .getSettingManager()
         .getSettings()
         .stream()
         .filter(setting2 -> setting2.getSettingType().equals(FeatureType.FEATURE) && ((ModuleSetting)setting2).getModule().getClass().equals(module2.getClass()))
         .map(setting2 -> (ModuleSetting)setting2)
         .forEach(moduleSetting -> {
            if (moduleSetting instanceof CheckboxSetting) {
               this.settingWidgets.add(new MenuCheckboxWidget(menuClickGuiScreen2, moduleSetting));
            } else if (moduleSetting instanceof NumberSetting) {
               this.settingWidgets.add(new MenuSliderWidget(menuClickGuiScreen2, moduleSetting));
            } else if (moduleSetting instanceof ComboSetting) {
               this.settingWidgets.add(new MenuComboWidget(menuClickGuiScreen2, moduleSetting));
            } else if (moduleSetting instanceof MultiComboSetting) {
               this.settingWidgets.add(new MenuMultiComboWidget(menuClickGuiScreen2, moduleSetting));
            } else if (moduleSetting instanceof ColorSetting) {
               this.settingWidgets.add(new MenuColorWidget(menuClickGuiScreen2, moduleSetting));
            }
         });
      this.layoutSettings();
   }

   private void layoutSettings() {
      float f = this.y + getHeaderHeight();

      for (MenuSettingWidget menuSettingWidget : this.settingWidgets) {
         if (menuSettingWidget.getModuleSetting().shouldShow()) {
            menuSettingWidget.setX(this.x);
            menuSettingWidget.setY(f);
            f += menuSettingWidget.getHeight();
         }
      }

      this.bottomY = f;
   }

   public float getBottomY() {
      return this.y + getHeaderHeight() + this.getSettingsHeight() * this.expandProgress;
   }

   public float getSettingsHeight() {
      float f4 = 0.0F;
      for (MenuSettingWidget menuSettingWidget : this.settingWidgets) {
         if (menuSettingWidget.getModuleSetting().shouldShow()) {
            f4 += menuSettingWidget.getHeight();
         }
      }
      return f4;
   }

   public float getSettingsHeight(float f, float f2, float f3) {
      float f4 = 0.0F;
      for (MenuSettingWidget menuSettingWidget : this.settingWidgets) {
         if (menuSettingWidget.getModuleSetting().shouldShow()) {
            f4 += menuSettingWidget.getTotalHeight(f, f2, f3);
         }
      }
      return f4;
   }

   public float getTotalHeight(float f, float f2, float f3) {
      float totalSettingsHeight = this.getSettingsHeight(f, f2, f3);

      if (ModuleManager.clickGui.moduleAnimation.isEnabled()) {
         float target = this.expanded ? 1.0F : 0.0F;
         this.expandProgress = SmoothingUtils.approachWithMinimumStep(this.expandProgress, target, 16.0F, 0.001F);
         if (Math.abs(this.expandProgress - target) <= 0.005F) {
            this.expandProgress = target;
         }
         this.expandProgress = MathHelper.clamp_float(this.expandProgress, 0.0F, 1.0F);
      } else {
         this.expandProgress = this.expanded ? 1.0F : 0.0F;
      }

      return getHeaderHeight() + totalSettingsHeight * this.expandProgress;
   }

   public boolean isExpanded() {
      return this.expanded;
   }

   public void setPosition(float f, float f2) {
      this.x = f;
      this.y = f2;
      this.layoutSettings();
   }

   public float getY() {
      return this.y;
   }

   @Override
   public void render(float f, float f2, float f3) {
      if (this.settingWidgets.size() > 0) {
         this.arrowOffset = SmoothingUtils.approachWithMinimumStep(this.arrowOffset, this.expanded ? 2.5F : -2.5F, 20.0F, 0.01F);
         GlStateManager.enableBlend();
         GlStateManager.disableTexture2D();
         RenderUtils.applyBlendFunc();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 0.8F);
         float f4 = this.x + getWidth() - 4.0F;
         float f5 = this.y + 7.0F;
         GL11.glLineWidth(2.0F);
         GL11.glEnable(2848);
         GL11.glBegin(3);
         GL11.glVertex2f(f4, f5 + this.arrowOffset / 2.0F);
         GL11.glVertex2f(f4 - 2.0F, f5 - this.arrowOffset / 2.0F);
         GL11.glVertex2f(f4 - 4.0F, f5 + this.arrowOffset / 2.0F);
         GL11.glEnd();
         GL11.glDisable(2848);
         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
      }

      if (this.expandProgress > 0.001F) {
         this.layoutSettings();
         float settingsVisibleHeight = this.getSettingsHeight(f, f2, f3) * this.expandProgress;
         RenderUtils.enableScissorDoubleScale(this.x, this.y + getHeaderHeight(), getWidth(), settingsVisibleHeight);

         for (MenuSettingWidget menuSettingWidget : this.settingWidgets) {
            if (menuSettingWidget.getModuleSetting().shouldShow()) {
               menuSettingWidget.render(f, f2, f3);
            }
         }

         RenderUtils.disableScissor();

         if (!this.settingWidgets.isEmpty() && settingsVisibleHeight >= 4.0F) {
            RenderUtils.drawVerticalGradientRect(
               this.x, this.y + getHeaderHeight(), this.x + getWidth(), this.y + getHeaderHeight() + 4.0F, 1610612736, 3092790
            );
            float visibleBottom = this.y + getHeaderHeight() + settingsVisibleHeight;
            RenderUtils.drawVerticalGradientRect(
               this.x, visibleBottom - 4.0F, this.x + getWidth(), visibleBottom, 3092790, 1610612736
            );
         }
      }
   }

   public static float getHeaderHeight() {
      return 14.0F;
   }

   @Override
   public void tick() {
   }

   public List<MenuSettingWidget> getSettingWidgets() {
      return this.settingWidgets;
   }

   @Override
   public void handleMouseClick(float f, float f2, int i) {
      if (f > this.x && f < this.x + getWidth() && f2 >= this.y && f2 < this.y + getHeaderHeight()) {
         if (i == 0) {
            if (this.module.canBeToggled()) {
               this.module.toggle();
            }
         } else if (i == 1) {
            this.expanded = !this.expanded;
         } else if (i == 2 && this.module.canBeToggled()) {
            this.menuClickGuiScreen.buttons.stream().flatMap(menuCategoryPanel -> menuCategoryPanel.moduleWidgets.stream()).forEach(menuModuleWidget -> menuModuleWidget.awaitingKeyBind = false);
            this.awaitingKeyBind = true;
         }
         return;
      }

      float visibleBottom = this.y + getHeaderHeight() + this.getSettingsHeight() * this.expandProgress;
      if (this.expandProgress > 0.01F && f2 >= this.y + getHeaderHeight() && f2 <= visibleBottom) {
         for (MenuSettingWidget menuSettingWidget : this.settingWidgets) {
            if (menuSettingWidget.getModuleSetting().shouldShow()) {
               menuSettingWidget.handleMouseClick(f, f2, i);
            }
         }
      }
   }

   public void addRects(IRectRenderer iRectRenderer, float f) {
      TextSelection textSelection = this.menuClickGuiScreen.getTextSelection();
      String text = textSelection.getTextBuffer().getText();
      if (textSelection.isVisible() && text.length() >= 3 && this.module.getName().toLowerCase(Locale.ROOT).contains(text)) {
         Color color2 = new Color(this.menuClickGuiScreen.getClickGuiTheme().getModuleBackgroundColor());
         iRectRenderer.addRect(
            this.x,
            this.y,
            getWidth(),
            getHeaderHeight(),
            RenderUtils.lerpColor(color2.getRGB(), color2.brighter().brighter().getRGB(), (float)InterpolationUtils.oscillate(0.3, 1.0, 750L))
         );
      } else {
         iRectRenderer.addRect(this.x, this.y, getWidth(), getHeaderHeight(), this.menuClickGuiScreen.getClickGuiTheme().getModuleBackgroundColor());
      }

      if (this.expandProgress > 0.001F) {
         float totalSettingsHeight = this.getSettingsHeight();
         float settingsVisibleHeight = totalSettingsHeight * this.expandProgress;
         if (settingsVisibleHeight > 0.5F) {
            iRectRenderer.addRect(
               this.x,
               this.y + getHeaderHeight(),
               getWidth(),
               settingsVisibleHeight,
               this.menuClickGuiScreen.getClickGuiTheme().getSettingBackgroundColor()
            );
         }
      }
   }

   public static float getWidth() {
      return MenuCategoryPanel.getWidth();
   }

   @Override
   public boolean keyTyped(char c, int i) {
      if (this.awaitingKeyBind) {
         this.awaitingKeyBind = false;
         boolean flag = false;
         if (i == 1) {
            this.module.setKeyBind(0);
            flag = true;
         } else {
            this.module.setKeyBind(i);
         }

         novoline.getNotificationManager()
            .show(
               new Notification(
                   NotificationType.INFO,
                   "Click GUI",
                   "Feature " + this.module.getName() + " was " + (flag ? "unbound" : "bound to " + Keyboard.getKeyName(i)),
                   3000L
                )
             );
          return false;
       } else {
          return true;
       }
    }

   public float getX() {
      return this.x;
   }

   public void setX(float f) {
      this.x = f;
   }

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
      if (this.expandProgress > 0.01F) {
         Iterator iterator2 = this.settingWidgets.iterator();

         while (iterator2.hasNext()) {
            ((MenuWidget)iterator2.next()).handleMouseRelease(f, f2, i);
         }
      }
   }

   @Override
   public void reset() {
      Iterator iterator2 = this.settingWidgets.iterator();

      while (iterator2.hasNext()) {
         ((MenuWidget)iterator2.next()).reset();
      }

      this.awaitingKeyBind = false;
   }

   public void setY(float f) {
      this.y = f;
   }

   @Override
   public void renderText(FloatSupplier floatSupplier, WidgetType widgetType, float f, boolean flag) {
      this.labelColor = RenderUtils.smoothBlendColors(
         this.labelColor, this.module.isEnabled() ? this.menuClickGuiScreen.getClickGuiTheme().getSecondaryColor(true) : -2039584, 14.0F
      );
      String text = this.awaitingKeyBind ? ">> Press any key <<" : this.module.getName();
      if (!this.awaitingKeyBind && Keyboard.isKeyDown(29) && this.module.getKeyBind() != 0) {
         text = text + " [" + Keyboard.getKeyName(this.module.getKeyBind()) + "]";
      }

      if (widgetType == WidgetType.FEATURE && this.module.isEnabled() == flag) {
         floatSupplier.drawStringCentered(text, this.x + getWidth() / 2.0F, this.y + getHeaderHeight() / 2.0F + 1.0F, this.labelColor);
      }

      if (widgetType == WidgetType.REGULAR && this.expandProgress > 0.001F) {
         floatSupplier.flush();
         float settingsVisibleHeight = this.getSettingsHeight() * this.expandProgress;
         RenderUtils.enableScissorDoubleScale(this.x, this.y + getHeaderHeight(), getWidth(), settingsVisibleHeight);

         for (MenuSettingWidget menuSettingWidget : this.settingWidgets) {
            if (menuSettingWidget.getModuleSetting().shouldShow()) {
               menuSettingWidget.renderText(floatSupplier, widgetType, f, flag);
            }
         }

         floatSupplier.flush();
         RenderUtils.disableScissor();
      }
   }
}
