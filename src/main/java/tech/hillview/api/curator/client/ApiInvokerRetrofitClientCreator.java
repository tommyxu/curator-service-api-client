/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.x.discovery.ServiceInstance;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


/**
 * Created by tommy on 5/11/16.
 */
class ApiInvokerRetrofitClientCreator implements ApiInvokerCreator {
  @Override
  public <T> T getInvoker(Class<T> apiInterface, ApiInterfaceMeta apiInterfaceMeta, ServiceInstance serviceInstance) {
    boolean ssl = false;
    String protocol = "http";
    Integer port = serviceInstance.getPort();

    if (serviceInstance.getPort() == null && serviceInstance.getSslPort() != null) {
      ssl = true;
      protocol = "https";
      port = serviceInstance.getSslPort();
    }

    String fullUrl = String.format("%s://%s:%s%s",
      protocol,
      serviceInstance.getAddress(),
      port,
      apiInterfaceMeta.getPath());

    ObjectMapper objectMapper = new ObjectMapper();

    ApiServiceExceptionConverter exceptionConverter = new ApiServiceExceptionConverterImpl(objectMapper);

    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(fullUrl)
      .addCallAdapterFactory(new DirectCallAdapterFactory(apiInterfaceMeta, exceptionConverter))
      .addConverterFactory(JacksonConverterFactory.create(objectMapper))
      .build();

    T api = retrofit.create(apiInterface);
    return api;  }
}
