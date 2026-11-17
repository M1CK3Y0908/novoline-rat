package wtf.novoline.module.hud;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.MultiComboSetting.Option;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.FontAdapter;
import wtf.novoline.utils.render.RenderUtils;

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
      float baseY = instance != null ? instance.getY() : 0.0F;
      boolean isBottom = sr.getFloat2() / 2.0F < baseY;
      List<Option> elements = arrayList2.arrayList.getElements();
      boolean hideStatus = elements.get(0).isSelected();
      boolean hideVisuals = elements.get(1).isSelected();
      boolean boundOnly = elements.get(2).isSelected();
      FontAdapter font = arrayList2.getFont();
      List<Module> list = isBottom ? ModuleManager.modulesByWidthReversed : ModuleManager.modulesByWidth;

      float widthPadding = arrayList2.widthPadding.getCurrentValue();
      float heightPadding = arrayList2.heightPadding.getCurrentValue();
      float rightLineWidth = arrayList2.rightLineWidth.getCurrentValue();
      float rowHeight = font.getHeight() + heightPadding;
      float textPadY = (heightPadding + font.getHeight() / 2.0F) / 2.0F;
      float textPadX = widthPadding / 2.0F;

      float baseX = instance != null ? instance.getX() : (sr.getFloat() - (instance != null ? instance.getWidth() : 0.0F));
      boolean isLeft = baseX < sr.getScaledWidth_double() / 2.0;
      float rightEdge = (instance != null ? instance.x : 0.0F) + sr.getFloat();

      cachedRowHeight = rowHeight;
      cachedPadX = textPadX;
      cachedPadY = textPadY;
      cachedLineWidth = rightLineWidth;
      cachedIsLeft = isLeft;

      float targetOffsetY = 0.0F;
      float totalHeight = 0.0F;
      float firstEntryWidth = 0.0F;
      entryCount = 0;

      float retractTarget = -3.0F - rightLineWidth;

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
            float rowX = isLeft ? baseX + offsetX - totalEntryWidth + rightLineWidth : rightEdge - offsetX - rightLineWidth;
            float rowY = baseY + module.getArrayListOffsetY();

            Entry e = ENTRIES[entryCount++];
            e.module = module;
            e.name = module.getName();
            e.suffix = suffix;
            e.hasSuffix = hasSuffix;
            e.x = rowX;
            e.y = rowY;
            e.width = totalEntryWidth;
            e.nameWidth = nameWidth;
            e.nameColor = ModuleManager.themeColor.getColor(rowX, rowY);
            e.suffixColor = arrayList2.status.getColor();
         }
      }

      if (instance != null) {
         instance.width = firstEntryWidth > 0.0F ? firstEntryWidth : 60.0F;
         instance.height = totalHeight > 0.0F ? totalHeight : rowHeight;
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
      if (!bloom && !arrayList2.background.isEnabled()) {
         return;
      }
      if (bloom && (!arrayList2.background.isEnabled() || !arrayList2.backgroundShadow.isEnabled())) {
         return;
      }
      if (entryCount == 0) {
         return;
      }

      float rowHeight = cachedRowHeight;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer wr = tessellator.getWorldRenderer();

      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      RenderUtils.applyBlendFunc();

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
      boolean drawRightLine = arrayList2.rightLine.isEnabled();

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

      // Pass 2: Accent Lines (single batched draw call)
      if (drawRightLine) {
         GlStateManager.disableTexture2D();
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
         for (int i = 0; i < entryCount; i++) {
            Entry e = ENTRIES[i];
            float lineX = isLeft ? e.x : e.x + e.width;
            int col = e.nameColor;
            float r = (col >> 16 & 0xFF) / 255.0F;
            float g = (col >> 8 & 0xFF) / 255.0F;
            float b = (col & 0xFF) / 255.0F;
            float a = (col >> 24 & 0xFF) / 255.0F;
            wr.pos(lineX, e.y + rowHeight, 0.0).color(r, g, b, a).endVertex();
            wr.pos(lineX + lineWidth, e.y + rowHeight, 0.0).color(r, g, b, a).endVertex();
            wr.pos(lineX + lineWidth, e.y, 0.0).color(r, g, b, a).endVertex();
            wr.pos(lineX, e.y, 0.0).color(r, g, b, a).endVertex();
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
}
