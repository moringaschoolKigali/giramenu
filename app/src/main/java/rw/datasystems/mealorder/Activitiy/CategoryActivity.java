package rw.datasystems.mealorder.Activitiy;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import rw.datasystems.mealorder.Util.Constants;
import rw.datasystems.mealorder.Fragment.ItemDetailFragment;
import rw.datasystems.mealorder.ServiceReceiver.MealOrderService;
import rw.datasystems.mealorder.R;
import rw.datasystems.mealorder.UI.CustomFontButton;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rw.datasystems.mealorder.UI.CustomFontText;
import rw.datasystems.mealorder.UI.CustomViewGroup;
import rw.datasystems.mealorder.adapters.CategoryAdapter;
import rw.datasystems.mealorder.adapters.OrderItemAdapter;
import rw.datasystems.mealorder.models.Auth;
import rw.datasystems.mealorder.models.Category;
import rw.datasystems.mealorder.models.OrderItem;

import static rw.datasystems.mealorder.R.id.home2;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    public static CustomFontText totalprice;
    public static CustomFontText categorywelcome;
    public static CustomFontButton back;
    public static View v;
    public static CustomFontText pagination;
    private View view;
    private Dialog dialog;
   // private DisconnectTask disconnectTask;
    private CustomFontText totalpricetext, toordertext, remove;
    private CustomFontText yourordertext;
    private CustomFontButton btnconfirm;
    private Menu menu;
    private CustomFontButton previous;
    private CustomFontButton addtorder, next ;
    CategoryActivity.DisconnectTask disconnectTask;
    private static int length = 2;

    public class Confirm extends Dialog {
        public Confirm(Context context, boolean t) {
            super(context, t, new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                    finish();
                    startActivity(new Intent(getApplicationContext(), ActivateActivity.class));


                    Constants.option = 1;


                }
            });
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            setContentView(R.layout.dialog_confirm);

            setContentView(R.layout.dialog_confirm);

            CustomFontText code = (CustomFontText) findViewById(R.id.code);
            code.setText(Constants.orderId);

            WindowManager.LayoutParams params = getWindow().getAttributes();

            params.height = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            params.width = WindowManager.LayoutParams.FLAG_FULLSCREEN;

            getWindow().setAttributes(params);

        }
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
        getCategories loadCategory = new getCategories();
        loadCategory.execute((Void) null);

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


//        if (Constants.option == 0) {
//            setContentView(R.layout.dialog_option);
//
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//            View decorView = getWindow().getDecorView();
//
//            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
////            decorView.setSystemUiVisibility(uiOptions);
//
//
//            final CustomFontButton eatin = (CustomFontButton) findViewById(R.id.eatin);
//            eatin.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Constants.option = 1;
//                    CategoryActivity.this.recreate();
//                }
//            });
//
//            final CustomFontButton takeaway = (CustomFontButton) findViewById(R.id.takeaway);
//            takeaway.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Constants.option = 2;
//                    CategoryActivity.this.recreate();
//
//                }
//            });
//
//            CustomFontButton french = (CustomFontButton) findViewById(R.id.french);
//            CustomFontButton eng = (CustomFontButton) findViewById(R.id.english);
//            final CustomFontText option = (CustomFontText) findViewById(R.id.option);
//
//            option.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Constants.clickct  += 1;
//                    if(Constants.clickct == 20)
//                    {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(CategoryActivity.this, R.style.myDialog));
//                        builder.setTitle("Bragging rights!");
//                        builder.setMessage("This app was built by Emmanuel KALISA with love in Kigali :)");
//                        builder.create().show();
//                    }
//                }
//            });
//
//            french.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (Constants.lang == 1) {
//                        //change to french
//                        eatin.setText("SUR PLACE");
//                        option.setText("SUR PLACE");
//                        takeaway.setText("À EMPORTER");
//                        Constants.lang = 2;
//                        ((CustomFontText)findViewById(R.id.poweredby)).setText("Offert par");
//                    }
//                }
//            });
//
//            eng.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (Constants.lang == 2) {
//                        eatin.setText("DINE IN");
//                        option.setText("DINE IN?");
//                        takeaway.setText("TAKE AWAY");
//
//                        ((CustomFontText)findViewById(R.id.poweredby)).setText("Powered by");
//
//                        Constants.lang = 1;
//
//                    }
//                }
//            });
//
//
//        }
//        else {

            setContentView(R.layout.activity_category);
            v = findViewById(R.id.root);

            hidePagination();

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            View decorView = getWindow().getDecorView();

            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

            final boolean confirm = getIntent().getBooleanExtra(ItemDetailFragment.ARG_CONFIRM, false);

            boolean orderadded = getIntent().getBooleanExtra("OrderAdded", false);

            if (orderadded) {
                Snackbar.make(findViewById(android.R.id.content), "Order Added", Snackbar.LENGTH_SHORT).show();
            }

            if (confirm) {

                startActivity(new Intent(getApplicationContext(), ConfirmActivity.class));
                finish();

            }

            Constants c = new Constants();
            if (Constants.orderitems == null || Constants.orderitems.size() == 0)

                c.Init();

            mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a grid layout manager
            int sz = 3;
            boolean tabletSize = getResources().getBoolean(R.bool.isTablet);

            if (tabletSize) {
                // do something
            } else {
                sz = 2;
            }

            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, sz);
            mRecyclerView.setLayoutManager(gridLayoutManager);

            if (Constants.categories.size() > 0) {
                CategoryAdapter mAdapter = new CategoryAdapter(CategoryActivity.this, Constants.categories);
//                if (Constants.categories.size() == 2){
//                  mRecyclerView.setAdapter(mAdapter);
//                  mRecyclerView.setPadding(50,400,200,10);
//                  }else{
//                    return 0;
////                       mRecyclerView.setAdapter(mAdapter);
//                  }
                mRecyclerView.setAdapter(mAdapter);


                //setTheAdapter(mAdapter);
//                ItemTouchHelper.Callback callback =
//                        new SimpleItemTouchHelperCallback(mAdapter);
//                ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//                touchHelper.attachToRecyclerView(mRecyclerView);
            }
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

//            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//            drawer.setDrawerListener(toggle);
//            toggle.syncState();

//            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//            navigationView.setNavigationItemSelectedListener(this);

            categorywelcome = (CustomFontText) findViewById(R.id.categorywelcome);

            if (Constants.orderitems.size() > 0)
                categorywelcome.setText("Here's a look at your order");
            else
                categorywelcome.setText("You've not chosen anything from the menu. Feel free to begin by choosing one of the categories to the left");


            totalprice = (CustomFontText) findViewById(R.id.totalprice);
            btnconfirm = (CustomFontButton) findViewById(R.id.confirm);

            Float sum = Float.valueOf(0);
            for (int i = 0; i < Constants.orderitems.size(); i++) {

                sum += Constants.orderitems.get(i).current_price * Constants.orderitems.get(i).qnty;
            }

            btnconfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (Constants.orderitems == null || Constants.orderitems.size() == 0) {

                        Snackbar.make(mRecyclerView, "Nothing to confirm! please add an item", Snackbar.LENGTH_SHORT).show();

                        return;
                    }

                    confirmTask confirm1 = new confirmTask(
                            Constants.orderitems);
                    confirm1.execute((Void) null);

                }
            });

            totalprice.setText((String.format("%,.0f", sum) + " RWF"));

            final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.plate_item_list);
            assert recyclerView != null;

            if (Constants.orderitems.size() > 0)
                recyclerView.setAdapter(new OrderItemAdapter(CategoryActivity.this, false, Constants.orderitems));


            toordertext = (CustomFontText) findViewById(R.id.toordertext);
            yourordertext = (CustomFontText) findViewById(R.id.yourordertext);
            remove = (CustomFontText) findViewById(R.id.remove);
            totalpricetext = (CustomFontText) findViewById(R.id.totalpricetext);


            back = (CustomFontButton) findViewById(R.id.back);
            back.setVisibility(Constants.isCategories ? View.INVISIBLE : View.VISIBLE);

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    back.setVisibility(View.INVISIBLE);
                    CategoryAdapter mAdapter = new CategoryAdapter(CategoryActivity.this, Constants.categories);
                    mRecyclerView.setAdapter(mAdapter);
                    setTheAdapter(mAdapter);

                    Constants.isCategories = true;

                }
            });


            //previous = (CustomFontButton) findViewById(R.id.previous);
            //next = (CustomFontButton) findViewById(R.id.next);
            addtorder = (CustomFontButton) findViewById(R.id.addtoorder);
            pagination = (CustomFontText) findViewById(R.id.pagination);

//            previous.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void nClick(View v) {
//                    if (Constants.currentpage > 0) {
//                        Constants.currentpage = Constants.currentpage - 1;
//
//                        if (Constants.isCategories) {
//                            CategoryAdapter mAdapter = new CategoryAdapter(CategoryActivity.this, Constants.categories);
//                            mRecyclerView.setAdapter(mAdapter);
//                            setTheAdapter(mAdapter);
//
//                            back.setVisibility(View.INVISIBLE);
//                        } else {
//                            CategoryAdapter mAdapter = new CategoryAdapter(CategoryActivity.this, Constants.subcategories);
//                            mRecyclerView.setAdapter(mAdapter);
//                            setTheAdapter(mAdapter);
//
//                            back.setVisibility(View.VISIBLE);
//                        }
//
//                    }
//
//                }
//            });

//            //next.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    if (Constants.isCategories) {
//                        if (Constants.categories.size() > (Constants.currentpage + 1) * 6) {
//                            Constants.currentpage += 1;
//                            CategoryAdapter mAdapter = new CategoryAdapter(CategoryActivity.this, Constants.categories);
//                            mRecyclerView.setAdapter(mAdapter);
//                            back.setVisibility(View.INVISIBLE);
//                        }
//                    } else {
//                        if (Constants.subcategories.size() > (Constants.currentpage + 1) * 6) {
//                            CategoryAdapter mAdapter = new CategoryAdapter(CategoryActivity.this, Constants.subcategories);
//                            mRecyclerView.setAdapter(mAdapter);
//                            back.setVisibility(View.VISIBLE);
//                        }
//                    }
//                }
//            });

            addtorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(mRecyclerView, "please complete an order first", Snackbar.LENGTH_LONG).show();
                }
            });

//        }

    }

    private void setTheAdapter(CategoryAdapter adapter)
    {
//        ItemTouchHelper.Callback callback =
//                new SimpleItemTouchHelperCallback(adapter);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(mRecyclerView);
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

        if (blockingView != null) {
            WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
            manager.removeView(blockingView);
        }
    }


    @Override
    public void onBackPressed() {

        try {

//            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//
//            if (drawer.isDrawerOpen(GravityCompat.START)) {
//                drawer.closeDrawer(GravityCompat.START);
//            }
        } catch (Exception e) {

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.category, menu);
        this.menu = menu;
        changeLanguage(true);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.language)
        {
            changeLanguage();
        }
        else if (item.getItemId() == home2){
            if (Constants.orderitems.size() > 0){
//                home2.setVisibility(View.GONE);
                findViewById(R.id.home2).setVisibility(View.INVISIBLE);
            }else {
                Intent intent = new Intent(getApplicationContext(), ActivateActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                Constants.getReturn = false;
            }
        }
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

                        disconnectTask = new DisconnectTask(finalMUsername1.getText().toString(), finalMPassword1.getText().toString());
                        disconnectTask.execute((Void)null);

                    }
                }
            });

            if (view.getParent() == null) {
                dialog = new Dialog(CategoryActivity.this);
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

                            disconnectTask = new DisconnectTask(finalMUsername.getText().toString(), finalMPassword.getText().toString());
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
            toordertext.setText("Quelque chose à commander?");
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
            toordertext.setText("Something to order?");
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

            toordertext.setText("Quelque chose à commander?");
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

            toordertext.setText("Something to order?");
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

//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.nav_home) {
//
//
//            CategoryAdapter mAdapter = new CategoryAdapter(CategoryActivity.this, Constants.categories);
//            mRecyclerView.setAdapter(mAdapter);
//            setTheAdapter(mAdapter);
//
//        }
//
//        /*else if (id == R.id.nav_exit) {
//
//            Constants.option = 0;
//
//            Intent intent =  new Intent(getApplicationContext(), ActivateActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
//
//
//        }*/ else if (id == R.id.nav_disconnect) {
//            view = getLayoutInflater().inflate(R.layout.activity_disconnect, null);
//            EditText mPassword = (EditText) view.findViewById(R.id.password);
//            EditText mUsername = (EditText) view.findViewById(R.id.username);
//            CustomFontButton mActivateButton = (CustomFontButton) view.findViewById(R.id.button);
//
//            final EditText finalMUsername1 = mUsername;
//            final EditText finalMPassword1 = mPassword;
//            mActivateButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    if (disconnectTask == null) {
//
//                        disconnectTask = new DisconnectTask(finalMUsername1.getText().toString(), finalMPassword1.getText().toString());
//                        disconnectTask.execute((Void)null);
//
//                    }
//                }
//            });
//
//            if (view.getParent() == null) {
//                dialog = new Dialog(CategoryActivity.this);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.setCancelable(true);
//                dialog.setContentView(view);
//                dialog.show();
//
//            } else {
//
//                mPassword = (EditText) view.findViewById(R.id.password);
//                mUsername = (EditText) view.findViewById(R.id.username);
//                mActivateButton = (CustomFontButton) view.findViewById(R.id.activate_button);
//
//                final EditText finalMPassword = mPassword;
//                final EditText finalMUsername = mUsername;
//                mActivateButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if (disconnectTask == null) {
//
//                            disconnectTask = new DisconnectTask(finalMUsername.getText().toString(), finalMPassword.getText().toString());
//                            disconnectTask.execute((Void)null);
//
//                        }
//                    }
//                });
//
//                dialog.show();
//            }
//
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

    private class getCategories extends AsyncTask<Object, Object, Boolean> {

        private boolean mStatus;
        public int size;

        @Override
        protected Boolean doInBackground(Object... params) {

            class Controller implements Callback<List<Category>> {

                public void start() {

                    Gson gson = new GsonBuilder().setLenient().create();
                    GsonConverterFactory factory = GsonConverterFactory.create(gson);
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.baseUrl)
                            .addConverterFactory(factory).build();
                    MealOrderService mealOrderService = retrofit.create(MealOrderService.class);

                    Call<List<Category>> call = mealOrderService.listCategories();
                    call.enqueue(this);
                }


                @Override
                public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {

                    if (response.isSuccessful()) {
                        mStatus = true;
                        List<Category> categories = response.body();
                        Constants.categories = categories;

                        int len = categories.size();

//                        if (len==2){
//                            mRecyclerView.setPadding(110,200,0,0);
//                        }
//                        else{
//                            return;
//                        }
//                        if (Constants.categories.size() == 2){
//                            mRecyclerView.setPadding(50,400,200,10);
//                        mRecyclerView.setLayoutManager(new GridLayoutManager(this, mRecyclerView.center));
//                        }else{
//                       mRecyclerView.setAdapter(mAdapter);
//                        }

                        if (pagination != null) {
                            int j = (int) Math.ceil(len / 6.0);

                            //pagination.setText("1/" + String.valueOf(j));
                            pagination.setText("");
                        }
                        if (mRecyclerView != null) {
                            CategoryAdapter mAdapter = new CategoryAdapter(CategoryActivity.this, Constants.categories);
                            mRecyclerView.setAdapter(mAdapter);
                            setTheAdapter(mAdapter);

                        }
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

            if (mStatus) {

            }

            super.onPostExecute(aBoolean);
        }
    }

    public class DisconnectTask extends AsyncTask<Object, Object, Boolean>
    {
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
//                                if(progressDialog != null)
//                                    progressDialog.dismiss();
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
}
