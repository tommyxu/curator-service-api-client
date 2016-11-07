/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client.exception;


public class ApiCallException extends RuntimeException {
  public ApiCallException(String message) {
    super(message);
  }

  public ApiCallException(String message, Exception e) {
    super(message, e);
  }
}
