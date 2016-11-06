package tech.hillview.api.curator.client.test.api;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import tech.hillview.api.curator.client.annotation.ApiClient;


@ApiClient(service = "account-service", path = "/api/v1/", url = "http://localhost:8080")
public interface AccountServiceApi {
    @POST("account")
    Long createAccount(@Body AccountCreationRequest request);

    @GET("account/{accountId}")
    AccountInfo getAccount(@Path("accountId") Long accountId);
}
