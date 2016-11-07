package tech.hillview.api.curator.client.test;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import tech.hillview.api.curator.client.ApiClientFactory;
import tech.hillview.api.curator.client.ApiClientFactoryImpl;
import tech.hillview.api.curator.client.spring.ApiClientBeanRegister;
import tech.hillview.api.curator.client.test.api.AccountServiceApi;
import tech.hillview.api.curator.client.test.internal.WalletServiceApi;


@Configuration
@ComponentScan(basePackageClasses = WalletServiceApi.class)
public class AccountApiTestConfig {
  @Bean
  public CuratorFramework curatorFramework() throws InterruptedException {
    final String zkUri = "192.168.99.100:2181";
    CuratorFramework curator = CuratorFrameworkFactory.newClient(zkUri, new ExponentialBackoffRetry(500, 15));
    curator.start();
    curator.blockUntilConnected();
    return curator;
  }

  @Bean
  public ApiClientFactory apiClientFactory(CuratorFramework curator) {
    return new ApiClientFactoryImpl(curator);
  }

  @Bean
  public AccountServiceApi accountServiceApi(ApiClientFactory apiClientFactory) {
    return apiClientFactory.create(AccountServiceApi.class);
  }

  @Bean
  public ApiClientBeanRegister scanner(ApiClientFactory apiClientFactory) {
    ApiClientBeanRegister register = new ApiClientBeanRegister();
    register.setApiClientFactory(apiClientFactory);
    register.setPackageNames(new String[] { "tech.hillview.api.curator.client.test" });
    return register;
  }
}
