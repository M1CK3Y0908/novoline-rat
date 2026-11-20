package rat.novoline.client;

import rat.novoline.api.annotation.PathParam;
import rat.novoline.api.annotation.Route;
import rat.novoline.enums.HttpMethod;

@Route(value = "/profiles/{userId}", httpMethod = HttpMethod.GET)
public class GetProfilesRequest extends AnnotatedApiRequest {
   @PathParam(value = "userId", enabled = true)
   private long userId;
}
