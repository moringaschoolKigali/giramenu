package rw.datasystems.mealorder.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import rw.datasystems.mealorder.*;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import rw.datasystems.mealorder.Activitiy.ItemDetailActivity;
import rw.datasystems.mealorder.Activitiy.ItemListActivity;
import rw.datasystems.mealorder.Fragment.ItemDetailFragment;
import rw.datasystems.mealorder.UI.CustomFontText;
import rw.datasystems.mealorder.Util.Constants;
import rw.datasystems.mealorder.models.Items;
import rw.datasystems.mealorder.models.Price;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

        private List<Items> mValues = null;
        private final Context mContext;
        private final boolean mTwoPane;

    public ItemAdapter(Context context, boolean TwoPane, List<Items> items) {

            mContext = context;
            mTwoPane  = TwoPane;

            int i = Constants.currentpageitems;
            int end  = items.size()  > (i * 6 ) + 6 ? (i * 6 ) + 6 : items.size();

            mValues = items;

            try {
                //mValues = items.subList(i * 6 , end);
                int j = (int) Math.ceil(items.size()/6.0);
                //ItemListActivity.pagination.setText(i+1+"/" + String.valueOf(j));
                ItemListActivity.pagination.setText("");
            }

            catch (Exception e)
            {
                if(items != null)
                    mValues  = items;
                e.printStackTrace();
            }


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.mItem = mValues.get(position);
            holder.name.setText(mValues.get(position).getName());
           //holder.category.setText(Constants.currentcat);

            String url = mValues.get(position).cover;
            ImageView cover = (ImageView) holder.mView.findViewById(R.id.cover);
            if(url != null)
                url = Constants.baseUrl + "/cover/" + url.replace(".jpg", "")  + "?type=3";

            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.drawable.thumb)
                    .error(R.drawable.thumb)
                    .into(cover);


            List<Price> prices = mValues.get(position).getPrices();
            int minPrice = Integer.MAX_VALUE;
            for (int i = 0; i < prices.size(); i++) {

                int price = (int)Float.parseFloat(prices.get(i).getPrice());

                if(price < minPrice)
                    minPrice = price;

            }

            holder.price.setText(String.valueOf(minPrice) + " RWF" );
            holder.price.setText("");

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    {
                        Intent intent = new Intent(mContext, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, position);
                        intent.putExtra(ItemDetailFragment.ARG_CAT_ID, mValues.get(position).subcategory_id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final CustomFontText name;
            public final CustomFontText price;
  //          public final CustomFontText time;
            //public final rw.datasystems.mealorder.UI.CustomFontButton category;
            public final ImageView cover;

            public Items mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                name = (CustomFontText) view.findViewById(R.id.name);
                price = (CustomFontText) view.findViewById(R.id.price);
    //            time = (CustomFontText) view.findViewById(R.id.time);

               // category = (rw.datasystems.mealorder.UI.CustomFontButton) view.findViewById(R.id.catlabel);
                cover = (ImageView) view.findViewById(R.id.cover);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + name.getText() + "'";
            }
        }
    }