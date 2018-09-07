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

## 6. Logging Interceptor

While developing your app and for debugging purposes it is nice to have a log feature integrated to show request and response
information. Since logging is not integrated by default anymore in Retrofit 2, we need to add a **logging interceptor** for **OkHttp**.
Luckily **OkHttp** already ships with this interceptor and we only need to activate it for our `OkHttpClient`.

```
// create OkHttp client
OkHttpClient.Builder httpClient = new OkHttpClient.Builder();  

// create logging interceptor
HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
logging.setLevel(Level.BODY);

// add logging as last interceptor
httpClient.addInterceptor(logging);  // <-- this is the important line!

Retrofit retrofit = new Retrofit.Builder()  
   .baseUrl(API_BASE_URL)
   .addConverterFactory(GsonConverterFactory.create())
   .client(httpClient.build())
   .build();
```

---

### Log Levels

The logging interceptor allows us to change how much data should be logged and it has 4 different levels. These are:

1. `NONE`: No logging. Use this log level for production environments to enhance your apps performance by skipping any logging operation.
2. `BASIC`: Log request type, url, size of request body, response status and size of response body.
3. `HEADERS`: Log request and response headers, request type, url, response status.
4. `BODY`: Log request and response headers and body. This is the most complete log level and will print out every related information
for your request and response.


# 7. Uploading Files to Server

Using Retrofit 2, we need to use either OkHttp’s `RequestBody` or `MultipartBody.Part` classes and encapsulate our file into a request body.
Let’s have a look at the interface definition for file uploads.

```
public interface FileUploadService {  
    @Multipart
    @POST("upload")
    Call<ResponseBody> upload(
        @Part("description") RequestBody description,
        @Part MultipartBody.Part file
    );
}
```

Here,

* The description is just a string value wrapped within a `RequestBody` instance.
* We use the `MultipartBody.Part` class that allows us to send the actual file name besides the binary file data with the request.

---

### Android Client Code

```
private void uploadFile(Uri fileUri) {  
    // create upload service client
    FileUploadService service = ServiceGenerator.createService(FileUploadService.class);

    // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
    // use the FileUtils to get the actual file by uri
    File file = FileUtils.getFile(this, fileUri);

    // create RequestBody instance from file
    RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);

    // MultipartBody.Part is used to send also the actual file name
    MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

    // add another part within the multipart request
    String descriptionString = "hello, this is description speaking";
    RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);

    // finally, execute the request
    Call<ResponseBody> call = service.upload(description, body);
    call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            Log.v("Upload", "success");
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Log.e("Upload error:", t.getMessage());
        }
    });
}
```

# 8. Passing Multiple Parts

`@PartMap` is an additional annotation for a request parameter, which allows us to specify how many and which parts we send during runtime.
This can very helpful if your form is very long, but only a few of those input field values are actually send.

```
public interface FileUploadService {  
    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadFileWithPartMap(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part file);
}
```

---

Finally, let's use this method and view the entire code from creating the Retrofit service, to filling the request with data and enqueuing the request:

```
Uri fileUri = ... // from a file chooser or a camera intent

// create upload service client
FileUploadService service =  
        ServiceGenerator.createService(FileUploadService.class);

// create part for file (photo, video, ...)
MultipartBody.Part body = prepareFilePart("photo", fileUri);

// create a map of data to pass along
RequestBody description = createPartFromString("hello, this is description speaking");  
RequestBody place = createPartFromString("Magdeburg");  
RequestBody time = createPartFromString("2016");

HashMap<String, RequestBody> map = new HashMap<>();  
map.put("description", description);  
map.put("place", place);  
map.put("time", time);

// finally, execute the request
Call<ResponseBody> call = service.uploadFileWithPartMap(map, body);  
call.enqueue(...);  
```
