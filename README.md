# curator-service-api-client

API client factory for Restful services registered in *ZooKeeper* via *Curator* or *Spring Cloud* with features like **Service Discovery** and **Load balancing**.


Table of Contents
=================

  * [What's This](#whats-this)
  * [Features](#features)
  * [Usage](#usage)
     * [Prepare an API interface](#prepare-an-api-interface)
     * [Apply @ApiClient annotation](#apply-apiclient-annotation)
     * [Create ApiClientFactory](#create-apiclientfactory)
     * [Create ApiClient implementation](#create-apiclient-implementation)
  * [Spring Integration](#spring-integration)


## What's This

Usually, services are registered in ZooKeeper at *'/services'* like the following structure:

```
/services
/services/account-service
/services/account-service/ddb643f3-6240-428b-9083-b13e6db2230
/services/account-service/466885c8-1589-40ed-8310-c10b487d050b
```

> If you don't know how to register services in ZooKeeper. You can use *Spring Cloud ZooKeeper*, or *Curator Framework (with Service Discovery Extenstion)* directly.

You try to call its RESTful API on them, but without any mood to dealing with instance discovery and load-balancing.

You want a client factory to create one for you!


## Features

* Service instances discovery is based on *Curator Framework* directly. *Spring Cloud* is **NOT** required. In our testing, *Spring Cloud* forces you to enable web environment to discover other service instances which means you have to start web environment and listen on a port.
* If more than one instances are found in ZooKeeper, every API-call selects the next service instance (Round-Robin).
* The change of service instances will be automatically reflected in the coming API calls even after the client is created. It is the basis, of course.
* Protect your investment to re-use your *Retrofit* Java interface definition. You can easily remove this project off your dependencies when you decide to use *Retrofit* only, hope not.
* Support fixed list of server urls in testing if ZooKeeper environment is not passed in.
* Loosen *Retrofit* standard. Both `Call<T>` and `T` are supported as return type.

## Usage

### Prepare an API interface

Have your API client annotated in [Retrofit](https://square.github.io/retrofit/) style.

```java
public interface AccountServiceApi {
    @POST("account")
    Long createAccount(@Body AccountCreationRequest request);

    @GET("account/{accountId}")
    AccountInfo getAccount(@Path("accountId") Long accountId);
}
```

**NOTE**: You can also use classic Retrofit `Call<T>` as return type. But do **NOT** mix them in the same interface. We cannot implement mixing style without modifying Retrofit itself.

### Apply @ApiClient annotation

```java
@ApiClient(service = "account-service", path = "/api/v1/", url = "http://localhost:8080")
public interface AccountServiceApi {
    // ...
}
```

Annotation properties:

* service: service name registered in ZooKeeper `/services`
* path: a path prefix. It is prepend before the target url defined in @GET/@POST
* url: a list of server address (host:port). This is optional if you only use ZooKeeper to find service instances.
* errorBodyType: the Class<?> of the errorBody. The body of an error http response (4xx, 5xx) will be converted to this type via JacksonMapper. The default value is `String.class` which means no conversion required.

> All java classes provided by this project are under package *tech.hillview.api.curator.client*

### Create ApiClientFactory

```java
CuratorFramework curator = CuratorFrameworkFactory.newClient(zkUri, ...);
curator.start();
curator.blockUntilConnected();

ApiClientFactory apiClientFactory = new ApiClientFactoryImpl(curator);
```

If no curator is provided (or null), the ```url``` property on @ApiClient annotation is used to locate service instances.

### Create ApiClient implementation

```java
AccountServiceApi accountApi = apiClientFactory.create(AccountServiceApi.class);
```

All's done. Now you can call:

```java
accountApi.getAccount(500L);
```

> If HTTP response error (4xx or 5xx) is caught, an ApiServiceException is thrown out. If IO error occurs, an ApiCallException is thrown out.

## Spring Integration

We provide a scanner to create and register `@ApiClient` bean automatically. So you can `@Autowired` these clients later.

```java
@Bean
public CuratorFramework curatorFramework() throws InterruptedException {
  final String zkUri = "localhost:2181";
  CuratorFramework curator = CuratorFrameworkFactory.newClient(zkUri, new ExponentialBackoffRetry(2000, 15));
  curator.start();
  curator.blockUntilConnected();
  return curator;
}

@Bean
public ApiClientFactory apiClientFactory(CuratorFramework curator) {
  return new ApiClientFactoryImpl(curator);
}

@Bean
public ApiClientBeanRegister scanner(ApiClientFactory apiClientFactory) {
  ApiClientBeanRegister register = new ApiClientBeanRegister();
  register.setApiClientFactory(apiClientFactory);
  register.setPackageNames(new String[] { "tech.hillview.api.curator.client.test" });
  return register;
}
```

In your service code:

```java
@Service
class UserService {

  @Autowired
  private AccountServiceApi accountServiceApi;

  // ...
}
```

Note:

* The implementation return from `ApiClientFactory.create()` is **Thread-Safe**. You only need one ApiClient instance for each service. Best used in singleton pattern with IoC framework, such as *Spring Framework* or *Guice*.
* The creation of *CuratorFramework* instance is not necessary if you are using *Spring Cloud ZooKeeper*. Re-use it if possible.
