/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client;


import java.util.HashMap;
import java.util.Map;

public class ServiceInstancePayload {
  private String id;
  private String name;
  private Map<String, String> metadata = new HashMap<>();

  @SuppressWarnings("unused")
  private ServiceInstancePayload() {
  }

  public ServiceInstancePayload(String id, String name, Map<String, String> metadata) {
    this.id = id;
    this.name = name;
    this.metadata = metadata;
  }

  public String getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, String> getMetadata() {
    return this.metadata;
  }

  public void setMetadata(Map<String, String> metadata) {
    this.metadata = metadata;
  }

  @Override
  public String toString() {
    return "ServiceInstancePayload{" + "id='" + this.id + '\'' +
      ", name='" + this.name + '\'' +
      ", metadata=" + this.metadata +
      '}';
  }
}