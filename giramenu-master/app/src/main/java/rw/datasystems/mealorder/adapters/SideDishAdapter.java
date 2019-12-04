package rw.datasystems.mealorder.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
//import pl.polak.clicknumberpicker.ClickNumberPickerListener;
//import pl.polak.clicknumberpicker.ClickNumberPickerView;
//import pl.polak.clicknumberpicker.PickerClickType;
import rw.datasystems.mealorder.Activitiy.CategoryActivity;
import rw.datasystems.mealorder.Activitiy.ExcludeListActivity;
import rw.datasystems.mealorder.Activitiy.SideDishActivity;
import rw.datasystems.mealorder.R;
import rw.datasystems.mealorder.UI.ClickNumber.ClickNumberPickerListener;
import rw.datasystems.mealorder.UI.ClickNumber.ClickNumberPickerView;
import rw.datasystems.mealorder.UI.ClickNumber.PickerClickType;
import rw.datasystems.mealorder.UI.CustomFontText;
import rw.datasystems.mealorder.Util.Constants;

import rw.datasystems.mealorder.models.Sidedish;

import java.util.HashMap;
import java.util.List;

public class SideDishAdapter extends RecyclerView.Adapter<SideDishAdapter.ViewHolder> {

        private final List<Sidedish> mValues;
        private final Context mContext;
        private final int quantity;
        private int quantityholder;

    public SideDishAdapter(Context context, List<Sidedish> items) {

            mValues = items;
            mContext = context;
            quantity = Constants.pendingOrderItem.qnty;
            quantityholder = 0;

            Constants.addedsidedishes = new HashMap<>();
            Constants.controlPicker = true;

            if(items.size() > 6)
                CategoryActivity.pagination.setText("↓");
            else
                CategoryActivity.pagination.setText("");

    }


    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {

        super.onViewAttachedToWindow(holder);
    }

    @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_side, parent, false);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.mItem = mValues.get(position);
            holder.name.setText(mValues.get(position).getName());

            String url = mValues.get(position).cover;
            ImageView cover = (ImageView) holder.mView.findViewById(R.id.cover);
            if(url != null)
                url = Constants.baseUrl + "/cover/" + url.replace(".jpg", "")  + "?type=7";

            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.drawable.thumb)
                    .error(R.drawable.thumb)
                    .into(cover);

            holder.qnty.setClickNumberPickerListener(new ClickNumberPickerListener() {

                @Override
                public void onValueChange(float previousValue, float currentValue, PickerClickType pickerClickType) {

                    if(previousValue == currentValue){
                        return;
                    }
                    if(pickerClickType == PickerClickType.LEFT)
                    {
                        if(quantityholder > 0)
                        {
                            if (Constants.addedsidedishes.containsKey(String.valueOf(position)))
                            {
                                if(Constants.addedsidedishes.get(String.valueOf(position)).quantity != null)
                                {
                                    Integer qty = Constants.addedsidedishes.get(String.valueOf(position)).quantity;

                                    if(qty > 1 )
                                    {
                                        Sidedish temp = mValues.get(position);
                                        temp.quantity = qty - 1;
                                            Constants.addedsidedishes.put(String.valueOf(position), temp);
                                    }
                                    else
                                    {
                                        Constants.addedsidedishes.remove(String.valueOf(position));
                                    }

                                    quantityholder = quantityholder -1;
                                    holder.qnty._setPickerValue(currentValue);
                                }
                                else
                                {
                                    holder.qnty._setPickerValue(previousValue);

                                    new AlertDialog.Builder(holder.mView.getContext())
                                            .setMessage("Sorry no quantity of sidedish added")
                                            .setCancelable(true)
                                            .show();

                                    Log.d("SIDEDISH", "QNTY SHOULDNT BE NULL");
                                }
                            }
                            else
                            {
                                holder.qnty._setPickerValue(previousValue);
                                new AlertDialog.Builder(holder.mView.getContext())
                                        .setMessage("Sorry no quantity of sidedish added")
                                        .setCancelable(true)
                                        .show();

                                 Log.d("SIDEDISH", "NOT ADDED ANY QNTY OF THIS SIDEDISH");
                            }


                        }
                        else
                        {
                            holder.qnty._setPickerValue(previousValue);
                            Log.d("SIDEDISH", "NOT ADDED ANY QNTY OF ANY SIDEDISH");
                            new AlertDialog.Builder(holder.mView.getContext())
                                    .setMessage("Sorry no quantity of sidedish added")
                                    .setCancelable(true)
                                    .show();

                        }
                    }
                    else if(pickerClickType == PickerClickType.RIGHT)
                    {

                        if(quantityholder < quantity)
                        {
                            if (Constants.addedsidedishes.containsKey(String.valueOf(position)))
                            {

                                Sidedish temp = Constants.addedsidedishes.get(String.valueOf(position));
                                temp.quantity = temp.quantity +1;
                                Constants.addedsidedishes.put(String.valueOf(position), temp);

                            }
                            else
                            {
                                Sidedish temp = mValues.get(position);
                                temp.quantity = 1;
                                Constants.addedsidedishes.put(String.valueOf(position), temp);
                            }

                            quantityholder =  quantityholder +1;
                            holder.qnty._setPickerValue(currentValue);
                        }
                        else
                        {
                            holder.qnty._setPickerValue(previousValue);
                            //holder.qnty._setPickerValue(-1);
                            new AlertDialog.Builder(holder.mView.getContext())
                                    .setMessage("Sorry can't go beyond order quantity")
                                    .setCancelable(true)
                                    .create()
                                    .show();

                            Log.d("SIDEDISH", "CANT GO BEYOND THRESH");
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
            public final ImageView cover;
            public final ImageView overlay;
            public final ClickNumberPickerView qnty;

            public Sidedish mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                name = (CustomFontText) view.findViewById(R.id.name);
                overlay = (ImageView) view.findViewById(R.id.overlay);
                cover = (ImageView) view.findViewById(R.id.cover);
                qnty = (ClickNumberPickerView) view.findViewById(R.id.quantitypicker);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + name.getText() + "'";
            }
        }
    }