## Dynamic Fragments

This document is showing us how to use fragments dynamically with two fragments (a list and a detail fragment). The activity which will hold those two fragments (in landscape mode) and one at the time (in portrait mode) will hundle the event from the list fragment to adapt the display on the detail fragment.


* 1) Create the List Fragment
  * create the Fragment that extends ListFragment
  * create the layout that contains a listview (+id/list) and tewtview (+id/empty)
  * inflate the fragment with is own layout in onCreateView

* 2) Create the detail Fragment
  * create the Fragment that extends Fragment
  * create the custom layout
  * add update(...) method
  * add newInstance(...) static method
  * inflate the fragment with is own layout in onCreateView

> each fragment need a empty constructor

* 3) Create the activity
  * create different kind of layout : (for example)
    * portrait (one fragment at the same time) > contains only one FrameLayout
    * landscape (two fragments onthe same screen) > contains our two fragments
  * in OnCreate(..) if portrait mode = do the transaction to display the list fragment in the FrameLayout

* 4) Hundle communication : Add Interface
  * Add inner public Interface *CustomListener* in *List Fragment.java* with callback method
  * Add listener in the *List Fragment.java*
  * Implements onAttach / onListItemClick

* 5) Hundle communication : Implements the interface *CustomListener* in *Activity.java*
  * implements callback method
