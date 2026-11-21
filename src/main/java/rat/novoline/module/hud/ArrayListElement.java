package rat.novoline.module.hud;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.MultiComboSetting.Option;
import rat.novoline.utils.math.SmoothingUtils;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.utils.render.RenderUtils;

public class ArrayListElement extends HudElement {
   public static ArrayListElement instance;
   private float width;
   private float height;
   private ScaledResolution scaledResolution;

   public static class Entry {
      public Module module;
      public String name;
      public String suffix;
      public float x;
      public float y;
      public float width;
      public float nameWidth;
      public int nameColor;
      public int suffixColor;
      public boolean hasSuffix;
      public boolean animating;
   }

   private static final Entry[] ENTRIES = new Entry[128];
   static {
      for (int i = 0; i < ENTRIES.length; i++) {
         ENTRIES[i] = new Entry();
      }
   }
   private static int entryCount = 0;
   private static float cachedRowHeight = 0.0F;
   private static float cachedPadX = 0.0F;
   private static float cachedPadY = 0.0F;
   private static float cachedLineWidth = 1.0F;
   private static boolean cachedIsLeft = false;

   public ArrayListElement() {
      super("arraylist", 0.0F, 0.0F, true);
      instance = this;
   }

   @Override
   public float getX() {
      return this.scaledResolution != null ? this.x + this.scaledResolution.getFloat() - this.getWidth() : 0.0F;
   }

   @Override
   public float getY() {
      return this.y;
   }

   @Override
   public float getWidth() {
      return this.width;
   }

   @Override
   public float getHeight() {
      return this.height;
   }

   public static boolean isFilteredOut(boolean hideVisuals, boolean boundOnly, Module module) {
      if (module instanceof ArrayList || module instanceof Watermark || module instanceof Notifications
            || module instanceof TargetHUD || module instanceof PostProcessing || module instanceof ClickGui) {
         return true;
      }
      return (boundOnly && module.getKeyBind() == 0)
            || (hideVisuals && module.getCategory() == Category.VISUAL)
            || module.getCategory() == Category.CLIENT;
   }

   public static void updatePositions(ScaledResolution sr) {
      if (Minecraft.getMinecraft().gameSettings.showDebugInfo || ModuleManager.arrayList == null || !ModuleManager.arrayList.isEnabled()) {
         entryCount = 0;
         return;
      }
      if (instance != null) {
         instance.scaledResolution = sr;
      }
      ArrayList arrayList2 = ModuleManager.arrayList;
      FontAdapter font = arrayList2.getFont();
      float widthPadding = arrayList2.widthPadding.getCurrentValue();
      float heightPadding = arrayList2.heightPadding.getCurrentValue();
      float lineWidth = arrayList2.lineWidth.getCurrentValue();
      float rowHeight = font.getHeight() + heightPadding;
      float textPadY = (heightPadding + font.getHeight() / 2.0F) / 2.0F;
      float textPadX = widthPadding / 2.0F;

      boolean drawTop = arrayList2.outline.isSelected("Top");
      boolean drawBottom = arrayList2.outline.isSelected("Bottom");
      boolean drawLeft = arrayList2.outline.isSelected("Left");
      boolean drawRight = arrayList2.outline.isSelected("Right");

      float baseY = (instance != null ? instance.getY() : 0.0F) + (drawTop ? lineWidth : 0.0F);
      boolean isBottom = sr.getFloat2() / 2.0F < baseY;
      List<Option> elements = arrayList2.arrayList.getElements();
      boolean hideStatus = elements.get(0).isSelected();
      boolean hideVisuals = elements.get(1).isSelected();
      boolean boundOnly = elements.get(2).isSelected();
      List<Module> list = isBottom ? ModuleManager.modulesByWidthReversed : ModuleManager.modulesByWidth;

      float baseX = instance != null ? instance.getX() : (sr.getFloat() - (instance != null ? instance.getWidth() : 0.0F));
      boolean isLeft = baseX < sr.getScaledWidth_double() / 2.0;
      float rightEdge = (instance != null ? instance.x : 0.0F) + sr.getFloat();

      cachedRowHeight = rowHeight;
      cachedPadX = textPadX;
      cachedPadY = textPadY;
      cachedLineWidth = lineWidth;
      cachedIsLeft = isLeft;

      float targetOffsetY = 0.0F;
      float totalHeight = 0.0F;
      float firstEntryWidth = 0.0F;
      entryCount = 0;

      float retractTarget = -3.0F - lineWidth;

      for (Module module : list) {
         if (isFilteredOut(hideVisuals, boundOnly, module)) {
            continue;
         }

         boolean isEnabled = module.isEnabled();
         float currentOffsetX = module.getArrayListOffsetX();

         // Fast skip: disabled module already fully retracted off-screen
         if (!isEnabled && currentOffsetX <= retractTarget) {
            continue;
         }

         float nameWidth = module.getNameWidth(font);
         String suffix = module.getSuffix();
         boolean hasSuffix = !hideStatus && suffix != null && !suffix.isEmpty();
         float suffixWidth = hasSuffix ? module.getSuffixWidth(font) : 0.0F;
         float totalEntryWidth = nameWidth + widthPadding + suffixWidth;

         if (isEnabled) {
            module.setArrayListOffsetY(SmoothingUtils.approachWithMinimumStep(module.getArrayListOffsetY(), targetOffsetY, 10.0F, 0.02F));
            module.setArrayListOffsetX(SmoothingUtils.approachWithMinimumStep(currentOffsetX, totalEntryWidth, 10.0F, 0.02F));
            targetOffsetY += rowHeight;
            totalHeight += rowHeight;
            if (firstEntryWidth == 0.0F) {
               firstEntryWidth = totalEntryWidth;
            }
         } else {
            module.setArrayListOffsetX(SmoothingUtils.approachWithMinimumStep(currentOffsetX, retractTarget, 10.0F, 0.02F));
         }

         float offsetX = module.getArrayListOffsetX();
         if (offsetX > -2.0F && entryCount < ENTRIES.length) {
            float rowX = isLeft ? baseX + offsetX - totalEntryWidth + (drawLeft ? lineWidth : 0.0F) : rightEdge - offsetX - (drawRight ? lineWidth : 0.0F);
            float rowY = baseY + module.getArrayListOffsetY();

            int rowIndex = entryCount;
            Entry e = ENTRIES[entryCount++];
            e.module = module;
            e.name = module.getName();
            e.suffix = suffix;
            e.hasSuffix = hasSuffix;
            e.x = rowX;
            e.y = rowY;
            e.width = totalEntryWidth;
            e.nameWidth = nameWidth;
            e.nameColor = ModuleManager.themeColor.getColor(rowX, rowY, rowIndex);
            e.suffixColor = arrayList2.status.getColor();
            boolean isAnimatingX = !isEnabled || Math.abs(offsetX - totalEntryWidth) > 1.0F;
            boolean isAnimatingY = isEnabled && Math.abs(module.getArrayListOffsetY() - (targetOffsetY - rowHeight)) > 1.0F;
            e.animating = isAnimatingX || isAnimatingY;
         }
      }

      if (instance != null) {
         instance.width = (firstEntryWidth > 0.0F ? firstEntryWidth : 60.0F) + (drawLeft ? lineWidth : 0.0F) + (drawRight ? lineWidth : 0.0F);
         instance.height = (totalHeight > 0.0F ? totalHeight : rowHeight) + (drawTop ? lineWidth : 0.0F) + (drawBottom ? lineWidth : 0.0F);
      }
   }

   public static void renderArrayListEffects(boolean bloom, ScaledResolution sr) {
      boolean glow = ModuleManager.postProcessing != null && ModuleManager.postProcessing.isGlowEnabled();
      renderArrayListEffects(bloom, glow, sr);
   }

   public static void renderArrayListEffects(boolean bloom, boolean glow, ScaledResolution sr) {
      if (Minecraft.getMinecraft().gameSettings.showDebugInfo || ModuleManager.arrayList == null || !ModuleManager.arrayList.isEnabled()) {
         return;
      }
      ArrayList arrayList2 = ModuleManager.arrayList;
      boolean bg = arrayList2.background.isEnabled();
      boolean drawTop = arrayList2.outline.isSelected("Top");
      boolean drawBottom = arrayList2.outline.isSelected("Bottom");
      boolean drawLeft = arrayList2.outline.isSelected("Left");
      boolean drawRight = arrayList2.outline.isSelected("Right");
      boolean hasOutline = drawTop || drawBottom || drawLeft || drawRight;

      if (!bg && !hasOutline) {
         return;
      }
      if (entryCount == 0) {
         return;
      }

      float rowHeight = cachedRowHeight;
      float lineWidth = cachedLineWidth;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer wr = tessellator.getWorldRenderer();

      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      RenderUtils.applyBlendFunc();

      if (bg) {
         if (!bloom || !glow) {
            RenderUtils.setColor(-16777216);
            wr.begin(7, DefaultVertexFormats.POSITION);
            for (int i = 0; i < entryCount; i++) {
               Entry e = ENTRIES[i];
               wr.pos(e.x, e.y + rowHeight, 0.0).endVertex();
               wr.pos(e.x + e.width, e.y + rowHeight, 0.0).endVertex();
               wr.pos(e.x + e.width, e.y, 0.0).endVertex();
               wr.pos(e.x, e.y, 0.0).endVertex();
            }
            tessellator.draw();
         } else {
            wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
            for (int i = 0; i < entryCount; i++) {
               Entry e = ENTRIES[i];
               int col = e.nameColor;
               float r = (col >> 16 & 0xFF) / 255.0F;
               float g = (col >> 8 & 0xFF) / 255.0F;
               float b = (col & 0xFF) / 255.0F;
               wr.pos(e.x, e.y + rowHeight, 0.0).color(r, g, b, 1.0F).endVertex();
               wr.pos(e.x + e.width, e.y + rowHeight, 0.0).color(r, g, b, 1.0F).endVertex();
               wr.pos(e.x + e.width, e.y, 0.0).color(r, g, b, 1.0F).endVertex();
               wr.pos(e.x, e.y, 0.0).color(r, g, b, 1.0F).endVertex();
            }
            tessellator.draw();
         }
      }

      if (hasOutline) {
         wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
         int defaultColor = -16777216;

         if (drawTop) {
            Entry e0 = ENTRIES[0];
            int col = (!bloom || !glow) ? defaultColor : e0.nameColor;
            float x1 = e0.x - (drawLeft ? lineWidth : 0.0F);
            float x2 = e0.x + e0.width + (drawRight ? lineWidth : 0.0F);
            float y1 = e0.y - lineWidth;
            float y2 = e0.y;
            drawSolidQuad(wr, x1, y1, x2, y2, col);
         }

         if (drawBottom) {
            Entry eLast = ENTRIES[entryCount - 1];
            int col = (!bloom || !glow) ? defaultColor : eLast.nameColor;
            float x1 = eLast.x - (drawLeft ? lineWidth : 0.0F);
            float x2 = eLast.x + eLast.width + (drawRight ? lineWidth : 0.0F);
            float y1 = eLast.y + rowHeight;
            float y2 = eLast.y + rowHeight + lineWidth;
            drawSolidQuad(wr, x1, y1, x2, y2, col);
         }

         if (drawLeft) {
            for (int i = 0; i < entryCount; i++) {
               Entry e = ENTRIES[i];
               int col = (!bloom || !glow) ? defaultColor : e.nameColor;
               float x1 = e.x - lineWidth;
               float x2 = e.x;
               float y1 = e.y;
               float y2 = e.y + rowHeight;
               drawSolidQuad(wr, x1, y1, x2, y2, col);

               if (!cachedIsLeft && i < entryCount - 1 && !e.animating && !ENTRIES[i + 1].animating) {
                  Entry next = ENTRIES[i + 1];
                  int nextCol = (!bloom || !glow) ? defaultColor : next.nameColor;
                  if (e.x < next.x) {
                     drawSolidQuad(wr, e.x - lineWidth, e.y + rowHeight, next.x - lineWidth, e.y + rowHeight + lineWidth, col);
                  } else if (e.x > next.x) {
                     drawSolidQuad(wr, next.x - lineWidth, e.y + rowHeight - lineWidth, e.x - lineWidth, e.y + rowHeight, nextCol);
                  }
               }
            }
         }

         if (drawRight) {
            for (int i = 0; i < entryCount; i++) {
               Entry e = ENTRIES[i];
               int col = (!bloom || !glow) ? defaultColor : e.nameColor;
               float x1 = e.x + e.width;
               float x2 = e.x + e.width + lineWidth;
               float y1 = e.y;
               float y2 = e.y + rowHeight;
               drawSolidQuad(wr, x1, y1, x2, y2, col);

               if (cachedIsLeft && i < entryCount - 1 && !e.animating && !ENTRIES[i + 1].animating) {
                  Entry next = ENTRIES[i + 1];
                  int nextCol = (!bloom || !glow) ? defaultColor : next.nameColor;
                  float rCurr = e.x + e.width;
                  float rNext = next.x + next.width;
                  if (rCurr > rNext) {
                     drawSolidQuad(wr, rNext + lineWidth, e.y + rowHeight, rCurr + lineWidth, e.y + rowHeight + lineWidth, col);
                  } else if (rCurr < rNext) {
                     drawSolidQuad(wr, rCurr + lineWidth, e.y + rowHeight - lineWidth, rNext + lineWidth, e.y + rowHeight, nextCol);
                  }
               }
            }
         }

         tessellator.draw();
      }

      GlStateManager.enableTexture2D();
   }

   @Override
   public void draw(ScaledResolution scaledResolution2, float f, boolean flag) {
      if (Minecraft.getMinecraft().gameSettings.showDebugInfo || !ModuleManager.arrayList.isEnabled()) {
         return;
      }
      this.scaledResolution = scaledResolution2;
      if (entryCount == 0) {
         return;
      }

      ArrayList arrayList2 = ModuleManager.arrayList;
      float rowHeight = cachedRowHeight;
      float padX = cachedPadX;
      float padY = cachedPadY;
      float lineWidth = cachedLineWidth;
      boolean isLeft = cachedIsLeft;
      boolean drawBackground = arrayList2.background.isEnabled();
      boolean drawTop = arrayList2.outline.isSelected("Top");
      boolean drawBottom = arrayList2.outline.isSelected("Bottom");
      boolean drawLeft = arrayList2.outline.isSelected("Left");
      boolean drawRight = arrayList2.outline.isSelected("Right");
      boolean hasOutline = drawTop || drawBottom || drawLeft || drawRight;

      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer wr = tessellator.getWorldRenderer();

      // Pass 1: Background Rectangles (single batched draw call)
      if (drawBackground) {
         int bgCol = arrayList2.backgroundColor.getColor();
         RenderUtils.setColor(bgCol);
         GlStateManager.disableTexture2D();
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         wr.begin(7, DefaultVertexFormats.POSITION);
         for (int i = 0; i < entryCount; i++) {
            Entry e = ENTRIES[i];
            wr.pos(e.x, e.y + rowHeight, 0.0).endVertex();
            wr.pos(e.x + e.width, e.y + rowHeight, 0.0).endVertex();
            wr.pos(e.x + e.width, e.y, 0.0).endVertex();
            wr.pos(e.x, e.y, 0.0).endVertex();
         }
         tessellator.draw();
      }

      // Pass 2: Outline (single batched draw call)
      if (hasOutline) {
         GlStateManager.disableTexture2D();
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         wr.begin(7, DefaultVertexFormats.POSITION_COLOR);

         // 1. Top Outline
         if (drawTop) {
            Entry e0 = ENTRIES[0];
            float x1 = e0.x - (drawLeft ? lineWidth : 0.0F);
            float x2 = e0.x + e0.width + (drawRight ? lineWidth : 0.0F);
            float y1 = e0.y - lineWidth;
            float y2 = e0.y;
            drawSolidQuad(wr, x1, y1, x2, y2, e0.nameColor);
         }

         // 2. Bottom Outline
         if (drawBottom) {
            Entry eLast = ENTRIES[entryCount - 1];
            float x1 = eLast.x - (drawLeft ? lineWidth : 0.0F);
            float x2 = eLast.x + eLast.width + (drawRight ? lineWidth : 0.0F);
            float y1 = eLast.y + rowHeight;
            float y2 = eLast.y + rowHeight + lineWidth;
            drawSolidQuad(wr, x1, y1, x2, y2, eLast.nameColor);
         }

         // 3. Left Outline
         if (drawLeft) {
            for (int i = 0; i < entryCount; i++) {
               Entry e = ENTRIES[i];
               float x1 = e.x - lineWidth;
               float x2 = e.x;
               float y1 = e.y;
               float y2 = e.y + rowHeight;
               drawSolidQuad(wr, x1, y1, x2, y2, e.nameColor);

               if (!isLeft && i < entryCount - 1 && !e.animating && !ENTRIES[i + 1].animating) {
                  Entry next = ENTRIES[i + 1];
                  if (e.x < next.x) {
                     // Current row is wider to the left; step is below current row
                     float sx1 = e.x - lineWidth;
                     float sx2 = next.x - lineWidth;
                     float sy1 = e.y + rowHeight;
                     float sy2 = e.y + rowHeight + lineWidth;
                     drawSolidQuad(wr, sx1, sy1, sx2, sy2, e.nameColor);
                  } else if (e.x > next.x) {
                     // Next row is wider to the left; step is above next row
                     float sx1 = next.x - lineWidth;
                     float sx2 = e.x - lineWidth;
                     float sy1 = e.y + rowHeight - lineWidth;
                     float sy2 = e.y + rowHeight;
                     drawSolidQuad(wr, sx1, sy1, sx2, sy2, next.nameColor);
                  }
               }
            }
         }

         // 4. Right Outline
         if (drawRight) {
            for (int i = 0; i < entryCount; i++) {
               Entry e = ENTRIES[i];
               float x1 = e.x + e.width;
               float x2 = e.x + e.width + lineWidth;
               float y1 = e.y;
               float y2 = e.y + rowHeight;
               drawSolidQuad(wr, x1, y1, x2, y2, e.nameColor);

               if (isLeft && i < entryCount - 1 && !e.animating && !ENTRIES[i + 1].animating) {
                  Entry next = ENTRIES[i + 1];
                  float rCurr = e.x + e.width;
                  float rNext = next.x + next.width;
                  if (rCurr > rNext) {
                     // Current row is wider to the right; step is below current row
                     float sx1 = rNext + lineWidth;
                     float sx2 = rCurr + lineWidth;
                     float sy1 = e.y + rowHeight;
                     float sy2 = e.y + rowHeight + lineWidth;
                     drawSolidQuad(wr, sx1, sy1, sx2, sy2, e.nameColor);
                  } else if (rCurr < rNext) {
                     // Next row is wider to the right; step is above next row
                     float sx1 = rCurr + lineWidth;
                     float sx2 = rNext + lineWidth;
                     float sy1 = e.y + rowHeight - lineWidth;
                     float sy2 = e.y + rowHeight;
                     drawSolidQuad(wr, sx1, sy1, sx2, sy2, next.nameColor);
                  }
               }
            }
         }

         tessellator.draw();
      }

      // Pass 3: Text Rendering
      GlStateManager.enableTexture2D();
      FontAdapter font = arrayList2.getFont();
      for (int i = 0; i < entryCount; i++) {
         Entry e = ENTRIES[i];
         font.drawStringWithShadow(e.name, e.x + padX, e.y + padY, e.nameColor);
         if (e.hasSuffix) {
            font.drawStringWithShadow(e.suffix, e.x + padX + e.nameWidth, e.y + padY, e.suffixColor);
         }
      }

      GlStateManager.disableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private static void drawSolidQuad(WorldRenderer wr, float x1, float y1, float x2, float y2, int col) {
      float minX = Math.min(x1, x2);
      float maxX = Math.max(x1, x2);
      float minY = Math.min(y1, y2);
      float maxY = Math.max(y1, y2);
      float r = (col >> 16 & 0xFF) / 255.0F;
      float g = (col >> 8 & 0xFF) / 255.0F;
      float b = (col & 0xFF) / 255.0F;
      float a = (col >> 24 & 0xFF) / 255.0F;
      wr.pos(minX, maxY, 0.0).color(r, g, b, a).endVertex();
      wr.pos(maxX, maxY, 0.0).color(r, g, b, a).endVertex();
      wr.pos(maxX, minY, 0.0).color(r, g, b, a).endVertex();
      wr.pos(minX, minY, 0.0).color(r, g, b, a).endVertex();
   }
}
