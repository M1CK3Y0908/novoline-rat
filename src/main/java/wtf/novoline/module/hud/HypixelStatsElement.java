package wtf.novoline.module.hud;

import net.minecraft.client.gui.ScaledResolution;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.misc.HypixelStats;
import wtf.novoline.module.misc.HypixelStats.PlayTimeType;
import wtf.novoline.utils.render.FontAdapter;
import wtf.novoline.utils.render.RenderUtils;

public class HypixelStatsElement extends HudElement {
   private static final int UNUSED_INT = 10;
   private final HypixelStats hypixelStats;

   public HypixelStatsElement() {
      super("hypixel-stats", 5.0F, 20.0F, true, false);
      this.hypixelStats = ModuleManager.hypixelStats;
   }

   @Override
   public void draw(ScaledResolution scaledResolution, float f, boolean flag) {
      if (this.hypixelStats.isEnabled() && this.hypixelStats.isVisible()) {
         RenderUtils.drawShadow(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 1.5F, 1610612736);
         RenderUtils.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), -1155456735);
         int c1 = ModuleManager.themeColor.getColor(this.getX(), this.getY());
         int c2 = ModuleManager.themeColor.getColor(this.getX() + this.getWidth(), this.getY());
         RenderUtils.drawGradientLine(
            this.getX(),
            this.getY(),
            this.getX() + this.getWidth(),
            this.getY(),
            3.0F,
            c1,
            c2,
            false
         );
         FontAdapter titleFont = this.hypixelStats.getTitleFont();
         titleFont.drawStringWithShadow("Hypixel Stats", this.getX() + 3.0F, this.getY() + 7.0F, -1);
         int i = 20;
         FontAdapter contentFont = this.hypixelStats.getContentFont();

         for (java.util.Map.Entry entry : this.hypixelStats.getStats().getValues().entrySet()) {
            if ((entry.getKey() != PlayTimeType.PLAY_TIME || this.hypixelStats.showPlayTime.isEnabled())
               && (entry.getKey() != PlayTimeType.STAFF_BANS_SINCE_LOGIN || this.hypixelStats.hypixelStats.isEnabled())) {
               String displayName = ((PlayTimeType)entry.getKey()).getDisplayName();
               contentFont.drawStringWithShadow(displayName, this.getX() + 3.0F, this.getY() + i, -1);
               contentFont
                  .drawStringWithShadow(
                     (String)entry.getValue(),
                     this.getX() + this.getWidth() - 5.0F - contentFont.getStringWidth((String)entry.getValue()),
                     this.getY() + i,
                     -1
                  );
               i += 10;
            }
         }
      }
   }

   @Override
   public float getWidth() {
      return 150.0F;
   }

   @Override
   public float getHeight() {
      int size2 = this.hypixelStats.getStats().getValues().size();
      if (!this.hypixelStats.hypixelStats.isEnabled()) {
         size2--;
      }

      if (!this.hypixelStats.showPlayTime.isEnabled()) {
         size2--;
      }

      return 20 + size2 * 10;
   }
}
