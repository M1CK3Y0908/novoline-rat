package client.m1ck3y.novoline.features.command;

import java.io.Serializable;
import java.util.stream.Collectors;
import net.minecraft.util.EnumChatFormatting;
import client.m1ck3y.novoline.client.ApiClient;
import client.m1ck3y.novoline.client.discord.GatewayClient;
import client.m1ck3y.novoline.client.discord.GatewayUser;
import client.m1ck3y.novoline.enums.ClientState;

public class IrcCommand extends Command {
   public IrcCommand() {
      super("IRC commands", "irc");
   }

   private String getStateName(ClientState clientState) {
      return switch (clientState) {
         case IN_MENU -> "In Menu";
         case IN_SINGLEPLAYER -> "In Singleplayer";
         case ON_SERVER -> "On Server";
      };
   }

   @Override
   public void execute(String[] texts) {
      if (texts.length == 2 && texts[1].equals("users")) {
         this.sendMessage(EnumChatFormatting.WHITE + "Users online: " + EnumChatFormatting.GREEN + this.getGatewayClient().getGatewayUserList().size());

         for (GatewayUser gatewayUser : this.getGatewayClient().getGatewayUserList()) {
            String text = String.format("%04d", gatewayUser.getId());
            String text2 = EnumChatFormatting.GREEN
               + "● "
               + EnumChatFormatting.GRAY
               + gatewayUser.getRank()
               + " "
               + EnumChatFormatting.RESET
               + gatewayUser.getUsername()
               + " ("
               + text
               + ")";
            if (!gatewayUser.getPresence().isEmpty()) {
               text2 = text2
                  + EnumChatFormatting.GRAY
                  + " | "
                  + EnumChatFormatting.RESET
                  + gatewayUser.getPresence()
                     .stream()
                     .map(gatewayPresence -> gatewayPresence.getType() == ClientState.ON_SERVER ? gatewayPresence.getServerIp() : this.getStateName(gatewayPresence.getType()))
                     .collect(Collectors.joining(EnumChatFormatting.GRAY + ", " + EnumChatFormatting.RESET));
            }

            this.sendMessage((String) text2);
         }
      } else {
         this.sendLocalized("command.wrong_arguments");
         this.sendMessage(".irc users");
      }
   }

   private GatewayClient getGatewayClient() {
      return this.novoline.getIrcConnection().getGatewayClient();
   }

   public ApiClient getApiClient() {
      return this.novoline.getIrcConnection().getApiClient();
   }
}
