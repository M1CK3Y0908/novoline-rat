package client.m1ck3y.novoline.ui;

import net.minecraft.client.gui.GuiButton;

public class ButtonUtils {
   public static boolean containsMouse(GuiButton guiButton, int i, int i2) {
      return i >= guiButton.xPosition && i2 >= guiButton.yPosition && i < guiButton.xPosition + guiButton.getButtonWidth() && i2 < guiButton.yPosition + guiButton.getHeight();
   }
}
