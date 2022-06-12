package com.ezcorporate.Assignments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.CRM.Inquery;
import com.ezcorporate.CRM.RecyclerViewCRM.ModelClassOfLeads;
import com.ezcorporate.Database.AllFields;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAssignments extends AppCompatActivity {

    int year,month,day,hour,minute;
    Calendar calendar;
    String URL_PREFIX_DOMAIN="";
    String USERID="";
    Spinner spinPerson,spinPriority;
    LinearLayout linLDate,linLTime;
    List<ModelClassOfLeads> lperson,lpriority;
    String finalPerson,finalPriority;
    ModelClassOfLeads modelClassOfLeads;
    TextView tvDeadLineDate,tvDeadlineTime;
    EditText edtDetails,edtTitle ;
    Dialog progressDialog;
    ArrayAdapter<String> dataAdapter;
    ImageView ivGoback,ivDone;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);
        URL_PREFIX_DOMAIN= SharedPrefManager.getInstance(getApplicationContext()).getUrl();
        USERID=SharedPrefManager.getInstance(getApplicationContext()).getUId();
        init();
        setListener();
        GetDropDownList();

    }

    private void init() {

        lperson=new ArrayList<>();
        lpriority=new ArrayList<>();
        spinPerson  = findViewById(R.id.spin_person);
        spinPriority  = findViewById(R.id.spin_priority);
        tvDeadLineDate = findViewById(R.id.tv_dldate);
        tvDeadlineTime = findViewById(R.id.tv_dltime);
        edtDetails = findViewById(R.id.edt_assign_details);
        edtTitle = findViewById(R.id.edt_assign_title);
        ivGoback = findViewById(R.id.go_back);
        ivDone = findViewById(R.id.iv_done);
        linLDate=findViewById(R.id.lin_ldate);
        linLTime=findViewById(R.id.lin_ltime);

    }

    private void setListener() {
        ivGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_left, R.anim.out_right);
            }
        });

        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title=edtTitle.getText().toString();
                String detail=edtDetails.getText().toString();
                String dldate=tvDeadLineDate.getText().toString();
                String dltime=tvDeadlineTime.getText().toString();
                if(title.equals("")){
                    edtTitle.setError("");
                    edtTitle.setFocusable(true);
                }else if(finalPerson.equals("")){
                    Toast.makeText(AddAssignments.this,"Choose Persone",Toast.LENGTH_SHORT).show();
                }else if(finalPriority.equals("")){
                    Toast.makeText(AddAssignments.this,"Set Priority",Toast.LENGTH_SHORT).show();
                }else if(dldate.equals("-")){
                    tvDeadLineDate.setError("");
                    tvDeadLineDate.setFocusable(true);
                }else if(dltime.equals("-")){
                    tvDeadlineTime.setError("");
                    tvDeadlineTime.setFocusable(true);
                }else if(detail.equals("")){
                    edtDetails.setError("");
                    edtDetails.setFocusable(true);
                }else {
                    uploadAssignment(USERID,finalPerson,title,detail,dldate,dltime,finalPriority);
                }
            }
        });

        linLDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddAssignments.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    tvDeadLineDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                }

                            }, year, month, day);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
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
                    TimePickerDialog datePickerDialog = new TimePickerDialog(AddAssignments.this,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    tvDeadlineTime.setText(setTimeAmPm(hourOfDay,minute));
                                }
                            },hour,minute,false);
                    datePickerDialog.show();
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }
            }
        });
    }

    public void getList(List<ModelClassOfLeads> lpriority,List<ModelClassOfLeads> lperson){
        List<String> sperson=new ArrayList<>();
        List<String> spriority=new ArrayList<>();
//        sperson.add("Choose Person");
//        spriority.add("Set Priority");
        for (int i=0;i<lperson.size();i++) {
            sperson.add(lperson.get(i).getName());
        }
        for (int i=0;i<lpriority.size();i++) {
            spriority.add(lpriority.get(i).getName());
        }
       dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,sperson);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPerson.setAdapter(dataAdapter);

        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,spriority);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPriority.setAdapter(dataAdapter);
        setClickOfSpin();
    }

    public void setClickOfSpin(){
        spinPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String itemName;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    itemName=parent.getItemAtPosition(position).toString();
                    if(!itemName.equals("Set Priority")){
                        finalPriority=lpriority.get(position).getInqId();
                        Log.i("finalid","group="+finalPriority);
                    }else {
                        finalPriority="";
                        Log.i("finalid","group="+finalPriority);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinPerson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String itemName;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    itemName=parent.getItemAtPosition(position).toString();
                    if(!itemName.equals("Choose Person")){
                        finalPerson=lperson.get(position).getInqId();
                        Log.i("finalid","group="+finalPerson);
                    }else {
                        finalPerson="";
                        Log.i("finalid","group="+finalPerson);
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

    public void GetDropDownList(){
        progressDialog=CheckConnectivity.dialogForProgres(this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN+ServiceUrls.URL_DD_USER_PRIOR, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("asgnmt_periorty");
                    for (int i=0; i<jsonArray.length();i++){
                        modelClassOfLeads=new ModelClassOfLeads();
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        modelClassOfLeads.setName(jsonObject1.getString("priorty_name"));
                        modelClassOfLeads.setInqId(jsonObject1.getString("priorty_id"));
                        lpriority.add(modelClassOfLeads);
                    }
                    jsonArray=jsonObject.getJSONArray("asgnmt_users");
                    for (int i=0; i<jsonArray.length();i++){
                        modelClassOfLeads=new ModelClassOfLeads();
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        modelClassOfLeads.setName(jsonObject1.getString("user_name"));
                        modelClassOfLeads.setInqId(jsonObject1.getString("user_id"));
                        lperson.add(modelClassOfLeads);
                    }
                    getList(lpriority,lperson);
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

    public void uploadAssignment(final String userid, final String assigntoID, final String assigntitle, final String assigndetail, final String dldate, final String dltime, final String priorityID) {
        progressDialog=CheckConnectivity.dialogForProgres(this);
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN + ServiceUrls.URL_UPDATE_ASSIGN, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("assign_update");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String messagea = jsonObject1.getString("message");
                        finish();
                        overridePendingTransition(com.ezcorporate.R.anim.in_left, com.ezcorporate.R.anim.out_right);
                        Toast.makeText(AddAssignments.this,messagea,Toast.LENGTH_SHORT).show();
                        Log.i("message",messagea);
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
                params.put("user_id", userid);
                params.put("assign_to", assigntoID);
                params.put("assign_deadline", dldate);
                params.put("deadline_time", dltime);
                params.put("assign_title",assigntitle);
                params.put("assign_detail",assigndetail);
                params.put("priorty_id",priorityID);
                return params;
            }
        };
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest1);
    }
}
