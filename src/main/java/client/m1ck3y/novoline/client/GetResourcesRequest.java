package client.m1ck3y.novoline.client;

import client.m1ck3y.novoline.api.annotation.PathParam;
import client.m1ck3y.novoline.api.annotation.Route;
import client.m1ck3y.novoline.enums.HttpMethod;

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
