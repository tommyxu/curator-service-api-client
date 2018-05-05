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


class AlwaysFirstChooser implements ServiceInstanceChooser {

  public AlwaysFirstChooser() {

  }

  @Override
  public Optional<ServiceInstance> chooseServiceInstance(List<ServiceInstance<Map>> instances) {
    return (instances.size() == 0) ? Optional.empty() : Optional.of(instances.get(0));
  }
}
