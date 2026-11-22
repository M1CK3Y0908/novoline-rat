package client.m1ck3y.novoline.client;

import client.m1ck3y.novoline.api.annotation.PathParam;
import client.m1ck3y.novoline.api.annotation.Route;
import client.m1ck3y.novoline.enums.HttpMethod;

@Route(value = "/profiles/cosmetics/textures/{texHash}", httpMethod = HttpMethod.GET)
public class GetProfilesCosmeticsTexturesRequest extends AnnotatedApiRequest {
   @PathParam(value = "texHash", enabled = true)
   private final String texHash;

   private GetProfilesCosmeticsTexturesRequest(String text) {
      this.texHash = text;
   }

   public static GetProfilesCosmeticsTexturesRequest createGetProfilesCosmeticsTexturesRequest(String text) {
      return new GetProfilesCosmeticsTexturesRequest(text);
   }
}
