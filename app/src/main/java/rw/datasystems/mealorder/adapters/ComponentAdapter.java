package rw.datasystems.mealorder.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import rw.datasystems.mealorder.UI.AutoResizeTextView;
import rw.datasystems.mealorder.Util.Constants;
import rw.datasystems.mealorder.Activitiy.ExcludeListActivity;
import rw.datasystems.mealorder.R;
import rw.datasystems.mealorder.models.Component;

public class ComponentAdapter extends RecyclerView.Adapter<ComponentAdapter.ViewHolder> {

        private final List<Component> mValues;
        private final Context mContext;



    public ComponentAdapter(Context context, List<Component> components) {

            mContext = context;

            int i = Constants.currentpage;


            int end  = components.size()  > (i * 6 ) + 6 ? (i * 6 ) + 6 : components.size();
            mValues = components.subList(i * 6 , end);


            Constants.excluded = new HashMap<>();

        }

        @Override
        public ComponentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_new_component, parent, false);

            return new ComponentAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ComponentAdapter.ViewHolder holder, final int position) {

            holder.mComponent = mValues.get(position);
            holder.component.setText(mValues.get(position).getName());

            String url = mValues.get(position).cover;

            if(url != null)
                url = Constants.baseUrl + "/cover/" + url.replace(".jpg", "")  + "?type=4";

            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.drawable.thumb)
                    .error(R.drawable.thumb)
                    .into(holder.cover);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(holder.overlay.getVisibility() ==  View.VISIBLE)
                    {
                        //add
                        if(Constants.excluded.containsKey(String.valueOf(position)))
                        Constants.excluded.remove(String.valueOf(position));

                        ExcludeListActivity.addtorder.setText("NEXT");
                        holder.overlay.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        Constants.excluded.put(String.valueOf(position), mValues.get(position));
                        //exclude
                        holder.overlay.setVisibility(View.VISIBLE);
                        ExcludeListActivity.addtorder.setText("NEXT");

                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final AutoResizeTextView component;
            public final ImageView cover;
            public final ImageView overlay;
            public  Component mComponent;

            public ViewHolder( View mView) {

                super(mView);
                this.mView = mView;

                component = (AutoResizeTextView) mView.findViewById(R.id.name);
                cover = (ImageView) mView.findViewById(R.id.cover);
                overlay = (ImageView) mView.findViewById(R.id.overlay);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + component.getText() + "'";
            }
        }
    }