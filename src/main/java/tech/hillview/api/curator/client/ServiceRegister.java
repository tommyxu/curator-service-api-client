/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client;

import org.apache.curator.framework.CuratorFramework;

import java.io.Closeable;
import java.util.List;
import java.util.Map;


/**
 * Created by tommy on 10/11/16.
 */
public interface ServiceRegister extends Closeable {
  /**
   * register a service instance. default implementation is registering to ZooKeeper via Curator.
   * @param name service name
   * @param id instance id, usually a UUID
   * @param address ip address
   * @param port access port
   * @param sslPort ssl port, can be null
   * @param serviceType as a meta field. such as "http" or "tcp", can be null
   * @param addressList as a meta field. list of addresses to help discovery program to locate service intelligently, can be null
   * @param meta other meta data, can be null
   */
  void registerInstance(
    String name,
    String id,
    String address,
    Integer port,
    Integer sslPort,
    String serviceType,
    List<String> addressList,
    Map<String, ?> meta);

  static ServiceRegister create(CuratorFramework curatorFramework) {
    return new ServiceRegisterImpl(curatorFramework);
  }
}
