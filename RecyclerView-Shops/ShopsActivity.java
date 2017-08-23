package com.kelvinhado.recyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;

public class ShopsActivity extends AppCompatActivity implements ShopListAdapter.ListItemClickListener {

    private ArrayList<Shop> dummyShops;
    private ShopListAdapter mAdapter;
    private RecyclerView mShopList;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops);

        //dummy content
        Shop shop1 = new Shop("KFC", 4.9);
        Shop shop2 = new Shop("McDonalds", 3.9);
        Shop shop3 = new Shop("Burger King", 3.3);
        dummyShops = new ArrayList<>();
        dummyShops.add(shop1);
        dummyShops.add(shop2);
        dummyShops.add(shop3);
        //end dummy content

        mShopList = (RecyclerView) findViewById(R.id.rv_shops);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mShopList.setLayoutManager(layoutManager);
        mShopList.setHasFixedSize(true); //used to improve performance if child size never changes
        // display items using the adapter
        mAdapter = new ShopListAdapter(dummyShops, this);
        mShopList.setAdapter(mAdapter);
    }


    @Override
    public void onListItemClicked(int itemPosition) {
        if(mToast != null) mToast.cancel();
        mToast = Toast.makeText(this, dummyShops.get(itemPosition).getShopName(), Toast.LENGTH_LONG);
        mToast.show();
    }
}
