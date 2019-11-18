package rw.datasystems.mealorder.ServiceReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import rw.datasystems.mealorder.Activitiy.ActivateActivity;

/**
 * Created by vakinwande on 07/06/2017.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, ActivateActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }

}
