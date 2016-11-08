/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client.test.server;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.hillview.api.curator.client.ServiceInstanceSerializer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static spark.Spark.awaitInitialization;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.stop;


@Component
public class AccountApiServer implements Runnable {
  private static final Logger log = LoggerFactory.getLogger(AccountApiServer.class);

  private int serverPort;
  private ServiceDiscovery<Map> serviceDiscovery;

  @Autowired
  private CuratorFramework curator;

  public AccountApiServer(CuratorFramework curator) {
    this.curator = curator;
  }

  @PostConstruct
  public void run() {
    serverPort = selectPort();

    port(serverPort);
    initRoute();
    awaitInitialization();
    registerService();

    waitForDiscoverySync();
  }

  private void initRoute() {
    AtomicInteger accountId = new AtomicInteger();
    get("/hello", (req, res) -> "Hello World");
    post("/api/v1/account", (req, res) -> {
      String t = req.body();
      return "" + accountId.getAndIncrement();
    });
  }

  private void waitForDiscoverySync() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public int selectPort() {
    int port = 0;
    if (port == 0) {
      try (ServerSocket s = new ServerSocket(0)) {
        port = s.getLocalPort();
      } catch (IOException e) {
        throw new RuntimeException("No listening port available.", e);
      }
    }
    return port;
  }

  @PreDestroy
  public void close() {
    try {
      serviceDiscovery.close();
    } catch (IOException e) {
      ;
    }
    stop();
  }

  private void registerService() {
    ServiceInstance<Map> serviceInstance = new ServiceInstance<>("account-service", UUID.randomUUID().toString(),
      "127.0.0.1", serverPort, null,
      new HashMap<>(),
      System.currentTimeMillis(), ServiceType.DYNAMIC,
      null);

    serviceDiscovery = ServiceDiscoveryBuilder.builder(Map.class)
      .client(curator)
      .basePath("/services") // properties.getRoot())
      .serializer(new ServiceInstanceSerializer<>(Map.class))
      .thisInstance(serviceInstance)
      .build();

    try {
      serviceDiscovery.start();
      log.debug("service registered");
    } catch (Exception e) {
      throw new RuntimeException("cannot register service", e);
    }
  }

  public void setCurator(CuratorFramework curator) {
    this.curator = curator;
  }
}
