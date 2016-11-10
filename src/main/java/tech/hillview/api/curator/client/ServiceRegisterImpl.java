/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by tommy on 10/11/16.
 */
public class ServiceRegisterImpl implements ServiceRegister {

  private static final Logger log = LoggerFactory.getLogger(ServiceRegisterImpl.class);

  private CuratorFramework curatorFramework;

  private ServiceDiscovery<Map>  serviceDiscovery;

  public CuratorFramework getCuratorFramework() {
    return curatorFramework;
  }

  public void setCuratorFramework(CuratorFramework curatorFramework) {
    this.curatorFramework = curatorFramework;
  }

  public ServiceRegisterImpl(CuratorFramework curatorFramework) {
    this.curatorFramework = curatorFramework;
  }

  public ServiceRegisterImpl() {
  }

  @Override
  public void registerInstance(String name, String id, String address, Integer port, Integer sslPort, String serviceType, List<String> addressList, Map<String, ?> meta) {

    Map<String, Object> metaData = new HashMap<>(meta == null ? Collections.emptyMap() : meta);
    metaData.put("addressList", addressList);
    metaData.put("serviceType", serviceType);

    ServiceInstance<Map> serviceInstance = new ServiceInstance<>(
      name,
      id,
      address,
      port, sslPort,
      metaData,
      System.currentTimeMillis(),
      ServiceType.DYNAMIC,
      null);

    serviceDiscovery = ServiceDiscoveryBuilder.builder(Map.class)
      .client(curatorFramework)
      .basePath(Constants.SERVICES_PATH)
      .serializer(new ServiceInstanceSerializer<>(Map.class))
      .thisInstance(serviceInstance)
      .build();

    try {
      serviceDiscovery.start();
    } catch (Exception e) {
      throw new RuntimeException("Cannot register service", e);
    }

    log.debug("Service {} registered", name);
  }

  @Override
  public void close() throws IOException {
    serviceDiscovery.close();

    log.debug("Service unregistered");
  }
}
