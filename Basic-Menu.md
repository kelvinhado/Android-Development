##How to add and use items to the menu of an Activity

We you create a new blank Activity in Android Studio, It actually create 3 files :
- src/your.package/MainActivity.java
- res/layout/activity_main.xml
- res/menu/menu_main_activity.xml

###1) add items to the menu xml file.

*menu_main_activity*
```xml
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    tools:context=".MainPage">

    <item
        android:id="@+id/menu_add"
        android:icon="@android:drawable/ic_menu_add"
        app:showAsAction="ifRoom"
        android:title="add"/>

    <item
        android:id="@+id/action_settings"         android:title="@string/action_settings"
        android:orderInCategory="100"
        app:showAsAction="never" />
</menu>
```

###2) implement methods to use our menu item

*MainActivity.java*
```java
  @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.menu_main_activity4, menu);
       return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       int id = item.getItemId();
      switch(id) {
        case R.id.action_settings :
            // do smth
            break;
        case R.id.menu_add :
            // do smth else
            break;
        default :
            // default action
            break;

      }
       return super.onOptionsItemSelected(item);
   }
```
