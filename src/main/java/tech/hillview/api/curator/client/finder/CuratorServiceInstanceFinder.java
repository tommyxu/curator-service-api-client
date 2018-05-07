/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client.finder;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.ServiceCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hillview.api.curator.client.Constants;
import tech.hillview.api.curator.client.ServiceInstanceFinder;
import tech.hillview.api.curator.client.ServiceInstanceSerializer;
import tech.hillview.api.curator.client.exception.ApiCallException;
import tech.hillview.api.curator.client.exception.ApiConfigException;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * Created by tommy on 5/11/16.
 */
public class CuratorServiceInstanceFinder implements ServiceInstanceFinder, Closeable {
  private Logger log = LoggerFactory.getLogger(CuratorServiceInstanceFinder.class);

  private final CuratorFramework curator;

  private String serviceName;
  private ServiceCache<Map> serviceCache;

  public CuratorServiceInstanceFinder(CuratorFramework curator, String serviceName) {
    this.curator = curator;
    this.serviceName = serviceName;
    createServiceCache();
  }

  synchronized private void createServiceCache() {
    ServiceDiscovery<Map> serviceDiscovery = ServiceDiscoveryBuilder.builder(Map.class)
      .client(curator)
      .basePath(Constants.SERVICES_PATH)
      .serializer(new ServiceInstanceSerializer<>(Map.class))
      .build();

    serviceCache = serviceDiscovery.serviceCacheBuilder()
      .name(serviceName)
      .build();

    serviceCache.addListener(new ServiceCacheListener() {
      @Override
      public void cacheChanged() {
        log.debug("service {} chooser changed to '{}'.", serviceName, serviceCache.getInstances());
      }
      @Override
      public void stateChanged(CuratorFramework client, ConnectionState newState) {
        log.warn("ZooKeeper connection state changed: {}", newState);
      }
    });

    try {
      serviceCache.start();
      log.debug("service cache started.");
    } catch (Exception ex) {
      throw new ApiConfigException("Cannot start service discovery", ex);
    }
  }

  @Override
  public void close() {
    try {
      serviceCache.close();
    } catch (IOException e) {
      log.warn("serviceCache close error. the connection could have been lost.");
    }
  }

  @Override
  public List<ServiceInstance<Map>> getServiceInstance() {
    List<ServiceInstance<Map>> instanceList = serviceCache.getInstances();
    return instanceList;
  }
}
