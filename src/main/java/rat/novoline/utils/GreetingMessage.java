package rat.novoline.utils;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import rat.novoline.Novoline;
import rat.novoline.event.LoadWorldEvent;
import rat.novoline.utils.language.Language;

public class GreetingMessage {
   private final Novoline novoline = Novoline.getNovoline();
   @EventLink
   public final Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> {
      this.novoline.sendChatMessage(Language.translate("greeting.line_1", "Novoline", "3.0"));

      for (int i = 2; i <= 8; i++) {
         this.novoline.sendChatMessage(Language.translate("greeting.line_" + i));
      }

      this.novoline.getBus().unsubscribe(this);
   };
}
