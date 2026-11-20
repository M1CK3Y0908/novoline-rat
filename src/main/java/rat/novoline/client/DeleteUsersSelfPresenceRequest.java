package rat.novoline.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import rat.novoline.api.annotation.Query;
import rat.novoline.api.annotation.Route;
import rat.novoline.enums.HttpMethod;
import rat.novoline.enums.QueryType;

@Route(value = "/users/@self/presence", httpMethod = HttpMethod.DELETE)
@Query(QueryType.JSON)
public class DeleteUsersSelfPresenceRequest extends AnnotatedApiRequest {
   @SerializedName("sid")
   @Expose
   private String sid;
   @SerializedName("hide")
   @Expose
   private boolean hide;
}
