package rat.novoline.client;

import rat.novoline.api.annotation.PathParam;
import rat.novoline.api.annotation.Query;
import rat.novoline.api.annotation.Route;
import rat.novoline.enums.HttpMethod;
import rat.novoline.enums.QueryType;

@Route(value = "/resources/{resourceId}/comments/{commentId}", httpMethod = HttpMethod.DELETE)
@Query(QueryType.JSON)
public class DeleteResourcesCommentsRequest extends AnnotatedApiRequest {
   @PathParam(value = "resourceId", enabled = true)
   private String resourceId;
   @PathParam(value = "commentId", enabled = true)
   private String commentId;
}
