package tech.hillview.api.curator.client;

/**
 * Created by tommy on 5/11/16.
 */
public interface ApiClientFactory {
  <T> T create(Class<T> apiInterface);
}
