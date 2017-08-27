## Intents 

source : https://developer.android.com/reference/android/content/Intent.html

### Explicit Intent

We use explicit intent when we know exactly which activity we are targeting 
Launch an activity using intent : 

```java
Intent startIntent = new Intent(context, destinationActivity.class);
startIntent.putExtra(Intent.EXTRA_TEXT, textEntered); // it's nice to use already defined.
startActivity(startChildActivityIntent);
```

Retreave information about the intent that started the activity 
```java
Intent intentThatStartedThisActivity = getIntent();
if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
	String textEntered = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
}
```

### Implicit Intent

Implicit Intent indicate an action to be done, take picture, make a call.. We don't care about which app will execute your request, Android allows the user to choose between differents app that can hundle the request with the passed data.

#### Open a web page 
```java
private void openWebPage(String url) {
    Uri webpage = Uri.parse(url);
    /* we create an Intent with the action, ACTION_VIEW because we want to VIEW the
     * contents of this Uri.
     */
    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
    /*
     * This is a check we perform with every implicit Intent that we launch. In some cases,
     * the device where this code is running might not have an Activity to perform the action
     * with the data we've specified. Without this check, in those cases your app would crash.
     */
    if (intent.resolveActivity(getPackageManager()) != null) {
        startActivity(intent);
    }
}
```

#### Open a Map 
```java
private void openMap(String url) {
    Uri webpage = Uri.parse(url);
    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
    intent.setData(geoLocation); // Using setData to set the Uri of this Intent has the exact same affect as passing it  in the Intent's constructor. This is simply an alternate way of doing this.
    if (intent.resolveActivity(getPackageManager()) != null) {
        startActivity(intent);
    }
```

#### Share 

This method shares text and allows the user to select which app they would like to use to share the text. Using ShareCompat's IntentBuilder, we get some really cool functionality for free. The chooser that is started using the {@link IntentBuilder#startChooser()} method will create a chooser when more than one app on the device can handle the Intent. This happens when the user has, for example, both a texting app and an email app. If only one Activity on the phone can handle the Intent, it will automatically be launched.

```java
private void shareText(String textToShare) {
    String mimeType = "text/plain";
	String title = "Learning How to Share";
    ShareCompat.IntentBuilder
            .from(this)
            .setType(mimeType)
            .setChooserTitle(title)
            .setText(textToShare)
            .startChooser();
}

You can think of *MIME types* similarly to file extensions. They aren't the exact same, but MIME types help a computer determine which applications can open which content. Forexample, if you double click on a .pdf file, you will be presented with a list of programs that can open PDFs. Specifying the MIME type as text/plain has a similar affect on our implicit Intent. With text/plain specified, all apps that can handle text content
in some way will be offered when we call startActivity on this particular Intent.


