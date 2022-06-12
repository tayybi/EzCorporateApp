package com.ezcorporate.VirtualCallSystem;

/**
 * Created by jeet on 24/12/16.
 */
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.CRM.Inquery;
import com.ezcorporate.Database.AllFields;
import com.ezcorporate.Database.DataBaseHelper;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.R;
import com.ezcorporate.VirtualCallSystem.RecyclerView.ModelClassOfCallLogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CallsDialog extends Activity
{
    TextView tvExistingInq,tvNewInquiry,tvGenralCall,tvNamePhone,tvExistingCount,tvLater;
    public static String phone_no="";
    public String dbId="";
    EditText etComment;
    String URL_PREFIX_DOMAIN="";
    String USERID="";
    DataBaseHelper dataBaseHelper;
    JSONArray array;
    JSONObject obj;
    Dialog progressDialog;
    String clickedMode="";
    public static String ATTEND="attend";
    public static String UNATTEND="unattend";
    Context context;
    String dateString,callDuration,callType;
    ImageView ivDone;

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.setFinishOnTouchOutside(false);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.custom_dialog_call_states);
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            context=CallsDialog.this;
            URL_PREFIX_DOMAIN=SharedPrefManager.getInstance(getApplicationContext()).getUrl();
            USERID=SharedPrefManager.getInstance(getApplicationContext()).getUId();
            phone_no=getIntent().getExtras().getString("phone_no");
            dbId=getIntent().getExtras().getString("db_id");
            initializeContent();
            setListener();
            if(callType(phone_no).equals("Missed")){
                dataBaseHelper.insertCallLogs(ATTEND,phone_no,dateString,callDuration,"",callType,callType(phone_no));
                Log.i("calllogs","ofline insert db");
                lengthOfDB();
                CallsDialog.this.finish();
                Log.i("calllogs","missed ofline uploaded");
            }else {
                try {
                    if(CheckConnectivity.checkInternetConnection(context)){
                        isUserExist(USERID,phone_no);
                        Log.i("phoneofuser",phone_no);
                    }else {
                        tvNamePhone.setText(phone_no);
                        tvExistingInq.setText("Not Exist");
                        Toast.makeText(context,"Internet not Connected",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }
            }


        }
        catch (Exception e)
        {
            Log.d("Exception", e.toString());
            e.printStackTrace();
        }

    }

    private void initializeContent()
    {
        array=new JSONArray();
        dataBaseHelper=new DataBaseHelper(context);
        tvNamePhone   = findViewById(R.id.tv_namephone);
        tvExistingInq   = findViewById(R.id.tv_existing_inquiry);
        tvExistingCount   = findViewById(R.id.tv_existing_count);
        tvNewInquiry   = findViewById(R.id.tv_new_inquiry);
        tvGenralCall   =  findViewById(R.id.tv_genral_call);
        tvLater   =  findViewById(R.id.tv_later);
        etComment   = findViewById(R.id.et_comments);
        ivDone   = (ImageView) findViewById(R.id.iv_done);

    }

    private void setListener() {
        ivDone.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v)
            {
                String comentee=etComment.getText().toString();
                if(clickedMode.equals("")){
                    Toast.makeText(context,"Choose Call Type",Toast.LENGTH_SHORT).show();
                }else if(comentee.equals("")){
                    Toast.makeText(context,"Write Comment",Toast.LENGTH_SHORT).show();
                }else {
                    if(dbId!=null){
                        uploadcallogslatter(clickedMode);
                    }else {
                        callLogs(phone_no,comentee,clickedMode,ATTEND);
                    }
                }
            }
        });
        tvLater.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v)
            {
                clickedMode="Later";
                if (dbId!=null){
                    CallsDialog.this.finish();
                }else {
                    callLogs(phone_no,"",clickedMode,UNATTEND);
                }
            }
        });
        tvGenralCall.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                tvGenralCall.setBackground(getDrawable(R.drawable.buttonwhite));
                tvNewInquiry.setBackground(null);
                tvExistingInq.setBackground(null);
                clickedMode="General";
            }
        });
        tvExistingInq.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(tvExistingInq.getText().equals("Not Exist")){
                    Toast.makeText(context,"Customer Not Exist",Toast.LENGTH_SHORT).show();
                }else {
                    tvExistingInq.setBackground(getDrawable(R.drawable.buttonwhite));
                    tvNewInquiry.setBackground(null);
                    tvGenralCall.setBackground(null);
                    clickedMode="Existing";
                    if (dbId!=null){
                        uploadcallogslatter(clickedMode);
                        Intent intent = new Intent(context, TasksListofCallerExist.class);
                        intent.putExtra("phone_no", phone_no);
                        startActivity(intent);
                        CallsDialog.this.finish();
                    }else {
                        callLogs(phone_no,"",clickedMode,ATTEND);
                        Intent intent = new Intent(context, TasksListofCallerExist.class);
                        intent.putExtra("phone_no", phone_no);
                        startActivity(intent);
                        CallsDialog.this.finish();
                    }


                }
            }
        });
        tvNewInquiry.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                tvNewInquiry.setBackground(getDrawable(R.drawable.buttonwhite));
                tvGenralCall.setBackground(null);
                tvExistingInq.setBackground(null);
                clickedMode="New Inquiry";
                if (dbId!=null){
                    uploadcallogslatter(clickedMode);
                    Intent intent = new Intent(context, Inquery.class);
                    intent.putExtra("phone_no", phone_no);
                    startActivity(intent);
                    CallsDialog.this.finish();
                }else {
                    callLogs(phone_no,"",clickedMode,ATTEND);
                    Intent intent = new Intent(context, Inquery.class);
                    startActivity(intent);
                    CallsDialog.this.finish();
                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void callLogs(String phone, String comment, String calMode,String attOrUnatt) {
        String strOrder = CallLog.Calls.DATE + " DESC";
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, strOrder);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        while (managedCursor.moveToNext()) {
            String phNum = managedCursor.getString(number);
            String callTypeCode = managedCursor.getString(type);

            long seconds=managedCursor.getLong(date);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy  hh:mm:ss a");
            dateString = formatter.format(new Date(seconds));

            String strcallDate = managedCursor.getString(date);
            Date callDate = new Date(Long.valueOf(strcallDate));

            callDuration = managedCursor.getString(duration);
            callType = null;
            int callcode = Integer.parseInt(callTypeCode);
            switch (callcode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "Outgoing";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callType = "Incoming";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callType = "Missed";
                    break;
            }
            if(phone.equalsIgnoreCase(phNum))
            {
                if(CheckConnectivity.checkInternetConnection(context)){
                    if(attOrUnatt.equals(ATTEND)){

                        try {
                            obj=new JSONObject();
                            obj.put("MobileNo", phone_no);
                            obj.put("UserID", USERID);
                            obj.put("CallDate", dateString);
                            obj.put("CallDuration", callDuration);
                            obj.put("CallMode", calMode);
                            obj.put("CallType", callType);
                            obj.put("CallComment", comment);
                            array.put(obj);
                            updateLogs(array.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        dataBaseHelper.insertCallLogs(attOrUnatt,phone_no,dateString,callDuration,comment,callType,calMode);
                        Log.i("calllogs","ofline insert db");
                        lengthOfDB();
                        CallsDialog.this.finish();
                    }
                }else {
                    dataBaseHelper.insertCallLogs(attOrUnatt,phone_no,dateString,callDuration,comment,callType,calMode);
                    Log.i("calllogs","ofline insert db");
                    lengthOfDB();
                    CallsDialog.this.finish();
                }
                break;
            }
        }
        managedCursor.close();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void isUserExist(final String userid, final String phoneNo){
        progressDialog=CheckConnectivity.dialogForProgres(context);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN+ServiceUrls.URL_USER_EXIST_OR_NOT, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("responsecalllog", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("call_update");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String isexist=jsonObject1.getString("is_customr");
                        String count=jsonObject1.getString("task_count");
                        if(isexist.equals("yes")){
                            String name=jsonObject1.getString("cust_name");
                            tvNamePhone.setText(name);
                            tvExistingCount.setText(count);
                            tvExistingInq.setText("Customer Exist");
                        }else if(isexist.equals("no")){
                            tvNamePhone.setText(phone_no);
                            tvExistingInq.setText("Not Exist");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.i("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("MobileNo",phoneNo);
                params.put("UserID",userid);
                return params;
            }
        };
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updateLogs(final String callarray){
       // progressDialog=CheckConnectivity.dialogForProgres(context);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN+ServiceUrls.URL_UPLOAD_CALL_LOGS, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    CallsDialog.this.finish();
                    Log.i("calllogs","online updated");
                    if(dbId!=null){
                        Log.i("calllogs","deletedID"+dbId+" "+dataBaseHelper.deleteTableData(AllFields.TABLE_CALL_LOGS,AllFields.CALLER_ID,Integer.parseInt(dbId)));

                    }
                    lengthOfDB();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
               // progressDialog.dismiss();
                Log.i("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                Log.i("newobject",callarray);
                params.put("call_logs", callarray);
                return params;
            }
        };
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void showPushNotification(int count){
        if(count!=0){
            mBuilder = new NotificationCompat.Builder(this.getApplicationContext(), "notify_001");
            Intent ii = new Intent(this.getApplicationContext(), PendingLogsList.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

            NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
            //bigText.bigText(verseurl);
            bigText.setBigContentTitle("Please Update Logs");
            bigText.setSummaryText("Log's need Connection");

            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
            mBuilder.setContentTitle("Your Title");
            mBuilder.setSmallIcon(R.drawable.logo);
            mBuilder.setOngoing(true);
            mBuilder.setContentText(count+" logs required updation.");
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setStyle(bigText);

            mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "YOUR_CHANNEL_ID";
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                mNotificationManager.createNotificationChannel(channel);
                mBuilder.setChannelId(channelId);
            }
            mNotificationManager.notify(0, mBuilder.build());
        }else {
            mNotificationManager.cancel(0);
        }
    }

    public  void lengthOfDB(){
        Cursor cursor1=dataBaseHelper.showTableData(AllFields.TABLE_CALL_LOGS);
        Cursor cursor2=dataBaseHelper.showTableData(AllFields.TABLE_NEW_INQUIRY);
        Log.i("dbcounting",cursor1.getCount()+":logs=inquiry:"+cursor2.getCount());
        if(cursor1.getCount()!=0 || cursor2.getCount()!=0) {
            showPushNotification(cursor1.getCount());
            cursor1.close();
            cursor2.close();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void uploadcallogslatter(String clickedMode) {

        Cursor cursor = dataBaseHelper.showTableData(AllFields.TABLE_CALL_LOGS);

        if (cursor == null) {
            Log.i("error", "dbMenueNull");
        } else {
            array=new JSONArray();
            if (cursor.moveToFirst()) {
                do {
                    String namephone = cursor.getString(cursor.getColumnIndex(AllFields.CALLER_NAME_PHONE));
                    String date = cursor.getString(cursor.getColumnIndex(AllFields.CALL_DATE));
                    String duration = cursor.getString(cursor.getColumnIndex(AllFields.CALL_DURATION));
                    String comments = cursor.getString(cursor.getColumnIndex(AllFields.CALLER_COMMENTS));
                    String type = cursor.getString(cursor.getColumnIndex(AllFields.CALLER_TYPE));
                    String modee = cursor.getString(cursor.getColumnIndex(AllFields.CALLER_MODE));
                    String attendorunattend = cursor.getString(cursor.getColumnIndex(AllFields.CALL_ATTEND_OR_UNATTENDED));
                    String callerIdes= cursor.getString(cursor.getColumnIndex(AllFields.CALLER_ID));
                    if(callerIdes.equals(dbId)){
                        try {
                            obj=new JSONObject();
                            obj.put("MobileNo", namephone);
                            obj.put("UserID", USERID);
                            obj.put("CallDate", date);
                            obj.put("CallDuration", duration);
                            obj.put("CallMode", clickedMode);
                            obj.put("CallType", type);
                            obj.put("CallComment", comments);
                            array.put(obj);
                            updateLogs(array.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } while (cursor.moveToNext());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        tvLater.performClick();
//    }


    public String callType(String phoneno){
        String typeee ="";
        String strOrder = CallLog.Calls.DATE + " DESC";
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, strOrder);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        while (managedCursor.moveToNext()) {
            String phNum = managedCursor.getString(number);
            String callTypeCode = managedCursor.getString(type);

            long seconds=managedCursor.getLong(date);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy  hh:mm:ss a");
            dateString = formatter.format(new Date(seconds));

            String strcallDate = managedCursor.getString(date);
            Date callDate = new Date(Long.valueOf(strcallDate));

            callDuration = managedCursor.getString(duration);
            callType = null;
            int callcode = Integer.parseInt(callTypeCode);
            switch (callcode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "Outgoing";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callType = "Incoming";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callType = "Missed";
                    break;
            }
            if(phoneno.equalsIgnoreCase(phNum))
            {

                typeee=callType;
                break;
            }
        }
        managedCursor.close();
        return typeee;
    }
}