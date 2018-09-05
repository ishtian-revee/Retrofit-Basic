# Retrofit

Some questions and necessary information regarding **Retrofit**

## 1. What is Retrofit?

* A type-safe **REST** client for **Android** and **Java**.
* Hides all messy implementation details on networking side and also low level
java connections, threading, parsing **JSON** responses and so on.
* Faster and easier development of network request.
* Works on both **Android** and **Java**.

## 2. HTTP Request Methods

Retrofit provides an annotation for each of the main standard request methods.
We simply use the appropriate ***Retrofit annotations*** for each HTTP method:
`@GET`, `@POST`, `@PUT`, `@DELETE`, `@PATCH` or `@HEAD`

## 3. An API Client (GitHub Client)

The following code defines the `GitHubClient` and a method `reposForUser` to request the list of repositories for a given user.
The `@GET` annotation declares that this request uses the **HTTP GET method**. The code snippet also illustrates the usage of
Retrofit’s path parameter replacement functionality. In the defined method the `{user}` path will be replaced with the given
variable values when calling the `reposForUser` method.

```
public interface GitHubClient {  
    @GET("/users/{user}/repos")   // the path contains the endpoint of the api
    Call<List<GitHubRepo>> reposForUser(
        @Path("user") String user
    );
}
```

---

There is a defined class `GitHubRepo`. This class comprises required class properties to map the response data.

```
public class GitHubRepo {
    // fields
    private String name;

    // getters
    public String getName(){ return this.name; }
}
```

## 4. Retrofit REST Client

After describing the API interface and the object model, it’s time to prepare an actual request. Once we have created an adapter,
we are able to create a **client**. We will use the **client** to execute the actual requests.

```
private static final String API_BASE_URL = "https://api.github.com/";

private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)                                // specify the base url of the api
            .addConverterFactory(GsonConverterFactory.create());  // need GSON to convert between java objects and JSON

// creating retrofit object
private static Retrofit retrofit = builder.build();
```

---

After doing a ton of prep work, it’s time to reap the benefits and finally make our request.
```
// create a very simple REST adapter or the client
GitHubClient client =  retrofit.create(GitHubClient.class);

// call an actual method to our client
Call<List<GitHubRepo>> call = client.reposForUser("ishtian-revee");   // it fetchs a list of the Github repositories

// execute the call asynchronously and get a positive or negative callback
call.enqueue(new Callback<List<GitHubRepo>>() {  
    @Override
    public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {
        // the network call was a success and we got a response
        // TODO: use the repository list and display it
    }

    // this will be called if there is a network failure, like no internet
    @Override
    public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {
        // the network call was a failure
        // TODO: handle error
    }
});
```

## 5. Send Objects as Request Body

Retrofit offers the ability to pass objects within the **request body**. Objects can be specified for use as HTTP request
body by using the `@Body` annotation.

```
public interface UserClient {  
    @POST("/users")
    Call<Task> createUser(@Body User user);   // retrofit will create a request body to pass user object
}
```

---

And the user class could be look like this:
```
public class User {
    // fields
    private int id;
    private String username;

    public User(int id, String username){
        this.id = id;
        this.username = username;
    }

    // getters
    public String getId(){ return this.id; }
    public String getUsername(){ return this.username; }
}
```

---

Instantiating a new `User` object fills its properties with values for id and username. Further, when passing the object to the
service class, the object fields and values will be converted to **JSON**

```
User user = new Task(1, "ishtian revee");  
Call<Task> call = taskService.createTask(task);
call.enqueue(new Callback<List<GitHubRepo>>() {  
    @Override
    public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {
        // we can access to the response body and get information
        Toast.makeText(this, "Success! User id: " + response.body().getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {
        // the network call was a failure
    }
});
```
