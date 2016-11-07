package tech.hillview.api.curator.client;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.apache.curator.x.discovery.UriSpec;
import tech.hillview.api.curator.client.exception.ApiConfigException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by tommy on 5/11/16.
 */
class FixedListServiceInstanceFinder implements ServiceInstanceFinder {
  private List<ServiceInstance<Map>> fixedInstances;

  public FixedListServiceInstanceFinder() {
  }

  public FixedListServiceInstanceFinder(String serviceName, String[] serverUrls) {
    registerServerUrls(serviceName, serverUrls);
  }

  public void registerServerUrls(String serviceName, String[] serverUrls) {
    fixedInstances = new ArrayList<>();
    for (String serverUrl : serverUrls) {
      URL url;
      try {
        url = new URL(serverUrl);
      } catch (MalformedURLException me) {
        throw new ApiConfigException("Server url '" + serverUrl + "' is incorrect.", me);
      }
      boolean ssl = url.getProtocol().equals("https");

      ServiceInstance<Map> serviceInstance = new ServiceInstance<>(
        serviceName,
        UUID.randomUUID().toString(),
        url.getHost(),
        ssl ? null : url.getPort(),
        ssl ? url.getPort() : null,
        new HashMap(),
        System.currentTimeMillis(),
        ServiceType.DYNAMIC,
        new UriSpec());

      fixedInstances.add(serviceInstance);
    }
  }

  @Override
  public List<ServiceInstance<Map>> getServiceInstance() {
    return fixedInstances;
  }
}
