package client.m1ck3y.novoline.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import client.m1ck3y.novoline.api.annotation.Query;
import client.m1ck3y.novoline.api.annotation.Route;
import client.m1ck3y.novoline.enums.HttpMethod;
import client.m1ck3y.novoline.enums.QueryType;

@Route(value = "/users/@self/presence", httpMethod = HttpMethod.DELETE)
@Query(QueryType.JSON)
public class DeleteUsersSelfPresenceRequest extends AnnotatedApiRequest {
   @SerializedName("sid")
   @Expose
   private String sid;
   @SerializedName("hide")
   @Expose
   private boolean hide;
}
