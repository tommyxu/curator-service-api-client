/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client;

import org.apache.curator.framework.CuratorFramework;


/**
 * Created by tommy on 5/11/16.
 */
public interface ApiClientFactory {
  <T> T create(Class<T> apiInterface);

  static ApiClientFactory create(CuratorFramework curatorFramework) {
    return new ApiClientFactoryImpl(curatorFramework);
  }
}
