/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hillview.api.curator.client.exception.ApiCallException;
import tech.hillview.api.curator.client.chooser.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


public class ApiClientFactoryImpl implements ApiClientFactory {

  private static final Logger log = LoggerFactory.getLogger(ApiClientFactoryImpl.class);

  private CuratorFramework curator;

  public ApiClientFactoryImpl() {
  }

  public ApiClientFactoryImpl(CuratorFramework curator) {
    this.curator = curator;
  }

  private Cache createCache() {
    return CacheBuilder.<String, Object>newBuilder()
      .maximumSize(100)
      .expireAfterAccess(15, TimeUnit.MINUTES)
      .build();
  }

  @Override
  public <T> T create(final Class<T> apiInterface) {
    final ApiInterfaceMeta apiInterfaceMeta = new ApiInterfaceMeta(apiInterface);

    final ServiceInstanceFinder finder;
    final ServiceInstanceChooser instanceChooser;
    if (curator != null) {
      finder = new CuratorServiceInstanceFinder(curator, apiInterfaceMeta.getService());
      instanceChooser = new RoundRobinChooser();
//      instanceChooser = new StickyServiceChooser();
    } else {
      finder = new FixedListServiceInstanceFinder(apiInterfaceMeta.getService(), apiInterfaceMeta.getUrls());
      instanceChooser = new RoundRobinChooser();
    }

    final Cache<String, T> cache = createCache();

    final ApiInvokerCreator apiInvokerCreator = new ApiInvokerRetrofitClientCreator();

    Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(),
      new Class<?>[] { apiInterface }, new InvocationHandler() {

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        log.debug("Call API method: {}", method.getName());

        Optional<ServiceInstance> instance = instanceChooser.chooseServiceInstance(finder.getServiceInstance());
        if (instance.isPresent()) {
          ServiceInstance serviceInstance = instance.get();

          // apiInterface + serviceInstance is key
          String cacheKey = serviceInstance.getId() + serviceInstance.getAddress(); // serviceInstance.getPort() + serviceInstance.getSslPort();
          T target = cache.get(cacheKey, () -> {
            T apiInvoker = apiInvokerCreator.getInvoker(apiInterface, apiInterfaceMeta, serviceInstance);
            log.debug("Create service chooser of {}: {}", apiInterfaceMeta.getService(), serviceInstance.getId());
            return apiInvoker;
          });

          try {
            return method.invoke(target, args);
          } catch (InvocationTargetException invocationException) {
            if (invocationException.getTargetException() != null) {
              throw invocationException.getTargetException();
            } else {
              throw invocationException;
            }
          }
        } else {
          throw new ApiCallException("No server chooser");
        }
      }
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
