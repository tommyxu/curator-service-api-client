/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client;

import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


class StickyServiceChooser implements ServiceInstanceChooser {

  public StickyServiceChooser() {

  }

  @Override
  public Optional<ServiceInstance> chooseServiceInstance(List<ServiceInstance<Map>> instances) {
    return (instances.size() == 0) ? Optional.empty() : Optional.of(instances.get(0));
  }
}
