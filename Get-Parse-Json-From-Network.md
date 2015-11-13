
## Download and Parse Json object

Most of the connections between servers and clients are using Json format.
In Android it's now forbidden to make *http request* on the main thread, before we had to implement an "AsyncTask" to run request but now the can directly use the **Volley** and **Jackson libraries** that will do all the job for us !

Documentations:
- Volley : https://developer.android.com/training/volley/simple.html
- Jackson : http://www.tutos-android.com/parsing-json-jackson-android or
https://github.com/FasterXML/jackson-databind

We will first get the json from the server (using Volley) and then automatically convert this Json to a ObjectArray.

> Note that the code below works in an activity, if you decide to work with fragment, you will probably have to change 'this' to getActivity()
### 1) Setup the android project

#### a) add Internet permissions in the *AndroidManifest*

add thos two lines in the <manifest> tag :

```xml
<manifest (...)>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.INTERNET"/>
    (...)
</manifest>
```

#### b) add libraries

There is serveral way to add libraries (cf google) you can download them manually or do like this

You have to add two libraries in the *build.gradle (Module App)* in gradle Scripts tree.
You have to add those lines in dependencies :
```xml
compile 'com.fasterxml.jackson.core:jackson-core:2.4.2'
compile 'com.fasterxml.jackson.core:jackson-annotations:2.4.0'
compile 'com.fasterxml.jackson.core:jackson-databind:2.4.2'
compile 'com.mcxiaoke.volley:library:1.0.19'
```

and add packagingOptions in android { } brackets :
```xml
packagingOptions {
    exclude 'META-INF/LICENSE'
    exclude 'META-INF/NOTICE'
}
```

So this is how the *build.gradle* file look like :

*build.gradle*
```xml
apply plugin: 'com.android.application'
android {
    (...)
    packagingOptions {
        exclude 'META-INF/LICENSE'
        exclude 'META-INF/NOTICE'
    }
}

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:appcompat-v7:22.0.0'
    compile 'com.fasterxml.jackson.core:jackson-core:2.4.2'
    compile 'com.fasterxml.jackson.core:jackson-annotations:2.4.0'
    compile 'com.fasterxml.jackson.core:jackson-databind:2.4.2'
    compile 'com.mcxiaoke.volley:library:1.0.19'
}
```
#### c) *Sync* the project
after each modification in this file, android is asking you to sync the project so that it can take thos changement in consideration.
When it's done you are ready to work with them :)


### 2) Transmitting Network Data Using Volley

in *MainActivity.java*
```java

// Instantiate the RequestQueue.
RequestQueue queue = Volley.newRequestQueue(this);
String url ="http://the_url_of_the_server_that_is_supposed_to_send_you_a_Json.com";

// Request a string response from the provided URL.
StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
      //if its works
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
              //note that the *mTextView* is a TextView that you define in the main_activity.xml and matched. */
              /* TODO */
              mTextView.setText("Response is: "+ response);
          }
        },

        // if it does not work
        new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            /* TODO */
            mTextView.setText("That didn't work!");
        }
});

// Add the request to the RequestQueue.
queue.add(stringRequest);
```
you have one method that is called if everything went well and an other one if it didn't work.
In this example you have to set the URL to the server and implement those two methods and that's it !

You can see that the response the Json is in the object *response* (the attribute the the onResponse method).
So now we have to parse this json into an array of java objects.



### 3) Parse Json to Java Object Using Jackson

For example let's say, the Json Object is designed that way :
```json
{"employees":[
    {"id":"123", "lastName":"Doe"},
    {"id":"007", "lastName":"Smith"},
    {"id":"444", "lastName":"Jones"}
]}
```
so first we have to create a java class that will match perfectly those attributes. Let's do it :

*employee.java*
```java
public class Employee {
  //attributes have to have the same name with Json
  private int id;            
  private String lastName;

  @Override
    public String toString() {
        return "employee n°" + id + " : " + lastName;
    }
}
```

We are now ready to parse the Json. you just have to do like this :

in *MainActivity.java*
```java
//use the ObjectMapper from org.codehaus.jackson.map.ObjectMapper
final ObjectMapper objectMapper = new ObjectMapper();
String jsonObject = response; //we are getting in from the onResponse in the *MainActivity.java*

try {
    List<Employee> employees = objectMapper.readValue(response, new TypeReference<List<Employee>>(){});
    mTextView.setText("" + employeesList);
}
catch (IOException e) {
    e.printStackTrace();
}

```


### 4) Final result for MainActivity.java

This is how does look the MainActivty.java when we download and then read the json :

in *MainActivity.java*
```java
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.codehaus.jackson.map.ObjectMapper;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      final TextView mTextView = (TextView) findViewById(R.id.tv1);
      final ObjectMapper objectMapper = new ObjectMapper();

      // Instantiate the RequestQueue.
      RequestQueue queue = Volley.newRequestQueue(this);
      String url ="http://the_url_of_the_server_that_is_supposed_to_send_you_a_Json.com";

      // Request a string response from the provided URL.
      StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
              //if its works
              new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                  try {
                          List<Employee> employees = objectMapper.readValue(response, new TypeReference<List<Employee>>(){});
                          mTextView.setText(employees);
                      }
                      catch (IOException e) {
                          e.printStackTrace();
                      }
                  }
              },
              // if it does not work
              new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                  mTextView.setText("That didn't work!");
              }
      });
      // Add the request to the RequestQueue.
      queue.add(stringRequest);

    }
}
```

### 5) Improvment of the using of RequestQueue

this line :
>  RequestQueue queue = Volley.newRequestQueue(this);

does not have to be called everytime, it's better to Instantiate it once and use the same one everywhere in our application.
To do this, we have to create an application class that we will called *VolleyApplication* (for example) :

#### a) create Application class
*VolleyApplication.java*
```java
import android.app.Application;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

//TODO Add this line to the manifest : android:name=".http.VolleyApplication" in application node
public class VolleyApplication extends Application {

    private static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(this);
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
```
#### b) update the manifest

In fact, we have to tell in the manifest that we want our Application class to be **loaded** at the beginning of the application. Otherwise, the class will never be called and executed.

Just add this line inside the <application> node in the manifest like below :
*AndroidManifest.xml*
```xml
<manifest (...)>
    (...)
    <application
      android:name=".network.VolleyApplication"
      (...)
    </application>
</manifest>
```

#### c) ready to use

Now, you don't need to Instantiate the RequestQueue. When you need it (no matter where in your app), you can access it by using :
```java
VolleyApplication.getRequestQueue().add(myRequest);
```

### 6) Improvment : create custom jacksonRequest that extends jacksonRequest

#### 6.1) create a jacksonRequest.java  (COPY and PASTE it directly)
```java

public class JacksonRequest<T> extends Request<T> {
    private final ObjectMapper objectMapper;
    private final Class<T> mClazz;
    private final Response.Listener<T> mListener;

    public JacksonRequest(int method, String url, Class<T> clazz,
                          Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mClazz = clazz;
        this.mListener = listener;
        objectMapper = new ObjectMapper();
    }

    public JacksonRequest(int method, String url, Class<T> clazz,
                          Response.Listener<T> listener, Response.ErrorListener errorListener, ObjectMapper objectMapper) {
        super(method, url, errorListener);
        this.mClazz = clazz;
        this.mListener = listener;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {

        Log.d("reponse ", response.toString());

        try {
            String json = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(objectMapper.readValue(json, mClazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonMappingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonParseException e) {
            return Response.error(new ParseError(e));
        } catch (IOException e) {
            return Response.error(new ParseError(e));
        }
    }
}


```

creating a custom class that will extend jacksonRequest like below :
```java
import com.android.volley.Response;

public class EmployeesRequest extends JacksonRequest<Employees> {

    private static final String url = "https://www.giveme_employees_list";
    public EmployeesRequest(Response.Listener<Station> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url ,Employees.class, listener, errorListener);

    }

}
```
>note that the **Employees.class** in *JacksonRequest<Employees>*, Employees.java is a new class that will just extends ArrayList<Employee>.
>You can add parameters to the constructor if needed.

Now we don't have to specify anymore the "Method.GET, url, type of class". It will be donne once in this class.


This is how the activity look like at the end :
in *MainActivity.java*
```java
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.codehaus.jackson.map.ObjectMapper;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      final TextView mTextView = (TextView) findViewById(R.id.tv1);

      EmployeesRequest employeesRequest = new EmployeesRequest(
              //if its works
              new Response.Listener<Employees>() {
              @Override
              public void onResponse(Employees employees) {
                  try {
                          mTextView.setText(employees);
                      }
                      catch (IOException e) {
                          e.printStackTrace();
                      }
                  }
              },
              // if it does not work
              new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                  mTextView.setText("That didn't work!");
              }
      });
      // Add the request to the RequestQueue.
      VolleyApplication.getRequestQueue().add(employeesRequest);
    }
}
