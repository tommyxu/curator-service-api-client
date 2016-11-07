/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client.exception;


public class ApiServiceException extends RuntimeException {

  public ApiServiceException(String message) {
    super(message);
  }

  public ApiServiceException(String message, Object body) {
    super(message);
    this.body = body;
  }

  public ApiServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  private Object body;


  public Object getBody() {
    return body;
  }

  public void setBody(Object body) {
    this.body = body;
  }

  @Override
  public String toString() {
    return "ApiServiceException{" + super.toString() +
      " body=" + body +
      '}';
  }
}
