package tech.hillview.api.curator.client;

import org.apache.curator.x.discovery.ServiceInstance;


/**
 * Created by tommy on 5/11/16.
 */
interface ApiInvokerCreator {
  <T> T getInvoker(Class<T> apiInterface, ApiInterfaceMeta apiInterfaceMeta, ServiceInstance serviceInstance);
}
