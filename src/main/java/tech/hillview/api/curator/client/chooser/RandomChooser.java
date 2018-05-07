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
import java.util.Random;


class RandomChooser implements ServiceInstanceChooser {
  private Random random = new Random();

  public RandomChooser() {

  }

  @Override
  public Optional<ServiceInstance> chooseServiceInstance(List<ServiceInstance<Map>> instances) {
    return (instances.size() == 0) ? Optional.empty() : Optional.of(instances.get(random.nextInt(instances.size())));
  }
}
