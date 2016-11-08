/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import tech.hillview.api.curator.client.exception.ApiConfigException;
import tech.hillview.api.curator.client.exception.ApiServiceException;


/**
 * Created by tommy on 7/11/16.
 */
public class ApiServiceExceptionConverterImpl implements ApiServiceExceptionConverter {
  private final ObjectMapper mapper;

  public ApiServiceExceptionConverterImpl(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public <T> Object convert(int statusCode, byte[] errorBody, Class<T> errorBodyType) {
    T error;
    try {
      if (errorBodyType.equals(String.class)) {
        error = (T) new String(errorBody);
      } else {
        error = (mapper.readValue(errorBody, errorBodyType));
      }
    } catch (Exception ex) {
      throw new ApiConfigException("Cannot parse error body to specified type " + errorBodyType);
    }
    ApiServiceException apiServiceException =
      new ApiServiceException("Got error response (status: " + statusCode + "): " + new String(errorBody), error);
    throw apiServiceException;
  }
}
