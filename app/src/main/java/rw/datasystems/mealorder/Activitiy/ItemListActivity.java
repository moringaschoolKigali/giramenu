package rw.datasystems.mealorder.Activitiy;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rw.datasystems.mealorder.Util.Constants;
import rw.datasystems.mealorder.Fragment.ItemDetailFragment;
import rw.datasystems.mealorder.ServiceReceiver.MealOrderService;
import rw.datasystems.mealorder.R;
import rw.datasystems.mealorder.UI.CustomFontButton;
import rw.datasystems.mealorder.UI.CustomFontText;
import rw.datasystems.mealorder.UI.CustomViewGroup;
import rw.datasystems.mealorder.adapters.ItemAdapter;
import rw.datasystems.mealorder.adapters.OrderItemAdapter;
import rw.datasystems.mealorder.models.Auth;
import rw.datasystems.mealorder.models.Items;
import rw.datasystems.mealorder.models.Price;
import rw.datasystems.mealorder.models.Related;

import java.util.ArrayList;
import java.util.List;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerView recyclerView;
    private CustomFontText catname;
    private CustomFontText categorywelcome;
    public static CustomFontText pagination;
    private CustomFontText totalpricetext, remove;
    private CustomFontText yourordertext;
    private CustomFontButton btnconfirm;
    private Menu menu;
    private CustomFontButton previous;
    private CustomFontButton addtorder, next;
    public static CustomFontButton back;
    private List<Items> items;
    private int count;
    List<Related> AllOffers = new ArrayList<>();
    DisconnectTask disconnectTask;
    private View view;
    private Dialog dialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.category, menu);
        this.menu = menu;
        changeLanguage(true);

        return true;
    }

    private void hidePagination() {

        //findViewById(R.id.previous).setVisibility(View.INVISIBLE);
        //findViewById(R.id.pagination).setVisibility(View.INVISIBLE);
        //findViewById(R.id.next).setVisibility(View.INVISIBLE);

        findViewById(R.id.addtoorder).setVisibility(View.INVISIBLE);
        findViewById(R.id.confirm).setVisibility(View.VISIBLE);

        if(Constants.orderitems.size()> 0) {

            findViewById(R.id.confirm).setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        back = (CustomFontButton) findViewById(R.id.back);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        Constants.currentpageitems = 0;

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // onBackPressed();
                }
            });
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        String Id = getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID);
        int fromOffer = getIntent().getIntExtra("fromOffers", -1);

        if(fromOffer != -1 )
        {
            for(int i= fromOffer; i< fromOffer+1; i++) {

                String offerid = Constants.offers.get(i)._id;

                try {
                    getOffer go = new getOffer(offerid);
                    go.execute((Void) null);
                }
                catch (Exception e)
                {

                }
                
            }
        }
        else {

            getItems getItems = new getItems(Id);
            getItems.execute((Void) null);
        }

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }


        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        catname = (CustomFontText) findViewById(R.id.categoryname);
        //previous = (CustomFontButton) findViewById(R.id.previous);
        //next = (CustomFontButton) findViewById(R.id.next);
        addtorder = (CustomFontButton) findViewById(R.id.addtoorder);

//        addShift(previous);
//        addShift(next);

        hidePagination();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        assert recyclerView != null;


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Please give my app this permission!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            } else {
                disableStatusBar();
            }
        } else {
            disableStatusBar();
        }

        categorywelcome = (CustomFontText) findViewById(R.id.categorywelcome);

        if (Constants.orderitems != null && Constants.orderitems.size() > 0)
            categorywelcome.setText("Here's a look at your order");
        else
            categorywelcome.setText("You've not chosen anything from the menu. Feel free to begin by choosing one of the categories to the left");

        CustomFontText totalprice = (CustomFontText) findViewById(R.id.totalprice);
        btnconfirm = (CustomFontButton) findViewById(R.id.confirm);

        Float sum = Float.valueOf(0);

        if (Constants.orderitems == null) return;

        for (int i = 0; i < Constants.orderitems.size(); i++) {

            sum += Constants.orderitems.get(i).current_price * Constants.orderitems.get(i).qnty;
        }

        totalprice.setText((String.format("%,.0f", sum) + " RWF"));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.plate_item_list);
        assert recyclerView != null;

        if (Constants.orderitems.size() > 0)
            recyclerView.setAdapter(new OrderItemAdapter(ItemListActivity.this, false, Constants.orderitems));


        yourordertext = (CustomFontText) findViewById(R.id.yourordertext);
        remove = (CustomFontText) findViewById(R.id.remove);
        totalpricetext = (CustomFontText) findViewById(R.id.totalpricetext);
        pagination = (CustomFontText) findViewById(R.id.pagination);


        if (Constants.lang == 2) {
            yourordertext.setText("Votre commande");
            totalpricetext.setText(R.string.total);

            if (Constants.orderitems.size() > 0) {
                remove.setText("retirer");
                categorywelcome.setText("Voici votre commande");
            } else {
                categorywelcome.setText("Vous n'avez rien choisi dans le menu. N'hésitez pas à commencer par choisir une des catégories à gauche");

            }

            btnconfirm.setText("Finir la commande");
            back.setText("arrière");
        } else {

            yourordertext.setText("Your order");
            totalpricetext.setText(R.string.total);

            if (Constants.orderitems.size() > 0) {
                remove.setText("remove");
                categorywelcome.setText("Here is a look at your order");
            } else {
                categorywelcome.setText("You've not chosen anything from the menu. Feel free to begin by choosing one of the categories to the left");
            }

            btnconfirm.setText("Finish order");
            back.setText("back");
        }


    }

//    private void addShift(final CustomFontButton button) {
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (button.getId() == R.id.next) {
//                    if (items != null) {
//                        if (items.size() > (Constants.currentpageitems + 1) * 6) {
//
//                            Constants.currentpageitems += 1;
//                            recyclerView.setAdapter(new ItemAdapter(ItemListActivity.this, mTwoPane, items));
//
//                        }
//                    }
//                } else {
//                    if (items != null) {
//                        if (Constants.currentpageitems > 0) {
//                            Constants.currentpageitems -= 1;
//                            recyclerView.setAdapter(new ItemAdapter(ItemListActivity.this, mTwoPane, items));
//                        }
//                    }
//                }
//            }
//        });
//    }


    protected void disableStatusBar() {

        WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // this is to enable the notification to receive touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (40 * getResources().getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        blockingView = new CustomViewGroup(this);
        manager.addView(blockingView, localLayoutParams);
    }

    public static final int OVERLAY_PERMISSION_REQ_CODE = 4545;
    protected CustomViewGroup blockingView = null;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.language) {
            changeLanguage();
        }
//        else if (item.getItemId() == R.id.home2){
//            Intent intent = new Intent(getApplicationContext(), ActivateActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//        }
        else if (item.getItemId() == R.id.disc)
        {
            view = getLayoutInflater().inflate(R.layout.activity_disconnect, null);
            EditText mPassword = (EditText) view.findViewById(R.id.password);
            EditText mUsername = (EditText) view.findViewById(R.id.username);
            CustomFontButton mActivateButton = (CustomFontButton) view.findViewById(R.id.button);

            final EditText finalMUsername1 = mUsername;
            final EditText finalMPassword1 = mPassword;
            mActivateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (disconnectTask == null) {

                        disconnectTask = new ItemListActivity.DisconnectTask(finalMUsername1.getText().toString(), finalMPassword1.getText().toString());
                        disconnectTask.execute((Void)null);

                    }
                }
            });

            if (view.getParent() == null) {
                dialog = new Dialog(ItemListActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(view);
                dialog.show();

            } else {

                mPassword = (EditText) view.findViewById(R.id.password);
                mUsername = (EditText) view.findViewById(R.id.username);
                mActivateButton = (CustomFontButton) view.findViewById(R.id.activate_button);

                final EditText finalMPassword = mPassword;
                final EditText finalMUsername = mUsername;
                mActivateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (disconnectTask == null) {

                            disconnectTask = new ItemListActivity.DisconnectTask(finalMUsername.getText().toString(), finalMPassword.getText().toString());
                            disconnectTask.execute((Void)null);

                        }
                    }
                });

                dialog.show();
            }
        }

        return super.onOptionsItemSelected(item);

    }

    private void changeLanguage() {

        MenuItem langItem = menu.findItem(R.id.language);

        if (Constants.lang == 1) {


            Constants.lang = 2;

            yourordertext.setText("Votre commande");
            //previous.setText("Page précédente");
            //next.setText("Page suivante");
            addtorder.setText("Ajouter à la liste");

            totalpricetext.setText(R.string.total);

            if (Constants.orderitems.size() > 0) {
                remove.setText("retirer");
                categorywelcome.setText("Voici votre commande");
            } else {
                categorywelcome.setText("Vous n'avez rien choisi dans le menu. N'hésitez pas à commencer par choisir une des catégories à gauche");

            }

            btnconfirm.setText("Finir la commande");
            back.setText("arrière");
            langItem.setTitle("   English");

        } else {


            Constants.lang = 1;

            yourordertext.setText("Your order");
            //previous.setText("Previous page");
            //next.setText("Next page");
            addtorder.setText("NEXT");
            totalpricetext.setText(R.string.total);

            if (Constants.orderitems.size() > 0) {
                remove.setText("remove");
                categorywelcome.setText("Here is a look at your order");
            } else {
                categorywelcome.setText("You've not chosen anything from the menu. Feel free to begin by choosing one of the categories to the left");
            }

            btnconfirm.setText("Finish order");
            back.setText("back");
            langItem.setTitle("   French");

        }

    }

    private void changeLanguage(boolean update) {

        MenuItem langItem = menu.findItem(R.id.language);

        if (Constants.lang == 2) {

            yourordertext.setText("Votre commande");
            //previous.setText("Page précédente");
            //next.setText("Page suivante");
            addtorder.setText("Ajouter à la liste");

            totalpricetext.setText(R.string.total);

            if (Constants.orderitems.size() > 0) {
                remove.setText("retirer");
                categorywelcome.setText("Voici votre commande");
            } else {
                categorywelcome.setText("Vous n'avez rien choisi dans le menu. N'hésitez pas à commencer par choisir une des catégories à gauche");

            }

            btnconfirm.setText("Finir la commande");
            back.setText("arrière");
            langItem.setTitle("   English");

        } else {


            yourordertext.setText("Your order");
            //previous.setText("Previous page");
            //next.setText("Next page");
            addtorder.setText("NEXT");
            totalpricetext.setText(R.string.total);

            if (Constants.orderitems.size() > 0) {
                remove.setText("remove");
                categorywelcome.setText("Here is a look at your order");
            } else {
                categorywelcome.setText("You've not chosen anything from the menu. Feel free to begin by choosing one of the categories to the left");
            }

            btnconfirm.setText("Finish order");
            back.setText("back");
            langItem.setTitle("   French");

        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (blockingView != null) {
            WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
            manager.removeView(blockingView);
        }
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

    private class getOffer extends AsyncTask<Object, Object, Boolean> {


        private final String id;

        public getOffer(String id)
        {
            this.id = id;
            
        }
        
        @Override
        protected Boolean doInBackground(Object... params) {
            class Controller implements Callback<List<Related>> {
                @Override
                public void onResponse(Call<List<Related>> call, Response<List<Related>> response) {

                    if(response.isSuccessful())
                    count ++;
                    AllOffers.addAll(response.body());

                    //if(count == Constants.offersCount)
                    {
                        if(AllOffers.size() < 7)
                        {
                            pagination.setVisibility(View.INVISIBLE);
                        }

                        List<Items> items = new ArrayList<>();
                        List<Items> itemscopy = new ArrayList<>();

                        for (Related related: AllOffers
                                ) {

                            ArrayList<Price> price = new ArrayList<>();
                            price.add(0, new Price("", String.valueOf(related.price)));
                            items.add(new Items(related.name, price, related._id));

                        }

                        recyclerView.setAdapter(new ItemAdapter(ItemListActivity.this, mTwoPane, items));

                        for (int i = 0; i < items.size(); i++) {
                            Items item  = items.get(i);
                            item.subcategory_id = "offers";
                            itemscopy.add(item);
                        }

                        if(items.size() > 0)
                            Constants.allitems.put("offers", itemscopy);
                    }

                }

                @Override
                public void onFailure(Call<List<Related>> call, Throwable t) {

                }

                public void start() {
                    Gson gson = new GsonBuilder().setLenient().create();
                    GsonConverterFactory factory = GsonConverterFactory.create(gson);
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.baseUrl)
                            .addConverterFactory(factory).build();
                    MealOrderService mealOrderService = retrofit.create(MealOrderService.class);

                    Call<List<Related>> call = mealOrderService.listMyOffers(id);

                    call.enqueue(this);
                }
            }

            Controller controller = new Controller();
            controller.start();

            return null;
        }
    }

        private class getItems extends AsyncTask<Object, Object, Boolean> {

        private String subcatid;

        public getItems(String subcatid) {
            this.subcatid = subcatid;
        }

        private boolean mStatus;

        @Override
        protected Boolean doInBackground(Object... params) {

            class Controller implements Callback<List<Items>> {

                public void start() {
                    Gson gson = new GsonBuilder().setLenient().create();
                    GsonConverterFactory factory = GsonConverterFactory.create(gson);
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.baseUrl)
                            .addConverterFactory(factory).build();
                    MealOrderService mealOrderService = retrofit.create(MealOrderService.class);
                    Call<List<Items>> call = mealOrderService.listItems(subcatid);

                    call.enqueue(this);
                }


                @Override
                public void onResponse(Call<List<Items>> call, Response<List<Items>> response) {

                    if (response.isSuccessful()) {
                        mStatus = true;

                        items = response.body();
                        if(items.size() < 7)
                        {
                            pagination.setVisibility(View.INVISIBLE);
                        }
                        recyclerView.setAdapter(new ItemAdapter(ItemListActivity.this, mTwoPane, items));

                        if (items.size() > 0)
                            catname.setText(Constants.currentcat);
                        catname.setVisibility(View.VISIBLE);

                        Constants.allitems.put(subcatid, items);

                    }
                }

                @Override
                public void onFailure(Call<List<Items>> call, Throwable t) {

                    mStatus = false;
                }
            }

            Controller controller = new Controller();
            controller.start();

            return mStatus;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {


            if (!mStatus) {

               // Snackbar.make(recyclerView, "Unable to load details", Snackbar.LENGTH_SHORT).show();

            }


            super.onPostExecute(aBoolean);
        }
    }


    public class DisconnectTask extends AsyncTask<Object, Object, Boolean> {
        String username;
        String password;

        public DisconnectTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Object... params) {

            class AuthTokenController implements Callback<String>
            {
                public void start()
                {
                    Gson gson = new GsonBuilder().setLenient().create();
                    GsonConverterFactory factory = GsonConverterFactory.create(gson);
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.baseUrl)
                            .addConverterFactory(factory).build();

                    MealOrderService mealOrderService = retrofit.create(MealOrderService.class);

                    Call<String> call = mealOrderService.disconnect(new Auth(username, password, Constants.table_num))    ;

                    call.enqueue(this);
                }

                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    boolean responseStatus = response.isSuccessful();

                    if(responseStatus)
                    {
                        Constants.option = 0;
                        Constants.lang = 1;
                        Constants.token = "";

                        Intent intent =  new Intent(getApplicationContext(), ActivateActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    System.out.print("failure");

                    return;

                }
            }

            try {
                AuthTokenController controller = new AuthTokenController();
                controller.start();

            }
            catch (Exception e)
            {
                return false;
            }
            return null;
        }
    }
}
