package client.m1ck3y.novoline.client;

public class IrcConnection {
   private final ApiClient apiClient;

   public ApiClient getApiClient() {
      return this.apiClient;
   }

   public IrcConnection(ApiClient apiClient2) {
      this.apiClient = apiClient2;
   }
}
