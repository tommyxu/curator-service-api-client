package tech.hillview.api.curator.client;

import tech.hillview.api.curator.client.exception.ApiCallException;
import tech.hillview.api.curator.client.exception.ApiServiceException;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


public class DirectCallAdapterFactory extends CallAdapter.Factory {
  @Override
  public CallAdapter<?> get(final Type returnType, Annotation[] annotations, Retrofit retrofit) {
    return new CallAdapter<Object>() {
      @Override public Type responseType() {
        return returnType;
      }

      @Override public Object adapt(Call call) {
        try {
          Response response = call.execute();
          if (response.isSuccessful()) {
            return response.body();
          } else {
            throw new ApiServiceException(response.errorBody().string());
          }
        } catch (Exception e) {
          throw new ApiCallException(e.getMessage(), e);
        }
      }
    };
  }
}
