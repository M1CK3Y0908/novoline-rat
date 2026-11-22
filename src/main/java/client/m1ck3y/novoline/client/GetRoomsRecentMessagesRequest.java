package client.m1ck3y.novoline.client;

import client.m1ck3y.novoline.api.annotation.PathParam;
import client.m1ck3y.novoline.api.annotation.Route;
import client.m1ck3y.novoline.client.discord.Snowflake;
import client.m1ck3y.novoline.enums.HttpMethod;

@Route(value = "/rooms/{roomId}/recentMessages", httpMethod = HttpMethod.GET)
public class GetRoomsRecentMessagesRequest extends AnnotatedApiRequest {
   @PathParam(value = "roomId", enabled = true)
   private final Snowflake snowflake;

   public static GetRoomsRecentMessagesRequest createGetRoomsRecentMessagesRequest(Snowflake snowflake2) {
      return new GetRoomsRecentMessagesRequest(snowflake2);
   }

   private GetRoomsRecentMessagesRequest(Snowflake snowflake2) {
      this.snowflake = snowflake2;
   }
}
