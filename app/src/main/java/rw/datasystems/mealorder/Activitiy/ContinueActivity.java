package rw.datasystems.mealorder.Activitiy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rw.datasystems.mealorder.Util.Constants;
import rw.datasystems.mealorder.ServiceReceiver.MealOrderService;
import rw.datasystems.mealorder.R;
import rw.datasystems.mealorder.UI.CustomFontButton;
import rw.datasystems.mealorder.models.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class ContinueActivity extends AppCompatActivity {


    public class confirmTask extends AsyncTask<Object, Object, Boolean>
    {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        private boolean mStatus;
        private List<OrderItem> mItems;

        public confirmTask(List<OrderItem> mItems) {
            this.mItems = mItems;
        }

        @Override
        protected Boolean doInBackground(Object... params) {

            class Controller implements Callback<String>
            {

                public void start()
                {
                    List<OrderItem> mItems2 = new ArrayList<>();

                    for (OrderItem item : mItems)
                    {
                        int index = item.item_name.indexOf(":");
                        if(index != -1)
                        {
                            item.item_name = item.item_name.substring(0, index);
                        }

                        index = item.item.name.indexOf(":");
                        if(index != -1)
                        {
                            item.item.name = item.item.name.substring(0, index);
                        }
                        mItems2.add(item);
                    }

                    Gson gson = new GsonBuilder().setLenient().create();
                    GsonConverterFactory factory = GsonConverterFactory.create(gson);
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.baseUrl)
                            .addConverterFactory(factory).build();
                    MealOrderService mealOrderService = retrofit.create(MealOrderService.class);

                    Call<String> call = mealOrderService.confirm(mItems2, Constants.token, String.valueOf(Constants.option));
                    call.enqueue(this);
                }

                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    // progressDialog.dismiss();
                    if(response.isSuccessful())
                    {
                        mStatus = true;
                        String confirmId  = response.body();
                        Constants.orderId = confirmId;
                        Constants.orderitems = null;

                        startActivity(new Intent(getApplicationContext(), ConfirmActivity.class));
                        finish();

                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(findViewById(android.R.id.content), "Unable to confirm", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    //    progressDialog.dismiss();
                    mStatus = false;
                }
            }

            Controller controller = new Controller();
            controller.start();

            return mStatus;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if(mStatus) {

            }

            super.onPostExecute(aBoolean);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue);

        if(Constants.lang == 2)
        {
            ((CustomFontButton) findViewById(R.id.addmore)).setText("Ajouter plus");
            ((CustomFontButton) findViewById(R.id.finish)).setText("Finir la commande");
        }

        findViewById(R.id.finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirmTask confirm1 = new confirmTask(
                        Constants.orderitems);
                confirm1.execute((Void) null);

            }
        });
        findViewById(R.id.addmore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext() , CategoryActivity.class).putExtra("OrderAdded", true));
                Constants.isCategories = true;

            }
        });



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
