package client.m1ck3y.novoline.features.module.modules.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Random;
import java.util.StringJoiner;
import net.minecraft.util.ResourceLocation;
import client.m1ck3y.novoline.Novoline.Directories;
import client.m1ck3y.novoline.features.command.MessagesCommand;
import client.m1ck3y.novoline.event.UpdateEvent;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.setting.CheckboxSetting;
import client.m1ck3y.novoline.setting.NumberSetting;
import client.m1ck3y.novoline.utils.time.Stopwatch;

public class Spammer extends AbstractMessageModule {
   private final NumberSetting spammer = settingManager.register(new NumberSetting(this, "Delay (ms)", 1000, 100, 5000));
   private final CheckboxSetting randomCharacters = settingManager.register(new CheckboxSetting(this, "Random characters", true));
   private final Stopwatch messageTimer = new Stopwatch();
   @EventLink
   private final Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (this.messages.size() > 0 && this.messageTimer.hasElapsedFloat(this.spammer.getCurrentValue())) {
         String randomMessage = this.getRandomMessage();
         if (this.randomCharacters.isEnabled()) {
            StringJoiner stringJoiner = new StringJoiner(" ");

            for (String text : randomMessage.split(" ")) {
               Random rng = minecraft.thePlayer.getRNG();
               if (rng.nextInt(10) % 5 == 0) {
                  stringJoiner.add(String.valueOf(rng.nextInt(4096)));
               }

               stringJoiner.add(text);
            }

            randomMessage = stringJoiner.toString();
         }

         minecraft.thePlayer.sendChatMessage(randomMessage);
         this.messageTimer.reset();
      }
   };

   public Spammer() {
      super(
         Category.MISC,
         "Spammer",
         "Spams in the chat",
         Directories.STORAGE_DIR.resolve("spammer.novo"),
         new ResourceLocation("novoline", "default_spammer.novo")
      );
      MessagesCommand messagesCommand = new MessagesCommand(this, "manage spammer messages", "spammer", "sp");
      novoline.getCommandManager().register(messagesCommand);
   }
}
