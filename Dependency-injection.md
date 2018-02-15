
# Dagger 2 

source : Jake Wharton talks : https://www.youtube.com/watch?v=plK0zyRLIP8
 
## UnderStanding of the problem

Let's say we want to implements a Twitter application which uses a twitter API
We would start with something like this (bad code)

```java
public class Tweeter {
	public void tweet(String tweet) {
		TwitterApi api = new TwitterApi();
		api.postTweet("Jake", tweet)
	}
}
```
```java
public class TwitterApi {
	public void postTweet(String user, String tweet) {
		OkHttpClient client = new OkHttpClient();
		client.newCall...
	}
}
```
```java
Tweeter tweetApp  = new Tweeter();
tweetApp.tweet("my new tweet")
```

Here we are recreating the OkHttpClient & TwitterApi each time we use it, which is very bad. So we should first use a var for the client

```java
public class TwitterApi {
	OkHttpClient client;

	public TwitterApi(OkHttpClient client) {
		this.client = client;
	}

	public void postTweet(String user, String tweet) {
		mClient.newCall...
	}
}
```
```java
public class Tweeter {
	private final TwitterApi api;
	private final String user;

	public Tweeter(TwitterApi api, String user) {
		this.api = api;
	}

	public void tweet(String tweet) {
		api.postTweet("Jake", tweet)
	}
}
```

### Add complexity 
What if i want to add  new fonctionality thats need Timeline 
```java
public class Timeline {
	private final TwitterApi api;
	private final String user;

	public Timeline(TwitterApi api, String user) {
		this.api = api;
	}
}
```
```java
// Usage 
TwitterApi twitterApi = new TwitterApi(new OkHttpClient()); //--> NetWork Module
Tweeter tweeter = new Tweeter(twitterApi); 		// --> TwitterModule
Timeline timeline = new Timeline(twitterApi);	// --> TwitterModule
```

Both Tweeter and Timeline needs the same object..

## Introduction to Dagger2

### Contructors Injection
Let's use Dagger with Constructor Injection

```java
@Module
public class NetWorkModule {

	@Provides	@Singleton
	OkHttpClient provideOkHttp() {
		return new OkHttpClient();
	}

	@Provides	@Singleton
	TwitterApi provideTwitterApi(OkHttpClient client) {
		return new TwitterApi(client);
	}
}
```
Here we create a module that take care of the creation of OkHttp & the Twitter Api. 
Dagger is looking to the methods return types to complete the graph 

```java
@Module 
public class TwitterModule {
	private final String user;

	public TwitterModule(String user) {
		this.user = user;
	}

	@Provides @Singleton
	Tweeter provideTweeter(TwitterApi api) { // Here Dagger knows TwitterApi because it's declared in another module
		return new Tweeter(api, this.user)
	}
	

	@Provides @Singleton
	TimeLine provideTimeline(TwitterApi api) {
		return new TimeLine(api, this.user)
	}
} 
```

now that we have our two modules, we can Inject the Deps like below :

```java
public class Tapp {
	private final Tweeter tweeter;
	private final Timeline timeline; 

	@Inject 
	public Tapp(Tweeter tweeter, Timeline timeline) { //the two parameters will be the deps to inject
		this.tweeter = tweeter;  //because var is final, the tweeter will be immutable
		this.timeline = timeline;
	}
}
```

#### Downstream injection 
Because Dagger know how to create the OkHttpClient, we can directly use @Inject in the TwitterApi and remove the provides like below :

```java
@Module
public class NetWorkModule {

	@Provides	@Singleton
	OkHttpClient provideOkHttp() {
		return new OkHttpClient();
	}

	// @provides @Singleton provideTwitterApi(OkHttpClient client) has been deleted
}
```
```java
@Singleton //singleton is place on top of the class
public class TwitterApi {
	OkHttpClient client;

	@Inject 
	public TwitterApi(OkHttpClient client) {
		this.client = client;
	}

	public void postTweet(String user, String tweet) {
		mClient.newCall...
	}
}
```

## Method Injection
Properties
- @Inject on methods
- Method parameters are dependencies
- Injection happens afer object is fully instantiated
- Only one valid use case: passing 'this' to a dependencies

```java
public class Tapp {
	....

	@Inject 
	public void enableStreaming(Streaming streaming) {
		streaming.register(this);
	}
}
```

## Field Injection

- @Inject on fields
- Fields may not be private of final
- Injection happens afer object is fully instantiated
- Object is usually responsible for or aware of injection

```java
public class Tapp {
	@Inject Tweeter;
	@Inject Timeline;
}
```
tada !! Not so fast !
To really make it happens we need the following component.

## Components

### Components : Basic knowledge
Properties 
- Bridge between modules an injection
- Component are interface

```java
@Singleton // All deps inside will be singleton
@Component(modules = { 
	NetworkModule.class,
	TwitterModule.class })// In the component, we list the modules that are use by this component
public interface TwitterComponent {
	//write abstract method declaration
	Tweeter Tweeter();
	Timeline TimeLine();
}
```

```java
TwitterComponent component = Dagger_TwitterComponent.builder() //meths that matches listed module
//  .networkModule(new NetworkModule())  //NOT REQUIRED CAUSE  IMPLICIT DEFAULT CONST
	.twitterModule(new TwitterModule("Kelvin"))
	.build()


Tweeter tweeter = component.tweeter();
Timeline timeline = component.timeline();
```

### Components : Use Fields Injection 

For instance, for the following Activity.
```java
public class TwittActivity {
	@Inject Tweeter tweeter;
	@Inject Timeline timeline;
	// ...
}
```

If we want to use fields injection, we must pass our App to the component to let him inject those fields.

```java
@Singleton 
@Component(modules = { 
	NetworkModule.class,
	TwitterModule.class })
public interface TwitterComponent {
	void injectActivity(TwittActivity twittActivity);
}
```
```java
TwitterComponent component = Dagger_TwitterComponent.builder() //meths that matches listed module
	.twitterModule(new TwitterModule("Kelvin"))
	.build()

TwittActivity activity = // Android creates activity
component.injectActivity(activity);
```

Instead of void inject...() we can directly return the instance of the injected object :

```java
@...
public interface TwitterComponent {
	TwittActivity injectActivity(TwittActivity twittActivity);
}

TwittActivity activity = // Android creates activity
component.injectActivity(activity).method();
```

## Scopes 

we use to have only one component but now we are going to split them up.

```java
@Singleton
@Component(modules = NetworkModule.class) 
public interface ApiComponent {
	TwitterApi api();  // must explicitly be declared for the other component to use it 
}
```
```java
// ---@Singleton---- scope are limited to only one component
@Component(
	dependencies = ApiComponent.class  // This component now depend on the ApiComponent, and can no longer be create whitout it
	modules = TwitterModule.class) 		// We did this because TwitterModules needs the TwitterApi
public interface TwitterComponent {
	TwitterApp app();
}
```

Here are both modules that are used 
Because the component is unscoped the Modules that the component use need to be unscoped too*

```java
@Module 
public class TwitterModule {
	private final String user;

	public TwitterModule(String user) {
		this.user = user;
	}

	@Provides //---@Singleton---
	Tweeter provideTweeter(TwitterApi api) { // Here Dagger knows TwitterApi because it's declared in another module
		return new Tweeter(api, this.user)
	}
	

	@Provides //---@Singleton---
	TimeLine provideTimeline(TwitterApi api) {
		return new TimeLine(api, this.user)
	}
} 
```
```java
@Module
public class NetWorkModule {

	@Provides	@Singleton
	OkHttpClient provideOkHttp() {
		return new OkHttpClient();
	}
}
```

Lets build those two component 
```java
ApiComponent ApiComponent = Dagger_ApiComponent.create();

TwitterComponent twitterComponent = Dagger_TwitterComponent.builder()
	.ApiComponent(ApiComponent)
	.twitterModule(new TwitterModule("Kelvin"))
	.build():

component.app.run();
```

### Custom Scopes

Properties

- single instance
- Singleton is the largest scope 
- use to shorter lifetime

```java
@Scope
public @interface User {
}
```
```java
@User
@Component(
	dependencies = ApiComponent.class  // This component now depend on the ApiComponent, and can no longer be create whitout it
	modules = TwitterModule.class) 		// We did this because TwitterModules needs the TwitterApi
public interface TwitterComponent {
	TwitterApp app();
}
```


```java
@Module 
public class TwitterModule {
	private final String user;

	public TwitterModule(String user) {
		this.user = user;
	}

	@Provides @User
	Tweeter provideTweeter(TwitterApi api) { // Here Dagger knows TwitterApi because it's declared in another module
		return new Tweeter(api, this.user)
	}
	

	@Provides @User
	TimeLine provideTimeline(TwitterApi api) {
		return new TimeLine(api, this.user)
	}
} 
```

So the component's scope is @User.
We instanciate the TwitterComponent for a given user. But when he logs out for example, we just have to throw away thatinstance and the ApiComponent(network module) will still be here for the next client ;)