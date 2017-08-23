package com.kelvinhado.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kelvin on 23/08/2017.
 */

public class ShopListAdapter extends RecyclerView.Adapter<ShopListAdapter.ShoplistViewHolder> {
    private ArrayList<Shop> shopList;
    private ListItemClickListener listener;

    public ShopListAdapter(ArrayList<Shop> shopList, ListItemClickListener listener) {
        this.shopList = shopList;
        this.listener = listener;
    }

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

    class ShoplistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView shopNameView;
        TextView shopRateView;

        public ShoplistViewHolder(View itemView) {
            super(itemView);
            shopNameView = (TextView) itemView.findViewById(R.id.tv_shop_name);
            shopRateView = (TextView) itemView.findViewById(R.id.tv_shop_rate);
            itemView.setOnClickListener(this);
        }

        void bind(Shop shop) {
            shopNameView.setText(shop.getShopName());
            shopRateView.setText(String.valueOf(shop.getShopRate()));
        }

        @Override
        public void onClick(View view) {
            listener.onListItemClicked(getAdapterPosition());
        }
    }

    public interface ListItemClickListener {
        void onListItemClicked(int itemPosition);
    }
}
