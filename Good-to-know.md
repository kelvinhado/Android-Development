## Nice to know :

### SP and DP in xml layouts : 
They both are automatically scaled to be the same approximate physical size regardless of the density of the pixels on the screen.
The first Android phone was 160dpi and on those 1dp = 1px
Modern devices have 480dpi or more
Conversion : px = dp * (dpi / 160)
SP works like DP but are scale to the user preferences, it's important to use them for accessibility.

### Translatable strings.xml

If the value of the string is never shown to the user we can use the properties "translatable=false", with that, the android will not wast time trying to translate this String. 

### Plurals in string.xml

you can specify how a string ressource should be displayed depanding on a number

```xml
<plurals name="charge_notification_count">
   <item quantity="zero">You never hit that button</item>
   <item quantity="one">You hit that button %d time</item>
   <item quantity="other">You hit that button %d times</item>
</plurals>
```

```java
String output = getResources().getQuantityString(
					R.plurals.charge_notification_count, 
					chargingReminders, 	// to know which string item should be selected
					chargingReminders);	// the int that will be displayed
```