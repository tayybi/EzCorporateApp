package com.ezcorporate.Assignments.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.ezcorporate.Assignments.RecyclerViewAssignment.RecyclerAssigList;
import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.CRM.RecyclerViewCRM.ModelClassOfLeads;
import com.ezcorporate.CRM.RecyclerViewCRM.RecyclerTasksList;
import com.ezcorporate.Others.ModelClassOfAssigment;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListOfAssignments extends Fragment {
    String URL_PREFIX_DOMAIN="null";
    String USERID="";
    Context context;
    public String LISTNAME;
    List<ModelClassOfAssigment> listtodo;
    List<ModelClassOfAssigment> listassigned;
    ModelClassOfAssigment modelClassOfAssigment;
    Dialog progressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    public static FragmentActivity listofAssignment;
    RecyclerView recyclerViewOfList;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        listofAssignment=getActivity();
        URL_PREFIX_DOMAIN = SharedPrefManager.getInstance(context.getApplicationContext()).getUrl();
        USERID = SharedPrefManager.getInstance(context.getApplicationContext()).getUId();
        if (getArguments() != null) {
            LISTNAME = getArguments().getString("name");
            getAssignmentList(USERID);
            Log.i("taskid", LISTNAME + "|" + USERID);
        } else {
            Log.i("taskidnull", LISTNAME + "|" + USERID);
        }
        init();
    }
    private void init() {
        listtodo=new ArrayList<>();
        listassigned=new ArrayList<>();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_task_list,null);
        recyclerViewOfList=v.findViewById(R.id.recycler_task_list);
        swipeRefreshLayout=v.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onRefresh() {
               getAssignmentList(USERID);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        listofAssignment=getActivity();
        return v;
        }
    public void  setRecycler(List list){
        RecyclerAssigList recyclerTransforList=new RecyclerAssigList(getContext(),list,LISTNAME);
        recyclerTransforList.notifyDataSetChanged();
        recyclerViewOfList.setLayoutManager(new LinearLayoutManager(getContext()));
       // recyclerViewOfList.addItemDecoration(new DividerItemDecoration(recyclerViewOfList.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewOfList.setAdapter(recyclerTransforList);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getAssignmentList(final String userid){
        progressDialog=CheckConnectivity.dialogForProgres(context);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN+ServiceUrls.URL_ASSIGN_LIST, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    listtodo.clear();
                    listassigned.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("asgnmt_todo");
                    for (int i=0; i<jsonArray.length();i++){
                        modelClassOfAssigment =new ModelClassOfAssigment();
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        modelClassOfAssigment.setId(jsonObject1.getString("asgnmnt_id"));
                        modelClassOfAssigment.setTitle(jsonObject1.getString("assign_title"));
                        modelClassOfAssigment.setDescrep(jsonObject1.getString("assign_desc"));
                        modelClassOfAssigment.setDeadline(jsonObject1.getString("assign_dead_line"));
                        modelClassOfAssigment.setAssignBy(jsonObject1.getString("assign_assignby"));
                        modelClassOfAssigment.setPriority(jsonObject1.getString("assign_periority"));
                        listtodo.add(modelClassOfAssigment);
                    }
                    jsonArray=jsonObject.getJSONArray("asgnmt_asigned");
                    for (int i=0; i<jsonArray.length();i++){
                        modelClassOfAssigment =new ModelClassOfAssigment();
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        modelClassOfAssigment.setId(jsonObject1.getString("asgnmnt_id"));
                        modelClassOfAssigment.setTitle(jsonObject1.getString("assign_title"));
                        modelClassOfAssigment.setDescrep(jsonObject1.getString("assign_desc"));
                        modelClassOfAssigment.setDeadline(jsonObject1.getString("assign_dead_line"));
                        modelClassOfAssigment.setAssignBy(jsonObject1.getString("assign_assignto"));
                        modelClassOfAssigment.setPriority(jsonObject1.getString("assign_periority"));
                        listassigned.add(modelClassOfAssigment);
                    }
                    if(LISTNAME.equals("ToDo")){
                        Collections.reverse(listtodo);
                        setRecycler(listtodo);
                        Log.i("reyclerrunning","todo is running");
                    }else {
                        Collections.reverse(listassigned);
                        setRecycler(listassigned);
                        Log.i("reyclerrunning","Assigned is running");
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
                params.put("user_id",userid);
                return params;
            }
        };
        SingletonVolley.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
