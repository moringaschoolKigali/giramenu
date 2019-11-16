package rw.datasystems.mealorder.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import rw.datasystems.mealorder.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import rw.datasystems.mealorder.Activitiy.CategoryActivity;
import rw.datasystems.mealorder.Activitiy.ItemListActivity;
import rw.datasystems.mealorder.Fragment.ItemDetailFragment;
import rw.datasystems.mealorder.ServiceReceiver.MealOrderService;
import rw.datasystems.mealorder.UI.AutoResizeTextView;
import rw.datasystems.mealorder.UI.CustomFontText;
import rw.datasystems.mealorder.Util.Constants;
import rw.datasystems.mealorder.models.Category;

//implements ItemTouchHelperAdapter
public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final Context mContext;
    private List<Category> mDataset;
    private ViewGroup parent;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View mView;
        public ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public  CategoryAdapter(Context context, List<Category> myDataset) {

        mContext = context;
        mDataset = myDataset;
        int i = Constants.currentpage;

        int end  = myDataset.size()  > (i * 6 ) + 6 ? (i * 6 ) + 6 : myDataset.size();
        //mDataset = myDataset.subList(i * 6 , end);

        int j = (int) Math.ceil(myDataset.size()/6.0);
        //CategoryActivity.pagination.setText(i+1+"/" + String.valueOf(j));

        if(myDataset.size() > 6)
            CategoryActivity.pagination.setText("↓");

    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        ViewHolder vh = new ViewHolder(v);
        this.parent = parent;
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {


         if (vh instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) vh;



            AutoResizeTextView name = (AutoResizeTextView) holder.mView.findViewById(R.id.name);

            CustomFontText count = (CustomFontText) holder.mView.findViewById(R.id.count);
            ImageView cover = (ImageView) holder.mView.findViewById(R.id.cover);
            //count.setText(String.valueOf(mDataset.get(position).number) + " items");
            count.setText(String.valueOf(""));
            name.setText(mDataset.get(position).name);
            String url = mDataset.get(position).cover;


            if (mDataset.get(position).getParent_category() == null || mDataset.get(position).getParent_category().equals("")) {
                if(url != null) {
                    url = url.replace(".jpg", "");
                    url = url.replace(".png", "");
                    url = Constants.baseUrl + "/cover/" + url  + "?type=1";
                }
            } else {

                if(url != null) {
                    url = url.replace(".jpg", "");
                    url = url.replace(".png", "");
                    url = Constants.baseUrl + "/cover/" + url + "?type=1";
                }
            }

            Picasso.with(mContext)
                    .load(url)
                    .placeholder(R.drawable.thumb)
                    .error(R.drawable.thumb)
                    .into(cover);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mDataset.get(position).hasSubCats) {

                        if (mDataset.get(position).getParent_category() == null || mDataset.get(position).getParent_category().equals("")) {
                            //were dealing with a main category load up its subcategories
                            String catid = mDataset.get(position)._id;
                            getCategories getCategories = new getCategories(catid);
                            getCategories.execute((Void) null);

                        } else {

                            Constants.currentcat = mDataset.get(position).name;
                            Intent intent = new Intent(mContext, ItemListActivity.class);
                            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, mDataset.get(position).getId());

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }
                    }

                    else
                    {

                        Constants.currentcat = mDataset.get(position).name;
                        Intent intent = new Intent(mContext, ItemListActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, mDataset.get(position).getId());

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }

                }
            });
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return mDataset.size();

    }

    @Override
    public int getItemViewType(int position) {

        int len = mDataset.size();
        {

            return super.getItemViewType(position);
        }
    }

    private class getCategories extends AsyncTask<Object, Object, Boolean>
    {

        private String catid;

        public getCategories(String catid) {
            this.catid = catid;
        }

        private boolean mStatus;

        @Override
        protected Boolean doInBackground(final Object... params) {

            class Controller implements Callback<List<Category>>
            {

                public void start()
                {

                    Gson gson = new GsonBuilder().setLenient().create();
                    GsonConverterFactory factory = GsonConverterFactory.create(gson);
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.baseUrl)
                            .addConverterFactory(factory).build();
                    MealOrderService mealOrderService = retrofit.create(MealOrderService.class);

                    Call<List<Category>> call = mealOrderService.listSubCategories(catid);
                    call.enqueue(this);
                }


                @Override
                public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {

                    if(response.isSuccessful())
                    {
                        mStatus = true;
                        List<Category> categories = response.body();

                        Constants.currentpage = 0;

                        mDataset = categories;

                        int i = Constants.currentpage;

                        int j = (int) Math.ceil(mDataset.size()/6.0);

//                        CategoryActivity.pagination.setText(i+1+"/" + String.valueOf(j));

                        if(mDataset.size() > 6)
                            CategoryActivity.pagination.setText("↓");
                        else
                            CategoryActivity.pagination.setText("");

                        int end  = mDataset.size()  > (i * 6 ) + 6 ? (i * 6 ) + 6 : mDataset.size();
                        //mDataset = mDataset.subList(i * 6 , end);

                        Constants.subcategories = categories;

                        Constants.isCategories = false;
                        notifyDataSetChanged();

                        CategoryActivity.back.setVisibility(View.VISIBLE);

                    }
                }

                @Override
                public void onFailure(Call<List<Category>> call, Throwable t) {

                    mStatus = false;
                }
            }

            Controller controller = new Controller();
            controller.start();

            return mStatus;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if(!mStatus) {
               // Snackbar.make(parent, "Unable to load details", Snackbar.LENGTH_SHORT).show();

            }

            super.onPostExecute(aBoolean);
        }
    }

}

