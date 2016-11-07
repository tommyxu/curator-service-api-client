/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client;

import tech.hillview.api.curator.client.annotation.ApiClient;
import tech.hillview.api.curator.client.exception.ApiConfigException;


/**
 * Created by tommy on 5/11/16.
 */
public class ApiInterfaceMeta {

  private String service;
  private String path;
  private Class<?> errorBodyType;
  private String[] urls;

  public <T> ApiInterfaceMeta(Class<T> apiInterface) {
    ApiClient clientAnnotation = apiInterface.getAnnotation(ApiClient.class);
    if (clientAnnotation == null) {
      throw new ApiConfigException("Annotation not found");
    }
    setPath(clientAnnotation.path());
    setUrls(clientAnnotation.url());
    setService(clientAnnotation.service().length() == 0 ? apiInterface.getSimpleName() : clientAnnotation.service());
    setErrorBodyType(clientAnnotation.errorBodyType());
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String[] getUrls() {
    return urls;
  }

  public void setUrls(String[] urls) {
    this.urls = urls;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public Class<?> getErrorBodyType() {
    return errorBodyType;
  }

  public void setErrorBodyType(Class<?> errorBodyType) {
    this.errorBodyType = errorBodyType;
  }
}
