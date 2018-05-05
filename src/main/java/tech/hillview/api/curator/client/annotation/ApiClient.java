/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApiClient {
  String service(); // service name in the zookeeper registry

  String path() default ""; // path prefix, /api/v1/

  Class<?> errorBodyType() default String.class;

  String[] url() default {}; // for fixed server list, no tail slash
}
