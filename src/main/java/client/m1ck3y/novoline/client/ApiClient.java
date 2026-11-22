package client.m1ck3y.novoline.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import client.m1ck3y.novoline.client.PostResourcesRatingsRequest.PositiveType;
import client.m1ck3y.novoline.gson.CosmeticDeserializer;
import client.m1ck3y.novoline.gson.InstantStringDeserializer;
import client.m1ck3y.novoline.util.AuthenticationResponse;
import client.m1ck3y.novoline.util.Profile;
import client.m1ck3y.novoline.util.RestClientOptions;
import client.m1ck3y.novoline.utils.config.CloudListResult;
import client.m1ck3y.novoline.utils.config.CloudScript;
import client.m1ck3y.novoline.utils.config.ConfigComment;
import client.m1ck3y.novoline.utils.cosmetic.Cosmetic;

public class ApiClient {
   private final RestClientOptions restClientOptions;
   private static final Gson gson = new GsonBuilder()
      .registerTypeAdapter(Instant.class, new InstantStringDeserializer())
      .registerTypeAdapter(Cosmetic.class, new CosmeticDeserializer())
      .create();
   private ApiRequestHandler apiRequestHandler;
   private final ApiHttpDispatcher apiHttpDispatcher;

   public CompletableFuture<Void> deleteResourceComment(DeleteResourcesCommentsRequest deleteResourcesCommentsRequest) {
      return this.executeVoid(deleteResourcesCommentsRequest);
   }

   public RestClientOptions getRestClientOptions() {
      return this.restClientOptions;
   }

   public CompletableFuture<Void> postResourceRating(PostResourcesRatingsRequest postResourcesRatingsRequest) {
      return this.executeVoid(postResourcesRatingsRequest);
   }

   public CompletableFuture uploadFile(String text, byte[] bArray) {
      return this.uploadFile(FileUploadRequest.createFileUploadRequest(text, bArray));
   }

   public CompletableFuture deleteResourceRating(String text) {
      return this.deleteResourceRating(DeleteResourcesRatingsRequest.createDeleteResourcesRatingsRequest(text));
   }

   public CompletableFuture deleteResource(String text) {
      return this.deleteResource(DeleteResourcesRequest.createDeleteResourcesRequest(text));
   }

   public CompletableFuture<ConfigComment> editResourceComment(PatchResourcesCommentsRequest patchResourcesCommentsRequest) {
      return this.requestJson(patchResourcesCommentsRequest, ConfigComment.class);
   }

   public CompletableFuture<Void> editResource(PatchResourcesRequest patchResourcesRequest) {
      return this.executeVoid(patchResourcesRequest);
   }

   private <T> CompletableFuture<T> requestJson(ApiRequest apiRequest, Type type) {
      return this.dispatch(apiRequest, java.net.http.HttpResponse.BodyHandlers.ofString(), this.jsonDeserializer(type));
   }

   public ApiHttpDispatcher getApiHttpDispatcher() {
      return this.apiHttpDispatcher;
   }

   private CompletableFuture<Void> executeVoid(ApiRequest apiRequest) {
      return this.dispatch(apiRequest, java.net.http.HttpResponse.BodyHandlers.ofString(), httpResponse -> null);
   }

   public CompletableFuture<Void> deleteResourceRating(DeleteResourcesRatingsRequest deleteResourcesRatingsRequest) {
      return this.executeVoid(deleteResourcesRatingsRequest);
   }

   public CompletableFuture updatePresence(PatchUsersSelfPresenceRequest patchUsersSelfPresenceRequest) {
      return CompletableFuture.completedFuture(null);
   }

   public CompletableFuture<CloudListResult> listResources(GetResourcesRequest2 getResourcesRequest2) {
      return this.requestJson(getResourcesRequest2, CloudListResult.class);
   }

   public ApiClient(RestClientOptions restClientOptions2) {
      this.restClientOptions = restClientOptions2;
      this.apiHttpDispatcher = ApiHttpDispatcher.createApiHttpDispatcher(this);
   }

   public CompletableFuture rateResource(String text, PositiveType positiveType) {
      return this.postResourceRating(PostResourcesRatingsRequest.createPostResourcesRatingsRequest(text, positiveType));
   }

   public CompletableFuture authenticate(String text) {
      return CompletableFuture.completedFuture(new AuthenticationResponse("1"));
   }

   public CompletableFuture<InputStream> getCosmeticTexture(GetProfilesCosmeticsTexturesRequest getProfilesCosmeticsTexturesRequest) {
      return this.dispatch(getProfilesCosmeticsTexturesRequest, java.net.http.HttpResponse.BodyHandlers.ofInputStream(), HttpResponse::body);
   }

   public CompletableFuture<CloudScript> createResource(PutResourcesRequest putResourcesRequest) {
      return this.requestJson(putResourcesRequest, CloudScript.class);
   }

   public CompletableFuture<ConfigComment> createResourceComment(PutResourcesCommentsRequest putResourcesCommentsRequest) {
      return this.requestJson(putResourcesCommentsRequest, ConfigComment.class);
   }

   public CompletableFuture<AuthenticationResponse> login(PostAuthenticationLoginRequest postAuthenticationLoginRequest) {
      return this.requestJson(postAuthenticationLoginRequest, AuthenticationResponse.class);
   }

   public CompletableFuture getResource(String text) {
      return this.getResource(GetResourcesRequest.createGetResourcesRequest(text));
   }

   public void setApiRequestHandler(ApiRequestHandler apiRequestHandler2) {
      this.apiRequestHandler = apiRequestHandler2;
   }

   public CompletableFuture downloadResourceFile(String text) {
      return this.downloadResourceFile(GetResourcesFileRequest.createGetResourcesFileRequest(text));
   }

   public CompletableFuture<CloudScript> getResource(GetResourcesRequest getResourcesRequest) {
      return this.requestJson(getResourcesRequest, CloudScript.class);
   }

   private <T, U> CompletableFuture<T> dispatch(ApiRequest apiRequest, java.net.http.HttpResponse.BodyHandler<U> bodyHandler, Function<HttpResponse<U>, T> function2) {
      return this.apiHttpDispatcher.sendAsync(apiRequest.authentication(this.apiRequestHandler), bodyHandler).thenApply(function2);
   }

   public CompletableFuture<Profile> getProfile(GetProfilesRequest getProfilesRequest) {
      return this.requestJson(getProfilesRequest, Profile.class);
   }

   public ApiRequestHandler getApiRequestHandler() {
      return this.apiRequestHandler;
   }

   private <R> Function<HttpResponse<String>, R> jsonDeserializer(Type type) {
      return httpResponse -> gson.fromJson(httpResponse.body(), type);
   }

   public CompletableFuture<Void> deleteResource(DeleteResourcesRequest deleteResourcesRequest) {
      return this.executeVoid(deleteResourcesRequest);
   }

   public CompletableFuture getCosmeticTexture(String text) {
      return this.getCosmeticTexture(GetProfilesCosmeticsTexturesRequest.createGetProfilesCosmeticsTexturesRequest(text));
   }

   public CompletableFuture<InputStream> downloadResourceFile(GetResourcesFileRequest getResourcesFileRequest) {
      return this.dispatch(getResourcesFileRequest, java.net.http.HttpResponse.BodyHandlers.ofInputStream(), HttpResponse::body);
   }

   public CompletableFuture<Void> uploadFile(FileUploadRequest fileUploadRequest) {
      return this.executeVoid(fileUploadRequest);
   }

   public CompletableFuture<Collection<ConfigComment>> getResourceComments(GetResourcesCommentsRequest getResourcesCommentsRequest) {
      Type type = (new TypeToken<LinkedList<ConfigComment>>() {}).getType();
      return this.requestJson(getResourcesCommentsRequest, type);
   }
}
