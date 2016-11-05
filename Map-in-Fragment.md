### How to insert a Google Map into a fragment

- MainActivity.class : Activity that will hole the fragment
- MapsFragment.class : fragment that will hold the Map
- fragment_maps.xml : layout for the fragment

#### 1) Get the API Key

First of all, you need to get the API key from Google and put it in the Manifest.
https://console.developers.google.com

#### 2) fragment_maps.xml

We are using a MapView with a custom id (mapView here).


```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    >

    <com.google.android.gms.maps.MapView
        android:id="@+id/mapView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</RelativeLayout>

```

#### 3) MapsFragment (extends support.v4.app.Fragment)

> Make sure that you are using "import android.support.v4.app.Fragment;""

```java
package com.kelvinhado.kebab;

import android.support.v4.app.Fragment;
(...)
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap map;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    // this method have to be implemented because the class implements "OnMapReadyCallback"
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        //custom settings
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMaxZoomPreference(18);

        /* TODO add your markers and stuffs */
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
```

#### 3) MainActivity
Display the fragment into the 'FrameLayout' of the main activity.

```java
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
(...)
// display the fragment into the
Fragment fragment = Fragment.instantiate(this, MapsFragment.class.getName());
FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
ft.replace(R.id.frame_container, fragment);
ft.commit();

```
