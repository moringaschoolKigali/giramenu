package rw.datasystems.mealorder.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import rw.datasystems.mealorder.UI.CustomFontText;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import rw.datasystems.mealorder.Util.Constants;
import rw.datasystems.mealorder.Activitiy.ExcludeListActivity;

import rw.datasystems.mealorder.R;
import rw.datasystems.mealorder.models.Component;

public class NewComponentAdapter extends RecyclerView.Adapter<NewComponentAdapter.ViewHolder> {

        private final List<Component> mValues;
        private final Context mContext;


    public NewComponentAdapter(Context context, List<Component> items) {

            mValues = items;
            mContext = context;


            Constants.excluded = new HashMap<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_new_component, parent, false);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.mItem = mValues.get(position);
            holder.name.setText(mValues.get(position).getName());

            String url = mValues.get(position).cover;
            ImageView cover = (ImageView) holder.mView.findViewById(R.id.cover);
            if(url != null)
                url = Constants.baseUrl + "/cover/" + url.replace(".jpg", "")  + "?type=4";

            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.drawable.thumb)
                    .error(R.drawable.thumb)
                    .into(cover);

            holder.cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(holder.overlay.getVisibility() ==  View.VISIBLE)
                    {
                        //add
                        if(Constants.excluded.containsKey(String.valueOf(position)))
                            Constants.excluded.remove(String.valueOf(position));

                        holder.overlay.setVisibility(View.INVISIBLE);
                        if(Constants.lang == 1)
                        //ExcludeListActivity.addtorder.setText("No, Add to Order");
                        ExcludeListActivity.addtorder.setText("Next");
                        else
                            ExcludeListActivity.addtorder.setText("Non, Ajouter à la liste");

                    }
                    else
                    {
                        Constants.excluded.put(String.valueOf(position), mValues.get(position));
                        //exclude
                        holder.overlay.setVisibility(View.VISIBLE);
                        if(Constants.lang == 1)
                        //ExcludeListActivity.addtorder.setText("Done, Add to Order");
                        ExcludeListActivity.addtorder.setText("Next");
                        else
                            ExcludeListActivity.addtorder.setText("Ajouter à la liste");

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

            public Component mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                name = (CustomFontText) view.findViewById(R.id.name);
                overlay = (ImageView) view.findViewById(R.id.overlay);
                cover = (ImageView) view.findViewById(R.id.cover);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + name.getText() + "'";
            }
        }
    }