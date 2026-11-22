package client.m1ck3y.novoline.client;

import client.m1ck3y.novoline.api.annotation.PathParam;
import client.m1ck3y.novoline.api.annotation.Query;
import client.m1ck3y.novoline.api.annotation.Route;
import client.m1ck3y.novoline.enums.HttpMethod;
import client.m1ck3y.novoline.enums.QueryType;

@Route(value = "/authentication/login", httpMethod = HttpMethod.POST)
@Query(QueryType.QUERY)
public class PostAuthenticationLoginRequest extends AnnotatedApiRequest {
   @PathParam("token")
   private String token;
}
