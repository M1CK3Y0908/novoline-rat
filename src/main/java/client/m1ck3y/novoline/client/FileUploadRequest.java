package client.m1ck3y.novoline.client;

import java.util.List;
import client.m1ck3y.novoline.api.annotation.PathParam;
import client.m1ck3y.novoline.api.annotation.Route;
import client.m1ck3y.novoline.enums.HttpMethod;
import client.m1ck3y.novoline.utils.http.MultipartRequestBody;
import client.m1ck3y.novoline.utils.http.RequestBody;
import client.m1ck3y.novoline.utils.http.MultipartRequestBody.Holder;

@Route(value = "/resources/{resourceId}/file", httpMethod = HttpMethod.POST)
public class FileUploadRequest extends AnnotatedApiRequest {
   @PathParam(value = "resourceId", enabled = true)
   private final String resourceId;
   private final byte[] byteArray;

   private FileUploadRequest(String text, byte[] bArray) {
      this.resourceId = text;
      this.byteArray = bArray;
   }

   @Override
   public RequestBody getRequestBody() {
      return new MultipartRequestBody(List.of(new Holder("file", "resource.bin", this.byteArray)));
   }

   public static FileUploadRequest createFileUploadRequest(String text, byte[] bArray) {
      return new FileUploadRequest(text, bArray);
   }
}
