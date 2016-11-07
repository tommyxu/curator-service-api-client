package tech.hillview.api.curator.client.test;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tech.hillview.api.curator.client.test.api.AccountCreationRequest;
import tech.hillview.api.curator.client.test.api.AccountServiceApi;

import java.math.BigDecimal;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AccountApiTestConfig.class)
public class AccountApiTest {
  private Logger log = LoggerFactory.getLogger(AccountApiTest.class);

  @Autowired
  private AccountServiceApi serviceApi;

  @Ignore
  @Test
  public void testAccountServiceApi() throws Exception {
    int times = 3;
    int sleepTime = 1000;
    for (int t = 0; t < times; t++) {
      try {
        Long accountId = serviceApi.createAccount(new AccountCreationRequest(BigDecimal.valueOf(times)));
        log.info("account created: {}", accountId);
      } catch (Exception ex) {
        log.error("cannot create account on #{}", t);
      }
      log.info("wait a while");
      Thread.sleep(sleepTime);
    }
  }
}
