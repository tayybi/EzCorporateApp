package com.ezcorporate.CRM.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.CRM.RecyclerViewCRM.ModelClassOfLeads;
import com.ezcorporate.CRM.RecyclerViewCRM.RecyclerTasksList;
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

public class TaskListFragment extends Fragment {
    String URL_PREFIX_DOMAIN="null";
    String USERID="";
    Context context;
    public String TASKID;
    List<ModelClassOfLeads> list;
    ModelClassOfLeads modelClassOfCustomer;
    Dialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    public static FragmentActivity taskListFragment;
    RecyclerView recyclerViewOfList;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        URL_PREFIX_DOMAIN = SharedPrefManager.getInstance(context.getApplicationContext()).getUrl();
        USERID = SharedPrefManager.getInstance(context.getApplicationContext()).getUId();

        if (getArguments() != null) {
            TASKID = getArguments().getString("taskid");
            getTasklists(USERID, TASKID);
            Log.i("taskid", TASKID + "|" + USERID);
        } else {
            Log.i("taskidnull", TASKID + "|" + USERID);
        }
        init();
    }

    private void init() {
        list=new ArrayList<>();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(com.ezcorporate.R.layout.fragment_task_list,null);
        recyclerViewOfList=v.findViewById(com.ezcorporate.R.id.recycler_task_list);
        swipeRefreshLayout=v.findViewById(com.ezcorporate.R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onRefresh() {
                getTasklists(USERID, TASKID);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        taskListFragment=getActivity();
        return v;
        }

    public void  setRecycler(List list){
        RecyclerTasksList recyclerTransforList=new RecyclerTasksList(getContext(),list);
        recyclerTransforList.notifyDataSetChanged();
        recyclerViewOfList.setLayoutManager(new LinearLayoutManager(getContext()));
       // recyclerViewOfList.addItemDecoration(new DividerItemDecoration(recyclerViewOfList.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewOfList.setAdapter(recyclerTransforList);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getTasklists(final String userid, final String taskid){
        progressDialog=CheckConnectivity.dialogForProgres(context);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN+ServiceUrls.URL_LIST_OF_ANY_TASK, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);

                try {
                    list.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("taskd_list");
                    for (int i=0; i<jsonArray.length();i++){
                        modelClassOfCustomer=new ModelClassOfLeads();
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        modelClassOfCustomer.setInqId(jsonObject1.getString("inq_id"));
                        modelClassOfCustomer.setName(jsonObject1.getString("customer_name"));
                        modelClassOfCustomer.setDate(jsonObject1.getString("task_date"));
                        modelClassOfCustomer.setLeadOpp(jsonObject1.getString("isleadopp"));
                        modelClassOfCustomer.setFollowUpId(jsonObject1.getString("flwup_id"));
                        modelClassOfCustomer.setTodo(jsonObject1.getString("to_do"));
                        modelClassOfCustomer.setDayLeft(jsonObject1.getString("days_left"));
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
                params.put("TaskID",taskid);
                return params;
            }
        };
        SingletonVolley.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);

    }
}
