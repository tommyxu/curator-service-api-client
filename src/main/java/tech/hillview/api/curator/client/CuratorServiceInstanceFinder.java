package tech.hillview.api.curator.client;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hillview.api.curator.client.exception.ApiCallException;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * Created by tommy on 5/11/16.
 */
class CuratorServiceInstanceFinder implements ServiceInstanceFinder {
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
      .basePath("/services") // properties.getRoot())
      .serializer(new ServiceInstanceSerializer<>(Map.class))
      // .thisInstance(serviceInstance.get())
      .build();

//    try {
//      //      serviceDiscovery.start();
//      //      instances = new ArrayList<>(serviceDiscovery.queryForInstances(serviceName));
//      //      serviceDiscovery.close();
//    } catch (Exception ex) {
//      throw new ApiCallException("Cannot start service discovery", ex);
//    }
    //    return instances;

    serviceCache = serviceDiscovery.serviceCacheBuilder()
      .name(serviceName)
      .build();

    //    serviceCache.addListener(new ServiceCacheListener() {
    //      @Override
    //      public void cacheChanged() {
    ////        log.info("Instance Changed:", serviceCache.getInstances());
    //      }
    //      @Override
    //      public void stateChanged(CuratorFramework client, ConnectionState newState) {
    ////        log.warn("ZooKeeper connection state changed: {}", newState);
    //      }
    //    });
    try {
      serviceCache.start();
    } catch (Exception ex) {
      throw new ApiCallException("Cannot start service discovery", ex);
    }
  }

  //  @PostConstruct() {
//
//  }

  @PreDestroy
  public void close() {
    try {
      serviceCache.close();
    } catch (IOException e) {
      log.warn("serviceCache close error", e);
    }
  }

  @Override
  public List<ServiceInstance<Map>> getServiceInstance() {
    List<ServiceInstance<Map>> instanceList = serviceCache.getInstances();
    return instanceList;
  }
}
