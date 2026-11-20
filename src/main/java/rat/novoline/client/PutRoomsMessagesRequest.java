package rat.novoline.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import rat.novoline.api.annotation.PathParam;
import rat.novoline.api.annotation.Query;
import rat.novoline.api.annotation.Route;
import rat.novoline.client.discord.Snowflake;
import rat.novoline.enums.HttpMethod;
import rat.novoline.enums.QueryType;

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
