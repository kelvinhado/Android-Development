## Preferences

### Setup the SettingsActivity

We will be using a MainActivty that will hold a menu button that lanched the SettingsActivity.

Create a menu xml file for the MainAcitivity ; 

*main_menu.xml*
```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item
        android:id="@+id/action_settings"
        android:orderInCategory="100"
        android:title="@string/action_settings"
        app:showAsAction="never" />
</menu>
```
 *MainActivity.java*
```java
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return true; // return true, so that the menu is displayed in the Toolbar
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
        Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
        startActivity(startSettingsActivity);
        return true;
    }
    return super.onOptionsItemSelected(item);
}
```

### Handle the navigation between activities : 

*main_menu.xml*
```xml
<activity android:name=".MainActivity"
    android:launchMode="singleTop"> 
    <!-- to not recreate MainActivity when we come from Settings -->
    ...
</activity>
<activity
    android:name=".SettingsActivity"
    android:label="@string/action_settings"
    android:parentActivityName=".MainActivity">
    <meta-data
        android:name="android.support.PARENT_ACTIVITY"
        android:value=".MainActivity" />
</activity>
```

*SettingsActivity.java*
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    ...
    ActionBar actionBar = this.getSupportActionBar();
    // Set the action bar back button to look like an up button
    if (actionBar != null) {
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    // When the home button is pressed, take the user back to the MainActivity
    if (id == android.R.id.home) {
        NavUtils.navigateUpFromSameTask(this);
    }
    return super.onOptionsItemSelected(item);
}
```
src : https://developer.android.com/training/implementing-navigation/ancestral.html

### Implements the PreferenceFragment

add support preference in the gradle file : 
```gradle
dependencies {
    ...
    compile 'com.android.support:preference-v7:25.1.0'
}
```
Add the preference screen discribed in xml under xml folder, here it's a checkbox

*xml/pref_main.xml*
```xml
<?xml version="1.0" encoding="utf-8"?>
<PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android">
    <CheckBoxPreference
        android:defaultValue="true"
        android:key="show_name"
        android:summaryOff="Hidden"
        android:summaryOn="Shown"
        android:title="Show Name" />
</PreferenceScreen>
```
Create the PreferenceFragment that extends PreferenceFragmentCompat (support v7)
```java
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_main);
    }

}
```

To bind the fragment with the activity we simply need to put the fragment in the SettingsActivity layout like below : 
```xml
<?xml version="1.0" encoding="utf-8"?>
<fragment xmlns:android="http://schemas.android.com/apk/res/android"
          android:id="@+id/activity_settings"
          android:name="android.example.com.visualizerpreferences.SettingsFragment"
          android:layout_width="match_parent"
          android:layout_height="match_parent"/>
```

Be careful, you must *set the preference theme* otherwise, the app will crash.. 
*values/styles.xml*
```xml
<ressources>
	<styles>
	...
	<item name="preferenceTheme">@style/PreferenceThemeOverlay</item>
	</styles>
</ressources>
```

### Clean code : Use resources

It's important to place all hard-coded values inside our resources files. It necessary to avoid mistyping and it's more maintanable.
For instance :
*values/strings.xml*
```xml
<resources>
	<string name="pref_name_key" translatable="false">show_name_pref</string>
	<string name="pref_name_value" translatable="false">Show Name</string>
</resources>

```
*values/bools.xml*
```xml
<resources>
    <bool name="pref_show_name_default">true</bool>
</resources>
```

### Notice the Activity when a preference is changed 

Our MainActivity, which is the activity that need to be updated when the preference is changed, needs to implements the  *SharedPreferences.OnSharedPreferenceChangeListener*. 

You then need to register the sharedPreference that need to be listenned. Here is the code below. Don't forget to unregister it when the activity is destroyed.

```java
import android.support.v7.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE = 88;
    private VisualizerView mVisualizerView;
    private AudioInputReader mAudioInputReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);
		setupSharedPreferences();
    }

    private void setupSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ...
        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_show_name_key))) {
            // update view according to the new preference.
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the listener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
```

### Types of preferences :

#### List Preference 

*values/arrays.xml* 
```xml
<resources>
    <!-- Label ordering must match values -->
    <array name="pref_color_option_labels">
        <item>@string/pref_color_label_red</item>
        <item>@string/pref_color_label_blue</item>
        <item>@string/pref_color_label_green</item>
    </array>

    <array name="pref_color_option_values">
        <item>@string/pref_color_red_value</item>
        <item>@string/pref_color_blue_value</item>
        <item>@string/pref_color_green_value</item>
    </array>
</resources>
```
*values/strings.xml* 
```xml
<resources>
    <!-- Label for the color preference -->
    <string name="pref_color_label">Shape Color</string>

    <!-- Label for X color preference -->
    <string name="pref_color_label_red">Red</string>
    <string name="pref_color_label_blue">Blue</string>
    <string name="pref_color_label_green">Green</string>

    <!-- Key name for color preference in SharedPreferences -->
    <string name="pref_color_key" translatable="false">color</string>

    <!-- Value in SharedPreferences for X color option -->
    <string name="pref_color_red_value" translatable="false">red</string>
    <string name="pref_color_blue_value" translatable="false">blue</string>
    <string name="pref_color_green_value" translatable="false">green</string>
</resources>

*MainActivity.java*
```java
private void loadColorFromPreferences(SharedPreferences sharedPreferences) {
    mVisualizerView.setColor(sharedPreferences.getString(getString(R.string.pref_color_key),
            getString(R.string.pref_color_red_value)));
}
```

### Update the preference summary programmaticaly

The preference summary is the little text that describe the actual state of the preference. We want that text to change whenever we change color selected from the ListPreference.

We will need to use the same OnPreferenceChangeListener as the one we used in the MainActivity.

```java
public class SettingsFragment extends PreferenceFragmentCompat implements
        OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_visualizer);
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();

        // Go through all of the preferences, and set up their preference summary.
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            // You don't need to set up preference summaries for checkbox preferences because
            // they are already set up in xml using summaryOff and summary On
            if (!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Figure out which preference was changed
        Preference preference = findPreference(key);
        if (null != preference) {
            // Updates the summary for the preference
            if (!(preference instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }

    // This method should check if the preference is a ListPreference and, if so, find the label
    // associated with the value. You can do this by using the findIndexOfValue and getEntries methods
    // of Preference.
    private void setPreferenceSummary(Preference preference, String value) {
        if (preference instanceof ListPreference) {
            // For list preferences, figure out the label of the selected value
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                // Set the summary to that label
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
```

#### Edit Text Preference

*pref_main.xml*
```xml
...
<EditTextPreference
    android:defaultValue="@string/pref_size_default"
    android:key="@string/pref_size_key"
    android:title="@string/pref_size_label" />
```
add the pref key and label inside the values/strings.xml

*MainActivity.java*
```java
private void loadSizeFromSharedPreferences(SharedPreferences sharedPreferences) {
    float minSize = Float.parseFloat(sharedPreferences.getString(getString(R.string.pref_size_key),
            getString(R.string.pref_size_default)));
    // use it 
}
```

#### Constraints

If the user enter an unexpected value into our shared preference, the app will crash when it tries to parse the value. To avoid this issues we can use OnPreferenceChanged instead of OnSharedPreferenceChanged. 

- SharedPreferenceChangeListener is triggered after any value is saved to the SharedPreferences file.
- PreferenceChangeListener is triggered before a value is saved to the SharedPreferences file. 
Because of this, it can prevent an invalid update to a preference. PreferenceChangeListeners are also attached to a single preference.

Generally the flow goes like this:

1) User updates a preference.
2) PreferenceChangeListener triggered for that preference.
3) The new value is saved to the SharedPreference file.
4) onSharedPreferenceChanged listeners are triggered.

*PreferenceFragment.java*
```java
public class SettingsFragment extends PreferenceFragmentCompat implements
        OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
 	@Override
    public void onCreatePreferences(Bundle bundle, String s) {
    	...
        Preference preference = findPreference(getString(R.string.pref_size_key));
        preference.setOnPreferenceChangeListener(this);
	}

	@Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // In this context, we're using the onPreferenceChange listener for checking whether the
        // size setting was set to a valid value.

        Toast error = Toast.makeText(getContext(), "Please select a number between 0.1 and 3", Toast.LENGTH_SHORT);

        // Double check that the preference is the size preference
        String sizeKey = getString(R.string.pref_size_key);
        if (preference.getKey().equals(sizeKey)) {
            String stringSize = (String) newValue;
            try {
                float size = Float.parseFloat(stringSize);
                // If the number is outside of the acceptable range, show an error.
                if (size > 3 || size <= 0) {
                    error.show();
                    return false;
                }
            } catch (NumberFormatException nfe) {
                // If whatever the user entered can't be parsed to a number, show an error
                error.show();
                return false;
            }
        }
        return true;
    }
}
```


