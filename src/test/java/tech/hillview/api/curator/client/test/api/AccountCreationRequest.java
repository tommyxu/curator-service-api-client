package tech.hillview.api.curator.client.test.api;

import java.math.BigDecimal;

public class AccountCreationRequest {

  private BigDecimal initialBalance;

  public BigDecimal getInitialBalance() {
    return initialBalance;
  }

  public void setInitialBalance(BigDecimal initialBalance) {
    this.initialBalance = initialBalance;
  }

  public AccountCreationRequest() {
  }

  public AccountCreationRequest(BigDecimal initialBalance) {
    this.initialBalance = initialBalance;
  }
}
