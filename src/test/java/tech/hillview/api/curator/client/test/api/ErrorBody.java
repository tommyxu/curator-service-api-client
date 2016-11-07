/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client.test.api;

public class ErrorBody {

  private String status;

  private String message;

  private String code;

  private String detail;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  @Override
  public String toString() {
    return "ErrorBody{" +
      "status='" + status + '\'' +
      ", message='" + message + '\'' +
      ", code='" + code + '\'' +
      ", detail='" + detail + '\'' +
      '}';
  }
}
