package tech.hillview.api.curator.client;

/**
 * Created by tommy on 7/11/16.
 */
public interface ApiServiceExceptionConverter {
  <T> Object convert(int statusCode, byte[] errorBody, Class<T> errorBodyType);
}
