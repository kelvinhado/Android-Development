### Create a ListFragment

The list fragment will display a listview cf ListView.md

#### 1) CustomListFragment (extends ListFragment)

> Make sure that you are using "import android.support.v4.app.ListFragment;""

```java
package com.kelvinhado.eventparis.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kelvinhado.eventparis.R;


public class EventsListFragment extends ListFragment {

    onEventSelectedListenner mListener = null;
    private Events events;                    // this will contains the list of object that we want to display
    private EventsListAdapter lvAdapter;      // => Go see the code for the adapter below


    public EventsListFragment() {
        // Required empty public constructor
    }
    public static EventsListFragment newInstance() {
        EventsListFragment myFragment = new EventsListFragment();
        Bundle args = new Bundle();
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO : Inflate the layout for this fragment => Go see the fragment_list below
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        events = new Events();
        lvAdapter = new EventsListAdapter(getActivity(), events);
    }

    @Override
    public void onStart() {
        super.onStart();
        // TODO : fetch data here and call the method below
        populateListView(dataSet.getRecords());
    }

    private void populateListView(Events events) {
        lvAdapter = new EventsListAdapter(getActivity(), events);
        setListAdapter(lvAdapter);
    }


   /* To hundle interation, the activity must implements this interface */

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onEventSelectedListenner) activity;
        } catch (ClassCastException e) {
            // Unchecked exception.
            throw new ClassCastException(activity.toString()
                    + " must implement onEventSelectedListenner");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Send the event to the host activity
        Event event = (Event) getListView().getItemAtPosition(position);
        mListener.onEventSelected(event);
    }

    /**
      * This interface must be implemented by activities that contain this
      * fragment to allow an interaction in this fragment to be communicated
      * to the activity and potentially other fragments contained in that
      * activity.
      */

    public // Interface interne
    interface onEventSelectedListenner {
        void onEventSelected(Event event);
    }


}

```
#### 2) Create the custom adapter, xml files
Go see the ListView.md

#### 3) Activity that hold the fragment
```java
public class MainActivity extends whatever
        implements EventsListFragment.onEventSelectedListenner { // !!!!


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // (...)

        // display the list fragment !
            final EventsListFragment eventsListFragment = EventsListFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutMainActivity, eventsListFragment)  
                .commit();
                // the frameLayoutMainActivity is detail below
        }


      @Override
      public void onEventSelected(Event event) {
          Log.d("DEBUG // MainActivity :", event.getFields().getTitle());
      }    

    }

```
#### 4) activity_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/frameLayoutMainActivity"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />

```
