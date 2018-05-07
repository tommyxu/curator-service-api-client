/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import tech.hillview.api.curator.client.exception.ApiCallException;
import tech.hillview.api.curator.client.exception.ApiServiceException;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public class DirectCallAdapterFactory extends CallAdapter.Factory {
  private Logger log = LoggerFactory.getLogger(DirectCallAdapterFactory.class);

  private ApiInterfaceMeta interfaceMeta;
  private final ApiServiceExceptionConverter exceptionConverter;

  public DirectCallAdapterFactory(ApiInterfaceMeta interfaceMeta, ApiServiceExceptionConverter exceptionConverter) {
    this.interfaceMeta = interfaceMeta;
    this.exceptionConverter = exceptionConverter;
  }

  @Override
  public CallAdapter get(final Type returnType, Annotation[] annotations, Retrofit retrofit) {
    log.debug("Get a new call adapter of {}", interfaceMeta.getApiInterface());

    return new CallAdapter() {
      boolean returnCallType = false;

      @Override public Type responseType() {
        if (returnType instanceof ParameterizedType) {
          ParameterizedType paramType = (ParameterizedType)returnType;
          if (paramType.getRawType().equals(Call.class)) {
            returnCallType = true;
            return paramType.getActualTypeArguments()[0];
          }
        }
        return returnType;
      }

      @Override public Object adapt(Call call) {
        if (returnCallType) {
          return call;
        } else {
          try {
            Response response = call.execute();
            if (response.isSuccessful()) {
              return response.body();
            } else {
              return exceptionConverter.convert(response.code(), response.errorBody().bytes(), interfaceMeta.getErrorBodyType());
            }
          } catch (ApiServiceException serviceException) {
            throw serviceException;
          } catch (Exception e) {
            throw new ApiCallException(e.getMessage(), e);
          }
        }
      }
    };
  }
}
