package rat.novoline.script.adapter;

import java.util.Map;
import rat.novoline.script.AbstractTypeAdapter;
import rat.novoline.script.ScriptValue;
import rat.novoline.script.TypeRegistry;
import rat.novoline.utils.notification.Notification;
import rat.novoline.utils.notification.NotificationType;
import rat.novoline.support.SneakyThrow;

public class NotificationTypeAdapter extends AbstractTypeAdapter<Notification> {
   private static String getTypeName(NotificationType notificationType) {
      return switch (notificationType) {
         case INFO -> "Info";
         case WARNING -> "Warning";
         case ERROR -> "Error";
         case SUCCESS -> "Success";
      };
   }

   public NotificationTypeAdapter(TypeRegistry typeRegistry) {
      super("Notification", typeRegistry);
   }

   public Notification unpack(Map map) {
      try {
      ScriptValue scriptValue = ScriptValue.createScriptValue(map);
      NotificationType notificationType = (NotificationType)scriptValue.getOptionalEnum("type", NotificationType.class)
         .orElseThrow(() -> new IllegalArgumentException("Unrecognized type: " + scriptValue.getString("type")));
      String text2 = (String) scriptValue.getOptional("text").orElse(getTypeName(notificationType));
      String string = scriptValue.getString("subText");
      int intValue = scriptValue.getInt("duration");
      return new Notification(notificationType, text2, string, intValue);
   } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }
}
