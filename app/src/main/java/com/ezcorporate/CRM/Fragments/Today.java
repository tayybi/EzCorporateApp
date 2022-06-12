package com.ezcorporate.CRM.Fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

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
import com.ezcorporate.CRM.RecyclerViewCRM.RecyclerTasksList;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;

/**
 * Created by Inovedia on 1/8/2017.
 */

public class Today extends Fragment {

    private TextView mTextMessage;
    PieChart pieTotalOpp, pieTotalLead,pieTotalTomarrow,pieTotalPending;
    String USERID="";
    RecyclerView recTodayTask,recPendingTask;
    String URL_PREFIX_DOMAIN="null";
    Context context;
    LinearLayout linDDpending,linDDtodaytask;
    ModelClassOfLeads modelClassOfLeads;
    ImageView ivDDpending,ivDDtoday;
    List<ModelClassOfLeads> listpending;
    SwipeRefreshLayout swipeRefreshLayout;
    List<ModelClassOfLeads> listtoday;
    String totalLeads,totalOpportunity,totalTomorrowTask,totalpendingTask;
    Dialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        URL_PREFIX_DOMAIN = SharedPrefManager.getInstance(context.getApplicationContext()).getUrl();
        USERID = SharedPrefManager.getInstance(context.getApplicationContext()).getUId();

            getTodayData(USERID);
            Log.i("taskid",  USERID);
            init();
    }

    boolean today=true;
    boolean pend=true;
    private void setlistener()  {
        linDDtodaytask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(!listtoday.isEmpty()) {
                        if(today) {
                            recTodayTask.setVisibility(View.VISIBLE);
                            ivDDtoday.setImageBitmap(BitmapFactory.decodeResource(getResources(), com.ezcorporate.R.drawable.arrow_up));
                            today = false;
                        }else{
                                recTodayTask.setVisibility(View.GONE);
                                ivDDtoday.setImageBitmap(BitmapFactory.decodeResource(getResources(), com.ezcorporate.R.drawable.arrow_down));
                                today=true;
                            }
                    } else {
                        recTodayTask.setVisibility(View.GONE);
                        ivDDtoday.setImageBitmap(BitmapFactory.decodeResource(getResources(), com.ezcorporate.R.drawable.arrow_down));
                        Toast.makeText(context, "No Task Today", Toast.LENGTH_SHORT).show();
                }
            }
        });
        linDDpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!listpending.isEmpty()) {
                    if(pend) {
                        recPendingTask.setVisibility(View.VISIBLE);
                        ivDDpending.setImageBitmap(BitmapFactory.decodeResource(getResources(), com.ezcorporate.R.drawable.arrow_up));
                        pend = false;
                    }else{
                        recPendingTask.setVisibility(View.GONE);
                        ivDDpending.setImageBitmap(BitmapFactory.decodeResource(getResources(), com.ezcorporate.R.drawable.arrow_down));
                        pend=true;
                    }
                } else {
                    recPendingTask.setVisibility(View.GONE);
                    ivDDpending.setImageBitmap(BitmapFactory.decodeResource(getResources(), com.ezcorporate.R.drawable.arrow_down));
                    Toast.makeText(context, "No Task Pending", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        listpending=new ArrayList<>();
        listtoday=new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(com.ezcorporate.R.layout.today,null);
        pieTotalLead = v.findViewById(com.ezcorporate.R.id.chart_total_leads);
        pieTotalOpp = v.findViewById(com.ezcorporate.R.id.chart_total_opportunities);
        pieTotalPending = v.findViewById(com.ezcorporate.R.id.chart_total_pending);
        pieTotalTomarrow = v.findViewById(com.ezcorporate.R.id.chart_total_tomarrows);
        recTodayTask=v.findViewById(com.ezcorporate.R.id.rec_today_task);
        recPendingTask=v.findViewById(com.ezcorporate.R.id.rec_pend_task);
        linDDpending=v.findViewById(com.ezcorporate.R.id.lin_dd_pending_task);
        linDDtodaytask=v.findViewById(com.ezcorporate.R.id.lin_dd_today_task);
        ivDDpending=v.findViewById(com.ezcorporate.R.id.iv_dd_pending);
        ivDDtoday=v.findViewById(com.ezcorporate.R.id.iv_dd_today);

        swipeRefreshLayout=v.findViewById(com.ezcorporate.R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onRefresh() {
                getTodayData(USERID);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        TaskListFragment.taskListFragment=getActivity();

        setlistener();

        return  v;
    }

    public void  setTodayRecycler(List list){
        RecyclerTasksList recyclerTransforList=new RecyclerTasksList(getContext(),list);
        recyclerTransforList.notifyDataSetChanged();
        recTodayTask.setLayoutManager(new LinearLayoutManager(getContext()));
        recTodayTask.addItemDecoration(new DividerItemDecoration(recTodayTask.getContext(), DividerItemDecoration.VERTICAL));
        recTodayTask.setAdapter(recyclerTransforList);
    }

    public void  setPendingRecycler(List list){
        RecyclerTasksList recyclerTransforList=new RecyclerTasksList(getContext(),list);
        recyclerTransforList.notifyDataSetChanged();
        recPendingTask.setLayoutManager(new LinearLayoutManager(getContext()));
        recPendingTask.addItemDecoration(new DividerItemDecoration(recPendingTask.getContext(), DividerItemDecoration.VERTICAL));
        recPendingTask.setAdapter(recyclerTransforList);
    }

    public void mmpieChartTotalPending(String totalno,PieChart pieTotalPending,String name) {
        ArrayList<Integer> color1=new ArrayList<>();
        color1.add(com.ezcorporate.R.color.colorPrimaryDark);
        color1.add(com.ezcorporate.R.color.colorPrimary);

        pieTotalPending.setUsePercentValues(true);
        pieTotalPending.getDescription().setEnabled(false);
        pieTotalPending.setExtraOffsets(5,10,5,5);
        pieTotalPending.setDragDecelerationFrictionCoef(0.9f);
        pieTotalPending.animateY(1000, Easing.EasingOption.EaseInOutCubic);
        pieTotalPending.setTransparentCircleRadius(61f);
        pieTotalPending.setCenterText(name+"\n\n"+totalno);
        pieTotalPending.setCenterTextColor(com.ezcorporate.R.color.colorPrimaryDark);
        pieTotalPending.setHoleRadius(80f);
        pieTotalPending.setCenterTextSize(10);
        pieTotalPending.setHoleColor(Color.WHITE);
        pieTotalPending.setDrawSliceText(false);
        pieTotalPending.setDrawEntryLabels(false);
        pieTotalPending.getLegend().setEnabled(false);
        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(Float.parseFloat(totalno)));
        yValues.add(new PieEntry(Float.parseFloat(totalno)-100));
        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setDrawValues(false);
        dataSet.setDrawIcons(true);
        dataSet.setSelectionShift(3f);
        dataSet.setSliceSpace(3f);
        dataSet.setColors(color1);
        PieData pieData = new PieData((dataSet));
        pieTotalPending.setData(pieData);
    }


    public void pieChartTotalPending(String totalno,PieChart pieTotalPending,String name,int totaloutof) {

        ArrayList<Integer> color1=new ArrayList<>();
        color1.add(Color.parseColor("#007408"));
        color1.add(Color.parseColor("#8fa93b"));

        pieTotalPending.setUsePercentValues(false);
        pieTotalPending.getDescription().setEnabled(false);
        pieTotalPending.setExtraOffsets(5,10,5,5);
        pieTotalPending.setDragDecelerationFrictionCoef(0.9f);
        pieTotalPending.setTransparentCircleRadius(61f);
        pieTotalPending.setCenterText(name+"\n\n"+totalno+"/"+totaloutof);
        pieTotalPending.setCenterTextColor(Color.parseColor("#5e6b26"));
        pieTotalPending.animateY(1000, Easing.EasingOption.EaseInOutCubic);
        pieTotalPending.setHoleRadius(80f);
        pieTotalPending.setHoleColor(Color.parseColor("#ffedd3"));
        pieTotalPending.setDrawSliceText(false);
        pieTotalPending.setCenterTextSize(10);
        pieTotalPending.setDrawEntryLabels(false);
        pieTotalPending.getLegend().setEnabled(false);
//        Legend l = pieTotalPending.getLegend();
//        l.setEnabled(true);
//        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
//        l.setXEntrySpace(2f);
//        l.setYEntrySpace(5f);
//        l.setYOffset(0f);
        ArrayList<PieEntry> yValues = new ArrayList<>();
        int a=Integer.parseInt(totalno);
        int b=totaloutof-a;
        Log.i("values",a+"|"+totaloutof);
        yValues.add(new PieEntry(a,"Total Leads"));
        yValues.add(new PieEntry(b,"Total Leads"));

        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setColors(color1);
        dataSet.setDrawValues(false);
        dataSet.setSelectionShift(3f);
        dataSet.setSliceSpace(1f);
        PieData pieData = new PieData((dataSet));
        pieTotalPending.setData(pieData);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getTodayData(final String userid){
        progressDialog=CheckConnectivity.dialogForProgres(context);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN+ServiceUrls.URL_TODAY_SUM_LIST, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);

                try {
                    listtoday.clear();
                    listpending.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("tday_lead");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        totalLeads=jsonObject1.getString("lead_count");
                    }
                    Log.i("totallead",totalLeads);
                    jsonArray=jsonObject.getJSONArray("tday_opprt");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        totalOpportunity=jsonObject1.getString("opp_count");
                    }
                    jsonArray=jsonObject.getJSONArray("tday_tomr_taskt");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        totalTomorrowTask=jsonObject1.getString("tomtask_count");
                    }
                    jsonArray=jsonObject.getJSONArray("tday_pend_taskt");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        totalpendingTask=jsonObject1.getString("pendtask_count");
                    }
                    jsonArray=jsonObject.getJSONArray("tday_pend_task");
                    for (int i=0; i<jsonArray.length();i++){
                        modelClassOfLeads=new ModelClassOfLeads();
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        modelClassOfLeads.setInqId(jsonObject1.getString("inq_id"));
                        modelClassOfLeads.setName(jsonObject1.getString("customer_name"));
                        modelClassOfLeads.setDate(jsonObject1.getString("task_date"));
                        modelClassOfLeads.setLeadOpp(jsonObject1.getString("isleadopp"));
                        modelClassOfLeads.setFollowUpId(jsonObject1.getString("flwup_id"));
                        modelClassOfLeads.setTodo(jsonObject1.getString("to_do"));
                        //modelClassOfLeads.setDayLeft(jsonObject1.getString("days_left"));
                        listpending.add(modelClassOfLeads);
                    }
                    Collections.reverse(listpending);
                    setPendingRecycler(listpending);
                    linDDpending.performClick();
                    jsonArray=jsonObject.getJSONArray("tday_today_tsk");
                    for (int i=0; i<jsonArray.length();i++){
                        modelClassOfLeads=new ModelClassOfLeads();
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        modelClassOfLeads.setInqId(jsonObject1.getString("inq_id"));
                        modelClassOfLeads.setName(jsonObject1.getString("customer_name"));
                        modelClassOfLeads.setDate(jsonObject1.getString("task_date"));
                        modelClassOfLeads.setLeadOpp(jsonObject1.getString("isleadopp"));
                        modelClassOfLeads.setFollowUpId(jsonObject1.getString("flwup_id"));
                        modelClassOfLeads.setTodo(jsonObject1.getString("to_do"));
                        //modelClassOfLeads.setDayLeft(jsonObject1.getString("days_left"));
                        listtoday.add(modelClassOfLeads);
                    }
                    Collections.reverse(listtoday);
                    setTodayRecycler(listtoday);
                    linDDtodaytask.performClick();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int totalLeadOpp=Integer.parseInt(totalLeads)+Integer.parseInt(totalOpportunity);
                int totalPendTomor=Integer.parseInt(totalpendingTask)+Integer.parseInt(totalTomorrowTask);
                pieChartTotalPending(totalLeads,pieTotalLead,"LEADS",totalLeadOpp);
                pieChartTotalPending(totalOpportunity,pieTotalOpp,"OPPORTUNITIES",totalLeadOpp);
                pieChartTotalPending(totalpendingTask,pieTotalPending,"PENDING TASKS",totalPendTomor);
                pieChartTotalPending(totalTomorrowTask,pieTotalTomarrow,"TOMORROW TASKS",totalPendTomor);
                progressDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onErrorResponse(VolleyError error) {
                Activity activity = getActivity();
                if(activity != null && isAdded())
                    progressDialog.dismiss();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(activity, "No enternet", Toast.LENGTH_LONG).show();
                }
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
        SingletonVolley.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);

    }




}
