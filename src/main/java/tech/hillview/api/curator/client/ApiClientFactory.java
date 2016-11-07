/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client;

/**
 * Created by tommy on 5/11/16.
 */
public interface ApiClientFactory {
  <T> T create(Class<T> apiInterface);
}
