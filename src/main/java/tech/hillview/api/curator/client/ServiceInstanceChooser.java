package tech.hillview.api.curator.client;

import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Created by tommy on 5/11/16.
 */
interface ServiceInstanceChooser {
  Optional<ServiceInstance> chooseServiceInstance(List<ServiceInstance<Map>> serviceName);
}
