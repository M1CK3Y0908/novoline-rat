package rat.novoline.client;

import rat.novoline.api.annotation.PathParam;
import rat.novoline.api.annotation.Route;
import rat.novoline.enums.HttpMethod;

@Route(value = "/resources/{resourceId}", httpMethod = HttpMethod.DELETE)
public class DeleteResourcesRequest extends AnnotatedApiRequest {
   @PathParam(value = "resourceId", enabled = true)
   private final String resourceId;

   private DeleteResourcesRequest(String text) {
      this.resourceId = text;
   }

   public static DeleteResourcesRequest createDeleteResourcesRequest(String text) {
      return new DeleteResourcesRequest(text);
   }
}
