package rat.novoline.module.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import rat.novoline.Novoline;
import rat.novoline.module.ModuleManager;
import rat.novoline.utils.render.FontAdapter;
import rat.novoline.utils.render.RenderUtils;

public class InformationElement extends HudElement {
   public static InformationElement instance;
   private static final Minecraft minecraft = Minecraft.getMinecraft();
   private boolean hasUserMoved = false;
   private float width = 120.0F;
   private float height = 60.0F;

   private static float cachedX = 0.0F;
   private static float cachedY = 0.0F;
   private static float cachedWidth = 120.0F;
   private static float cachedHeight = 60.0F;
   private static final List<InfoLine> cachedLines = new ArrayList<>();

   public static class InfoItem {
      public final String name;
      public final String value;

      public InfoItem(String name, String value) {
         this.name = name;
         this.value = value;
      }
   }

   public static class InfoLine {
      public final List<InfoItem> items = new ArrayList<>();

      public InfoLine(String name, String value) {
         this.items.add(new InfoItem(name, value));
      }

      public InfoLine add(String name, String value) {
         this.items.add(new InfoItem(name, value));
         return this;
      }
   }

   public InformationElement() {
      super("information", 0.0F, 0.0F, true);
      instance = this;
      this.width = cachedWidth;
      this.height = cachedHeight;
   }

   public void checkDefaultPosition(ScaledResolution sr) {
      if (!this.hasUserMoved || (this.x == 0.0F && this.y == 0.0F)) {
         float chatOffset = (minecraft.currentScreen instanceof GuiChat) ? 14.0F : 0.0F;
         this.x = 2.0F;
         this.y = sr.getFloat2() - this.height - 2.0F - chatOffset;
         cachedX = this.x;
         cachedY = this.y;
      }
   }

   @Override
   public HudElement setX(float f) {
      super.setX(f);
      if (f != 0.0F) {
         this.hasUserMoved = true;
         cachedX = this.x;
      }
      return this;
   }

   @Override
   public HudElement setY(float f) {
      super.setY(f);
      if (f != 0.0F) {
         this.hasUserMoved = true;
         cachedY = this.y;
      }
      return this;
   }

   @Override
   public void updateDragging(ScaledResolution scaledResolution, float f, float f2) {
      checkDefaultPosition(scaledResolution);
      super.updateDragging(scaledResolution, f, f2);
      if (this.isDragging()) {
         this.hasUserMoved = true;
         cachedX = this.x;
         cachedY = this.y;
      }
   }

   @Override
   public boolean handleMouseClick(float f, float f2, int i) {
      if (!this.hasUserMoved) {
         ScaledResolution sr = new ScaledResolution(minecraft);
         float scaleFactor = sr.getScaleFactor();
         float screenH = sr.getScaledHeight() * scaleFactor / 2.0F;
         float chatOffset = (minecraft.currentScreen instanceof GuiChat) ? 14.0F : 0.0F;
         this.x = 2.0F;
         this.y = screenH - this.height - 2.0F - chatOffset;
         cachedX = this.x;
         cachedY = this.y;
      }
      return super.handleMouseClick(f, f2, i);
   }

   @Override
   public float getWidth() {
      return this.width;
   }

   @Override
   public float getHeight() {
      return this.height;
   }

   public static void updateInformation(ScaledResolution sr) {
      if (instance == null || ModuleManager.information == null || !ModuleManager.information.isEnabled()) {
         return;
      }
      instance.checkDefaultPosition(sr);
      cachedX = instance.getX();
      cachedY = instance.getY();
      cachedWidth = instance.getWidth();
      cachedHeight = instance.getHeight();
   }

   @Override
   public void draw(ScaledResolution scaledResolution, float partialTicks, boolean inEditor) {
      updateInformation(scaledResolution);

      Information information = ModuleManager.information;
      if (information == null || !information.isEnabled()) {
         return;
      }

      EntityPlayerSP player = minecraft.thePlayer;
      if (player == null && !inEditor) {
         return;
      }

      List<InfoLine> lines = new ArrayList<>();

      // 1. Collect info lines
      if (information.elements.isSelected("FPS")) {
         lines.add(new InfoLine("FPS", String.valueOf(Minecraft.getDebugFPS())));
      }
      if (information.elements.isSelected("TPS")) {
         lines.add(new InfoLine("TPS", String.format(Locale.ENGLISH, "%.1f", information.getAverageTps())));
      }
      if (information.elements.isSelected("Speed")) {
         double speed = (player != null) ? getSpeedPerTick(player) * 20.0 * (minecraft.timer.timerSpeed < 1.0F ? 1.0F : minecraft.timer.timerSpeed) : 4.52;
         lines.add(new InfoLine("Speed", String.format(Locale.ENGLISH, "%.2f", speed)));
      }
      if (information.elements.isSelected("Reach")) {
         lines.add(new InfoLine("Reach", String.format(Locale.ENGLISH, "%.2f", information.getReach())));
      }
      if (information.elements.isSelected("Coordinates")) {
         if (player != null) {
            lines.add(new InfoLine("X", String.valueOf((int) player.posX))
               .add("Y", String.valueOf((int) player.posY))
               .add("Z", String.valueOf((int) player.posZ)));
         } else {
            lines.add(new InfoLine("X", "100").add("Y", "64").add("Z", "-200"));
         }
      }
      if (information.elements.isSelected("Ping")) {
         int ping = 0;
         if (player != null && minecraft.getNetHandler() != null) {
            NetworkPlayerInfo info = minecraft.getNetHandler().getPlayerInfo(player.getUniqueID());
            if (info != null) {
               ping = info.getResponseTime();
            }
         }
         lines.add(new InfoLine("Ping", ping + "ms"));
      }
      if (information.elements.isSelected("User Info")) {
         lines.add(new InfoLine(Novoline.getUserRole(), Novoline.getUserName()));
      }

      cachedLines.clear();
      cachedLines.addAll(lines);

      if (lines.isEmpty()) {
         this.width = 60.0F;
         this.height = 14.0F;
         return;
      }

      FontAdapter font = information.getFont();
      float lineHeight = (float) font.getHeight() + 2.0F;

      // 2. Compute bounding width
      float maxLineWidth = 0.0F;
      for (InfoLine line : lines) {
         float w = 0.0F;
         for (int j = 0; j < line.items.size(); j++) {
            InfoItem item = line.items.get(j);
            w += (float) (font.getStringWidth(item.name) + font.getStringWidth(": ") + font.getStringWidth(item.value));
            if (j < line.items.size() - 1) {
               w += (float) font.getStringWidth("  ");
            }
         }
         if (w > maxLineWidth) {
            maxLineWidth = w;
         }
      }

      this.width = maxLineWidth + (information.background.isEnabled() ? 8.0F : 0.0F);
      this.height = (float) lines.size() * lineHeight;
      cachedWidth = this.width;
      cachedHeight = this.height;

      float x = this.x;
      float y = this.y;

      // 3. Render lines: Name in Theme Gradient, Colon in Gray, Value in White
      for (int i = 0; i < lines.size(); i++) {
         InfoLine line = lines.get(i);
         float lineY = y + (float) i * lineHeight;

         // Background if enabled
         if (information.background.isEnabled()) {
            float lineW = 0.0F;
            for (int j = 0; j < line.items.size(); j++) {
               InfoItem item = line.items.get(j);
               lineW += (float) (font.getStringWidth(item.name) + font.getStringWidth(": ") + font.getStringWidth(item.value));
               if (j < line.items.size() - 1) {
                  lineW += (float) font.getStringWidth("  ");
               }
            }
            RenderUtils.drawRoundedRect(x - 2.0F, lineY - 1.0F, lineW + 6.0F, lineHeight, 2.0F, 0x60000000);
         }

         float curX = x + (information.background.isEnabled() ? 1.0F : 0.0F);

         for (int j = 0; j < line.items.size(); j++) {
            InfoItem item = line.items.get(j);

            // 1. Name: Theme color gradient
            for (int c = 0; c < item.name.length(); c++) {
               String ch = String.valueOf(item.name.charAt(c));
               int color = ModuleManager.themeColor.getColor((double) curX, (double) lineY, c, item.name.length());
               font.drawStringWithShadow(ch, curX, lineY, color);
               curX += (float) font.getStringWidth(ch);
            }

            // 2. Colon: Gray
            font.drawStringWithShadow(": ", curX, lineY, 0xFFAAAAAA);
            curX += (float) font.getStringWidth(": ");

            // 3. Value: White
            font.drawStringWithShadow(item.value, curX, lineY, -1);
            curX += (float) font.getStringWidth(item.value);

            // Spacing if more items on this line (e.g. Coordinates X, Y, Z)
            if (j < line.items.size() - 1) {
               font.drawStringWithShadow("  ", curX, lineY, -1);
               curX += (float) font.getStringWidth("  ");
            }
         }
      }
   }

   public static void renderInformationEffects(boolean bloom, boolean glow, ScaledResolution sr) {
      if (instance == null || ModuleManager.information == null || !ModuleManager.information.isEnabled()) {
         return;
      }
      if (cachedLines.isEmpty()) {
         return;
      }

      Information information = ModuleManager.information;
      FontAdapter font = information.getFont();
      float lineHeight = (float) font.getHeight() + 2.0F;
      float x = cachedX;
      float y = cachedY;

      if (!bloom || !glow) {
         // Blur pass: mask background rounded rectangles if background enabled
         if (information.background.isEnabled()) {
            for (int i = 0; i < cachedLines.size(); i++) {
               InfoLine line = cachedLines.get(i);
               float lineW = 0.0F;
               for (int j = 0; j < line.items.size(); j++) {
                  InfoItem item = line.items.get(j);
                  lineW += (float) (font.getStringWidth(item.name) + font.getStringWidth(": ") + font.getStringWidth(item.value));
                  if (j < line.items.size() - 1) {
                     lineW += (float) font.getStringWidth("  ");
                  }
               }
               float lineY = y + (float) i * lineHeight;
               RenderUtils.drawRoundedRect(x - 2.0F, lineY - 1.0F, lineW + 6.0F, lineHeight, 2.0F, -16777216);
            }
         }
      }
   }

   private static double getSpeedPerTick(Entity entity) {
      return Math.hypot(entity.posX - entity.prevPosX, entity.posZ - entity.prevPosZ);
   }
}
