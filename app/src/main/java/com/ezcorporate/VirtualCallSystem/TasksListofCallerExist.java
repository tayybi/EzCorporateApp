package com.ezcorporate.VirtualCallSystem;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.CRM.CRMDashBoard;
import com.ezcorporate.CRM.RecyclerViewCRM.ModelClassOfLeads;
import com.ezcorporate.CRM.RecyclerViewCRM.RecyclerLeadsList;
import com.ezcorporate.CRM.RecyclerViewCRM.RecyclerTasksList;
import com.ezcorporate.Database.DataBaseHelper;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TasksListofCallerExist extends AppCompatActivity {

    String URL_PREFIX_DOMAIN="";
    String USERID="";
    RecyclerView recyclerViewOfList;
    List<ModelClassOfLeads> list;
    ModelClassOfLeads modelClassOfCustomer;
    TextView tvTitle;
    ImageView ivGoBack,ivSerch,ivSerchCancel;
    EditText etTitleSearch;
    DataBaseHelper dataBaseHelper;
    SwipeRefreshLayout swipeRefreshLayout;
    Dialog progressDialog;
    public static TasksListofCallerExist taskListFragment;
    String phone_no;
    RecyclerTasksList recyclerTasksList;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ezcorporate.R.layout.activity_list_for_transfor);
        taskListFragment=TasksListofCallerExist.this;
        URL_PREFIX_DOMAIN=SharedPrefManager.getInstance(getApplicationContext()).getUrl();
        USERID=SharedPrefManager.getInstance(getApplicationContext()).getUId();
        phone_no    =   getIntent().getExtras().getString("phone_no");
        Log.i("userid",USERID);
        try {
            getTasksList(USERID,phone_no);
        }catch (Exception e){
            Log.i("Exception",""+e);
        }
        init();
        setListener();
    }

    private void setListener() {
        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskListFragment.finish();
                overridePendingTransition(com.ezcorporate.R.anim.in_left, com.ezcorporate.R.anim.out_right);
            }
        });
        ivSerchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskListFragment.finish();
                overridePendingTransition(com.ezcorporate.R.anim.in_left, com.ezcorporate.R.anim.out_right);
            }
        });
    }

    private void init() {
        dataBaseHelper=new DataBaseHelper(this);
        list=new ArrayList<ModelClassOfLeads>();
        ivGoBack=findViewById(com.ezcorporate.R.id.go_back);
        ivSerch=findViewById(com.ezcorporate.R.id.iv_serch);
        ivSerchCancel=findViewById(com.ezcorporate.R.id.iv_search_cancel);
        ivSerchCancel.setVisibility(View.VISIBLE);
        ivSerch.setVisibility(View.GONE);
        tvTitle=findViewById(com.ezcorporate.R.id.headingOfeditmanger);
        etTitleSearch=findViewById(com.ezcorporate.R.id.et_search);
        etTitleSearch.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        recyclerViewOfList=findViewById(com.ezcorporate.R.id.recycler_list_tronsfer);
        tvTitle.setText("Tasks");
        swipeRefreshLayout=findViewById(com.ezcorporate.R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onRefresh() {

                try {
                    getTasksList(USERID,phone_no);
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

    public void  setRecycler(List list){
        recyclerTasksList=new RecyclerTasksList(this,list);
        recyclerTasksList.notifyDataSetChanged();
        recyclerViewOfList.setLayoutManager(new LinearLayoutManager(TasksListofCallerExist.this));
        recyclerViewOfList.addItemDecoration(new DividerItemDecoration(recyclerViewOfList.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewOfList.setAdapter(recyclerTasksList);
    }
    public void getTasksList(final String userid,final String phoneNo){
        progressDialog=CheckConnectivity.dialogForProgres(this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN+ServiceUrls.URL_TASKLIST_OF_EXISTING, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    list.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("call_update");
                    for (int i=0; i<jsonArray.length();i++){
                        modelClassOfCustomer=new ModelClassOfLeads();
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        modelClassOfCustomer.setInqId(jsonObject1.getString("inq_id"));
                        modelClassOfCustomer.setName(jsonObject1.getString("customer_name"));
                        modelClassOfCustomer.setDate(jsonObject1.getString("task_date"));
                        modelClassOfCustomer.setLeadOpp(jsonObject1.getString("isleadopp"));
                        modelClassOfCustomer.setFollowUpId(jsonObject1.getString("flwup_id"));
                        modelClassOfCustomer.setTodo(jsonObject1.getString("to_do"));
                        list.add(modelClassOfCustomer);
                    }
                    setRecycler(list);

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
                params.put("UserID",userid);
                params.put("MobileNo",phoneNo);
                return params;
            }
        };
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

}
