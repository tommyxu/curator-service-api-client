/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client.test.api;

import java.math.BigDecimal;
import java.util.Date;

public class AccountInfo {

  private Long id;
  private BigDecimal balance;
  private Date createDate;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  @Override
  public String toString() {
    return "AccountInfo{" +
      "id=" + id +
      ", balance=" + balance +
      ", createDate=" + createDate +
      '}';
  }
}
