package client.m1ck3y.novoline.controls;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import client.m1ck3y.novoline.enums.AccountType;
import client.m1ck3y.novoline.ui.AccountListPanel;
import client.m1ck3y.novoline.ui.AltManagerScreen;
import client.m1ck3y.novoline.ui.NovolineButton;
import client.m1ck3y.novoline.ui.overlay.AccountActionOverlay;
import client.m1ck3y.novoline.ui.overlay.AccountCredentialsOverlay;
import client.m1ck3y.novoline.utils.account.Account;
import client.m1ck3y.novoline.utils.language.Language;
import client.m1ck3y.novoline.utils.notification.NotificationType;

public class AccountActionScreen extends AccountCredentialsOverlay {
   private final GuiScreen hostScreen;

   @Override
   public void init() {
      super.init();
      this.hostScreen
         .buttonList
         .add(
            new NovolineButton(
               this.blurRenderer2,
               -4,
               this.hostScreen.width / 2 - 100,
               this.hostScreen.height / 2 + 81,
               Language.translate("account_manager.gui.account_action.microsoft_account")
            )
         );
   }

   public AccountActionScreen(GuiScreen guiScreen) {
      super(guiScreen, Language.translate("account_manager.gui.add_account.title"), Language.translate("account_manager.gui.add_account.add"));
      this.hostScreen = guiScreen;
   }

   @Override
   public void firstButtonClicked() {
      String text = this.textSelection.getTextBuffer().getText();
      String text2 = this.textSelection2.getTextBuffer().getText();
      if (this.hostScreen instanceof AltManagerScreen altManagerScreen) {
         AccountListPanel accountListPanel = altManagerScreen.getAccountListPanel();
         AccountType accountType = this.getAccountType(text, text2);
         if (text.trim().isEmpty()) {
            this.showNotification(
               NotificationType.ERROR,
               Language.translate("account_manager.login_handling.notification.title"),
               Language.translate("account_manager.notification.login.empty_account.body"),
               5000
            );
            return;
         }

         Account account2 = this.accountManager.createAccount(text, text2, accountType);
         this.accountManager.add(account2);
         accountListPanel.addEntry(account2);
         accountListPanel.saveAccounts();
         accountListPanel.updateLayout();
         altManagerScreen.closeOverlay();
      }
   }

   @Override
   public void actionPerformed(GuiButton guiButton) {
      super.actionPerformed(guiButton);
      if (guiButton.id == -4 && this.hostScreen instanceof AltManagerScreen altManagerScreen) {
         altManagerScreen.closeOverlay();
         altManagerScreen.displayOverlay(new AccountActionOverlay(this.hostScreen));
      }
   }
}
