/**
 *                                  Apache License
 *                            Version 2.0, January 2004
 *                         http://www.apache.org/licenses/
 */
package tech.hillview.api.curator.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.hillview.api.curator.client.ServiceRegister;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by tommy on 10/11/16.
 */
public class ServiceRegisterHelper {
  private static final Logger log = LoggerFactory.getLogger(ServiceRegisterHelper.class);

  public static void registerHttpService(ServiceRegister serviceRegister, String serviceName, int port) {
    List<String> addressList = getAddressList();
    serviceRegister.registerInstance(
      serviceName,
      UUID.randomUUID().toString(),
      addressList.get(0),
      port,
      null,
      "http",
      addressList,
      Collections.emptyMap()
    );
  }

  @FunctionalInterface
  private interface FunctionWithException<T, R> {
    R apply(T t) throws Exception;
  }

  private static <T, R> Function<T, R> wrapDefault(FunctionWithException<T, R> func, R defaultValue) {
    return t -> {
      try {
        return func.apply(t);
      } catch (Exception ex) {
        return defaultValue;
      }
    };
  }

  public static List<String> getAddressList() {
    return Stream.concat(Stream.concat(getAddressList(false), getAddressList(true)), Stream.of("127.0.0.1"))
      .distinct()
      .collect(Collectors.toList());
  }

  private static Stream<String> getAddressList(boolean loopback) {
    try {
      return Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
        .filter(wrapDefault(NetworkInterface::isUp, false)::apply)
        .sorted(Comparator.comparing(NetworkInterface::getIndex))
        .flatMap((ifc)->
          Collections.list(ifc.getInetAddresses()).stream()
            .filter((inet)-> !(inet instanceof Inet6Address) && (loopback == inet.isLoopbackAddress())))
        .map(InetAddress::getHostAddress)
        ;
    } catch (Exception ex) {
      log.warn("Cannot get network address list.", ex);
      return Stream.empty();
    }
  }
}
