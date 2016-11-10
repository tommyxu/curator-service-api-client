/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client.test.server;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.hillview.api.curator.client.ServiceRegister;
import tech.hillview.api.curator.client.util.ServiceRegisterHelper;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.ServerSocket;
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

  @Autowired
  private CuratorFramework curator;

  private ServiceRegister serviceRegister;

  public AccountApiServer(CuratorFramework curator) {
    this.curator = curator;
  }

  @PostConstruct
  public void run() {
    serverPort = selectPort(0);

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

  public int selectPort(int port) {
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
      serviceRegister.close();
    } catch (IOException e) {
      log.warn("io", e);
    }
    stop();
  }

  private void registerService() {
    serviceRegister = ServiceRegister.create(curator);
    ServiceRegisterHelper.registerHttpService(serviceRegister, "account-service", serverPort);
  }

  public void setCurator(CuratorFramework curator) {
    this.curator = curator;
  }
}
