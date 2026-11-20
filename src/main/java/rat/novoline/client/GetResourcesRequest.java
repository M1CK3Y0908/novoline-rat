package rat.novoline.client;

import rat.novoline.api.annotation.PathParam;
import rat.novoline.api.annotation.Route;
import rat.novoline.enums.HttpMethod;

@Route(value = "/resources/{resourceId}", httpMethod = HttpMethod.GET)
public class GetResourcesRequest extends AnnotatedApiRequest {
   @PathParam(value = "resourceId", enabled = true)
   private final String resourceId;

   public static GetResourcesRequest createGetResourcesRequest(String text) {
      return new GetResourcesRequest(text);
   }

   private GetResourcesRequest(String text) {
      this.resourceId = text;
   }
}
