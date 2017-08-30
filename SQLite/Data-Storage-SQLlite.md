#Data Storage SQL Lite

## Create the contract for the DB

Create an inner class that specifies the table and column name. Note that the column ID is created by default.

*data/WaitlistContract.java*
```java
import android.provider.BaseColumns;

public class WaitlistContract {
    public static final class WaitlistEntry implements BaseColumns {
        public static final String TABLE_NAME = "waitlist";
        public static final String COLUMN_GUEST_NAME = "guestName";
        public static final String COLUMN_PARTY_SIZE = "partySize";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
```

## Create the Database

We should now create an DB Helper that will handle 
- the creation of the DB for the first time
- the upgrade of the DB when an app update change the DB scheme. If you change the database schema, you must increment the database version

*data/WaitlistDbHelper.java*
```java
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.waitlist.data.WaitlistContract.*;

public class WaitlistDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "waitlist.db";
    private static final int DATABASE_VERSION = 1; 

    // Constructor
    public WaitlistDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // String query that will create the table create a table to hold waitlist data
        final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " + WaitlistEntry.TABLE_NAME + " (" +
                WaitlistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WaitlistEntry.COLUMN_GUEST_NAME + " TEXT NOT NULL, " +
                WaitlistEntry.COLUMN_PARTY_SIZE + " INTEGER NOT NULL, " +
                WaitlistEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WaitlistEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
```

## Get all records

```java
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...

        // Create a DB helper (this will create the DB if run for the first time)
        WaitlistDbHelper dbHelper = new WaitlistDbHelper(this);

        // Keep a reference to the mDb until paused or killed. 
        // we get a writable database because you will be adding restaurant customers
        mDb = dbHelper.getWritableDatabase();
        TestUtil.insertFakeData(mDb);
        Cursor cursor = getAllGuests();
        ...
    }

	private Cursor getAllGuests() {
        return mDb.query(
                WaitlistContract.WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistContract.WaitlistEntry.COLUMN_TIMESTAMP
        );
    }
```

### Populate a RecyclerView with our datas

If you are using a RecyclerView to shows the data, you can directly pass the cursor to the adapter.
And then, use the onBindViewHolder to match data with views. see below : 
```java
@Override
public void onBindViewHolder(GuestViewHolder holder, int position) {
	if (!mCursor.moveToPosition(position))
	    return; 

	String name = mCursor.getString(mCursor.getColumnIndexWaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME));
	int partySize = mCursor.getInt(mCursor.getColumnIndex(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE));

	holder.nameTextView.setText(name);
	holder.partySizeTextView.setText(String.valueOf(partySize));
}
```

## Add a new records to the DB
```java
private long addNewGuest(String name, int partySize) {
    ContentValues cv = new ContentValues();
    cv.put(WaitlistContract.WaitlistEntry.COLUMN_GUEST_NAME, name);
    cv.put(WaitlistContract.WaitlistEntry.COLUMN_PARTY_SIZE, partySize);
    return mDb.insert(WaitlistContract.WaitlistEntry.TABLE_NAME, null, cv);
}
```

## Remove a record from the DB
```java
private boolean removeGuest(long id) {
    // COMPLETED (2) Inside, call mDb.delete to pass in the TABLE_NAME and the condition that WaitlistEntry._ID equals id
    return mDb.delete(WaitlistContract.WaitlistEntry.TABLE_NAME, WaitlistContract.WaitlistEntry._ID + "=" + id, null) > 0;
}
```

