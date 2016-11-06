package tech.hillview.api.curator.client.exception;


public class ApiServiceException extends RuntimeException {

  public ApiServiceException(String message) {
    super(message);
  }

  public ApiServiceException(String message, Throwable cause) {
    super(message, cause);
  }

}
