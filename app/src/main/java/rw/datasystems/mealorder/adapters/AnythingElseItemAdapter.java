package rw.datasystems.mealorder.adapters;

import android.content.Context;
import android.graphics.Typeface;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import rw.datasystems.mealorder.Activitiy.AnythingElseListActivity;
import rw.datasystems.mealorder.Util.Constants;
import rw.datasystems.mealorder.UI.CustomFontText;
import rw.datasystems.mealorder.R;
import rw.datasystems.mealorder.models.Items;

import java.util.HashMap;
import java.util.List;

public class AnythingElseItemAdapter extends RecyclerView.Adapter<AnythingElseItemAdapter.ViewHolder> {

        private List<Items> mValues = null;
        private final Context mContext;
        private final boolean mTwoPane;

    public AnythingElseItemAdapter(Context context, boolean TwoPane, List<Items> items) {

            mContext = context;
            mTwoPane  = TwoPane;
            Constants.related = new HashMap<>(items.size());

            if(items != null)
            {
                mValues  = items;
            }
             //

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_anythingelse, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.mItem = mValues.get(position);
            holder.name.setText(mValues.get(position).name);
           //holder.category.setText(Constants.currentcat);

            String url = mValues.get(position)._id;
            ImageView cover = (ImageView) holder.mView.findViewById(R.id.cover);
            if(url != null) {
                url = url.replace(".jpg", "");
                url = url.replace(".png", "");
                url = Constants.baseUrl + "/cover/" + url + "?type=5";
            }
            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.drawable.thumb)
                    .error(R.drawable.thumb)
                    .into(cover);

            final String price = mValues.get(position).prices.get(0).price;

            holder.price.setText(String.valueOf(price) + " RWF" );
            holder.price.setTypeface(null, Typeface.BOLD);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    {

                        if(holder.overlay.getVisibility() ==  View.VISIBLE)
                        {
                            //add

                            if(Constants.related.get((position)) != null)
                                Constants.related.remove(position);

                            holder.overlay.setVisibility(View.INVISIBLE);
                            if(Constants.lang == 1)
                                //AnythingElseListActivity.addtoorder.setText("Done, Continue");
                                AnythingElseListActivity.addtoorder.setText("Next");
                            else
                                AnythingElseListActivity.addtoorder.setText("Continuer");

                        }
                        else
                        {
                            Constants.related.put(position, mValues.get(position));
                            //exclude
                            holder.overlay.setVisibility(View.VISIBLE);
                            if(Constants.lang == 1)
                                //AnythingElseListActivity.addtoorder.setText("Done, Add to Order");
                                AnythingElseListActivity.addtoorder.setText("Next");
                            else
                                AnythingElseListActivity.addtoorder.setText("Continuer");

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
            public final ImageView cover;
            public final ImageView overlay;

            public Items mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                name = (CustomFontText) view.findViewById(R.id.name);
                price = (CustomFontText) view.findViewById(R.id.price);
                cover = (ImageView) view.findViewById(R.id.cover);
                overlay = (ImageView) view.findViewById(R.id.overlay);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + name.getText() + "'";
            }
        }
    }