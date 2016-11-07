/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.util.ClassUtils;
import tech.hillview.api.curator.client.ApiClientFactory;
import tech.hillview.api.curator.client.annotation.ApiClient;


/**
 * Created by tommy on 8/11/16.
 */
class ApiClientClassPathScanner extends ClassPathBeanDefinitionScanner {

  private static final Logger log = LoggerFactory.getLogger(ApiClientClassPathScanner.class);

  private static final String apiClientAnnotationName = ApiClient.class.getName();

  private ApiClientFactory apiClientFactory;

  public ApiClientClassPathScanner(BeanDefinitionRegistry registry, ApiClientFactory apiClientFactory) {
    super(registry, false);

    this.apiClientFactory = apiClientFactory;

    initFilters();
  }

  private void initFilters() {
    addIncludeFilter((metadataReader, metadataReaderFactory) -> {
      if (metadataReader.getAnnotationMetadata().hasAnnotation(apiClientAnnotationName)) {
        log.debug("@ApiClient detected on type: {}", metadataReader.getClassMetadata().getClassName());
        return true;
      } else {
        return false;
      }
    });
  }

  @Override
  protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
    if (registry instanceof SingletonBeanRegistry) {
      SingletonBeanRegistry beanRegistry = (SingletonBeanRegistry)registry;
      beanRegistry.registerSingleton(definitionHolder.getBeanName(),
        apiClientFactory.create(ClassUtils.resolveClassName(definitionHolder.getBeanDefinition().getBeanClassName(), null)));
    }
  }

  @Override
  protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
    return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
  }
}
