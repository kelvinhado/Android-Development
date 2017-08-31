# CursorLoader

A CursorLoader is a subclass of AsyncTaskLoader that queries a ContentProvider, via a ContentResolver and specific URI, and returns a Cursor of desired data. This loader runs its query on a background thread so that it doesn’t block the UI. When a CursorLoader is active, it is tied to a URI, and you can choose to have it monitor this URI for any changes in data; this means that the CursorLoader can deliver new results whenever the contents of our weather database change, and we can automatically update the UI to reflect any weather change!

