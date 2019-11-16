package rw.datasystems.mealorder.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import rw.datasystems.mealorder.UI.CustomFontButton;


import java.util.List;

import rw.datasystems.mealorder.Fragment.ItemDetailFragment;
import rw.datasystems.mealorder.R;
import rw.datasystems.mealorder.models.Price;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {

        private final List<Price> mValues;
        private final Context mContext;



    public PriceAdapter(Context context, List<Price> prices) {

            mValues = prices;
            mContext = context;

        }

        @Override
        public PriceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_new_price_class, parent, false);

            return new PriceAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final PriceAdapter.ViewHolder holder, final int position) {

            Float base = Float.valueOf(mValues.get(0).getPrice());

            holder.mPriceItem = mValues.get(position);

            Float curr = Float.valueOf(mValues.get(position).getPrice());
            holder.classname.setText(mValues.get(position).getName() + " (" + String.format("%,.0f", curr) + " RWF)");


//            if(curr - base > 0.0) {
//
//                holder.classname.setText(mValues.get(position).getName() + " (+" + String.valueOf(curr-base)+ " RWF)");
//            }
//
//            else
//
//                holder.classname.setText(mValues.get(position).getName());

            holder.classname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItemDetailFragment.change(mContext, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final CustomFontButton classname;

            public  Price mPriceItem;

            public ViewHolder( View mView) {

                super(mView);
                this.mView = mView;

                classname = (CustomFontButton) mView.findViewById(R.id.priceclassbtn);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + classname.getText() + "'";
            }
        }
    }