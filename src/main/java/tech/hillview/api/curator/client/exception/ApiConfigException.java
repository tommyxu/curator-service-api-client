package tech.hillview.api.curator.client.exception;

/**
 * Created by tommy on 5/11/16.
 */
public class ApiConfigException extends RuntimeException {
  public ApiConfigException(String message, Throwable cause) {
    super(message, cause);
  }

  public ApiConfigException(String message) {
    super(message);
  }
}
