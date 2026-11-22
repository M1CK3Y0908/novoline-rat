package client.m1ck3y.novoline.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import client.m1ck3y.novoline.api.annotation.PathParam;
import client.m1ck3y.novoline.api.annotation.Query;
import client.m1ck3y.novoline.api.annotation.Route;
import client.m1ck3y.novoline.enums.HttpMethod;
import client.m1ck3y.novoline.enums.QueryType;

@Route(value = "/resources/{resourceId}/ratings", httpMethod = HttpMethod.POST)
@Query(QueryType.JSON)
public class PostResourcesRatingsRequest extends AnnotatedApiRequest {
   @PathParam(value = "resourceId", enabled = true)
   private final String resourceId;
   @SerializedName("ratingType")
   @Expose
   private final PostResourcesRatingsRequest.PositiveType ratingType;

   public static PostResourcesRatingsRequest createPostResourcesRatingsRequest(String text, PostResourcesRatingsRequest.PositiveType positiveType) {
      return new PostResourcesRatingsRequest(text, positiveType);
   }

   private PostResourcesRatingsRequest(String text, PostResourcesRatingsRequest.PositiveType positiveType) {
      this.resourceId = text;
      this.ratingType = positiveType;
   }

   public enum PositiveType {
      POSITIVE,
      NEGATIVE;
   }
}
