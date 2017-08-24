## Recycler view : ShopList

### 0 - Add RecyclerView to the gradle

```gradle
dependencies {
	...
    compile 'com.android.support:recyclerview-v7:25.3.1'
}
```

### 1 - Create layout for recycler and items

*activity_shops.xml*
```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.v7.widget.RecyclerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/rv_shops"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.kelvinhado.recyclerview.ShopsActivity" />
```

*shop_list_item.xml*
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:tools="http://schemas.android.com/tools">

    <ImageView
        android:id="@+id/imageView"
        ... />

    <TextView
        android:id="@+id/tv_shop_rate"
        ... />

    <TextView
        android:id="@+id/tv_shop_name"
        ... />
</RelativeLayout>
```

### 3 - Create a POJO for shops

```java
public class Shop {
    private String shopName;
    private double shopRate;
    ...
}

```

### 2 - Create the adapter 

The adapter is responsible of 
- Creating a ViewHolder object for each RecyclerView item
- Returning the number of items in the data source
- Binding data from data source to each item
- Inflating each item view that will be displayed


1) Create an Adapter that extends RecyclerView.Adapter<ShopViewHolder>.
2) Add a ArrayList member variable that will hold the list of shops.
3) Populate this ArrayList using the constructor

### 3 - Create the ViewHolder
4) Create ShopListViewHolder as an innerclass that extends RecyclerView.ViewHolder
5) Create the constructor matching super() and implements a bind(shop) method.

*ShopListAdapter.ShoplistViewHolder*
```java
class ShoplistViewHolder extends RecyclerView.ViewHolder {

        TextView shopNameView;
        TextView shopRateView;

        public ShoplistViewHolder(View itemView) {
            super(itemView);
            shopNameView = (TextView) itemView.findViewById(R.id.tv_shop_name);
            shopRateView = (TextView) itemView.findViewById(R.id.tv_shop_rate);
        }

        void bind(Shop shop) {
            shopNameView.setText(shop.getShopName());
            shopRateView.setText(String.valueOf(shop.getShopRate()));
        }
    }
```

Now that the view holder is ready, we need to finalize the implementation of the Adapter. 
To do so, we need to implements the overrided method (because we extends RecyclerView.Adapter<X>).

### 4 - Override RecyclerView.Adapter methods

#### There are three methods :
- onCreateViewHolder() : This gets called when each new ViewHolder is created. This happens when the RecyclerView is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
- onBindViewHolder() : OnBindViewHolder is called by the RecyclerView to display the data at the specified position. In this method, we update the contents of the ViewHolder to display the correct indices in the list for this particular position, using the "position" argument that is conveniently passed into us.
- getItemCount() : This method simply returns the number of items to display. It is used behind the scenes to help layout our Views and for animations.

```java
    @Override
    public ShoplistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.shop_list_item, parent, false);
        // false : should not be attached to the parent immediately
        ShoplistViewHolder viewHolder = new ShoplistViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShoplistViewHolder holder, int position) {
        holder.bind(shopList.get(position));
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }
```

### 5 - Connect our Adapter with the RecylcerView 
```java
public class ShopsActivity extends AppCompatActivity {

    private ShopListAdapter mAdapter;
    private RecyclerView mShopList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        //dummy content
        Shop shop1 = new Shop("KFC", 4.9);
        Shop shop2 = new Shop("McDonalds", 3.9);
        Shop shop3 = new Shop("Burger King", 3.3);
        ArrayList<Shop> shops = new ArrayList<>();
        shops.add(shop1);
        shops.add(shop2);
        shops.add(shop3);
        //end dummy content

        mShopList = (RecyclerView) findViewById(R.id.rv_shops);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mShopList.setLayoutManager(layoutManager);
        mShopList.setHasFixedSize(true); //used to improve performance if child size never change
        // display items using the adapter
        mAdapter = new ShopListAdapter(shops);
        mShopList.setAdapter(mAdapter);
    }
}
```
A LinearLayoutManager is responsible for measuring and positioning item views within a RecyclerView into a linear list. This means that it can produce either a horizontal or vertical list depending on which parameter you pass in to the LinearLayoutManager constructor. By default, if you don't specify an orientation, you get a vertical list. In our case, we want a vertical list, so we don't need to pass in an orientation flag tonthe LinearLayoutManager constructor.

There are other LayoutManagers available to display your data in uniform grids, staggered grids, and more! See the developer documentation for more details.


### 6 - Handle item click 

1) Create an interface into the adapter 
```java
public interface ListItemClickListener {
    void onListItemClicked(int itemPosition);
}
```

2) Add a listener as a parameter to the adapter constructor and store it in member variable.

3) Make the ViewHolder implements the OnClickListener (!= ListItemClicked) and override the onClick method.
This method will call the onListItemClicked method of our listener, passing the getAdapterPosition() as parameter.
```java
... extends View.OnClickListener

@Override
public void onClick(View view) {
    listener.onListItemClicked(getAdapterPosition());
}
```

4) Inside the ViewModel constructor, set itemView.OnClickListener(this);
```java
public ShoplistViewHolder(View itemView) {
        super(itemView);
        ...
        itemView.setOnClickListener(this);
    }
```
5) The activity holding the adapter will then have to implements our ListItemClickListener.

