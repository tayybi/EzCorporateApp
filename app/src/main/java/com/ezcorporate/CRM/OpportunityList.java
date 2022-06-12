package com.ezcorporate.CRM;

import android.app.Dialog;
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
import com.ezcorporate.CRM.RecyclerViewCRM.ModelClassOfLeads;
import com.ezcorporate.CRM.RecyclerViewCRM.RecyclerLeadsList;
import com.ezcorporate.Database.DataBaseHelper;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.CRM.RecyclerViewCRM.ModelClassOfLeads;
import com.ezcorporate.CRM.RecyclerViewCRM.RecyclerLeadsList;
import com.ezcorporate.Database.DataBaseHelper;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;

public class OpportunityList extends AppCompatActivity {

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
    RecyclerLeadsList recyclerTransforList;
    public static OpportunityList opportunityList;
    Dialog progressDialog;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ezcorporate.R.layout.activity_list_for_transfor);
        opportunityList=OpportunityList.this;

        URL_PREFIX_DOMAIN=SharedPrefManager.getInstance(getApplicationContext()).getUrl();
        USERID=SharedPrefManager.getInstance(getApplicationContext()).getUId();

        Log.i("userid",USERID);
        init();
        progressDialog=CheckConnectivity.dialogForProgres(this);
        getLeadsList(USERID);
        setListener();
    }

    private void setListener() {
        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();
             overridePendingTransition(com.ezcorporate.R.anim.in_left, com.ezcorporate.R.anim.out_right);
            }
        });
        ivSerch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivSerch.setVisibility(View.GONE);
                ivSerchCancel.setVisibility(View.VISIBLE);
                tvTitle.setVisibility(View.GONE);
                etTitleSearch.setVisibility(View.VISIBLE);
            }
        });
        ivSerchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               etTitleSearch.setText("");
               etTitleSearch.setHint("Type Name");
            }
        });
        etTitleSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    public void filter(String text){
        ArrayList<ModelClassOfLeads> filteredList=new ArrayList<>();
        for(ModelClassOfLeads  modelClassOfLead:list){
            if(modelClassOfLead.getSubject().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(modelClassOfLead);
            }
        }
        recyclerTransforList.filterList(filteredList);
    }
    private void init() {

        dataBaseHelper=new DataBaseHelper(this);
        list=new ArrayList<ModelClassOfLeads>();
        ivGoBack=findViewById(com.ezcorporate.R.id.go_back);
        ivSerch=findViewById(com.ezcorporate.R.id.iv_serch);
        ivSerchCancel=findViewById(com.ezcorporate.R.id.iv_search_cancel);
        ivSerchCancel.setVisibility(View.GONE);
        ivSerch.setVisibility(View.VISIBLE);
        recyclerViewOfList=findViewById(com.ezcorporate.R.id.recycler_list_tronsfer);
        tvTitle=findViewById(com.ezcorporate.R.id.headingOfeditmanger);
        etTitleSearch=findViewById(com.ezcorporate.R.id.et_search);
        etTitleSearch.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("Opportunity");

        swipeRefreshLayout=findViewById(com.ezcorporate.R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onRefresh() {

                try {
                    getLeadsList(USERID);
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
        recyclerTransforList=new RecyclerLeadsList(this,list,"opportunity");
        recyclerTransforList.notifyDataSetChanged();
        recyclerViewOfList.setLayoutManager(new LinearLayoutManager(OpportunityList.this));
        recyclerViewOfList.addItemDecoration(new DividerItemDecoration(recyclerViewOfList.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewOfList.setAdapter(recyclerTransforList);
    }

    public void getLeadsList(final String userid){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN+ServiceUrls.URL_OPPORTUNITY_LIST, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);

                try {
                    list.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("lead_list");
                    for (int i=0; i<jsonArray.length();i++){
                        modelClassOfCustomer=new ModelClassOfLeads();
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        modelClassOfCustomer.setInqId(jsonObject1.getString("inq_id"));
                        modelClassOfCustomer.setName(jsonObject1.getString("customer_name"));
                        modelClassOfCustomer.setGroupName(jsonObject1.getString("group_name"));
                        modelClassOfCustomer.setSubject(jsonObject1.getString("inq_subj"));
                        modelClassOfCustomer.setDate(jsonObject1.getString("inq_date"));
                        modelClassOfCustomer.setMobileNo(jsonObject1.getString("mobile_no"));
                        list.add(modelClassOfCustomer);
                    }
                    Collections.reverse(list);
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
                return params;
            }
        };
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }


}
