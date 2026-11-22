package client.m1ck3y.novoline.client;

import client.m1ck3y.novoline.api.annotation.PathParam;
import client.m1ck3y.novoline.api.annotation.Route;
import client.m1ck3y.novoline.enums.HttpMethod;

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
