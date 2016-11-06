package tech.hillview.api.curator.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hillview.api.curator.client.exception.ApiCallException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


public class ApiClientFactoryImpl implements ApiClientFactory {

  private Logger log = LoggerFactory.getLogger(ApiClientFactoryImpl.class);

  private CuratorFramework curator;

  public ApiClientFactoryImpl() {
  }

  public ApiClientFactoryImpl(CuratorFramework curator) {
    this.curator = curator;
  }

  private Cache createCache() {
    Cache cache = CacheBuilder.newBuilder()
      .maximumSize(100)
      .expireAfterAccess(10, TimeUnit.MINUTES)
      .build();
    return cache;
  }

  @Override
  public <T> T create(final Class<T> apiInterface) {
    final ApiInterfaceMeta apiInterfaceMeta = new ApiInterfaceMeta(apiInterface);

    final ServiceInstanceFinder finder;
    if (curator != null) {
      finder = new CuratorServiceInstanceFinder(curator, apiInterfaceMeta.getService());
    } else {
      finder = new FixedListServiceInstanceFinder(apiInterfaceMeta.getService(), apiInterfaceMeta.getUrls());
    }

    final ServiceInstanceChooser instanceChooser = new RoundRobinServiceChooser();

    final ApiInvokerCreator apiInvokerCreator = new ApiInvokerRetrofitClientCreator();

    final Cache<String, T> cache = createCache();

    Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { apiInterface }, new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Optional<ServiceInstance> instance = instanceChooser.chooseServiceInstance(finder.getServiceInstance());
        if (instance.isPresent()) {
          ServiceInstance serviceInstance = instance.get();

          // apiInterface + serviceInstance is key
          String cacheKey = serviceInstance.getId() + serviceInstance.getAddress(); // serviceInstance.getPort() + serviceInstance.getSslPort();
          T target = cache.get(cacheKey, () -> {
            T apiInvoker = apiInvokerCreator.getInvoker(apiInterface, apiInterfaceMeta, serviceInstance);
            log.info("create api {} for service instance: {}", apiInterfaceMeta.getService(), serviceInstance.getId());
            return apiInvoker;
          });

          return method.invoke(target, args);
        } else {
          throw new ApiCallException("No server instance");
        }      }
    });

    return (T) proxy;
  }

  public CuratorFramework getCurator() {
    return curator;
  }

  public void setCurator(CuratorFramework curator) {
    this.curator = curator;
  }

}
