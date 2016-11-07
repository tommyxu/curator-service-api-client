package tech.hillview.api.curator.client;

import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.Map;


/**
 * Created by tommy on 5/11/16.
 */
interface ServiceInstanceFinder {
  List<ServiceInstance<Map>> getServiceInstance();
}
