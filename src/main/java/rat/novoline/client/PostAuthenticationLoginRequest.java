package rat.novoline.client;

import rat.novoline.api.annotation.PathParam;
import rat.novoline.api.annotation.Query;
import rat.novoline.api.annotation.Route;
import rat.novoline.enums.HttpMethod;
import rat.novoline.enums.QueryType;

@Route(value = "/authentication/login", httpMethod = HttpMethod.POST)
@Query(QueryType.QUERY)
public class PostAuthenticationLoginRequest extends AnnotatedApiRequest {
   @PathParam("token")
   private String token;
}
