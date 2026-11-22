package client.m1ck3y.novoline.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import client.m1ck3y.novoline.api.annotation.PathParam;
import client.m1ck3y.novoline.api.annotation.Query;
import client.m1ck3y.novoline.api.annotation.Route;
import client.m1ck3y.novoline.enums.HttpMethod;
import client.m1ck3y.novoline.enums.QueryType;

@Route(value = "/resources/{resourceId}/comments", httpMethod = HttpMethod.PUT)
@Query(QueryType.JSON)
public class PutResourcesCommentsRequest extends AnnotatedApiRequest {
   @PathParam(value = "resourceId", enabled = true)
   private String resourceId;
   @SerializedName("content")
   @Expose
   private String content;
}
