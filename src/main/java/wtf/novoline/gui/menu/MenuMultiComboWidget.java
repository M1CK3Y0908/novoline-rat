package wtf.novoline.gui.menu;

import net.minecraft.util.MathHelper;
import wtf.novoline.enums.WidgetType;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.ModuleSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.MultiComboSetting.Option;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.shader.FloatSupplier;

public class MenuMultiComboWidget extends MenuSettingWidget {
   private static final float OPTION_HEIGHT = 10.0F;
   private boolean open;
   private float expandProgress = 0.0F;

   public boolean isOpen() {
      return this.open;
   }

   public void setOpen(boolean open) {
      this.open = open;
   }

   @Override
   public boolean isAnimating() {
      if (!ModuleManager.clickGui.moduleAnimation.isEnabled()) {
         return false;
      }
      float target = this.open ? 1.0F : 0.0F;
      return this.expandProgress != target;
   }

   @Override
   public void render(float f, float f2, float f3) {
      MultiComboSetting multiComboSetting = (MultiComboSetting)this.moduleSetting;
      int secondaryColor = this.menuClickGuiScreen.getClickGuiTheme().getSecondaryColor(false);
      float totalOptionsHeight = multiComboSetting.getElements().size() * OPTION_HEIGHT;
      float optionsVisibleHeight = totalOptionsHeight * this.expandProgress;

      if (this.expandProgress > 0.001F) {
         float f4 = this.y + 16.5F;
         RenderUtils.enableScissorDoubleScale(this.x + 6.0F, f4, this.getWidth() - 12.0F, optionsVisibleHeight);

         Option[] options = multiComboSetting.getElements().toArray(new Option[0]);
         RenderUtils.drawRect(
            this.x + 4.0F + 2.0F, f4, this.x + this.getWidth() - 4.0F - 2.0F, f4 + totalOptionsHeight, RenderUtils.withAlpha(secondaryColor, 68)
         );

         RenderUtils.disableScissor();

         float bottomY = f4 + optionsVisibleHeight;
         RenderUtils.drawVerticalGradientRect(this.x + 6.0F, bottomY - 3.0F, this.x + this.getWidth() - 6.0F, bottomY, 3092790, 1610612736);
      }

      RenderUtils.drawVerticalGradientRect(this.x + 4.0F, this.y + 16.5F, this.x + this.getWidth() - 4.0F, this.y + 16.5F + 4.0F, 1610612736, 3092790);
      RenderUtils.drawVerticalGradientRect(this.x + 4.0F, this.y + 3.5F - 6.0F, this.x + this.getWidth() - 4.0F, this.y + 3.5F, 3092790, 721420288);
      RenderUtils.drawRect(this.x + 4.0F, this.y + 3.5F, this.x + 6.0F, this.y + 16.5F, secondaryColor);
      RenderUtils.drawRect(this.x + this.getWidth() - 6.0F, this.y + 3.5F, this.x + this.getWidth() - 4.0F, this.y + 16.5F, secondaryColor);
   }

   @Override
   public float getTotalHeight(float f, float f2, float f3) {
      int size2 = ((MultiComboSetting)this.moduleSetting).getElements().size();
      float totalOptionsHeight = size2 * OPTION_HEIGHT;

      if (ModuleManager.clickGui.moduleAnimation.isEnabled()) {
         float target = this.open ? 1.0F : 0.0F;
         this.expandProgress = SmoothingUtils.approachWithMinimumStep(this.expandProgress, target, 16.0F, 0.001F);
         if (Math.abs(this.expandProgress - target) <= 0.005F) {
            this.expandProgress = target;
         }
         this.expandProgress = MathHelper.clamp_float(this.expandProgress, 0.0F, 1.0F);
      } else {
         this.expandProgress = this.open ? 1.0F : 0.0F;
      }

      return 20.0F + totalOptionsHeight * this.expandProgress;
   }

   @Override
   public float getHeight() {
      int size2 = ((MultiComboSetting)this.moduleSetting).getElements().size();
      return 20.0F + size2 * OPTION_HEIGHT * this.expandProgress;
   }

   @Override
   public void handleMouseClick(float f, float f2, int i) {
      if (f > this.x + 4.5F && f < this.x + this.getWidth() - 4.5F && f2 > this.y + 3.5F && f2 < this.y + 16.5F) {
         if (i == 0) {
            this.open = !this.open;
         }
      } else if (this.expandProgress > 0.01F && i == 0) {
         MultiComboSetting multiComboSetting = (MultiComboSetting)this.moduleSetting;
         float f3 = this.y + 16.5F;
         float totalOptionsHeight = multiComboSetting.getElements().size() * OPTION_HEIGHT;
         float visibleHeight = totalOptionsHeight * this.expandProgress;

         if (f2 > f3 && f2 < f3 + visibleHeight) {
            for (int i2 = 0; i2 < multiComboSetting.getElements().size(); i2++) {
               if (f > this.x + 4.5F && f < this.x + this.getWidth() - 4.5F && f2 > f3 && f2 < f3 + OPTION_HEIGHT) {
                  multiComboSetting.getElements().get(i2).toggleSelection();
                  break;
               }

               f3 += OPTION_HEIGHT;
            }
         }
      }
   }

   @Override
   public void reset() {
   }

   @Override
   public void handleMouseRelease(float f, float f2, int i) {
   }

   @Override
   public boolean keyTyped(char c, int i) {
      return true;
   }

   public MenuMultiComboWidget(MenuClickGuiScreen menuClickGuiScreen2, ModuleSetting moduleSetting2) {
      super(menuClickGuiScreen2, moduleSetting2);
   }

   @Override
   public void tick() {
   }

   @Override
   public void renderText(FloatSupplier floatSupplier, WidgetType widgetType, float f, boolean flag) {
      if (widgetType == WidgetType.REGULAR) {
         int textColor = this.menuClickGuiScreen.getClickGuiTheme().getTextColor();
         int secondaryColor = this.menuClickGuiScreen.getClickGuiTheme().getSecondaryColor(true);
         MultiComboSetting multiComboSetting = (MultiComboSetting)this.moduleSetting;
         if (!flag) {
            floatSupplier.drawStringTopCentered(this.moduleSetting.getName(), this.x + this.getWidth() / 2.0F, this.y + 9.0F, textColor);
         }

         if (this.expandProgress > 0.001F) {
            float f2 = this.y + 16.5F;
            float totalOptionsHeight = multiComboSetting.getElements().size() * OPTION_HEIGHT;
            float optionsVisibleHeight = totalOptionsHeight * this.expandProgress;

            floatSupplier.flush();
            RenderUtils.enableScissorDoubleScale(this.x + 6.0F, f2, this.getWidth() - 12.0F, optionsVisibleHeight);

            for (Option option : multiComboSetting.getElements().toArray(new Option[0])) {
               String name = option.getName();
               if (option.isSelected() == flag) {
                  float f3 = this.x + this.getWidth() / 2.0F;
                  float f4 = f2 + 5.0F + 1.0F;
                  int i = option.isSelected() ? secondaryColor : textColor;
                  floatSupplier.drawStringCentered(name, f3, f4, i);
               }

               f2 += OPTION_HEIGHT;
            }

            floatSupplier.flush();
            RenderUtils.disableScissor();
         }
      }
   }
}
