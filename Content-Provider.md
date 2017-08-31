# Content Provider

https://developer.android.com/guide/topics/providers/content-providers.html

## Use an other app content provider

We can use a content resolver to access an other app data.
It has four methods that can be call to perfom an action on the Content Provider :
- Query()
- Insert()
- Update()
- Delete()

### Declare the permission

```xml
<uses-permission android:name="com.example.udacity.droidtermsexample.TERMS_READ" />
```
We can use a content resolver to access an other app data.

```java
ContentResolver resolver = getContentResolver();
// Call the query method on the resolver with the correct Uri from the contract class
Cursor cursor = resolver.query(DroidTermsExampleContract.CONTENT_URI,
	null, null, null, null);
```

## What's a cursor ?

Cursors are iterators that provide read/write access to the data of a content provider.
Once you get the cursor :

```java
if(cursor != null) { //always check if the cursor as been set
	return;
}

int colIndexA = cursor.getColumnIndex(CustomContract.COLUMN_A);
int colIndexB = cursor.getColumnIndex(CustomContract.COLUMN_B);
while (cursor.moveToNext()) {
	String word = cursor.getString(colIndexA);
	String definition = cursor.getString(colIndexB);
}
```

## Build our own content provider

We need to create a class that will extends ContentProvider an implements all the overrided methods.

*TaskContentProvider.java*
```java
public class TaskContentProvider extends ContentProvider {

    private TaskDbHelper mTaskDbHelper;

    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */
    @Override
    public boolean onCreate() {
        Context context = getContext();
        mTaskDbHelper = new TaskDbHelper(context);
        return true; // return true if the 
    }
    ...
} 
```
Then, you need to register that provider into the android manifest, you can set the attribute "exported" to true or flase, whether you want the ContentProvider to be accessible by other apps or not.

*AndroidManifest.xml*
```xml
<provider
    android:name="com.example.android.todolist.data.TaskContentProvider"
    android:authorities="com.example.android.todolist"
    android:exported="false"/> <!-- the content provider will not be reachable by other apps.-->
```

## Define URIs

We need to define URIs that will :
- Identify the provider
- Identify different type of data that the provider can work with

A URI is defined alike this : 
```
<scheme>//<authority>/<path>
```

For a provider, you can and should define several URI, depending if we want to access all items or just one, if we want to get results depending on the given column "_ID" or another one.

### WildCard characters

For URI there are two wildcard characters : "*" and "#"
Here are some example : 
- __path__ matches "patch" exactly
- __path/#__ matches "path" followed by a number
- __path/*__ matches "path" followed by a string
- __path/*/other/#__ matches "path" followed by a string followed by "other" followed by a number

In the contract of your sqlite db, we add the URI
*TaskContract.xml*
```java
public class TaskContract {
	public static final String AUTHORITY = "com.example.android.todolist";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_TASKS = "tasks"; // This is the path for the "tasks" directory

    public static final class TaskEntry implements BaseColumns {
        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();
        ...
    }
}
```

### UriMatcher

It determines what kind of URI the provider receives and match it to an integer constant. 

*TaskContentProvider.java*
```java
public class TaskContentProvider extends ContentProvider {

    // Final integer constants for the directory of tasks and a single item.
    // It's convention to use 100, 200, 300, etc for directories,
    // and related ints (101, 102, ..) for items in that directory.
    public static final int TASKS = 100;
    public static final int TASK_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher(); // we use sUriMatcher because its a final member

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS, TASKS);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS + "/#", TASK_WITH_ID);

        return uriMatcher;
    }
}
```

## Implements insert() 

*TaskContentProvider.java*
```java
@Override
public Uri insert(@NonNull Uri uri, ContentValues values) {

    final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

    int match = sUriMatcher.match(uri); // code to identify the match for the tasks directory
    Uri returnUri; // URI to be returned

    switch (match) {
        case TASKS: // defined in the contract
            long id = db.insert(TABLE_NAME, null, values);
            if ( id > 0 ) { 
                returnUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id);
            } else {
                throw new android.database.SQLException("Failed to insert row into " + uri);
            }
            break;
        default:
            throw new UnsupportedOperationException("Unknown uri: " + uri);
    }

    // Notify the resolver if the uri has been changed, and return the newly inserted URI
    getContext().getContentResolver().notifyChange(uri, null);
    // Return constructed uri (this points to the newly inserted row of data)
    return returnUri;
}
```

*MainActivity.java*
```java
public void onClickAddTask(View view) {
    ...
    ContentValues contentValues = new ContentValues();
    contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, input);
    contentValues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, mPriority);
    Uri uri = getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues); // returns the newly create task URI
    ...
}
```

## Implements bulkInsert()
```
@Override
public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
    final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

    switch (sUriMatcher.match(uri)) {

        case CODE_WEATHER:
            db.beginTransaction();
            int rowsInserted = 0;
            try {
                for (ContentValues value : values) {
                    long weatherDate =
                            value.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE);
                    if (!SunshineDateUtils.isDateNormalized(weatherDate)) {
                        throw new IllegalArgumentException("Date must be normalized to insert");
                    }

                    long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                    if (_id != -1) {
                        rowsInserted++;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }

            if (rowsInserted > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            return rowsInserted;

        default:
            return super.bulkInsert(uri, values);
    }
}
```

## Implements querry() 

*TaskContentProvider.java*
```java
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            // Query for the tasks directory
            case TASKS:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TASK_WITH_ID :
            	// Get the id from the URI
                String id = uri.getPathSegments().get(1);

                // Selection is the _ID column = ?, and the Selection args = the row ID from the URI
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                // Construct a query as you would normally, passing in the selection/args
                retCursor =  db.query(TABLE_NAME,
                        null,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
            	break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // CSet a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }
```


## Implements delete() 
```java
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int tasksDeleted; // starts as 0

        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case TASK_WITH_ID:
                // Get the task ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                tasksDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return tasksDeleted;
    }
```

## implements update() 
```java
@Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        //Keep track of if an update occurs
        int tasksUpdated;

        // match code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case TASK_WITH_ID:
                //update a single task by getting the id
                String id = uri.getPathSegments().get(1);
                //using selections
                tasksUpdated = mTaskDbHelper.getWritableDatabase().update(TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksUpdated != 0) {
            //set notifications if a task was updated
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return number of tasks updated
        return tasksUpdated;
    }
```

## Do the job in background

__Warning__ Operation on databases may take time, it's important call them from outside the main thread




## Use AsyncTaskLoader with RecyclerView

The main activity implements LoaderManager.LoaderCallbacks<Cursor> 

*MainActivity.java*
```java
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {


    // Constants for logging and referring to a unique loader
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;

    // Member variables for the adapter and RecyclerView
    private CustomCursorAdapter mAdapter;
    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewTasks);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CustomCursorAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }


    /**
     * Instantiates and returns a new AsyncTaskLoader with the given ID.
     * This loader will return task data as a Cursor or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Use a try/catch block to catch any errors in loading data
                try {
                    return getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            TaskContract.TaskEntry.COLUMN_PRIORITY);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener 
            // (the one implemented by the class)
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };

    }


    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapter.swapCursor(data);
    }


    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }
}
```

