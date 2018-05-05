/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client.chooser;

import org.apache.curator.x.discovery.ServiceInstance;
import tech.hillview.api.curator.client.ServiceInstanceChooser;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


public class RoundRobinChooser implements ServiceInstanceChooser {

  private AtomicInteger count = new AtomicInteger();

  @Override
  public Optional<ServiceInstance> chooseServiceInstance(List<ServiceInstance<Map>> instances) {
    if (instances.size() != 0) {
      int hash = count.getAndIncrement();
      return Optional.of(instances.get(hash % instances.size()));
    } else {
      return Optional.empty();
    }
  }
}
