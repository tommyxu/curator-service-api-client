package tech.hillview.api.curator.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApiClient {
  String service(); // for search in the zookeeper registry

  String[] url() default {}; // for fixed server list, no tail slash

  String path() default ""; // path prefix, /api/v1/
}
