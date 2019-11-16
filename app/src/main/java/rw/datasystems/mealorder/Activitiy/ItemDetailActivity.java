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
import rw.datasystems.mealorder.ServiceReceiver.MealOrderService;
import rw.datasystems.mealorder.Util.Constants;

import rw.datasystems.mealorder.R;
import rw.datasystems.mealorder.UI.CustomFontButton;
import rw.datasystems.mealorder.UI.CustomFontText;
import rw.datasystems.mealorder.UI.CustomViewGroup;
import rw.datasystems.mealorder.adapters.OrderItemAdapter;
import rw.datasystems.mealorder.models.Auth;


/**
 * An activity representing a single Item detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {

    ItemDetailFragment fragment;
    private Menu menu;
    private CustomFontText totalpricetext, remove;
    private CustomFontText yourordertext;

    public static CustomFontButton back;

    private CustomFontText categorywelcome;
    private CustomFontButton btnconfirm;
    private CustomFontText totalprice;
    DisconnectTask disconnectTask;
    private View view;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        toolbar.setTitle(getTitle());

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);

        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            int item = getIntent().getIntExtra(ItemDetailFragment.ARG_ITEM_ID, 0);
            String cat = getIntent().getStringExtra(ItemDetailFragment.ARG_CAT_ID);

            Bundle arguments = new Bundle();
            arguments.putInt(ItemDetailFragment.ARG_ITEM_ID, item);

            arguments.putString(ItemDetailFragment.ARG_CAT_ID, cat);
            fragment = new ItemDetailFragment();

            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

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

        yourordertext = (CustomFontText) findViewById(R.id.yourordertext);
        totalpricetext = (CustomFontText) findViewById(R.id.totalpricetext);
        totalprice = (CustomFontText) findViewById(R.id.totalprice);
        remove = (CustomFontText) findViewById(R.id.remove);
        btnconfirm = (CustomFontButton) findViewById(R.id.confirm);

        categorywelcome = (CustomFontText) findViewById(R.id.categorywelcome);

        if(Constants.lang  == 2) {

            yourordertext.setText("Votre commande");
            totalpricetext.setText(R.string.total);

            if(Constants.orderitems.size() > 0) {
                remove.setText("retirer");
                categorywelcome.setText("Voici votre commande");
            }
            else
            {
                categorywelcome.setText("Vous n'avez rien choisi dans le menu. N'hésitez pas à commencer par choisir une des catégories à gauche");

            }

            btnconfirm.setText("Finir la commande");
        }
        else
        {
            yourordertext.setText("Your order");
            totalpricetext.setText(R.string.total);

            if(Constants.orderitems == null)
                return;

            if(Constants.orderitems.size() > 0) {
                remove.setText("remove");
                categorywelcome.setText("Here is a look at your order");
            }
            else {
                categorywelcome.setText("You've not chosen anything from the menu. Feel free to begin by choosing one of the categories to the left");
            }

            btnconfirm.setText("Finish order");
        }

        Float sum = Float.valueOf(0);

        for (int i = 0; i < Constants.orderitems.size(); i++) {

            sum += Constants.orderitems.get(i).current_price * Constants.orderitems.get(i).qnty ;

        }

        btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Constants.orderitems.size() == 0 || Constants.orderitems == null) {

                    Snackbar.make(findViewById(R.id.root), "Nothing to confirm! please add an item", Snackbar.LENGTH_SHORT).show();

                    return;
                }


            }
        });

        totalprice.setText((String.format("%,.0f", sum) + " RWF"));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.plate_item_list);
        assert recyclerView != null;

        recyclerView.setAdapter(new OrderItemAdapter(getApplicationContext(), false, Constants.orderitems));

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
            if(fragment.back != null)
            fragment.back.setText("arrière");

        } else {

            yourordertext.setText("Your order");
            totalpricetext.setText(R.string.total);

            if (Constants.orderitems.size() > 0) {
                remove.setText("remove");
                categorywelcome.setText("Here is a look at your order");
            } else {
                categorywelcome.setText("You've not chosen anything from the menu. Feel free to begin by choosing one of the categories to the left");
            }
            if(btnconfirm != null)
            btnconfirm.setText("Finish order");
        }


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

                            disconnectTask = new ItemDetailActivity.DisconnectTask(finalMUsername1.getText().toString(), finalMPassword1.getText().toString());
                            disconnectTask.execute((Void)null);

                        }
                    }
                });

                if (view.getParent() == null) {
                    dialog = new Dialog(ItemDetailActivity.this);
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

                                disconnectTask = new ItemDetailActivity.DisconnectTask(finalMUsername.getText().toString(), finalMPassword.getText().toString());
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
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.category, menu);
        this.menu = menu;
        changeLanguage(true);
        return true;
    }

    private void changeLanguage() {

        MenuItem langItem = menu.findItem(R.id.language);

        if (Constants.lang == 1) {

            Constants.lang = 2;

            yourordertext.setText("Votre commande");

            totalpricetext.setText(R.string.total);

            if (Constants.orderitems.size() > 0) {
                remove.setText("retirer");
                categorywelcome.setText("Voici votre commande");
            } else {
                categorywelcome.setText("Vous n'avez rien choisi dans le menu. N'hésitez pas à commencer par choisir une des catégories à gauche");

            }

            btnconfirm.setText("Finir la commande");
            if(fragment.back != null)
                fragment.back.setText("arrière");
            langItem.setTitle("   English");

        } else {

            Constants.lang = 1;

            yourordertext.setText("Your order");
            totalpricetext.setText(R.string.total);

            if (Constants.orderitems.size() > 0) {
                remove.setText("remove");
                categorywelcome.setText("Here is a look at your order");
            } else {
                categorywelcome.setText("You've not chosen anything from the menu. Feel free to begin by choosing one of the categories to the left");
            }

            btnconfirm.setText("Finish order");
            if(fragment.back != null)
            fragment.back.setText("back");
            langItem.setTitle("   French");

        }

    }



    private void changeLanguage(boolean update) {

        MenuItem langItem = menu.findItem(R.id.language);

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
            if(fragment.back != null)
            fragment.back.setText("arrière");
            langItem.setTitle("   English");

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

            if(fragment.back != null)
                fragment.back.setText("back");

            langItem.setTitle("   French");

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
