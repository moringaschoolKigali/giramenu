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


import android.view.*;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rw.datasystems.mealorder.Fragment.ItemDetailFragment;
import rw.datasystems.mealorder.R;
import rw.datasystems.mealorder.ServiceReceiver.MealOrderService;
import rw.datasystems.mealorder.UI.CustomFontButton;
import rw.datasystems.mealorder.UI.CustomFontText;
import rw.datasystems.mealorder.UI.CustomViewGroup;
import rw.datasystems.mealorder.Util.Constants;
import rw.datasystems.mealorder.adapters.NewComponentAdapter;
import rw.datasystems.mealorder.adapters.OrderItemAdapter;
import rw.datasystems.mealorder.adapters.SideDishAdapter;
import rw.datasystems.mealorder.models.Auth;
import rw.datasystems.mealorder.models.Component;
import rw.datasystems.mealorder.models.OrderItem;
import rw.datasystems.mealorder.models.Price;
import rw.datasystems.mealorder.models.Sidedish;

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
public class SideDishActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerView recyclerView;
    private static List<Price> prices;
    private CustomFontText catname;
    static CustomFontText pagination;
    private CustomFontText categorywelcome = CategoryActivity.categorywelcome;
    private CustomFontText totalpricetext = CategoryActivity.totalprice;
//    private CustomFontText categorywelcome = CategoryActivity.categorywelcome;
//    private CustomFontText totalpricetext;
    private CustomFontText yourordertext, remove;
    private CustomFontButton btnconfirm;
    private Menu menu;
    public static CustomFontButton addtorder;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.language:
                changeLanguage();
                break;
//            case R.id.home2:
//                Intent intent = new Intent(getApplicationContext(), ActivateActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                break;
            case R.id.disc:
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

                            disconnectTask = new SideDishActivity.DisconnectTask(finalMUsername1.getText().toString(), finalMPassword1.getText().toString());
                            disconnectTask.execute((Void)null);

                        }
                    }
                });

                if (view.getParent() == null) {
                    dialog = new Dialog(SideDishActivity.this);
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

                                disconnectTask = new SideDishActivity.DisconnectTask(finalMUsername.getText().toString(), finalMPassword.getText().toString());
                                disconnectTask.execute((Void)null);

                            }
                        }
                    });

                    dialog.show();
                }
        }



         return true;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        hidePagination();

        pagination = (CustomFontText) findViewById(R.id.pagination);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        catname = (CustomFontText) findViewById(R.id.categoryname);
        addtorder = (CustomFontButton) findViewById(R.id.addtoorder);
        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        categorywelcome = (CustomFontText) findViewById(R.id.categorywelcome);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Please give my app this permission!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            } else {
                disableStatusBar();
            }
        }
        else {
            disableStatusBar();
        }

        if (Constants.orderitems.size() > 0)
            categorywelcome.setText("Here's a look at your order");
        else
            categorywelcome.setText("You've not chosen anything from the menu. Feel free to begin by choosing one of the categories to the left");

        CustomFontText totalprice = (CustomFontText) findViewById(R.id.totalprice);
        btnconfirm = (CustomFontButton) findViewById(R.id.confirm);

        Float sum = Float.valueOf(0);
        for (int i = 0; i < Constants.orderitems.size(); i++) {

            sum += Constants.orderitems.get(i).current_price * Constants.orderitems.get(i).qnty;
        }

        totalprice.setText((String.format("%,.0f", sum) + " RWF"));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.plate_item_list);
        assert recyclerView != null;

        if(Constants.orderitems.size() > 0)
            recyclerView.setAdapter(new OrderItemAdapter(SideDishActivity.this, false, Constants.orderitems));


        yourordertext = (CustomFontText) findViewById(R.id.yourordertext);
        remove = (CustomFontText) findViewById(R.id.remove);
        totalpricetext = (CustomFontText) findViewById(R.id.totalpricetext);

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

        }

        addtorder.setEnabled(true);
        addtorder.setOnClickListener(finish);

        catname.setText("Side Dish?");
        //addtorder.setText("No, Add to Order");
        addtorder.setText("NEXT");

        catname.setVisibility(View.VISIBLE);

        final ActionBar actionBar = getSupportActionBar();

        CustomFontButton back = (CustomFontButton) findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.controlPicker = false;
                onBackPressed();

            }
        });

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        if (findViewById(R.id.item_detail_container) != null) {
            mTwoPane = true;
        }

        btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constants.orderitems == null || Constants.orderitems.size() == 0 ) {

                    Snackbar.make(getWindow().getDecorView(), "Nothing to confirm! please add an item", Snackbar.LENGTH_LONG).show();

                    return;
                }
                else
                {
                    Snackbar.make(getWindow().getDecorView(), "Please click add to order or go back to change the order", Snackbar.LENGTH_LONG).show();
                    return;
                }

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.item_list);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        assert recyclerView != null;

        recyclerView.setAdapter(new SideDishAdapter(getApplicationContext(), Constants.currentItem.side_dishes));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Please give my app this permission!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            } else {
                disableStatusBar();
            }
        }
        else {
            disableStatusBar();
        }


    }

    View.OnClickListener finish = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Constants.orderitems.add(Constants.pendingOrderItem);

            OrderItem oitem2 = Constants.orderitems.get(Constants.orderitems.size() - 1);
            List<Sidedish> sidedishes = new ArrayList<>(Constants.addedsidedishes.values());
            oitem2.side_dishes = sidedishes;

            for (Sidedish sidedish : sidedishes
                    ) {

                String temp = oitem2.item.name;
                oitem2.item.name =  temp + " : " + sidedish.name + " (" + sidedish.quantity.toString() +  ") ";
                oitem2.item_name = temp + " : " + sidedish.name  + " (" + sidedish.quantity.toString() +  ") ";
            }

            Constants.orderitems.remove(Constants.orderitems.size() - 1);
            Constants.orderitems.add(oitem2);

            if(oitem2.item.related != null && oitem2.item.related.size() > 0) {

                Intent intent = new Intent(getApplicationContext(), AnythingElseListActivity.class);
                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, oitem2.item._id);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                finish();
            }
            else
            {
                startActivity(new Intent(getApplicationContext(), ContinueActivity.class).putExtra("OrderAdded", true));
                Constants.isCategories = true;
            }

            //finish();

        }
    };


    private void changeLanguage() {

        MenuItem langItem = menu.findItem(R.id.language);

        if (Constants.lang == 1) {

            Constants.lang = 2;
            catname.setText("Accompagnement ?");
            yourordertext.setText("Votre commande");

            totalpricetext.setText(R.string.total);

            if (Constants.orderitems.size() > 0) {
                remove.setText("retirer");
                categorywelcome.setText("Voici votre commande");
            } else {
                categorywelcome.setText("Vous n'avez rien choisi dans le menu. N'hésitez pas à commencer par choisir une des catégories à gauche");

            }

            btnconfirm.setText("Finir la commande");
            CategoryActivity.back.setText("arrière");
            langItem.setTitle("   English");

        } else {


            Constants.lang = 1;
            catname.setText("Side dish?");
            yourordertext.setText("Your order");

            //addtorder.setText("Add to order");
            addtorder.setText("NEXT");
            totalpricetext.setText(R.string.total);

            if (Constants.orderitems.size() > 0) {
                remove.setText("remove");
                categorywelcome.setText("Here is a look at your order");
            } else {
                categorywelcome.setText("You've not chosen anything from the menu. Feel free to begin by choosing one of the categories to the left");
            }

            btnconfirm.setText("Finish order");
            CategoryActivity.back.setText("back");
            langItem.setTitle("   French");

        }

    }

    private void changeLanguage(boolean update) {

        MenuItem langItem = menu.findItem(R.id.language);

        if (Constants.lang == 2) {

            catname.setText("Accompagnement ?");
            yourordertext.setText("Votre commande");

            totalpricetext.setText(R.string.total);

            if (Constants.orderitems.size() > 0) {
                remove.setText("retirer");
                categorywelcome.setText("Voici votre commande");
            } else {
                categorywelcome.setText("Vous n'avez rien choisi dans le menu. N'hésitez pas à commencer par choisir une des catégories à gauche");

            }

            btnconfirm.setText("Finir la commande");
            CategoryActivity.back.setText("arrière");
            langItem.setTitle("   English");

        } else {


            catname.setText("Side dish?");
            yourordertext.setText("Your order");

            addtorder.setText("NEXT");
            totalpricetext.setText(R.string.total);

            if (Constants.orderitems.size() > 0) {
                remove.setText("remove");
                categorywelcome.setText("Here is a look at your order");
            } else {
                categorywelcome.setText("You've not chosen anything from the menu. Feel free to begin by choosing one of the categories to the left");
            }

            btnconfirm.setText("Finish order");
            CategoryActivity.back.setText("back");
            langItem.setTitle("   French");

        }

    }
    
    private void hidePagination() {

        //findViewById(R.id.previous).setVisibility(View.INVISIBLE);
        findViewById(R.id.pagination).setVisibility(View.INVISIBLE);
        //findViewById(R.id.next).setVisibility(View.INVISIBLE);
    }

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
    protected void onDestroy() {

        super.onDestroy();

        if (blockingView!=null) {
            WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
            manager.removeView(blockingView);
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
