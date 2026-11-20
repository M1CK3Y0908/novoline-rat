package rat.novoline.gui.clickgui;

import rat.novoline.Novoline;

public interface ClickGuiComponent {
   ClickGuiScreen clickGuiScreen = Novoline.getNovoline().getClickGuiScreen();

   void handleKeyboardPress(char c, int i);

   void handleMouseRelease(float f, float f2, int i);

   boolean handleMouseClick(float f, float f2, int i);
}
