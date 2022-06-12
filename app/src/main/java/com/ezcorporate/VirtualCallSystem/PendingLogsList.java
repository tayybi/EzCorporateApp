package com.ezcorporate.VirtualCallSystem;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.MainActivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.BuildConfig;
import com.ezcorporate.CRM.CRMDashBoard;
import com.ezcorporate.CRM.Inquery;
import com.ezcorporate.CRM.RecyclerViewCRM.ModelClassOfLeads;
import com.ezcorporate.CRM.RecyclerViewCRM.RecyclerTasksList;
import com.ezcorporate.Database.AllFields;
import com.ezcorporate.Database.DataBaseHelper;
import com.ezcorporate.DefinationInfoCustomer.CustomerVerification;
import com.ezcorporate.DefinationInfoCustomer.ExistingCustomer;
import com.ezcorporate.DefinationInfoCustomer.NewCustomer;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.R;
import com.ezcorporate.REM.ListForTransfor;
import com.ezcorporate.VirtualCallSystem.RecyclerView.ModelClassOfCallLogs;
import com.ezcorporate.VirtualCallSystem.RecyclerView.RecyclerPendingLogsList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingLogsList extends AppCompatActivity {

    String URL_PREFIX_DOMAIN="";
    String USERID="";
    RecyclerView recyclerViewOfList;
    List<ModelClassOfCallLogs> listUnattended;
    ModelClassOfCallLogs modelClassOfCallLogs;
    TextView tvTitle;
    ImageView ivGoBack,ivSerch,ivSerchCancel;
    EditText etTitleSearch;
    DataBaseHelper dataBaseHelper;
    Button btnUploadInquiery;
    int progressStatus;
    Switch switchVCC;
    boolean checkedorunchecked;
    Handler handler;
    ArrayList<String> jsonidees;
    JSONArray array;

    private int STORAGE_PERMISSION_CODE = 1;
    JSONObject obj;
    int lgsCounts=0;
    int totalcountforprogeress=0;
    SwipeRefreshLayout swipeRefreshLayout;
    public static PendingLogsList taskListFragment;
    RecyclerPendingLogsList recyclerPendingLogsList;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    PhoneStateReceiver phoneStateReceiver = new PhoneStateReceiver();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ezcorporate.R.layout.activity_list_for_transfor);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE,Manifest.permission.SYSTEM_ALERT_WINDOW}, 10);

        taskListFragment=PendingLogsList.this;
        URL_PREFIX_DOMAIN=SharedPrefManager.getInstance(getApplicationContext()).getUrl();
        USERID=SharedPrefManager.getInstance(getApplicationContext()).getUId();

        init();
        setListener();
        Log.i("userid",USERID);
        try {
            getCallLogs();
        }catch (Exception e){
            Log.i("Exception",""+e);
        }
    }

    private void setListener() {
        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskListFragment.finish();
                overridePendingTransition(com.ezcorporate.R.anim.in_left, com.ezcorporate.R.anim.out_right);
            }
        });

        switchVCC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(lgsCounts==0){
                    if(switchVCC.isChecked()){
                       SharedPrefManager.getInstance(PendingLogsList.this).setVCCValue("no");
                        checkUncheck();
                    }else {
                        SharedPrefManager.getInstance(PendingLogsList.this).setVCCValue("yes");
                        checkUncheck();
                    }
                }else {
                    SharedPrefManager.getInstance(PendingLogsList.this).setVCCValue("no");
                    checkUncheck();
                    CheckConnectivity.showSnakeMessage(buttonView,"UpLoad logs First");
                }
            }
        });
        btnUploadInquiery.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (CheckConnectivity.checkInternetConnection(PendingLogsList.this)) {
                    if (array.toString() != null) {
                        updateLogs(array.toString());
                    } else {
                        Log.i("jsonArry", "null");
                    }
                    getInquiryDetailfromDB();
                } else {
                    Toast.makeText(PendingLogsList.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {

        dataBaseHelper=new DataBaseHelper(this);
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        listUnattended=new ArrayList<ModelClassOfCallLogs>();
        array=new JSONArray();
        btnUploadInquiery=findViewById(R.id.btn_sync_inquiries);
        btnUploadInquiery.setVisibility(View.VISIBLE);
        handler= new Handler();
        ivGoBack=findViewById(com.ezcorporate.R.id.go_back);
        ivSerch=findViewById(com.ezcorporate.R.id.iv_serch);
        ivSerchCancel=findViewById(com.ezcorporate.R.id.iv_search_cancel);
        switchVCC=findViewById(R.id.switch_vcc);
        switchVCC.setVisibility(View.VISIBLE);
        ivSerchCancel.setVisibility(View.GONE);
        ivSerch.setVisibility(View.GONE);
        tvTitle=findViewById(com.ezcorporate.R.id.headingOfeditmanger);
        etTitleSearch=findViewById(com.ezcorporate.R.id.et_search);
        etTitleSearch.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        recyclerViewOfList=findViewById(com.ezcorporate.R.id.recycler_list_tronsfer);
        tvTitle.setText("VCC");
        swipeRefreshLayout=findViewById(com.ezcorporate.R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onRefresh() {

                try {
                    getCallLogs();
                    lengthOfDB();
                    swipeRefreshLayout.setRefreshing(false);
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        checkUncheck();


    }

    public void  setRecycler(List list){
        recyclerPendingLogsList=new RecyclerPendingLogsList(this,list);
        recyclerPendingLogsList.notifyDataSetChanged();
        recyclerViewOfList.setLayoutManager(new LinearLayoutManager(PendingLogsList.this));
        recyclerViewOfList.addItemDecoration(new DividerItemDecoration(recyclerViewOfList.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewOfList.setAdapter(recyclerPendingLogsList);
    }

    public void getCallLogs() {

        Cursor cursor = dataBaseHelper.showTableData(AllFields.TABLE_CALL_LOGS);

        if (cursor == null) {
            Log.i("error", "dbMenueNull");
        } else {
            totalcountforprogeress=0;
            jsonidees=new ArrayList<>();
            array=new JSONArray();
            listUnattended.clear();
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
                    if(attendorunattend.equals(CallsDialog.ATTEND)){
                        try {
                            obj=new JSONObject();
                            obj.put("MobileNo", namephone);
                            obj.put("UserID", USERID);
                            obj.put("CallDate", date);
                            obj.put("CallDuration", duration);
                            obj.put("CallMode", modee);
                            obj.put("CallType", type);
                            obj.put("CallComment", comments);
                            array.put(obj);
                            jsonidees.add(callerIdes);
                            totalcountforprogeress++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        modelClassOfCallLogs = new ModelClassOfCallLogs();
                        modelClassOfCallLogs.setCallerNamePhone(namephone);
                        modelClassOfCallLogs.setCallDate(date);
                        modelClassOfCallLogs.setCallerDuration(duration);
                        modelClassOfCallLogs.setCallerType(type);
                        modelClassOfCallLogs.setCallerComments(comments);
                        modelClassOfCallLogs.setCallerMode(callerIdes);
                        listUnattended.add(modelClassOfCallLogs);
                    }

                } while (cursor.moveToNext());
                totalcountforprogeress=totalcountforprogeress+dataBaseHelper.showTableData(AllFields.TABLE_NEW_INQUIRY).getCount();
                Collections.reverse(listUnattended);
                setRecycler(listUnattended);
                Log.i("calllogs", "uploaded to list");
                Log.i("calllogs", "jsonArray"+array.length());
            }
        }
    }

    public void getInquiryDetailfromDB() {

        Cursor cursor = dataBaseHelper.showTableData(AllFields.TABLE_NEW_INQUIRY);
        Log.i("inquiryCount",""+cursor.getCount());

        if (cursor == null) {
            Log.i("error", "dbMenueNull");
        } else {
            if (cursor.moveToFirst()) {
                do {
                    String userinqid = cursor.getString(cursor.getColumnIndex(AllFields.INQUIRYID));
                    String ldate=cursor.getString(cursor.getColumnIndex(AllFields .LDATE));
                    String ltime=cursor.getString(cursor.getColumnIndex(AllFields.LTIME));
                    String lgroup=cursor.getString(cursor.getColumnIndex(AllFields.FINALGROUP));
                    String lcat=cursor.getString(cursor.getColumnIndex(AllFields.FINALCAT));
                    String lsubcat=cursor.getString(cursor.getColumnIndex(AllFields.FINALSUBCAT));
                    String lsource=cursor.getString(cursor.getColumnIndex(AllFields.FINALSOURCE));
                    String customercatagory=cursor.getString(cursor.getColumnIndex(AllFields.FINALCUSTOMERCAT));
                    String cname=cursor.getString(cursor.getColumnIndex(AllFields.CNAME));
                    String ccontactperson=cursor.getString(cursor.getColumnIndex(AllFields.CCONTACTPERSON));
                    String cmob=cursor.getString(cursor.getColumnIndex(AllFields.CMOBILE));
                    String cemail=cursor.getString(cursor.getColumnIndex(AllFields.CEMAIL));
                    String caddress=cursor.getString(cursor.getColumnIndex(AllFields.CADDRESS));
                    String cgeoloc=cursor.getString(cursor.getColumnIndex(AllFields.CGEOLOCATION));
                    String fmanagedby=cursor.getString(cursor.getColumnIndex(AllFields.FINALMANAGEDBY));
                    String ffollowedby=cursor.getString(cursor.getColumnIndex(AllFields.FINALFALLOWBY));
                    String fstages=cursor.getString(cursor.getColumnIndex(AllFields.FINALSTAGE));
                    String iestimatedBudjet=cursor.getString(cursor.getColumnIndex(AllFields.IESTIMATEDBUDGET));
                    String idate=cursor.getString(cursor.getColumnIndex(AllFields.IDATE));
                    String idtail=cursor.getString(cursor.getColumnIndex(AllFields.IDETAIL));
                    String icustno=cursor.getString(cursor.getColumnIndex(AllFields.ICUSTNO));
                    String iexpdate=cursor.getString(cursor.getColumnIndex(AllFields.IEXPDATE));
                    String isuject=cursor.getString(cursor.getColumnIndex(AllFields.ISUBJECT));
                    String fdate=cursor.getString(cursor.getColumnIndex(AllFields.FDATE));
                    String ftime=cursor.getString(cursor.getColumnIndex(AllFields.FTIME));
                    String ftodo=cursor.getString(cursor.getColumnIndex(AllFields.FTODO));
                    String finaltask=cursor.getString(cursor.getColumnIndex(AllFields.FINALTASK));
                    String stname=cursor.getString(cursor.getColumnIndex(AllFields.STNAME));
                    String stmobile=cursor.getString(cursor.getColumnIndex(AllFields.STMOBILE));
                    String stcontctpersn=cursor.getString(cursor.getColumnIndex(AllFields.STCONTACTPERSON));
                    String stemail=cursor.getString(cursor.getColumnIndex(AllFields.STEMAIL));
                    String fstkh=cursor.getString(cursor.getColumnIndex(AllFields.FINALSTKH));
                        uploadInquiryData(userinqid,USERID,ldate,ltime,lgroup,lcat,lsubcat,lsource,customercatagory,cname,
                                ccontactperson,cmob,cemail,caddress,cgeoloc,fmanagedby,ffollowedby,fstages,
                                idtail,icustno,isuject,iestimatedBudjet,idate,iexpdate,fdate,ftime,finaltask,ftodo,fstkh,
                                stname,stmobile,stcontctpersn,stemail);
                } while (cursor.moveToNext());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updateLogs(final String callarray){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN+ServiceUrls.URL_UPLOAD_CALL_LOGS, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.i("calllogs","online updated");
                    deleteTableData(jsonidees,"");
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

    public void uploadInquiryData(final String dbid, final String uName, final String ldate, final String ltime, final String lgroup, final String lcat, final String lsubcat, final String lsource, final String cCustCat, final String cname, final String ccontctpersn, final String cmob, final String cemail, final String cadd, final String cgeo, final String fmanageby, final String ffollowby, final String istage, final String idetail, final String icNo, final String isubject, final String iestimatedbudjet, final String idate, final String iexpdate, final String fdate, final String ftime, final String ftask, final String ftodo, final String stktitle, final String stkname, final String stkmobile, final String stkcntctPerson, final String stkemail) {
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN + ServiceUrls.URL_INQUIRY_UPDAT, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("rem_update");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String messagea = jsonObject1.getString("message");
                        if (messagea.equals("CINQ- Sucessfully Created")) {
                            deleteTableData(null,dbid);
                           Log.i("resultinquiry","inquiry uploaded online ");
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error", "" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("activ_user", uName);
                params.put("inq_date", ldate);
                params.put("inq_time", ltime);
                params.put("ld_grp_id", lgroup);
                params.put("ld_cat_id",lcat);
                params.put("ld_sbcat_id", lsubcat);
                params.put("ld_srs_id",lsource );
                params.put("cust_catg",cCustCat );
                params.put("customer_id","0" );
                params.put("cust_name", cname);
                params.put("cont_person", ccontctpersn);
                params.put("mob_no", cmob); ///
                params.put("e_mail", cemail);
                params.put("address", cadd);
                params.put("customer_geo", cgeo);
                params.put("inq_subj", isubject);
                params.put("opprt_level", istage);
                params.put("inq_detail", idetail);
                params.put("cust_inq_no", icNo);
                params.put("customer_budgt", iestimatedbudjet);
                params.put("cust_inq_date", idate);
                params.put("exp_inq_date", iexpdate);
                params.put("folow_date", fdate);
                params.put("follow_user", ffollowby);
                params.put("manag_user", fmanageby);
                params.put("folow_time", ftime);
                params.put("flwp_task", ftask );
                params.put("to_do", ftodo);
                params.put("stkh_title", stktitle);
                params.put("stkhld_name", stkname);
                params.put("stkhld_phn_no", stkmobile);//
                params.put("stkhld_cont_no", stkcntctPerson);//
                params.put("stkhld_e_mail",stkemail );
                return params;
            }
        };
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest1);

    }

    public void deleteTableData(ArrayList<String> ides,String id){
        if(!id.equals("")){
            Log.i("calllogs","deletedID"+id+" "+dataBaseHelper.deleteTableData(AllFields.TABLE_NEW_INQUIRY,AllFields.INQUIRYID,Integer.parseInt(id)));

        }else if(ides!=null){
            for(int i=0;i<ides.size();i++){
                Log.i("calllogs","deletedID"+ides.get(i)+" "+dataBaseHelper.deleteTableData(AllFields.TABLE_CALL_LOGS,AllFields.CALLER_ID,Integer.parseInt(ides.get(i))));
            }
        }

    }

    public void lengthOfDB(){
        Cursor cursor1=dataBaseHelper.showTableData(AllFields.TABLE_CALL_LOGS);
        Cursor cursor2=dataBaseHelper.showTableData(AllFields.TABLE_NEW_INQUIRY);
          Log.i("dbcounting",cursor1.getCount()+":logs=inquiry:"+cursor2.getCount());
            showPushNotification(cursor1.getCount(),cursor2.getCount());
            lgsCounts=cursor1.getCount()+cursor2.getCount();
            cursor1.close();
            cursor2.close();


    }
    public void showPushNotification(int logcount,int inqcount){
        if(logcount!=0 || inqcount!=0){
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
            mBuilder.setContentText(logcount+"Logs "+inqcount+" Inquries required updation");
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setStyle(bigText);

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

    public void checkUncheck(){
        if(SharedPrefManager.getInstance(getApplicationContext()).getVCCValue().equals("yes")){
            checkedorunchecked=false;
            btnUploadInquiery.setVisibility(View.GONE);
            recyclerViewOfList.setVisibility(View.GONE);
        }else {
            checkedorunchecked=true;
            btnUploadInquiery.setVisibility(View.VISIBLE);
            recyclerViewOfList.setVisibility(View.VISIBLE);
        }
        switchVCC.setChecked(checkedorunchecked);
    }
    @Override
    protected void onResume() {
        super.onResume();
        getCallLogs();
        lengthOfDB();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getCallLogs();
        lengthOfDB();
    }

}

