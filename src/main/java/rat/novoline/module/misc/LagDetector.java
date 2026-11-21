package rat.novoline.module.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.util.ResourceLocation;
import rat.novoline.Novoline;
import rat.novoline.Novoline.Fonts;
import rat.novoline.event.PacketReceiveEvent;
import rat.novoline.event.RenderScreenEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.module.ModuleManager;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.ColorSetting;
import rat.novoline.setting.ComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.utils.language.Language;
import rat.novoline.utils.math.InterpolationUtils;
import rat.novoline.utils.render.CFontRenderer;
import rat.novoline.utils.render.RenderUtils;
import rat.novoline.utils.render.ShadowRenderer;
import rat.novoline.utils.time.Stopwatch;

public class LagDetector extends Module {
   private final NumberSetting lagDetector = settingManager.register(new NumberSetting(this, "Delay (seconds)", 1, 1, 5));

   public FontAdapter getFont() {
      return ModuleManager.font != null ? ModuleManager.font.getFont(true) : Fonts.rubikSemiboldSize16;
   }
   private final CheckboxSetting alphaBlink = settingManager.register(new CheckboxSetting(this, "Alpha blink", true));
   private final CheckboxSetting glow = settingManager.register(new CheckboxSetting(this, "Glow", true));
   private static final ResourceLocation ICON = new ResourceLocation("novoline", "images/connection_problem.png");
   private final ColorSetting color = settingManager.register(new ColorSetting(this, "Color", -4776948));
   private final ShadowRenderer shadowRenderer = new ShadowRenderer(Novoline.framebufferManager);
   @EventLink
   public final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> lastPacketTimer.reset();
   @EventLink
   public final Listener<RenderScreenEvent> onRenderScreen = renderScreenEvent -> {
      if (lastPacketTimer.hasElapsed((long)(this.lagDetector.getCurrentValue() * 1000.0F)) && !minecraft.isSingleplayer()) {
         float f = (float)(renderScreenEvent.scaledResolution.getScaledWidth_double() / 2.0);
         float f2 = 1.0F;
         if (this.alphaBlink.isEnabled()) {
            f2 = (float)InterpolationUtils.oscillate(0.25, 1.0, 1200L);
         }

         int i = RenderUtils.withAlphaFraction(this.color.getColor(), f2);
         if (this.glow.isEnabled()) {
            this.shadowRenderer.submit(() -> RenderUtils.drawColoredTexture(f - 20.0F, 40.0F, 40.0F, 40.0F, i, ICON));
         } else {
            RenderUtils.drawColoredTexture(f - 20.0F, 40.0F, 40.0F, 40.0F, i, ICON);
         }

         String text = Language.translate("feature.lagdetector.msg");
         FontAdapter cFontRenderer = this.getFont();
         if (this.glow.isEnabled()) {
            this.shadowRenderer.submit(() -> cFontRenderer.drawString(text, f - cFontRenderer.getStringWidth(text) / 2.0F, 85.0F, i));
         } else {
            cFontRenderer.drawString(text, f - cFontRenderer.getStringWidth(text) / 2.0F, 85.0F, i);
         }

         if (this.glow.isEnabled()) {
            this.shadowRenderer.render(minecraft.getFramebuffer(), renderScreenEvent.scaledResolution, i, 10.0F, 3.0F, 1.3F, true);
         }
      }
   };
   private static final Stopwatch lastPacketTimer = new Stopwatch();

   public LagDetector() {
      super(Category.MISC, "LagDetector", "");
   }
}
