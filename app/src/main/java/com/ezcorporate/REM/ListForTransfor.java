package com.ezcorporate.REM;

import android.app.Dialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ezcorporate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.Others.ModelClassOfCustomer;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.REM.RecyclerView.RecyclerTransforList;

public class ListForTransfor extends AppCompatActivity {

    String URL_PREFIX_DOMAIN="";
    RecyclerView recyclerViewOfList;
    List<ModelClassOfCustomer> list;
    EditText etTitleSearch;
    TextView tvTitle;
    public static ListForTransfor listForTransfor;
    ModelClassOfCustomer modelClassOfCustomer;
    ImageView ivGoBack,ivSerch,ivSerchCancel;
    Dialog progressDialog;
    RecyclerTransforList recyclerTransforList;
    SwipeRefreshLayout swipeRefreshLayout;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_for_transfor);
        listForTransfor=ListForTransfor.this;
        URL_PREFIX_DOMAIN=SharedPrefManager.getInstance(getApplicationContext()).getUrl();

        init();
        setListener();
        progressDialog=CheckConnectivity.dialogForProgres(this);
        getListForTransfor();
    }

    private void setListener() {
        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();
                overridePendingTransition(R.anim.in_left,R.anim.out_right);
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
        ArrayList<ModelClassOfCustomer> filteredList=new ArrayList<>();
        for(ModelClassOfCustomer  modelClassOfLead:list){
            if(modelClassOfLead.getNameF().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(modelClassOfLead);
            }
        }
        recyclerTransforList.filterList(filteredList);
    }
    private void init() {

        list=new ArrayList<ModelClassOfCustomer>();
        ivGoBack=findViewById(R.id.go_back);
        ivSerch=findViewById(R.id.iv_serch);
        ivSerchCancel=findViewById(R.id.iv_search_cancel);
        ivSerchCancel.setVisibility(View.GONE);
        ivSerch.setVisibility(View.VISIBLE);
        recyclerViewOfList=findViewById(R.id.recycler_list_tronsfer);
        tvTitle=findViewById(R.id.headingOfeditmanger);
        etTitleSearch=findViewById(R.id.et_search);
        etTitleSearch.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("List For Transfer");


        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onRefresh() {
                getListForTransfor();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    public void  setRecycler(List list){
        recyclerTransforList=new RecyclerTransforList(this,list);
        recyclerTransforList.notifyDataSetChanged();
        recyclerViewOfList.setLayoutManager(new LinearLayoutManager(ListForTransfor.this));
        recyclerViewOfList.addItemDecoration(new DividerItemDecoration(recyclerViewOfList.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewOfList.setAdapter(recyclerTransforList);

    }

    public void getListForTransfor(){
        StringRequest stringRequest=new StringRequest(Request.Method.GET, URL_PREFIX_DOMAIN+ServiceUrls.URL_LIST_FOR_TRANSFER, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);

                try {
                    list.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("rem_info");
                    for (int i=0; i<jsonArray.length();i++){
                        modelClassOfCustomer=new ModelClassOfCustomer();
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        modelClassOfCustomer.setAgrementId(jsonObject1.getString("agreement_id"));
                        modelClassOfCustomer.setNameF(jsonObject1.getString("customer_namef"));
                        modelClassOfCustomer.setNameT(jsonObject1.getString("customer_namet"));
                        modelClassOfCustomer.setItemName(jsonObject1.getString("item_complete_name"));
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
        });
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

}
