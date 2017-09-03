#UI-Data-binding

Data binding is a cool feature that allow you to get rid of our old friend findViewById() ;)
Use an android studio version >2.1

## 6 steps :
1. Enable data binding in build.gradle
2. Create a POJO
3. Add <layout> as the root tag to the UI
4. Create a binding instance, set the content view using DatabindingUtil
5. Bind each attribute in the views to the corresponding data

### 1. Enable data binding in build.gradle

```gradle
android {
    ...
    dataBinding.enabled = true;
}
```

### 2. Create a POJO

Normally, it is best practice in Java to declare member variables as private and provide getters, but we are leaving these fields public for ease of use.

```java
public class BoardingPassInfo {

    public String passengerName;
    public Timestamp boardingTime;
    ...
    public long getMinutesUntilBoarding() {
        long millisUntilBoarding = boardingTime.getTime() - System.currentTimeMillis();
        return TimeUnit.MILLISECONDS.toMinutes(millisUntilBoarding);
    }
}
```

### 3. Add <layout> as the root tag to the UI

Inside your activity layout, you should change the root element to <layout> the root tag for activity_main to generate the binding class.
Note that __activity_main.xml__ will generate __ActivityMainBindinginding__

*activity_main.xml*
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:android="http://schemas.android.com/apk/res/android">
    <RelativeLayout> <!-- for example -->
    ...
    </RelativeLayout>
</layout>
```

### 4. Create a binding instance

This will help up access the UI component from the code. 

*MainActivity.java*
```java
ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Set the Content View using DataBindingUtil to the activity_main layout
        /*
         * DataBindUtil.setContentView replaces our normal call of setContent view.
         * DataBindingUtil also created our ActivityMainBinding that we will eventually use to
         * display all of our data.
         */
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // Load a BoardingPassInfo object with fake data using FakeDataUtils
        BoardingPassInfo fakeBoardingInfo = FakeDataUtils.generateFakeBoardingPassInfo();
        // Call displayBoardingPassInfo and pass the fake BoardingInfo instance
        displayBoardingPassInfo(fakeBoardingInfo);
    }
```


### 5. Bind each attribute in the views to the corresponding data

*MainActivity.java*
```java
    private void displayBoardingPassInfo(BoardingPassInfo info) {

        mBinding.textViewPassengerName.setText(info.passengerName);
        mBinding.textViewOriginAirport.setText(info.originCode);
        ...
        // use formatter
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.timeFormat), Locale.getDefault());
        String boardingTime = formatter.format(info.boardingTime);
        ...
        long totalMinutesUntilBoarding = info.getMinutesUntilBoarding();
        long hoursUntilBoarding = TimeUnit.MINUTES.toHours(totalMinutesUntilBoarding);
        long minutesLessHoursUntilBoarding =
                totalMinutesUntilBoarding - TimeUnit.HOURS.toMinutes(hoursUntilBoarding);
        //use ressources
        String hoursAndMinutesUntilBoarding = getString(R.string.countDownFormat,
                hoursUntilBoarding,
                minutesLessHoursUntilBoarding);
        ...
    }
```

