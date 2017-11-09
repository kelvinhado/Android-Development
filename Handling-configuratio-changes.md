## Handling configuration changes

Your activity can be destroyed and recreated at any time by the Android system.
Your activity should be able to recover from this situation. 
Two useful method : 
- onSaveInstanceState() called before your activity gets destroyed.
- onCreate() or onRestoreInstanceState() 

The bundle is not designed to carry large object. When the amount of data to relead is too high you have two option : 

#### Retain an object during configuration changes (carry the object)
Using Fragments : 
1. declare reference to a stateful object (setData/getData)
2. in onCreate() set setRetainInstance(true);
3. Use the fragment manager to retrieve the fragment. 

```java
public class RetainedFragment extends Fragment {

    // data object we want to retain
    private MyDataObject data;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(MyDataObject data) {
        this.data = data;
    }

    public MyDataObject getData() {
        return data;
    }
}
```

```java
public class MyActivity extends Activity {

    private static final String TAG_RETAINED_FRAGMENT = "RetainedFragment";

    private RetainedFragment mRetainedFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        mRetainedFragment = (RetainedFragment) fm.findFragmentByTag(TAG_RETAINED_FRAGMENT);

        // create the fragment and data the first time
        if (mRetainedFragment == null) {
            // add the fragment
            mRetainedFragment = new RetainedFragment();
            fm.beginTransaction().add(mRetainedFragment, TAG_RETAINED_FRAGMENT).commit();
            // load data from a data source or perform any calculation
            mRetainedFragment.setData(loadMyData());
        }

        // the data is available in mRetainedFragment.getData() even after 
        // subsequent configuration change restarts.
        ...
    }
}
```

