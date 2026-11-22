package client.m1ck3y.novoline.utils.account;

import com.mojang.authlib.exceptions.InvalidCredentialsException;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import client.m1ck3y.novoline.Novoline;
import client.m1ck3y.novoline.lang.AuthException;
import client.m1ck3y.novoline.utils.language.Language;
import client.m1ck3y.novoline.utils.notification.Notification;
import client.m1ck3y.novoline.utils.notification.NotificationType;

public final class LoginHandler {
   public static boolean reportLoginFailure(Throwable throwable) {
      Novoline.taggedLogger.error(throwable, "Login failed!");
      if (throwable.getCause() != null && throwable.getCause() instanceof AuthException && throwable.getCause().getCause() != null) {
         Throwable cause = throwable.getCause().getCause();
         String text = null;
         if (cause instanceof InvalidCredentialsException) {
            text = Language.translate("account_manager.login_handling.invalid_credentials.notification.body");
         } else if (!(cause instanceof IOException)) {
            text = cause.getMessage();
         }

         if (text != null) {
            showNotification(NotificationType.ERROR, Language.translate("account_manager.login_handling.notification.title"), text);
            return true;
         }
      }

      return false;
   }

   private static void showNotification(NotificationType notificationType, String text, String text2) {
      Novoline.getNovoline().getNotificationManager().show(new Notification(notificationType, text, text2, 5000L));
   }

   public static void onLoggedIn(Session session2) {
      Novoline.taggedLogger.debug("Logged in: {}", new Object[]{session2.getUsername()});
      Minecraft.getMinecraft().session = session2;
      Novoline.username = session2.getUsername();
      Novoline.userId = Novoline.getUserRole();
      showNotification(
         NotificationType.SUCCESS,
         Language.translate("account_manager.login_handling.notification.title"),
         Language.translate("account_manager.login_handling.logged_in.notification.body", session2.getUsername())
      );
   }
}
