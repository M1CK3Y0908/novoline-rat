package client.m1ck3y.novoline.features.module.modules.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import client.m1ck3y.novoline.Novoline.Directories;
import client.m1ck3y.novoline.features.command.MessagesCommand;
import client.m1ck3y.novoline.event.PlayerKillEvent;
import client.m1ck3y.novoline.features.module.Category;
import client.m1ck3y.novoline.setting.NumberSetting;
import client.m1ck3y.novoline.utils.ScheduledTask;

public class Killsults extends AbstractMessageModule {
   private final NumberSetting killsults = settingManager.register(new NumberSetting(this, "Delay (ms)", 150, 0, 1000));
   @EventLink
   private final Listener<PlayerKillEvent> onPlayerKill = playerKillEvent -> {
      if (this.messages.size() > 0) {
         final String text = this.getRandomMessage().replace("{player}", playerKillEvent.entityPlayer.getName());
         int i = (int)this.killsults.getCurrentValue();
         if (i > 0) {
            novoline.getTaskScheduler().schedule(new ScheduledTask(i) {
               @Override
               public void run() {
                  Killsults.access$000().thePlayer.sendChatMessage(text);
               }
            });
         } else {
            minecraft.thePlayer.sendChatMessage(text);
         }
      }
   };

   static Minecraft access$000() {
      return minecraft;
   }

   public Killsults() {
      super(
         Category.MISC,
         "Killsults",
         "Send custom message when you kill a player",
         Directories.STORAGE_DIR.resolve("killsults.novo"),
         new ResourceLocation("novoline", "default_killsults.novo")
      );
      MessagesCommand messagesCommand = new MessagesCommand(this, "manage killsults", "killsults", "ks") {
         @Override
         public void printUsage() {
            super.printUsage();
            this.sendLocalized("command.killsults.ph_1");
         }
      };
      novoline.getCommandManager().register(messagesCommand);
   }
}
