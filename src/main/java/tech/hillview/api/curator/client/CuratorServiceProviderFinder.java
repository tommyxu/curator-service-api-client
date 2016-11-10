/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.ServiceCacheListener;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hillview.api.curator.client.exception.ApiCallException;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Created by tommy on 5/11/16.
 */
class CuratorServiceProviderFinder implements ServiceInstanceFinder, Closeable {
  private Logger log = LoggerFactory.getLogger(CuratorServiceProviderFinder.class);

  private final CuratorFramework curator;

  private String serviceName;
  private ServiceProvider<Map> serviceProvider;

  public CuratorServiceProviderFinder(CuratorFramework curator, String serviceName) {
    this.curator = curator;
    this.serviceName = serviceName;
    createServiceProvider();
  }

  synchronized private void createServiceProvider() {
    ServiceDiscovery<Map> serviceDiscovery = ServiceDiscoveryBuilder.builder(Map.class)
      .client(curator)
      .basePath(Constants.SERVICES_PATH)
      .serializer(new ServiceInstanceSerializer<>(Map.class))
      .build();

    serviceProvider = serviceDiscovery.serviceProviderBuilder()
      .serviceName(serviceName)
      .providerStrategy(new RoundRobinStrategy<>())
      // .threadFactory()
      // .additionalFilter()
      // .downInstancePolicy()
      .build();

    try {
      serviceProvider.start();
      log.debug("service provider started working.");
    } catch (Exception ex) {
      throw new ApiCallException("Cannot start service provider working", ex);
    }
  }

  @Override
  public void close() {
    try {
      serviceProvider.close();
    } catch (Exception e) {
      log.warn("serviceProvider close error. The connection could have been lost.");
    }
  }

  @Override
  public List<ServiceInstance<Map>> getServiceInstance() {
    try {
      return Collections.singletonList(serviceProvider.getInstance());
    } catch (Exception e) {
      throw new ApiCallException("service instance fetch error", e);
    }
  }
}
