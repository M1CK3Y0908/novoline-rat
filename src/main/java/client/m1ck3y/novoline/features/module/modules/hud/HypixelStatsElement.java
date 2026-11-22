package client.m1ck3y.novoline.features.module.modules.hud;

import net.minecraft.client.gui.ScaledResolution;
import client.m1ck3y.novoline.Novoline.Fonts;
import client.m1ck3y.novoline.features.module.ModuleManager;
import client.m1ck3y.novoline.features.module.modules.misc.HypixelStats;
import client.m1ck3y.novoline.features.module.modules.misc.HypixelStats.PlayTimeType;
import client.m1ck3y.novoline.utils.render.FontAdapter;
import client.m1ck3y.novoline.utils.render.RenderUtils;

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
         RenderUtils.drawThemeGradientBar(
            this.getX(),
            this.getY(),
            this.getX() + this.getWidth(),
            this.getY() + 2.0F
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
