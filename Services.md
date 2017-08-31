# Service

Consider using a service when it does not affect directly the UI.

3 ways to start a service :
- start (startService from an activity)
- schedule (JobScheduler, Firebase JobDispatcher)
- bind (bindService from activity) can easily communicate back to the activity 
	https://developer.android.com/guide/components/bound-services.html

Note that a service can be both start and bind.

Careful : Service are started using the main thread. Consider using AsyncTask or Thread inside the onStartCommand() to perfom your action.

## Intent Service

Intent service runs in a separate background thread. All IntentService a running on the same background thread. They are processed one at a time.

This is how an intent service is defined

```java
public class CustomIntentService extends IntentService {

    public CustomIntentService() {
        super("CustomIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    	// do the job
    }
}
```