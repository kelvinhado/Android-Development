# Notification

https://developer.android.com/training/notify-user/build-notification.html

```java
private static final int WATER_REMINDER_NOTIFICATION_ID = 1138;
private static final int WATER_REMINDER_PENDING_INTENT_ID = 3417;
private static final int ACTION_DRINK_PENDING_INTENT_ID = 1;

public void displayNotification() {
	// - has a color of R.colorPrimary - use ContextCompat.getColor to get a compatible color
	// - has ic_drink_notification as the small icon
	// - uses icon returned by the largeIcon helper method as the large icon
	// - sets the title to the charging_reminder_notification_title String resource
	// - sets the text to the charging_reminder_notification_body String resource
	// - sets the style to NotificationCompat.BigTextStyle().bigText(text)
	// - sets the notification defaults to vibrate
	// - uses the content intent returned by the contentIntent helper method for the contentIntent
	// - automatically cancels the notification when the notification is clicked
	NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
	        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
	        .setSmallIcon(R.drawable.ic_drink_notification)
	        .setLargeIcon(largeIcon(context))
	        .setContentTitle(context.getString(R.string.charging_reminder_notification_title))
	        .setContentText(context.getString(R.string.charging_reminder_notification_body))
	        .setStyle(new NotificationCompat.BigTextStyle().bigText(
	                context.getString(R.string.charging_reminder_notification_body)))
	        .setDefaults(Notification.DEFAULT_VIBRATE)
	        .addAction(drinkWaterAction(context)) // we can add up to three actions to a notification
	        .setContentIntent(contentIntent(context))
	        .setAutoCancel(true);

	// to PRIORITY_HIGH.
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
	    notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
	}

	NotificationManager notificationManager = (NotificationManager)
	        context.getSystemService(Context.NOTIFICATION_SERVICE);

	// Pass in a unique ID of your choosing for the notification and notificationBuilder.build()
	notificationManager.notify(WATER_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
}

private static PendingIntent contentIntent(Context context) {
    Intent startActivityIntent = new Intent(context, MainActivity.class);

    // - Take the context passed in as a parameter
    // - Takes an unique integer ID for the pending intent (you can create a constant for
    //   this integer above
    // - Takes the intent to open the MainActivity you just created; this is what is triggered
    //   when the notification is triggered
    // - Has the flag FLAG_UPDATE_CURRENT, so that if the intent is created again, keep the
    // intent but update the data
    return PendingIntent.getActivity(
            context,
            WATER_REMINDER_PENDING_INTENT_ID,
            startActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);
}


// This method is necessary to decode a bitmap needed for the notification.
private static Bitmap largeIcon(Context context) {
    Resources res = context.getResources();
    Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_local_drink_black_24px);
    return largeIcon;
}

private static Action drinkWaterAction(Context context) {
    Intent incrementWaterCountIntent = new Intent(context, WaterReminderIntentService.class);
    incrementWaterCountIntent.setAction(ReminderTasks.ACTION_INCREMENT_WATER_COUNT);
    PendingIntent incrementWaterPendingIntent = PendingIntent.getService(
            context,
            ACTION_DRINK_PENDING_INTENT_ID,
            incrementWaterCountIntent,
            PendingIntent.FLAG_CANCEL_CURRENT);
    Action drinkWaterAction = new Action(R.drawable.ic_local_drink_black_24px,
            "I did it!",
            incrementWaterPendingIntent);
    return drinkWaterAction;
}

```

Don't forget to set the Activity to SingleTop to not be recreated when we click on the notification.
Add the permission for vibration.