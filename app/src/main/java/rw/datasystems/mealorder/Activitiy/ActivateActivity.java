package rw.datasystems.mealorder.Activitiy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import rw.datasystems.mealorder.*;
import rw.datasystems.mealorder.ServiceReceiver.KeepActiveService;
import rw.datasystems.mealorder.ServiceReceiver.MealOrderService;
import rw.datasystems.mealorder.ServiceReceiver.ServiceTools;
import rw.datasystems.mealorder.UI.CustomFontButton;
import android.widget.EditText;
import android.widget.TextView;
import rw.datasystems.mealorder.UI.CustomFontText;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rw.datasystems.mealorder.UI.CustomViewGroup;
import rw.datasystems.mealorder.Util.Constants;
import rw.datasystems.mealorder.models.Auth;
import rw.datasystems.mealorder.models.Offers;

/**
 *  Activate activity that requires the generated token number in the backend
 *  and the table number.
 */

public class ActivateActivity extends AppCompatActivity {

    /**
     * Keep track of the task to ensure we can cancel it if requested.
     */

    private ActivateTask mActivateTask = null;

    // UI references.
    private EditText mTokenView;
    private EditText mTableNumberView;
    private View mProgressView;
    private View mActivateFormView;
    private SharedPreferences preferences;
    private View view;
    private Dialog dialog;
    private EditText mPassword, mUsername;
    private CustomFontButton mActivateButton;
    private EditText mIpView;
    private RecyclerView offerTV;
    private OfferTask offerTask;
    private CustomFontText offerGuide;


    @Override
    public void onBackPressed() {

        if(dialog!= null)
            dialog.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        else
            if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                Constants.volumedown = Constants.volumedown + 1;
                if (Constants.volumedown == 5)
                {
                    //TODO make this more backward compatible
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        stopService(new Intent(this, KeepActiveService.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finishAndRemoveTask();
                    }
                }

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

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            finish();
        }

        Constants c = new Constants();
        c.Init();
        Constants.lang = 1;
        Constants.volumedown = 0;

        if (!ServiceTools.isServiceRunning(ActivateActivity.this.getApplicationContext(), KeepActiveService.class)
                ) {
            startService(new Intent(this, KeepActiveService.class));
        }


        final View vv = getLayoutInflater().inflate(R.layout.activity_home, null);

        offerTV = (RecyclerView) vv.findViewById(R.id.offers);
        offerGuide = (CustomFontText) vv.findViewById(R.id.offersguide);
        if(Constants.offersCount > 0)
            offerGuide.setText("Click to see details");
        else
            offerGuide.setText("No special offers today");

        offerTV.setNestedScrollingEnabled(false);
        offerTask = new OfferTask();
        offerTask.execute((Void) null);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        SliderLayout mSlider = (SliderLayout) vv.findViewById(R.id.slider);

        TextSliderView textSliderView = new TextSliderView(this);
        // initialize a SliderLayout

            textSliderView
                    .description("")
                    .image(Constants.baseUrl + "/cover.home/1")
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .error(R.drawable.slider1)
                    .setOnSliderClickListener(null);

            mSlider.addSlider(textSliderView);
            textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description("")
                    .image(Constants.baseUrl + "/cover.home/2")
                    .error(R.drawable.slide3)
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(null);

            mSlider.addSlider(textSliderView);



        view = getLayoutInflater().inflate(R.layout.activity_activate, null);

        setupActionBar();

        mPassword = (EditText) view.findViewById(R.id.password);
        mUsername = (EditText) view.findViewById(R.id.username);
        mIpView = (EditText) view.findViewById(R.id.ip);
        mTableNumberView = (EditText) view.findViewById(R.id.tablenum);
        mActivateFormView = view.findViewById(R.id.activate_form);
        mProgressView = view.findViewById(R.id.activate_progress);

        preferences = getSharedPreferences("com.mealorder", Context.MODE_PRIVATE);

        mTableNumberView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView CustomFontText, int id, KeyEvent keyEvent) {

                if (id == R.id.activate || id == EditorInfo.IME_NULL) {
                    ActivateActivity.this.attemptActivate();
                    return true;
                }
                return false;
            }
        });

        CustomFontButton mOrderButton = (CustomFontButton) vv.findViewById(R.id.button);

        if(!Constants.token.equals("")&&Constants.getReturn){
            startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
            Constants.isCategories = true;
            finish();
        }
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Constants.token.equals("")) {

                    if(view.getParent() == null)
                    {
                        dialog = new Dialog(ActivateActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(true);
                        dialog.setContentView(view);
                        dialog.show();
                    }
                    else
                    {
                        mPassword = (EditText) view.findViewById(R.id.password);
                        mUsername = (EditText) view.findViewById(R.id.username);
                        mTableNumberView = (EditText) view.findViewById(R.id.tablenum);
                        mIpView = (EditText) view.findViewById(R.id.ip);
                        mActivateFormView = view.findViewById(R.id.activate_form);
                        mProgressView = view.findViewById(R.id.activate_progress);
                        mActivateButton = (CustomFontButton) view.findViewById(R.id.activate_button);
                        mActivateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view1) {

//                                if (mActivateTask != null) {
//                                    mActivateTask
//                                }
//
                                String token  = mPassword.getText().toString();
                                String table = mTableNumberView.getText().toString();
                                String username = mUsername.getText().toString();
                                String ip = mIpView.getText().toString();

                                Constants.baseUrl = "http://" + ip;

                                mActivateTask = new ActivateTask(username, token, table);
                                try {
                                    mActivateTask.execute((Void) null);
                                } catch (Exception e) {

                                    if(dialog!=null) dialog.dismiss();
                                }
                            }
                        });


                        dialog.show();
                    }

                } else {

                    startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                    Constants.isCategories = true;
                    finish();
                }
            }
        });

        
        mActivateButton = (CustomFontButton) view.findViewById(R.id.activate_button);

        mActivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                if (mActivateTask != null) {
                    return;
                }

                String token  = mPassword.getText().toString();
                String table = mTableNumberView.getText().toString();
                String username = mUsername.getText().toString();
                String ip = mIpView.getText().toString();
                Constants.baseUrl = "http://" + ip;

                mActivateTask = new ActivateTask(username, token, table);
                try {
                    mActivateTask.execute((Void) null);
                } catch (Exception e) {

                }
            }
        });

        setContentView(vv);

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

    public void showOffers(View view)
    {
        startActivity(new Intent(getApplicationContext(), ItemListActivity.class).putExtra("fromOffers", true));
    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up rw.datasystems.mealorder.UI.CustomFontButton in the action bar.
            getSupportActionBar().setHomeButtonEnabled(false);
        }
    }

    /**
     * Attempts to activate the device using the credentials as
     * specified in the form.
     * If there are form errors (missing fields, etc.), the
     * errors are presented and no actual request is made.
     */

    private void attemptActivate() {

        if (mActivateTask != null) {
            return;
        }

        String token  = mTokenView.getText().toString();
        String table = mTableNumberView.getText().toString();
        String username = mUsername.getText().toString();
        String ip = mIpView.getText().toString();
        Constants.baseUrl = "http://" + ip;

        mActivateTask = new ActivateTask(username, token, table);
        try {
            mActivateTask.execute((Void) null);
        } catch (Exception e) {

        }

    }


    private boolean isTokenValid(String token) {

        String prefToken = preferences.getString("token", null);
        return prefToken != null && prefToken.equals(token);
    }

    /**
     * Shows the progress UI and hides the activate form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mActivateFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mActivateFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mActivateFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mActivateFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous task used to authenticate the device
     */
    public class ActivateTask extends AsyncTask<Void, Void, Boolean> {

        private final String mToken;
        private final String username;
        private final String mTableNumber;
        private boolean responseStatus;

        ActivateTask(String username, String password, String tableNumber) {

            mToken = password;
            this.username = username;
            mTableNumber = tableNumber;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            // TODO: attempt authentication against a network service.

            class AuthTokenController implements Callback<String>
            {
                public void start()
                {
                    Gson gson = new GsonBuilder().setLenient().create();
                    GsonConverterFactory factory = GsonConverterFactory.create(gson);
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.baseUrl)
                            .addConverterFactory(factory).build();

                    MealOrderService mealOrderService = retrofit.create(MealOrderService.class);

                    Call<String> call = mealOrderService.authenticate(new Auth(username, mToken, mTableNumber))    ;

                    call.enqueue(this);
                }

                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    responseStatus = response.isSuccessful();

                    if(responseStatus)
                    {
                        Constants.token = response.body();
                        Constants.table_num = mTableNumber;
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    System.out.print("failure");
                    responseStatus = false;
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

            return responseStatus;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if(dialog != null)dialog.dismiss();

        }

        @Override
        protected void onCancelled() {
            mActivateTask = null;
            showProgress(false);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final CustomFontText name;

        public ViewHolder(View mView) {
            super(mView);
            this.mView = mView;
            name = (CustomFontText) mView.findViewById(R.id.offeritem);
        }
    }

    public class OfferAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Offers> offers;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_offer, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            holder.name.setText(offers.get(position).name);
            holder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), ItemListActivity.class).putExtra("fromOffers", position));

                }
            });
        }

        @Override
        public int getItemCount() {
            return offers.size();
        }

        public OfferAdapter(List<Offers> offers) {
            this.offers = new ArrayList<>();
            this.offers = offers;
        }


    }
    
    public class OfferTask extends AsyncTask<Void, Void, Boolean> {

        private boolean responseStatus;

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            class GetOffersController implements Callback<List<Offers>>
            {
                public void start()
                {
                    Gson gson = new GsonBuilder().setLenient().create();
                    GsonConverterFactory factory = GsonConverterFactory.create(gson);
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.baseUrl)
                            .addConverterFactory(factory).build();
                    MealOrderService mealOrderService = retrofit.create(MealOrderService.class);

                    Call<List<Offers>> call = mealOrderService.listOffers();

                    call.enqueue(this);
                }

                @Override
                public void onResponse(Call<List<Offers>> call, Response<List<Offers>> response) {

                    responseStatus = response.isSuccessful();

                    if (responseStatus) {

                        List<Offers> offers = response.body();

                        StringBuilder sb = new StringBuilder();

                        for (int i =0; i< offers.size(); i ++)
                        {
                            sb.append(offers.get(i));
                            sb.append("<br/>");
                        }

                        Constants.offers = offers;
                        Constants.offersCount = offers.size();
                        offerTV.setAdapter(new OfferAdapter(offers));
                        if(Constants.offersCount > 0 )
                        offerGuide.setText("Click to see details");

                    }
                }

                @Override
                public void onFailure(Call<List<Offers>> call, Throwable t) {

                }
            }

            try {

                GetOffersController controller2 = new GetOffersController();
                controller2.start();
            }
            catch (Exception e)
            {
                return false;
            }

            return responseStatus;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if(dialog != null)dialog.dismiss();

        }

        @Override
        protected void onCancelled() {
            offerTask = null;
            showProgress(false);
        }
    }

}

