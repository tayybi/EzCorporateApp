package com.ezcorporate.CRM;

import android.annotation.TargetApi;
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

public class HoldsList extends AppCompatActivity {

    String URL_PREFIX_DOMAIN="";
    String USERID="";
    RecyclerView recyclerViewOfList;
    List<ModelClassOfLeads> list;
    ModelClassOfLeads modelClassOfCustomer;
    TextView tvTitle;
    Button btnHLead,btnHOpportunity;
    ImageView ivGoBack,ivSerch,ivSerchCancel;
    EditText etTitleSearch;
    public static HoldsList holdsList;
    DataBaseHelper dataBaseHelper;
    String UrlForLeadOpper;
    String UnholdLeadOppMode;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerLeadsList recyclerTransforList;
    Dialog progressDialog;

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ezcorporate.R.layout.activity_list_for_hold_lead_opp);
        holdsList=HoldsList.this;
        URL_PREFIX_DOMAIN=SharedPrefManager.getInstance(getApplicationContext()).getUrl();
        USERID=SharedPrefManager.getInstance(getApplicationContext()).getUId();

        Log.i("userid",USERID);

        init();
        setListener();
        btnHLead.performClick();
      //  startService(new Intent(HoldsList.this,ServiceForUpdate.class));
    }

    private void setListener() {
        btnHLead.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                try {
                    UrlForLeadOpper=URL_PREFIX_DOMAIN+ServiceUrls.URL_HOLD_LEAADS_LIST;
                    UnholdLeadOppMode="UnHoldLead";
                    btnHLead.setBackground(getDrawable(com.ezcorporate.R.drawable.buttonwhite));
                    btnHOpportunity.setBackgroundColor(getResources().getColor(com.ezcorporate.R.color.colorPrimary));
                    getLeadsList(USERID,UrlForLeadOpper,UnholdLeadOppMode);
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });
        btnHOpportunity.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                try {
                    UrlForLeadOpper=URL_PREFIX_DOMAIN+ServiceUrls.URL_HOLD_OPPORTUNITY_LIST;
                    UnholdLeadOppMode="UnHoldOpp";
                    btnHOpportunity.setBackground(getDrawable(com.ezcorporate.R.drawable.buttonwhite));
                    btnHLead.setBackgroundColor(getResources().getColor(com.ezcorporate.R.color.colorPrimary));
                    getLeadsList(USERID,UrlForLeadOpper,UnholdLeadOppMode);
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }
            }
        });
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
        tvTitle=findViewById(com.ezcorporate.R.id.headingOfeditmanger);
        etTitleSearch=findViewById(com.ezcorporate.R.id.et_search);
        etTitleSearch.setVisibility(View.GONE);
        btnHLead=findViewById(com.ezcorporate.R.id.btn_hold_lead);
        btnHOpportunity=findViewById(com.ezcorporate.R.id.btn_hold_oportunity);
        tvTitle.setVisibility(View.VISIBLE);
        recyclerViewOfList=findViewById(com.ezcorporate.R.id.recycler_list_tronsfer);
        tvTitle.setText("Hold");
        swipeRefreshLayout=findViewById(com.ezcorporate.R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onRefresh() {

                try {
                    getLeadsList(USERID,UrlForLeadOpper,UnholdLeadOppMode);
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

    public void  setRecycler(List list,String holdOrOpp){
        recyclerTransforList=new RecyclerLeadsList(this,list,holdOrOpp);
        recyclerTransforList.notifyDataSetChanged();
        recyclerViewOfList.setLayoutManager(new LinearLayoutManager(HoldsList.this));
        recyclerViewOfList.addItemDecoration(new DividerItemDecoration(recyclerViewOfList.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewOfList.setAdapter(recyclerTransforList);
    }

    public void getLeadsList(final String userid, String Url, final String holdOrOpp){
        progressDialog=CheckConnectivity.dialogForProgres(this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, Url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    list.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("lead_Hold_list");
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
                    setRecycler(list,holdOrOpp);

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
