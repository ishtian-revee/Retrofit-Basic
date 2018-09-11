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


## 7. Uploading Files to Server

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

## 8. Passing Multiple Parts

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

## 9. Uploading Multiple Files to Server

There are two ways to upload multiple files to server:

* Fixed
* Dynamic

We can use `List<>` for multiple files. Our interface can be look like this:

```
public interface FileUploadService {  
    // new code for multiple files
    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadMultipleFiles(
            @Part("description") RequestBody description,
            @Part List<MultipartBody.Part> files);
}
```

---

And now our method can be this:

```
...

List<MultipartBody.Part> parts = new ArrayList<>();

for(int i=0; i<fileUris.size(); i++){
  parts.add(prepareFilePart("" + i, fileUris.get(i)));
}

// finally, execute the request
Call<ResponseBody> call = service.uploadFileWithPartMap(createPartFromString(descriptio.getText().toString()), parts);

call.enqueue(...);  
```

## 10. Custom Request Headers

Retrofit provides two options to define HTTP request header fields:

* **Static**: Static headers can’t be changed for different requests. The header’s key and value are fixed and initiated with the app startup.
* **Dynamic**:  In contrast, dynamic headers must be set for each request.

### Static Headers

```
public interface UserService {  
    @Headers("Cache-Control: max-age=640000")
    @GET("/tasks")
    Call<List<Task>> getTasks();
}
```

```
public interface UserService {  
    @Headers({
        "Accept: application/vnd.yourapi.v1.full+json",
        "User-Agent: Your-App-Name"
    })
    @GET("/tasks/{task_id}")
    Call<Task> getTask(@Path("task_id") long taskId);
}
```

---

### Dynamic Headers

```
public interface UserService {  
    @GET("/tasks")
    Call<List<Task>> getTasks(@Header("Content-Range") String contentRange);
}
```

## 11. Synchronous and Asynchronous Request

 Retrofit supports **synchronous** and **asynchronous** request execution. Users define the concrete execution by setting a return type (synchronous) or not
 (asynchronous) to service methods.

 All we were doing all the way from the beginning was actually the asynchronous way.

 ***NOTE: If we run a synchronous network request on the UI thread, it will hold the entire UI until the request is done. That is why it makes
 the app crashes every time whenever we run a synchronous network request in UI thread. So make sure we always run synchronous network
 request on the thread which is not the UI thread.***

 The code given below could be the class for background thread for synchronous network request:

 ```
 public class BackgroundService extends IntentService {

    public BackgroundService() {
        super("Background Service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        User user = new User("Revee", "Engineer");

        // create retrofit instance
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://reqres.in/api/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        // get client and call object for the request
        UserClient client = retrofit.create(UserClient.class);
        Call<User> call = client.createAccount(user);

        // synchronous way of network request
        try {
            Response<User> result = call.execute();
            Log.i("Retrofit", "success!");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Retrofit", "failure!");
        }
    }
}
 ```

 ---

And in the UI thread we can call like this:

```
Intent intent = new Intent(PostActivity.this, BackgroundService.class);
startActivity(intent);
```

## 12. Manage Request Headers in OkHttp Interceptor

Adding HTTP request headers is a good practice to add information for API requests. A common example is authorization using the `Authorization` header field.
If we need the header field including its value on almost every request, we can use an interceptor to add this piece of information. This way, we don’t
need to add the `@Header` annotation to every endpoint declaration.

```
OkHttpClient.Builder httpClient = new OkHttpClient.Builder();  
httpClient.addInterceptor(new Interceptor() {  
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        // Request customization: add request headers
        Request.Builder requestBuilder = original.newBuilder()
                .header("Authorization", "auth-value"); // <-- this is the important line

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
});

OkHttpClient client = httpClient.build();
```

---

Using Retrofit 2 and an OkHttp interceptor, you can add multiple request headers with the same key. The method you need to use is `.addHeader`.

**NOTE:**
* `.header(key, val)`: will override preexisting headers identified by key
* `.addHeader(key, val)`: will add the header and don’t override preexisting ones

## 13. Dynamic URLs

Actually, it only requires us to add a single String parameter annotated with `@Url` in your endpoint definition.

```
public interface UserService {  
    @GET
    public Call<ResponseBody> profilePicture(@Url String url);
}
```

---

And the client code for using dynamic url can be:

```
// get a dynamic URL from the API
String profilePhoto = "https://s3.amazon.com/profile-picture/path";

UserService service = retrofit.create(UserService.class);  
service.profilePicture(profilePhoto);
```

## 14. Downloading Files from Server

The interface can be look like this:

```
public interface FileDownloadClient {

    @GET("images/futurestudio-university-logo.png")
    Call<ResponseBody> downloadFile();
}
```

---

As we are downloading files we need to make sure we have every permissions that required in `onColplete()` method:

```
if(ContextCompat.checkSelfPermission(DownloadActivity.this,
    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

    ActivityCompat.requestPermissions(DownloadActivity.this, new String[]{
          Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
}
```

---

### How to call request

```
FileDownloadService downloadService = ServiceGenerator.create(FileDownloadService.class);
Call<ResponseBody> call = downloadService.downloadFileWithDynamicUrlSync(fileUrl);

call.enqueue(new Callback<ResponseBody>() {  
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccess()) {
            Log.d(TAG, "server contacted and has file");
            boolean writtenToDisk = writeResponseBodyToDisk(response.body());
            Log.d(TAG, "file download was a success? " + writtenToDisk);
        } else {
            Log.d(TAG, "server contact failed");
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        Log.e(TAG, "error");
    }
});
```

---

### How to save the file

```
private boolean writeResponseBodyToDisk(ResponseBody body) {  
    try {
        // todo change the file location/name according to your needs
        File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "Future Studio Icon.png");
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            byte[] fileReader = new byte[4096];
            long fileSize = body.contentLength();
            long fileSizeDownloaded = 0;

            inputStream = body.byteStream();
            outputStream = new FileOutputStream(futureStudioIconFile);

            while (true) {
                int read = inputStream.read(fileReader);

                if (read == -1) { break; }

                outputStream.write(fileReader, 0, read);
                fileSizeDownloaded += read;
                Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
            }
            outputStream.flush();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (inputStream != null) { inputStream.close(); }

            if (outputStream != null) { outputStream.close(); }
        }
    } catch (IOException e) { return false; }
}
```

***NOTE: If you’re downloading a large file, Retrofit would try to move the entire file into memory. In order to avoid that, we've to add
a special annotation to the request declaration which is @Streaming***

## 15. Simple Error Handling

The best way to handle simple errors can be done by this in our call function:

```
call.enqueue(new Callback<User2>() {
            @Override
            public void onResponse(Call<User2> call, Response<User2> response) {
                // this means at least we got the response
                if (response.isSuccessful()) {
                    showMessage("server returned user: " + response.body());
                }else{      // server overloaded type errors, incorrect input errors
                    // we can handle it like this in simple way
//                    switch (response.code()) {
//                        case 404:
//                            showMessage("server returned error: user not found!");
//                            break;
//                        case 500:
//                            showMessage("server returned error: server is broken!");
//                            break;
//                        default:
//                            showMessage("server returned error: unknown error!");
//                    }

                    // we can also display error body
//                    try {
//                        showMessage("server returned error: " + response.errorBody().string());
//                    } catch (IOException e) {
//                        showMessage("Unknown error!");
//                        e.printStackTrace();
//                    }

                    // the best way to simple error handling
                    ApiError apiError = ErrorUtils.parseError(response);
                    showMessage(apiError.getMessage());
                }
            }
```

---

The java object for the error can be represented by the following class:

```
public class ApiError {
    // fields
    private int statusCode;
    private String endPoint;
    private String message = "Unknown error.";

    public int getStatusCode(){ return this.statusCode; }
    public String getEndPoint(){ return this.endPoint; }
    public String getMessage(){ return this.message; }
}
```

---

### Simple Error Handler

We will make use of the following class only having one `static` method which returns an `APIError` object. The `parseError` method expects
the response as parameter. Further, we need to make our Retrofit instance available to apply the appropriate response converter for the
received **JSON** error response.

```
public class ErrorUtils {
    public static ApiError parseError(Response<?> response){
        Converter<ResponseBody, ApiError> converter =
                GetUserIdActivity.retrofit.responseBodyConverter(ApiError.class, new Annotation[0]);

        ApiError error;
        try {
            error = converter.convert(response.errorBody());
        }catch (IOException e){
            return new ApiError();
        }
        return error;
    }
}
```

## 16. Send Data Form-Urlencoded

Performing form-urlencoded requests using Retrofit is sort of straight forward. It’s just another Retrofit annotation, which will adjust the
proper mime type of your request automatically to `application/x-www-form-urlencoded`. The following interface definitions for Retrofit
will show us how to annotate our service interface for form-encoded requests.

```
public interface TaskService {  
    @FormUrlEncoded
    @POST("tasks")
    Call<Task> createTask(@Field("title") String title);

    // @Field also supports lists/arrays

    // to send dynamic large number of data use map
    @FormUrlEncoded
    @POST("tasks")
    Call<Task> createTask(@FieldMap Map<String, Object> map);
}
```

## 17. Sending Plain Text Request Body

### Solution 1: Scalars Converter

Within the available converters, you’ll also find a **Retrofit Scalars Converter** that does the job of parsing any Java primitive to be
put within the request body. Conversion applies to both directions: *requests* and *responses*.

After adding the dependency we need to add the scalars converter to our Retrofit instance. Please be aware that the order we are adding
response converters matters! As a rule of thumb, add Gson as the last converter to our Retrofit instance

```
Retrofit retrofit = new Retrofit.Builder()  
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://your.base.url/")
        .build();
```

---

### Solution 2: Use RequestBody Class

The interface will look like this:

```
public interface ScalarService {  
    @POST("path")
    Call<ResponseBody> getStringRequestBody(@Body RequestBody body);
}
```

The `ResponseBody` class allows us to receive any response data. The following code snippet shows the usage of both used classes in more detail:

```
String text = "plain text request body";  
RequestBody body = RequestBody.create(MediaType.parse("text/plain"), text);

Call<String> call = userClient.sendMessage(body);
cal.enqueue(...);
```

## 18. Add Query Parameters to Every Requests

If you’ve used Retrofit before, you’re aware of the `@Query` annotation used to add query parameters for single requests. There are situations
where you want to add the same query parameter to every request, just like adding an `Authorization` header to every request passing the
authentication token. If you’re requesting an API which accepts an `apikey` as a request parameter, it’s valuable to use an interceptor
instead add the query parameter to every request method.

You can do that by adding a new request interceptor to the `OkHttpClient`. Intercept the actual request and get the `HttpUrl`. The http url is
required to add query parameters since it will change the previously generated request url by appending the query parameter name and its value.

```
OkHttpClient.Builder httpClient =  
    new OkHttpClient.Builder();
httpClient.addInterceptor(new Interceptor() {  
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        HttpUrl originalHttpUrl = original.url();

        HttpUrl url = originalHttpUrl.newBuilder()
                .addQueryParameter("apikey", "your-actual-api-key")
                .build();

        // Request customization: add request headers
        Request.Builder requestBuilder = original.newBuilder()
                .url(url);

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
});
```

## 19. OAuth Authentication

### OAuth Basics

OAuth is a token based authorization method which uses an access token for interaction between user and API. OAuth requires several steps
and requests against the API to get your access token.

* Register an app for the API you want to develop. Use the developer sites of the public API you're going to develop for.
* Save client id and client secret in your app.
* Request access to user data from your app.
* Use the authorization code to get the access token.
* Use the access token to interact with the API.

For OAuth authentication the interface will look like this:

```
public interface GitHubClient {
    // for oAuth authentication
    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    Call<AccessToken> getAccessToken(
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("code") String code
    );
}
```

---

As we can see, we need a `AccessToken` class.

```
public class AccessToken {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("token_type")
    private String tokenType;

    public String getAccessToken(){ return accessToken; }
    public String getTokenType(){ return tokenType; }
}
```

---

The activity class will look like this:

```
public class LoginActivity extends Activity {

  // oAuth credentials
  // it should either define client id and secret as constants or in string resources
  private static final String API_BASE_URL = "https://example.com/oauthloginpage";
  private static final String API_OAUTH_CLIENTID = "replace-me";
  private static final String API_OAUTH_CLIENTSECRET = "replace-me";
  private static final String API_OAUTH_REDIRECT = "nl.jpelgrm.retrofit2oauthrefresh://oauth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // for oAuth authentication
        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(API_BASE_URL + "/login" + "?client_id=" + API_OAUTH_CLIENTID + "&redirect_uri=" + API_OAUTH_REDIRECT));
        startActivity(intent);
    }
}
```

---

### Define Activity and Intent Filter in AndroidManifest.Xml

An intent in Android is a messaging object used to request action or information (communication) from another app or component.
The intent filter is used to catch a message from an intent, identified by intent's action, category and data.

```
<activity android:name=".ui.activities.RepositoryListActivity">
     <!--for oAuth authentication-->
     <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data
            android:host="redirect uri"
            android:scheme="callback" />
     </intent-filter>
</activity>
```

---

### Catch the Authorization Code

```
// for oAuth authentication
    @Override
    protected void onResume() {
        super.onResume();
        Uri uri = getIntent().getData();

        if(uri != null && uri.toString().startsWith(API_OAUTH_REDIRECT)){
            String code = uri.getQueryParameter("code");

            // initializing builder, declaring base url add adding converter factory
            builder = new Retrofit.Builder()
                    .baseUrl("https://github.com/")
                    .addConverterFactory(GsonConverterFactory.create());
            retrofit = builder.build();

            client = retrofit.create(GitHubClient.class);
            accessTokenCall = client.getAccessToken(API_OAUTH_CLIENTID, API_OAUTH_CLIENTSECRET, code);

            accessTokenCall.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    showMessage("success!");
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    showMessage("error!");
                }
            });
        }
    }
```

## 20. Optional & Multiple Query Parameters

* Query parameters with `@Query`
* Optional parameters by passing `null`
* Multiple query parameters with `List<>`

```
    // for multiple query parameters
    // multiple query parameters with @Query
    @GET("user")
    Call<ResponseBody> searchForUsers(
            @Query("id") int id,
            @Query("sort") String order,
            @Query("page") int page
    );

    // optional
    // Integer, String these are nullable types
    // multiple query parameters by passing null
    @GET("user")
    Call<ResponseBody> searchForUsers(
            @Query("id") Integer id,
            @Query("sort") String order,
            @Query("page") Integer page
    );

    // for searching multiple attributes at the same time
    // // multiple query parameters with List<>
    @GET("user")
    Call<ResponseBody> searchForUsers(
            @Query("id") List<Integer> id,
            @Query("sort") String order,
            @Query("page") Integer page
    );
```

---

The method calls will be:

```
// for multiple query parts
multipleQueryCall = client.searchForUsers(11, "asc", 1);

// for optional parameters
multipleQueryCall = client.searchForUsers(11, null, null);

// for multiple ids
multipleQueryCall = client.searchForUsers(Arrays.asList(11, 12, 13), null, 1);
```

## 21. Dynamic Query Parameters

Using map:

```
// dynamic query parameter by using map
@GET("user")
Call<ResponseBody> searchForUsers(
      @QueryMap Map<String, Object> map
);
```

---

And the method call can be:

```
// creating a map fot dynamic query parameter
Map<String, Object> map = new HashMap<>();
map.put("id", 11);
map.put("sort", "asc");
map.put("page", 1);
Call<ResponseBody> dynamicQueryCall = client.searchForUsers(map);
```
