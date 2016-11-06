package tech.hillview.api.curator.client;

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

    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(fullUrl)
      .addCallAdapterFactory(new DirectCallAdapterFactory())
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

    T api = retrofit.create(apiInterface);
    return api;  }
}
