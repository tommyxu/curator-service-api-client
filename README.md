# curator-service-api-client

API client factory for services registered in *ZooKeeper* via *Curator* or *Spring Cloud* with features like **Service Discovery** and **Load balancing**.

## What‘s This

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

* Service instances discovery is based on *Curator Framework* directly. *Spring Cloud* is not required. In our testing, Spring Cloud forces you to enable web environment to discover other service instances which means you have to listen on a port.
* If more than one instances are found in ZooKeeper, each API call selects the next instance (Round-Robin).
* Save your investment to re-use mature *Retrofit* Java interface definition. You can easily remove this off your dependencies when you decide to use *Retrofit* only, hope not.
* Support fixed list of server urls in testing if ZooKeeper environment is not passed in.


## Usage

### Prepare an API interface

Have your API client annotated in [Retrofit](https://square.github.io/retrofit/) style.

**NOTE**: Remember to change the return type from `Call<T>` to `T`. We will remove this restriction for the next release. Actually, we've done some work to support raw `T`.

```java
public interface AccountServiceApi {
    @POST("account")
    Long createAccount(@Body AccountCreationRequest request);

    @GET("account/{accountId}")
    AccountInfo getAccount(@Path("accountId") Long accountId);
}
```

### Apply @ApiClient annotation on it.

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

> All java classes provided by this project are under package *tech.hillview.api.curator.client*

### Create ApiClientFactory

```java
// CuratorFramework curator = CuratorFrameworkFactory.newClient(zkUri, ...);
//
// or
//
// @Bean /* SpringFramework */
// public ApiClientFactory apiClientFactory(CuratorFramework curator) ...

ApiClientFactory apiClientFactory = new ApiClientFactoryImpl(curator);
```

If no curator is provided (or null), the ```url``` property on @ApiClient annotation is used as service instances.

### Create ApiClient implementation

```java
AccountServiceApi accountApi = apiClientFactory.create(AccountServiceApi.class);
```

All's done. Now you can call

```java
accountApi.getAccount(500L);
```

## Consideration

* The implementation returned is **Thread Safe**. You only need one instance for each service. Best used with IoC, like *Spring Framework* or *Guice*.
* *CuratorFramework* instance creation is not necessary if you are using *Spring Cloud ZooKeeper*. Reuse it.
