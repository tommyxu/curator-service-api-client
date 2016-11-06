package tech.hillview.api.curator.client.test.api;

/**
 * JSend standard error response
 */
public class ErrorResponse {

  private String status = "error";

  private String message;

  private String code;

//  @JsonInclude(JsonInclude.Include.NON_NULL)
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
    return "ErrorResponse{" +
      "status='" + status + '\'' +
      ", message='" + message + '\'' +
      ", code='" + code + '\'' +
      ", detail='" + detail + '\'' +
      '}';
  }
}
