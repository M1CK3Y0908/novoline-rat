package client.m1ck3y.novoline.client;

import client.m1ck3y.novoline.api.annotation.PathParam;
import client.m1ck3y.novoline.api.annotation.Route;
import client.m1ck3y.novoline.enums.HttpMethod;

@Route(value = "/profiles/{userId}", httpMethod = HttpMethod.GET)
public class GetProfilesRequest extends AnnotatedApiRequest {
   @PathParam(value = "userId", enabled = true)
   private long userId;
}
