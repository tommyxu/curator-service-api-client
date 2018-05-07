/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hillview.api.curator.client.util.ServiceRegisterHelper;

import java.util.List;


/**
 * Created by tommy on 11/11/16.
 */
public class ServiceRegisterHelperTest {
  private static final Logger log = LoggerFactory.getLogger(AccountApiTest.class);

  @Test
  public void testGetAllAddress() {
    List<String> result = ServiceRegisterHelper.getAddressList();
    log.info("host address list: {}", result);
  }
}
