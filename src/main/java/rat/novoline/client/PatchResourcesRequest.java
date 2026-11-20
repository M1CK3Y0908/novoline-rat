package rat.novoline.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import rat.novoline.api.annotation.PathParam;
import rat.novoline.api.annotation.Query;
import rat.novoline.api.annotation.Route;
import rat.novoline.enums.HttpMethod;
import rat.novoline.enums.QueryType;

@Route(value = "/resources/{resourceId}", httpMethod = HttpMethod.PATCH)
@Query(QueryType.JSON)
public class PatchResourcesRequest extends AnnotatedApiRequest {
   @SerializedName("shortDescription")
   @Expose
   private String shortDescription;
   @PathParam(value = "resourceId", enabled = true)
   private String resourceId;
   @SerializedName("longDescription")
   @Expose
   private String longDescription;

   public static PatchResourcesRequest.EditResourceRequestBuilder builder() {
      return new PatchResourcesRequest.EditResourceRequestBuilder();
   }

   public static class EditResourceRequestBuilder {
      private String shortDescription;
      private String resourceId;
      private String longDescription;

      public PatchResourcesRequest.EditResourceRequestBuilder shortDescription(String text) {
         this.shortDescription = text;
         return this;
      }

      public PatchResourcesRequest.EditResourceRequestBuilder resourceId(String text) {
         this.resourceId = text;
         return this;
      }

      EditResourceRequestBuilder() {
      }

      @Override
      public String toString() {
         return "EditResourceRequest.EditResourceRequestBuilder(resourceId="
            + this.resourceId
            + ", shortDescription="
            + this.shortDescription
            + ", longDescription="
            + this.longDescription
            + ")";
      }

      public PatchResourcesRequest.EditResourceRequestBuilder longDescription(String text) {
         this.longDescription = text;
         return this;
      }
   }
}
