package com.ezcorporate.VirtualCallSystem;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.Database.DataBaseHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by theappguruz on 07/05/16.
 */
public class PhoneStateReceiver extends BroadcastReceiver {

    Context context1;
    boolean isPhoneCalling = false;
    boolean ringing= false;
    boolean recived= false;
DataBaseHelper dataBaseHelper;
    @Override
    public void onReceive(final Context context,Intent intent) {
        try {
            dataBaseHelper=new DataBaseHelper(context);
            System.out.println("Receiver start");
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            final String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                ringing = true;
            }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))) {
                recived = true;
                telephony.listen(new PhoneStateListener() {
                    @Override
                    public void onCallStateChanged(int state, String incomingNumber) {
                        super.onCallStateChanged(state, incomingNumber);
                    }
                }, PhoneStateListener.LISTEN_CALL_STATE);
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                final String check=SharedPrefManager.getInstance(context).getVCCValue();
                if(check.equals("no")){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final Intent intent = new Intent(context, CallsDialog.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra("phone_no",incomingNumber);
                            Log.i("callend",""+incomingNumber);
                            context.startActivity(intent);
                        }
                    },700);
                }
                Log.i("callend",check);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Exception", "" + e);
        }
    }


}


