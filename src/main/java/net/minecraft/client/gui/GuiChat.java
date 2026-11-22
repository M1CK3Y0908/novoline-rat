package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import client.m1ck3y.novoline.Novoline;
import client.m1ck3y.novoline.features.module.modules.hud.HudManager;

public class GuiChat extends GuiScreen {
   protected GuiTextField inputField;
   private final HudManager commandSuggestions = Novoline.getNovoline().getHudManager();
   private String historyBuffer = "";
   private int sentHistoryCursor = -1;
   private int autocompleteIndex;
   private List<String> foundPlayerNames = Lists.newArrayList();
   private static final Logger logger = LogManager.getLogger();
   private boolean playerNamesFound;
   private boolean waitingOnAutocomplete;
   private String defaultInputFieldText = "";

   public void onAutocompleteResponse(String[] matches) {
      if (this.waitingOnAutocomplete) {
         this.playerNamesFound = false;
         this.foundPlayerNames.clear();

         for (String text2 : matches) {
            if (text2.length() > 0) {
               this.foundPlayerNames.add(text2);
            }
         }

         String text3 = this.inputField.getText().substring(this.inputField.findNthWordFromPos(-1, this.inputField.getCursorPosition(), false));
         String commonPrefix = StringUtils.getCommonPrefix(matches);
         if (commonPrefix.length() > 0 && !text3.equalsIgnoreCase(commonPrefix)) {
            this.inputField
               .deleteFromCursor(this.inputField.findNthWordFromPos(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
            this.inputField.writeText(commonPrefix);
         } else if (this.foundPlayerNames.size() > 0) {
            this.playerNamesFound = true;
            this.autocompletePlayerNames();
         }
      }
   }

   public GuiChat(String defaultText) {
      this.defaultInputFieldText = defaultText;
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      this.waitingOnAutocomplete = false;
      if (keyCode == 15) {
         this.autocompletePlayerNames();
      } else {
         this.playerNamesFound = false;
      }

      if (keyCode == 1) {
         this.mc.displayGuiScreen((GuiScreen)null);
      } else if (keyCode == 28 || keyCode == 156) {
         String text = this.inputField.getText().trim();
         if (text.length() > 0) {
            this.sendChatMessage(text);
         }

         this.mc.displayGuiScreen((GuiScreen)null);
      } else if (keyCode == 200) {
         this.getSentHistory(-1);
      } else if (keyCode == 208) {
         this.getSentHistory(1);
      } else if (keyCode == 201) {
         this.mc.ingameGUI.getChatGUI().scroll(this.mc.ingameGUI.getChatGUI().getLineCount() - 1);
      } else if (keyCode == 209) {
         this.mc.ingameGUI.getChatGUI().scroll(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1);
      } else {
         this.inputField.textboxKeyTyped(typedChar, keyCode);
      }
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   public void getSentHistory(int msgPos) {
      int i = this.sentHistoryCursor + msgPos;
      int size2 = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
      i = MathHelper.clamp_int(i, 0, size2);
      if (i != this.sentHistoryCursor) {
         if (i == size2) {
            this.sentHistoryCursor = size2;
            this.inputField.setText(this.historyBuffer);
         } else {
            if (this.sentHistoryCursor == size2) {
               this.historyBuffer = this.inputField.getText();
            }

            this.inputField.setText(this.mc.ingameGUI.getChatGUI().getSentMessages().get(i));
            this.sentHistoryCursor = i;
         }
      }
   }

   @Override
   public void startClosing() {
      Keyboard.enableRepeatEvents(false);
      this.mc.ingameGUI.getChatGUI().resetScroll();
   }

   @Override
   protected void setText(String newChatText, boolean shouldOverwrite) {
      if (shouldOverwrite) {
         this.inputField.setText(newChatText);
      } else {
         this.inputField.writeText(newChatText);
      }
   }

   @Override
   protected void mouseReleased(int i, int i2, int i3) {
      this.commandSuggestions.getHudInputHandler().handleMouseRelease(i, i2);
      super.mouseReleased(i, i2, i3);
   }

   @Override
   public void updateScreen() {
      this.inputField.updateCursorCounter();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int eventDWheel = Mouse.getEventDWheel();
      if (eventDWheel != 0) {
         if (eventDWheel > 1) {
            eventDWheel = 1;
         }

         if (eventDWheel < -1) {
            eventDWheel = -1;
         }

         if (!isShiftKeyDown()) {
            eventDWheel *= 7;
         }

         this.mc.ingameGUI.getChatGUI().scroll(eventDWheel);
      }
   }

   private void sendAutocompleteRequest(String message, String autocompletePrefix) {
      if (message.length() >= 1) {
         BlockPos blockPos = null;
         if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK) {
            blockPos = this.mc.objectMouseOver.getBlockPos();
         }

         this.mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(message, blockPos));
         this.waitingOnAutocomplete = true;
      }
   }

   @Override
   protected void mouseClicked(int i, int i2, int i3) throws IOException {
      this.commandSuggestions.getHudInputHandler().handleMouseClick(i, i2, i3);
      if (i3 == 0) {
         IChatComponent chatComponent2 = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
         if (this.handleComponentClick(chatComponent2)) {
            return;
         }
      }

      this.inputField.mouseClicked(i, i2, i3);
      super.mouseClicked(i, i2, i3);
   }

   public GuiChat() {
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      handleInt(2, this.height - 14, this.width - 2, this.height - 2, Integer.MIN_VALUE);
      this.inputField.drawTextBox();
      IChatComponent chatComponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
      if (chatComponent != null && chatComponent.getChatStyle().getChatHoverEvent() != null) {
         this.handleComponentHover(chatComponent, mouseX, mouseY);
      }

      this.commandSuggestions.getHudRenderer().renderForEditor(mouseX, mouseY, partialTicks);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
      this.inputField = new GuiTextField(0, this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
      this.inputField.setMaxStringLength(256);
      this.inputField.update();
      this.inputField.setFocused(true);
      this.inputField.setText(this.defaultInputFieldText);
      this.inputField.setCanLoseFocus(false);
   }

   public void autocompletePlayerNames() {
      if (this.playerNamesFound) {
         this.inputField
            .deleteFromCursor(this.inputField.findNthWordFromPos(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
         if (this.autocompleteIndex >= this.foundPlayerNames.size()) {
            this.autocompleteIndex = 0;
         }
      } else {
         int nthWordFromPos = this.inputField.findNthWordFromPos(-1, this.inputField.getCursorPosition(), false);
         this.foundPlayerNames.clear();
         this.autocompleteIndex = 0;
         String text2 = this.inputField.getText().substring(nthWordFromPos).toLowerCase();
         String text3 = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
         this.sendAutocompleteRequest(text3, text2);
         if (this.foundPlayerNames.isEmpty()) {
            return;
         }

         this.playerNamesFound = true;
         this.inputField.deleteFromCursor(nthWordFromPos - this.inputField.getCursorPosition());
      }

      if (this.foundPlayerNames.size() > 1) {
         StringBuilder stringBuilder = new StringBuilder();

         for (String text4 : this.foundPlayerNames) {
            if (stringBuilder.length() > 0) {
               stringBuilder.append(", ");
            }

            stringBuilder.append(text4);
         }

         this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(stringBuilder.toString()), 1);
      }

      this.inputField.writeText(this.foundPlayerNames.get(this.autocompleteIndex++));
   }
}
