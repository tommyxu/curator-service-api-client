/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import tech.hillview.api.curator.client.ApiClientFactory;

import java.util.HashSet;
import java.util.Set;


public class ApiClientBeanRegister implements BeanDefinitionRegistryPostProcessor {
  private static final Logger log = LoggerFactory.getLogger(ApiClientBeanRegister.class);

  private String[] packageNames;
  private ApiClientFactory apiClientFactory;

  public ApiClientBeanRegister() {
  }

  public ApiClientBeanRegister(ApiClientFactory apiClientFactory, String packageName) {
    this.apiClientFactory = apiClientFactory;
    this.packageNames = new String[] { packageName };
  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
    ApiClientClassPathScanner scanner = new ApiClientClassPathScanner(registry, apiClientFactory);
    scanner.scan(packageNames);
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
  }

  public String[] getPackageNames() {
    return packageNames;
  }

  public void setPackageNames(String[] packageNames) {
    this.packageNames = packageNames;
  }

  public void setPackageClasses(Class<?> packageClasses[]) {
    Set<String> packageNames = new HashSet<>();
    for (Class<?> packageClass : packageClasses) {
      packageNames.add(packageClass.getPackage().getName());
    }
    setPackageNames(packageNames.toArray(new String[0]));
  }

  public ApiClientFactory getApiClientFactory() {
    return apiClientFactory;
  }

  public void setApiClientFactory(ApiClientFactory apiClientFactory) {
    this.apiClientFactory = apiClientFactory;
  }
}
