### ViewPager with fragment

Everything in one class

- MyPager.class
- activty_pager.xml

#### Layout & Drawable

*activity_pager*
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.kelvinhado.javarx.MyPager">
    <android.support.v4.view.ViewPager
        android:id="@+id/pager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_alignParentTop="true"
        android:layout_alignParentStart="true" />
    <android.support.design.widget.TabLayout
        android:id="@+id/tabDots"
        android:layout_width="match_parent"
        android:layout_height="20dp"
        app:tabBackground="@drawable/tab_selector"
        app:tabGravity="center"
        app:tabIndicatorHeight="0dp"/>
</RelativeLayout>
```
The "tabLayout" in the code above represents the dots at the bottom which represent the current selected fragment, so that the user is not lost during the navigation. We need to add the used selector to the @drawable folder. See below :

*tab_selector*
```xml
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:drawable="@drawable/tab_indicator_selected"
        android:state_selected="true"/>
    <item android:drawable="@drawable/tab_indicator_default"/>
</selector>
```
*tab_indicator_default*
```xml
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape
            android:innerRadius="0dp"
            android:shape="ring"
            android:thickness="4dp"
            android:useLevel="false">
            <solid android:color="@color/colorPagerDotDefault"/>
        </shape>
    </item>
</layer-list>
```
*tab_indicator_selected*
```xml
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <shape
            android:innerRadius="0dp"
            android:shape="ring"
            android:thickness="4dp"
            android:useLevel="false">
            <solid android:color="@color/colorPagerDotSelected"/>
        </shape>
    </item>
</layer-list>
```

### ViewPager activity
*MyPager.class*
```java
public class MyPager extends FragmentActivity {
    //number of page to display
    static final int NUM_ITEMS = 4;

    private ViewPager mPager;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypager);
        mPager = (ViewPager)findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        //link the tabLayout with the viewPager
        tabLayout.setupWithViewPager(mPager, true);

        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            Button bottomButton = (Button) findViewById(R.id.benefitPayment);
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 3) bottomButton.setText(getString(R.string.later));
                else bottomButton.setText(getString(R.string.benefit_mobile_payment));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }
    public void onClick(View view){
        switch(view.getId()){
            case R.id.buttonOne:
                break;
            case R.id.buttonTwo:
                //move to the last page
                mPager.setCurrentItem(NUM_ITEMS- 1);                
                break;
        }
    }

    // page adapter with the two required methods
    public static class MyAdapter extends FragmentPagerAdapter {
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
        @Override
        public Fragment getItem(int position) {
            return MyFragment.newInstance(position);
        }
    }


    public static class MyFragment extends Fragment {
        int position;
        private static final String POSITION = "position";
        public static Fragment newInstance(int position) {
            MyFragment fragment = new MyFragment();
            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt(POSITION, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            position = getArguments() != null ? getArguments().getInt(POSITION) : 1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // select a layout for each page
            int layout;
            switch (position){
                case 0: layout = R.layout.fragment_one; break;
                case 1: layout = R.layout.fragment_two; break;
                case 2: layout = R.layout.fragment_three; break;
            }
            ViewGroup rootView = (ViewGroup) inflater.inflate(layout, container, false);
            Log.d("fragNumber",""+ position);
            return rootView;
        }
    }
}
```
