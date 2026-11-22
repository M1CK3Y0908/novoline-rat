package client.m1ck3y.novoline.client;

import client.m1ck3y.novoline.api.annotation.PathParam;
import client.m1ck3y.novoline.api.annotation.Query;
import client.m1ck3y.novoline.api.annotation.Route;
import client.m1ck3y.novoline.enums.HttpMethod;
import client.m1ck3y.novoline.enums.QueryType;

@Route(value = "/resources/{resourceId}/comments/{commentId}", httpMethod = HttpMethod.DELETE)
@Query(QueryType.JSON)
public class DeleteResourcesCommentsRequest extends AnnotatedApiRequest {
   @PathParam(value = "resourceId", enabled = true)
   private String resourceId;
   @PathParam(value = "commentId", enabled = true)
   private String commentId;
}
