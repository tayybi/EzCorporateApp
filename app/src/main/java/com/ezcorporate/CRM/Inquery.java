package com.ezcorporate.CRM;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.MapsActivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.Database.AllFields;
import com.ezcorporate.Database.DataBaseHelper;
import com.ezcorporate.DefinationInfoCustomer.ExistingCustomer;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.MapsActivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.Database.AllFields;
import com.ezcorporate.Database.DataBaseHelper;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.VirtualCallSystem.CallsDialog;

public class Inquery extends AppCompatActivity {
    String URL_PREFIX_DOMAIN="";
    String USERID="";
    ImageView ivGoBack,ivDone;
    DataBaseHelper dataBaseHelper;
    Dialog progressDialog;
    int year,month,day,hour,minute;
    TextView tvLDate,tvLTime,tvFDate,tvFTime,tvInqDate,tvInqExpDate,tvCusGeoLoc;
    EditText etCusName,etCusMobileNo,etCusContactPerson,etCusAdress,etCusEmail;
    EditText etInqSubject,etInqDetail,etInqcusNo,etInqestimatedBudjet,etFollowTodo;
    EditText etSTKHName,etSTKHContctPerson,etSTKHMobile,etSTKHemail;
    LinearLayout linLDate,linLTime,linFDate,linFTime;
    Cursor cursor;
    Context context;
    Calendar calendar;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayAdapter<String> dataAdapter;
    List<String> lgroup,lcat,lsubcat,lsource,ltask,lstage,lcustumerCat,lstkh,lmanage,lfollow;
    Spinner spGroup,spCatagory,spSubCatagory,spSource,spTask,spStage,spCustomerCatagory,spSTKH,spManageBy,spFollowBy;
    String finalGroup,finaCat,finalSuCat,finalSoure,finalTask,finalStage,finalCustomerCat,finalSkh,finalFollowby,finalManageBy;
    LocationManager manager;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ezcorporate.R.layout.activity_inquery);
        URL_PREFIX_DOMAIN=SharedPrefManager.getInstance(getApplicationContext()).getUrl();
        USERID=SharedPrefManager.getInstance(getApplicationContext()).getUId();


        Log.i("myurl",URL_PREFIX_DOMAIN+"/"+USERID);
        init();
        setListener();
        progressDialog=CheckConnectivity.dialogForProgres(this);
        GetDropDownList();
            etCusMobileNo.setText(CallsDialog.phone_no);
    }
    private void setListener() {
        setClickOfSpin();

        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(com.ezcorporate.R.anim.in_left, com.ezcorporate.R.anim.out_right);
            }
        });
        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ldate=tvLDate.getText().toString();           //customer info
                String ltime=tvLTime.getText().toString();           //customer info
                String cname=etCusName.getText().toString();           //customer info
                String cmob=etCusMobileNo.getText().toString();
                String ccntctpersn=etCusContactPerson.getText().toString();
                String caddress=etCusAdress.getText().toString();
                String cgeoloc=tvCusGeoLoc.getText().toString();
                String cemail=etCusEmail.getText().toString();
                String idetail=etInqDetail.getText().toString();   ///inquiry data
                String icustNo=etInqcusNo.getText().toString();   ///inquiry data
                String isubject=etInqSubject.getText().toString();   ///inquiry data
                String iestimatedBudjet=etInqestimatedBudjet.getText().toString();   ///inquiry data
                String idate=tvInqDate.getText().toString();   ///inquiry data
                String iexpdate=tvInqExpDate.getText().toString();   ///inquiry data
                String fdate=tvFDate.getText().toString();   ///follow up
                String ftime=tvFTime.getText().toString();   ///follow up
                String ftodo=etFollowTodo.getText().toString();   ///follow up
                String stname=etSTKHName.getText().toString();   ///STKH
                String stmobile=etSTKHMobile.getText().toString();   ///STKH
                String stcontctpersn=etSTKHContctPerson.getText().toString();   ///STKH
                String stemail=etSTKHemail.getText().toString();   ///STKH

                try {
                    if(finalGroup.equals("")){
                        Toast.makeText(context,"Choose Lead Group",Toast.LENGTH_SHORT).show();
                    }else if(finaCat.equals("")){
                        Toast.makeText(context,"Choose Category",Toast.LENGTH_SHORT).show();
                    }else if(finalSuCat.equals("")){
                        Toast.makeText(context,"Choose Sub Category",Toast.LENGTH_SHORT).show();
                    }else if(finalSoure.equals("")){
                        Toast.makeText(context,"Choose Source",Toast.LENGTH_SHORT).show();
                    }else if(finalCustomerCat.equals("")){
                        Toast.makeText(context,"Choose Customer Category",Toast.LENGTH_SHORT).show();
                    }else if(cname.equals("")){
                        etCusName.setError("");
                    }else if(cmob.equals("")){
                        etCusMobileNo.setError("");
                    }else if(finalFollowby.equals("")){
                        Toast.makeText(context,"Choose FollowBy",Toast.LENGTH_SHORT).show();
                    }else if(finalManageBy.equals("")){
                        Toast.makeText(context,"Choose ManageBy",Toast.LENGTH_SHORT).show();
                    }else if(isubject.equals("")){
                        etInqSubject.setError("");
                    }else if(idate.equals("?")){
                        tvInqDate.setError("");
                    }else if(iexpdate.equals("?")){
                        tvInqExpDate.setError("");
                    }
//                else if(finalTask.equals("")){
//                    Toast.makeText(context,"Select Task",Toast.LENGTH_SHORT).show();
//                }
                    else {
                        if(CheckConnectivity.checkInternetConnection(context)){
                            uploadInquiryData(USERID, ldate, ltime, finalGroup, finaCat, finalSuCat, finalSoure, finalCustomerCat, cname, ccntctpersn,
                                    cmob, cemail, caddress, cgeoloc, finalFollowby, finalManageBy, finalStage, idetail, icustNo, isubject, iestimatedBudjet,
                                    idate, iexpdate, fdate, ftime, finalTask, ftodo, finalSkh, stname, stmobile, stcontctpersn, stemail);

                        }else {

                            dataBaseHelper.insertNewInquiryDB(USERID, ldate, ltime, finalGroup, finaCat, finalSuCat, finalSoure, finalCustomerCat, cname, ccntctpersn,
                                    cmob, cemail, caddress, cgeoloc, finalFollowby, finalManageBy, finalStage, idetail, icustNo, isubject, iestimatedBudjet,
                                    idate, iexpdate, fdate, ftime, finalTask, ftodo, finalSkh, stname, stmobile, stcontctpersn, stemail);
                            finish();
                            overridePendingTransition(com.ezcorporate.R.anim.in_left, com.ezcorporate.R.anim.out_right);
                            Log.i("calllogs","uploaded inquiry ofline");
                        }
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }



            }
        });

        tvCusGeoLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ContextCompat.checkSelfPermission(Inquery.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(Inquery.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    } else if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Toast.makeText(Inquery.this, "Please Turn On GPS", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(Inquery.this, MapsActivity.class);
                        startActivityForResult(intent, 90);
                        overridePendingTransition(com.ezcorporate.R.anim.in_right, com.ezcorporate.R.anim.out_left);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }
            }
        });
        tvInqDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    DatePickerDialog datePickerDialog = new DatePickerDialog(Inquery.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    tvInqDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                }

                            }, year, month, day);
                    //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    datePickerDialog.show();
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });

        tvInqExpDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    DatePickerDialog datePickerDialog = new DatePickerDialog(Inquery.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    tvInqExpDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                }

                            }, year, month, day);
                    //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    datePickerDialog.show();
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });

        linLDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    DatePickerDialog datePickerDialog = new DatePickerDialog(Inquery.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    tvLDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                }

                            }, year, month, day);
                    //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    datePickerDialog.show();
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });
        linFDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    DatePickerDialog datePickerDialog = new DatePickerDialog(Inquery.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    tvFDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                }

                            }, year, month, day);
                    //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    datePickerDialog.show();
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });
        linFTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    final String timeSet;
                    TimePickerDialog datePickerDialog = new TimePickerDialog(Inquery.this,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    tvFTime.setText(setTimeAmPm(hourOfDay,minute));
                                }
                            },hour,minute,false);
                    //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    datePickerDialog.show();
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });
        linLTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    TimePickerDialog datePickerDialog = new TimePickerDialog(Inquery.this,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    tvLTime.setText(setTimeAmPm(hourOfDay,minute));
                                }
                            },hour,minute,false);
                    //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    datePickerDialog.show();
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });

    }

    public String getDDId(String crntColName,String tablname,String colmName,String colmid)  {
        cursor=dataBaseHelper.showTableData(tablname);
        String rtnId = "";
        if(cursor==null){
            Log.i("error", "dbnull");
        }else {
            if (cursor.moveToFirst()) {
                do {
                    String name=cursor.getString(cursor.getColumnIndex(colmName));
                    if(name.equals(crntColName)){
                        rtnId=cursor.getString(cursor.getColumnIndex(colmid));
                        break;
                    }

                }while (cursor.moveToNext());
            }
        }
        return rtnId;
    }

    private void init() {
        context=this;
        dataBaseHelper = new DataBaseHelper(this);
        ivGoBack = findViewById(com.ezcorporate.R.id.go_back);
        ivDone = findViewById(com.ezcorporate.R.id.iv_done);

        etCusName=findViewById(com.ezcorporate.R.id.et_custm_name);
        etCusMobileNo=findViewById(com.ezcorporate.R.id.et_custm_mobileno);
        etCusContactPerson=findViewById(com.ezcorporate.R.id.et_custm_contct_person);
        etCusAdress=findViewById(com.ezcorporate.R.id.et_custm_address);
        tvCusGeoLoc=findViewById(com.ezcorporate.R.id.tv_custm_geoloc);
        etCusEmail=findViewById(com.ezcorporate.R.id.et_custm_email);

        etInqDetail=findViewById(com.ezcorporate.R.id.et_inq_detail);
        etInqcusNo=findViewById(com.ezcorporate.R.id.et_inq_number);
        etInqSubject=findViewById(com.ezcorporate.R.id.et_inq_subjec);
        etInqestimatedBudjet=findViewById(com.ezcorporate.R.id.et_inq_estimated_budget);
        etFollowTodo=findViewById(com.ezcorporate.R.id.et_todo);

        etSTKHName=findViewById(com.ezcorporate.R.id.et_stkh_name);
        etSTKHMobile=findViewById(com.ezcorporate.R.id.et_stkh_mobile);
        etSTKHContctPerson=findViewById(com.ezcorporate.R.id.et_stkh_cntct_prsn);
        etSTKHemail=findViewById(com.ezcorporate.R.id.et_stkh_email);

        tvLDate=findViewById(com.ezcorporate.R.id.tv_ldate);
        tvLTime=findViewById(com.ezcorporate.R.id.tv_ltime);
        tvFDate=findViewById(com.ezcorporate.R.id.tv_fdate);
        tvFTime=findViewById(com.ezcorporate.R.id.tv_ftime);
        tvInqDate=findViewById(com.ezcorporate.R.id.tv_inqdate);
        tvInqExpDate=findViewById(com.ezcorporate.R.id.tv_inq_expdate);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour=calendar.get(Calendar.HOUR_OF_DAY);
        minute=calendar.get(Calendar.MINUTE);

        tvFTime.setText(setTimeAmPm(hour,minute));
        tvLTime.setText(setTimeAmPm(hour,minute));
        tvFDate.setText(day + "-" + (month + 1) + "-" + year);
        tvLDate.setText(day + "-" + (month + 1) + "-" + year);
        tvInqDate.setText("?");
        tvInqExpDate.setText("?");

        linFDate=findViewById(com.ezcorporate.R.id.lin_fdate);
        linFTime=findViewById(com.ezcorporate.R.id.lin_ftime);
        linLDate=findViewById(com.ezcorporate.R.id.lin_ldate);
        linLTime=findViewById(com.ezcorporate.R.id.lin_ltime);

        spGroup = findViewById(com.ezcorporate.R.id.sp_group);
        spCatagory = findViewById(com.ezcorporate.R.id.sp_cat);
        spSubCatagory = findViewById(com.ezcorporate.R.id.sp_subcat);
        spStage = findViewById(com.ezcorporate.R.id.sp_stage);
        spSource = findViewById(com.ezcorporate.R.id.sp_source);
        spTask = findViewById(com.ezcorporate.R.id.sp_task);
        spSTKH = findViewById(com.ezcorporate.R.id.sp_stkh);
        spCustomerCatagory = findViewById(com.ezcorporate.R.id.sp_custom_cat);
        spManageBy = findViewById(com.ezcorporate.R.id.sp_manageby);
        spFollowBy = findViewById(com.ezcorporate.R.id.sp_followby);
        getList();

        swipeRefreshLayout=findViewById(com.ezcorporate.R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onRefresh() {
                try {
                    GetDropDownList();
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


    }

    public void setClickOfSpin(){
        spGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String itemName;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    itemName=parent.getItemAtPosition(position).toString();
                    if(!itemName.equals("Group")){
                        finalGroup=getDDId(itemName,AllFields.TABLE_DD_GROUP,AllFields.GROUPNAME,AllFields.GROUPID);
                        Log.i("finalid","group="+finalGroup);
                    }else {
                        finalGroup="";
                        Log.i("finalid","group="+finalGroup);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spCatagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String itemName;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    itemName=parent.getItemAtPosition(position).toString();
                    if(!itemName.equals("Catagory")){
                        finaCat=getDDId(itemName,AllFields.TABLE_DD_CAT,AllFields.CATAGRYNAME,AllFields.CATAGRYID);
                        Log.i("finalid","group="+finaCat);
                    }else {
                        finaCat="";
                        Log.i("finalid","group="+finaCat);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSubCatagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String itemName;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    itemName=parent.getItemAtPosition(position).toString();
                    if(!itemName.equals("SubCatagory")){
                        finalSuCat=getDDId(itemName,AllFields.TABLE_DD_SUB_CAT,AllFields.SUBCATNAME,AllFields.SUBCATID);
                        Log.i("finalid","group="+finalSuCat);
                    }else {
                        finalSuCat="";
                        Log.i("finalid","group="+finalSuCat);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String itemName;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    itemName=parent.getItemAtPosition(position).toString();
                    if(!itemName.equals("Source")){
                        finalSoure=getDDId(itemName,AllFields.TABLE_DD_SOURCE,AllFields.SOURCNAME,AllFields.SOURCEID);
                        Log.i("finalid","group="+finalSoure);
                    }else {
                        finalSoure="";
                        Log.i("finalid","group="+finalSoure);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spTask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String itemName;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    itemName=parent.getItemAtPosition(position).toString();
                    if(!itemName.equals("Task")){
                        finalTask=getDDId(itemName,AllFields.TABLE_DD_TASK,AllFields.TASKNAME,AllFields.TASKID);
                        Log.i("finalid","group="+finalTask);
                    }else {
                        finalTask="";
                        Log.i("finalid","group="+finalTask);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spStage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String itemName;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    itemName=parent.getItemAtPosition(position).toString();
                    if(!itemName.equals("Convert To Opportunity")){
                        finalStage=getDDId(itemName,AllFields.TABLE_DD_STAGE,AllFields.STAGENAME,AllFields.STAGEID);
                        Log.i("finalid","group="+finalStage);
                    }else {
                        finalStage="no";
                        Log.i("finalid","group="+finalStage);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSTKH.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String itemName;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    itemName=parent.getItemAtPosition(position).toString();
                    if(!itemName.equals("Title")){
                        finalSkh=getDDId(itemName,AllFields.TABLE_DD_STKH,AllFields.STKHNAME,AllFields.STKHID);
                        Log.i("finalid","group="+finalSkh);
                    }else {
                        finalSkh="";
                        Log.i("finalid","group="+finalSkh);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spCustomerCatagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String itemName;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    itemName=parent.getItemAtPosition(position).toString();
                    if(!itemName.equals("Customer Catagory")){
                        if(itemName.equals("Main Customer")){
                            finalCustomerCat="m_cust";
                            Log.i("finalid","group="+finalCustomerCat);
                        }else {
                            finalCustomerCat="e_cust";
                            Log.i("finalid","group="+finalCustomerCat);
                        }

                    }else {
                        finalCustomerCat="";
                        Log.i("finalid","group="+finalCustomerCat);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spFollowBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String itemName;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    itemName=parent.getItemAtPosition(position).toString();
                    if(!itemName.equals("Follow By")){
                        finalFollowby=getDDId(itemName,AllFields.TABLE_DD_USERS,AllFields.USERNAME,AllFields.USERID);
                            Log.i("finalid","group="+finalFollowby);
                    }else {
                        finalFollowby="";
                        Log.i("finalid","group="+finalFollowby);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spManageBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String itemName;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    itemName=parent.getItemAtPosition(position).toString();
                    if(!itemName.equals("Manage By")){
                            finalManageBy=getDDId(itemName,AllFields.TABLE_DD_USERS,AllFields.USERNAME,AllFields.USERID);
                            Log.i("finalid","group="+finalManageBy);
                    }else {
                        finalManageBy="";
                        Log.i("finalid","group="+finalManageBy);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getList(){
        lgroup=new ArrayList<>();
        lcat=new ArrayList<>();
        lsubcat=new ArrayList<>();
        lsource=new ArrayList<>();
        ltask=new ArrayList<>();
        lstkh=new ArrayList<>();
        lstage=new ArrayList<>();
        lmanage=new ArrayList<>();
        lfollow=new ArrayList<>();

        lcustumerCat=new ArrayList<>();
        lcustumerCat.add("Customer Category");
        lcustumerCat.add("Main Customer");
        lcustumerCat.add("End User");
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,lcustumerCat);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCustomerCatagory.setAdapter(dataAdapter);
        spCustomerCatagory.setSelection(0);

        cursor=dataBaseHelper.showTableData(AllFields.TABLE_DD_GROUP);
        lgroup.add("Group");
        if (cursor.moveToFirst()) {
                do {
                    String names=cursor.getString(cursor.getColumnIndex(AllFields.GROUPNAME));
                        lgroup.add(names);
                }while (cursor.moveToNext());
        }
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,lgroup);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGroup.setAdapter(dataAdapter);
        spGroup.setSelection(0);

        cursor=dataBaseHelper.showTableData(AllFields.TABLE_DD_CAT);
        lcat.add("Category");
        if (cursor.moveToFirst()) {
            do {
                String names=cursor.getString(cursor.getColumnIndex(AllFields.CATAGRYNAME));
                lcat.add(names);
            }while (cursor.moveToNext());
        }
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,lcat);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCatagory.setAdapter(dataAdapter);
        spCatagory.setSelection(0);

        cursor=dataBaseHelper.showTableData(AllFields.TABLE_DD_SUB_CAT);
        lsubcat.add("Sub Category");
        if (cursor.moveToFirst()) {
            do {
                String names=cursor.getString(cursor.getColumnIndex(AllFields.SUBCATNAME));
                lsubcat.add(names);
            }while (cursor.moveToNext());
        }
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,lsubcat);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubCatagory.setAdapter(dataAdapter);
        spSubCatagory.setSelection(0);

        cursor=dataBaseHelper.showTableData(AllFields.TABLE_DD_SOURCE);
        lsource.add("Source");
        if (cursor.moveToFirst()) {
            do {
                String names=cursor.getString(cursor.getColumnIndex(AllFields.SOURCNAME));
                lsource.add(names);
            }while (cursor.moveToNext());
        }
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,lsource);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSource.setAdapter(dataAdapter);
        spSource.setSelection(0);

        cursor=dataBaseHelper.showTableData(AllFields.TABLE_DD_STAGE);
        lstage.add("Convert To Opportunity");
        if (cursor.moveToFirst()) {
            do {
                String names=cursor.getString(cursor.getColumnIndex(AllFields.STAGENAME));
                lstage.add(names);
            }while (cursor.moveToNext());
        }
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,lstage);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStage.setAdapter(dataAdapter);
        spStage.setSelection(0);

        cursor=dataBaseHelper.showTableData(AllFields.TABLE_DD_TASK);
        ltask.add("Task");
        if (cursor.moveToFirst()) {
            do {
                String names=cursor.getString(cursor.getColumnIndex(AllFields.TASKNAME));
                ltask.add(names);
            }while (cursor.moveToNext());
        }
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,ltask);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTask.setAdapter(dataAdapter);
        spTask.setSelection(0);

        cursor=dataBaseHelper.showTableData(AllFields.TABLE_DD_STKH);
        lstkh.add("Title");
        if (cursor.moveToFirst()) {
            do {
                String names=cursor.getString(cursor.getColumnIndex(AllFields.STKHNAME));
                lstkh.add(names);
            }while (cursor.moveToNext());
        }
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,lstkh);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSTKH.setAdapter(dataAdapter);
        spSTKH.setSelection(0);

        cursor=dataBaseHelper.showTableData(AllFields.TABLE_DD_USERS);
        lmanage.add("Manage By");
        if (cursor.moveToFirst()) {
            do {
                String names=cursor.getString(cursor.getColumnIndex(AllFields.USERNAME));
                lmanage.add(names);
            }while (cursor.moveToNext());
        }
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,lmanage);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spManageBy.setAdapter(dataAdapter);
        spManageBy.setSelection(0);

        cursor=dataBaseHelper.showTableData(AllFields.TABLE_DD_USERS);
        lfollow.add("Follow By");
        if (cursor.moveToFirst()) {
            do {
                String names=cursor.getString(cursor.getColumnIndex(AllFields.USERNAME));
                lfollow.add(names);
            }while (cursor.moveToNext());
        }
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,lfollow);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFollowBy.setAdapter(dataAdapter);
        spFollowBy.setSelection(0);

    }

    public void GetDropDownList(){
        StringRequest stringRequest=new StringRequest(Request.Method.GET, URL_PREFIX_DOMAIN+ServiceUrls.URL_DROP_DOWN_LISTS, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                dataBaseHelper.deleteTable(AllFields.TABLE_DD_GROUP);
                dataBaseHelper.deleteTable(AllFields.TABLE_DD_CAT);
                dataBaseHelper.deleteTable(AllFields.TABLE_DD_SUB_CAT);
                dataBaseHelper.deleteTable(AllFields.TABLE_DD_SOURCE);
                dataBaseHelper.deleteTable(AllFields.TABLE_DD_TASK);
                dataBaseHelper.deleteTable(AllFields.TABLE_DD_STAGE);
                dataBaseHelper.deleteTable(AllFields.TABLE_DD_STKH);
                dataBaseHelper.deleteTable(AllFields.TABLE_DD_USERS);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("inq_group");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String name=jsonObject1.getString("group_name");
                        String id=jsonObject1.getString("group_id");
                       dataBaseHelper.insertDDGroup(name,id);
                    }
                    jsonArray=jsonObject.getJSONArray("inq_catagory");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String name=jsonObject1.getString("catagry_name");
                        String id=jsonObject1.getString("catagry_id");
                        dataBaseHelper.insertDDCat(name,id);
                    }
                    jsonArray=jsonObject.getJSONArray("inq_subcatagory");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String name=jsonObject1.getString("sbcatagry_name");
                        String id=jsonObject1.getString("sbcatagry_id");
                        dataBaseHelper.insertDDSubCat(name,id);
                    }
                    jsonArray=jsonObject.getJSONArray("inq_source");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String name=jsonObject1.getString("source_name");
                        String id=jsonObject1.getString("source_id");
                        dataBaseHelper.insertDDSource(name,id);
                        }
                        jsonArray=jsonObject.getJSONArray("inq_stkholderttl");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String name=jsonObject1.getString("stkholderttl_name");
                        String id=jsonObject1.getString("stkholderttl_id");
                        dataBaseHelper.insertDDSTKH(name,id);
                    }
                    jsonArray=jsonObject.getJSONArray("inq_oprtnty_stage");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String name=jsonObject1.getString("stage_name");
                        String id=jsonObject1.getString("stage_id");
                        dataBaseHelper.insertDDStage(name,id);
                    }
                    jsonArray=jsonObject.getJSONArray("inq_task");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String name=jsonObject1.getString("task_name");
                        String id=jsonObject1.getString("task_id");
                        dataBaseHelper.insertDDTask(name,id);
                    }
                    jsonArray=jsonObject.getJSONArray("inq_users");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        String name=jsonObject1.getString("user_name");
                        String id=jsonObject1.getString("user_id");
                        dataBaseHelper.insertDDInqUser(name,id);
                    }
                    getList();
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
        });
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    public String setTimeAmPm(int hourOfDay,int minute){
        String time;
        if(hourOfDay>=0 && hourOfDay<12){
            time = hourOfDay + ":" + minute + " AM";
            return time;
        } else {
            if(hourOfDay == 12){
                time = hourOfDay + ":" + minute + " PM";
                return time;
            } else{
                hourOfDay = hourOfDay -12;
                time = hourOfDay + ":" + minute + " PM";
                return time;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
           if (requestCode == 909) {
                try {
                    String s = data.getExtras().getString("latlong");
                    tvCusGeoLoc.setText(s);
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        }
    }
    public void uploadInquiryData(final String uName,final String ldate, final String ltime, final String lgroup, final String lcat, final String lsubcat, final String lsource, final String cCustCat, final String cname,final String ccontctpersn, final String cmob, final String cemail, final String cadd, final String cgeo,final String fmanageby, final String ffollowby,final String istage, final String idetail, final String icNo, final String isubject, final String iestimatedbudjet, final String idate, final String iexpdate, final String fdate, final String ftime, final String ftask, final String ftodo, final String stktitle, final String stkname, final String stkmobile, final String stkcntctPerson, final String stkemail) {
        progressDialog=CheckConnectivity.dialogForProgres(context);
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
                            finish();
                            overridePendingTransition(com.ezcorporate.R.anim.in_left, com.ezcorporate.R.anim.out_right);
                            Log.i("result","uploaded");
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
}
