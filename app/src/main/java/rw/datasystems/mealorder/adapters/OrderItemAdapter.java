package rw.datasystems.mealorder.adapters;

import android.content.Context;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import rw.datasystems.mealorder.UI.CustomFontText;

import java.util.List;

import rw.datasystems.mealorder.Activitiy.CategoryActivity;
import rw.datasystems.mealorder.R;
import rw.datasystems.mealorder.models.OrderItem;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

        private final List<OrderItem> mValues;
        private final Context mContext;
        private final boolean mTwoPane;


    public OrderItemAdapter(Context context, boolean twoPane, List<OrderItem> orderitems) {

        mValues = orderitems;
        mContext = context;
        mTwoPane  = twoPane;
    }

    @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_plate_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.mItem = mValues.get(position);
            holder.name.setText(String.valueOf(mValues.get(position).qnty) + " X  " + mValues.get(position).itemclass + " "+
                    mValues.get(position).item.getName());

            if(mValues.get(position).excluded != null && mValues.get(position).excluded.size() > 0)
            {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i =0; i< mValues.get(position).excluded.size(); i ++) {

                    if(i > 0) stringBuilder.append(",");

                    stringBuilder.append(" " + mValues.get(position).excluded.get(i).getName());

                }

                holder.name.setText(String.valueOf(mValues.get(position).qnty) + " X  " + mValues.get(position).itemclass + " "+
                        mValues.get(position).item.getName() + " without" + stringBuilder.toString());
            }

           // holder.time.setText(String.valueOf(mValues.get(position).item.getTime()) + " Mins" );

            holder.price.setText(mValues.get(position).qnty + " "  +mValues.get(position).itemclass);
            holder.price.setText(String.format("%,.0f", mValues.get(position).current_price) + " RWF");

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     {

                          mValues.remove(position);
                          notifyDataSetChanged();

                        float sum = 0f;

                        for (int i = 0; i < mValues.size(); i++) {

                            sum += (mValues.get(i).qnty * mValues.get(i).current_price);

                        }
                        if(mValues.size() > 0 )
                        {
                            if (CategoryActivity.totalprice != null) {
                                CategoryActivity.totalprice.setText(String.valueOf(sum) + " RWF");

                                Snackbar.make(CategoryActivity.v, "Order Removed", Snackbar.LENGTH_SHORT).show();

                            }

                        }
                        else {
                            CategoryActivity.categorywelcome.setText("You've not chosen anything from the menu. Feel free to begin by choosing one of the categories to the left");

                            if (CategoryActivity.totalprice != null) {
                                Snackbar.make(CategoryActivity.v, "Order Removed", Snackbar.LENGTH_SHORT).show();
                                CategoryActivity.totalprice.setText("0.0 RWF");
                            }
                        }
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
            //public final CustomFontText time;


            public OrderItem mItem;
            public View remove;

            public ViewHolder(View view) {

                super(view);
                mView = view;
                name = (CustomFontText) view.findViewById(R.id.name);
                price = (CustomFontText) view.findViewById(R.id.priceclass);
                //time = (CustomFontText) view.findViewById(R.id.time);
                remove = view.findViewById(R.id.remove);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + name.getText() + "'";
            }
        }
    }