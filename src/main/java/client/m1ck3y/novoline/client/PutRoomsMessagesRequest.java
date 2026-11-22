package client.m1ck3y.novoline.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import client.m1ck3y.novoline.api.annotation.PathParam;
import client.m1ck3y.novoline.api.annotation.Query;
import client.m1ck3y.novoline.api.annotation.Route;
import client.m1ck3y.novoline.client.discord.Snowflake;
import client.m1ck3y.novoline.enums.HttpMethod;
import client.m1ck3y.novoline.enums.QueryType;

@Query(QueryType.JSON)
@Route(value = "/rooms/{roomId}/messages", httpMethod = HttpMethod.PUT)
public class PutRoomsMessagesRequest extends AnnotatedApiRequest {
   @PathParam(value = "roomId", enabled = true)
   private final Snowflake snowflake;
   @SerializedName("content")
   @Expose
   private final String content;

   private PutRoomsMessagesRequest(Snowflake snowflake2, String text) {
      this.snowflake = snowflake2;
      this.content = text;
   }

   public static PutRoomsMessagesRequest createPutRoomsMessagesRequest(Snowflake snowflake2, String text) {
      return new PutRoomsMessagesRequest(snowflake2, text);
   }
}
