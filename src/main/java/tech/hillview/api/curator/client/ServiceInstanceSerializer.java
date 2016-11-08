/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client;

import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.InstanceSerializer;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServiceInstanceSerializer<T> extends JsonInstanceSerializer<T> implements InstanceSerializer<T>
{
  private static final Logger log = LoggerFactory.getLogger(ServiceInstanceSerializer.class);

  public ServiceInstanceSerializer(Class<T> payloadClass)
  {
    super(payloadClass);
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public ServiceInstance<T> deserialize(byte[] bytes) throws Exception
  {
    String json = new String(bytes);
    json = json.replaceAll("\"@class\"", "\"_ignore\"");
    log.debug("Parse service instance from json string (patched): {}", json);
    return super.deserialize(json.getBytes());
  }
}
