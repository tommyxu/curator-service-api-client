/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tech.hillview.api.curator.client.test.api.AccountCreationRequest;
import tech.hillview.api.curator.client.test.api.AccountServiceApi;
import tech.hillview.api.curator.client.test.config.AccountApiTestConfig;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AccountApiTestConfig.class)
public class AccountApiTest {
  private Logger log = LoggerFactory.getLogger(AccountApiTest.class);

  @Autowired
  private AccountServiceApi serviceApi;

//  @Ignore
  @Test
  public void testAccountServiceApi() throws Exception {
    int sleepTime = 200;
    try {
      AccountCreationRequest accountCreationRequest = new AccountCreationRequest();
      accountCreationRequest.setBalance(BigDecimal.TEN);
      accountCreationRequest.setName("ACC_" + ThreadLocalRandom.current().nextInt());
      Long accountId = serviceApi.createAccount(accountCreationRequest);
      log.info("account created: {}", accountId);
    } catch (Exception ex) {
      log.error("cannot create account", ex);
    }
    log.info("wait a while");
    Thread.sleep(sleepTime);
  }
}
