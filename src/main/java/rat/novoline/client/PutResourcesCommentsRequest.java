package rat.novoline.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import rat.novoline.api.annotation.PathParam;
import rat.novoline.api.annotation.Query;
import rat.novoline.api.annotation.Route;
import rat.novoline.enums.HttpMethod;
import rat.novoline.enums.QueryType;

@Route(value = "/resources/{resourceId}/comments", httpMethod = HttpMethod.PUT)
@Query(QueryType.JSON)
public class PutResourcesCommentsRequest extends AnnotatedApiRequest {
   @PathParam(value = "resourceId", enabled = true)
   private String resourceId;
   @SerializedName("content")
   @Expose
   private String content;
}
