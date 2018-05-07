/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client.test.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.hillview.api.curator.client.ApiClientFactory;
import tech.hillview.api.curator.client.spring.ApiClientBeanRegister;
import tech.hillview.api.curator.client.test.server.AccountApiTestServer;


@Configuration
public class AccountApiTestConfig {

  @Bean
  public CuratorFramework curatorFramework() throws InterruptedException {
    final String zkUri = "y550:32693/qname";
    CuratorFramework curator = CuratorFrameworkFactory.newClient(zkUri, new ExponentialBackoffRetry(500, 15));
    curator.start();
    curator.blockUntilConnected();
    return curator;
  }

//  @Bean
//  public AccountServiceApi accountServiceApi(ApiClientFactory apiClientFactory) {
//    return apiClientFactory.create(AccountServiceApi.class);
//  }

//  @Bean
//  public CuratorFramework curatorFramework() throws Exception {
//    final TestingServer zkTestServer = new TestingServer(true);
//
//    String connectString = zkTestServer.getConnectString();
//
//    CuratorFramework curator = CuratorFrameworkFactory.newClient(connectString, new ExponentialBackoffRetry(500, 15));
//    curator.start();
//    curator.blockUntilConnected();
//
//    return curator;
//  }

  @Bean
  public ApiClientFactory apiClientFactory(CuratorFramework curator) {
    return ApiClientFactory.create(curator);
  }

  @Bean
  public static ApiClientBeanRegister scanner(ApiClientFactory apiClientFactory) {
    ApiClientBeanRegister register = new ApiClientBeanRegister();
    register.setApiClientFactory(apiClientFactory);
    register.setPackageNames(new String[] { "tech.hillview.api.curator.client.test" });
    return register;
  }

  @Bean
  public AccountApiTestServer apiServer(CuratorFramework curator) {
    return new AccountApiTestServer(curator);
  }
}
