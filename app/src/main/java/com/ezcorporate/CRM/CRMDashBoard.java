package com.ezcorporate.CRM;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.CRM.Fragments.PagerAdapterooo;
import com.ezcorporate.CRM.RecyclerViewCRM.ModelClassOfLeads;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.CRM.Fragments.PagerAdapterooo;
import com.ezcorporate.CRM.RecyclerViewCRM.ModelClassOfLeads;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;

public class CRMDashBoard extends AppCompatActivity {
    String URL_PREFIX_DOMAIN="";
    String USERID="";
    Context context;
    List<String> listOfTask;
    TabLayout tabLayout;
    ImageView ivGoBack;
    ViewPager viewPager;
    Dialog progressDialog;
    public static boolean countfragment;
    ModelClassOfLeads modelClassOfCustomer;
    List<ModelClassOfLeads> list;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case com.ezcorporate.R.id.lead:
                    startActivity(new Intent(CRMDashBoard.this, LeadsList.class));
                    overridePendingTransition(com.ezcorporate.R.anim.in_right, com.ezcorporate.R.anim.out_left);
                    return true;
                case com.ezcorporate.R.id.opportunity:
                    startActivity(new Intent(CRMDashBoard.this, OpportunityList.class));
                    overridePendingTransition(com.ezcorporate.R.anim.in_right, com.ezcorporate.R.anim.out_left);
                    return true;
                case com.ezcorporate.R.id.holds_list:
                    startActivity(new Intent(CRMDashBoard.this, HoldsList.class));
                    overridePendingTransition(com.ezcorporate.R.anim.in_right, com.ezcorporate.R.anim.out_left);
                    return true;
            }
            return false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ezcorporate.R.layout.activity_crmdash_board);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA
                ,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.CALL_PHONE}, 10);

        URL_PREFIX_DOMAIN=SharedPrefManager.getInstance(getApplicationContext()).getUrl();
        USERID=SharedPrefManager.getInstance(getApplicationContext()).getUId();
        this.context=CRMDashBoard.this;
        Log.i("userid",USERID);
        init();
        setListener();
        try {
            getTasksList();
        }catch (Exception e){
            Log.i("Exception",""+e);
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(com.ezcorporate.R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FloatingActionButton fab = (FloatingActionButton) findViewById(com.ezcorporate.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, Inquery.class));
                overridePendingTransition(com.ezcorporate.R.anim.in_right, com.ezcorporate.R.anim.out_left);
            }
        });
    }

    private void setListener() {
        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(com.ezcorporate.R.anim.in_left, com.ezcorporate.R.anim.out_right);
            }
        });
    }

    public void init() {
        countfragment=true;
        list =new ArrayList<>();
        ivGoBack=findViewById(com.ezcorporate.R.id.go_back);


    }

    public void TabViewPager(){
            tabLayout = (TabLayout)findViewById(com.ezcorporate.R.id.sliding_tabs);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            viewPager = (ViewPager)findViewById(com.ezcorporate.R.id.viewpager);
            final PagerAdapterooo adapter = new PagerAdapterooo(context,getSupportFragmentManager(),list.size(),list);

            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
        for (int i=0;i<list.size();i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(adapter.getTabView(i));
            Log.i("taskname",""+list.get(i).getName());
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                for (int i=0;i<list.size();i++){
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    tab.setCustomView(adapter.getTabView(i));
                    Log.i("taskname",""+list.get(i).getName());
                }
                countfragment=true;
                viewPager.getAdapter().notifyDataSetChanged();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        }



        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getTasksList(){
        progressDialog=CheckConnectivity.dialogForProgres(context);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, URL_PREFIX_DOMAIN+ServiceUrls.URL_DROP_DOWN_LISTS, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    int ii=9;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("inq_task");
                    modelClassOfCustomer=new ModelClassOfLeads();
                    modelClassOfCustomer.setName("Today");
                   // modelClassOfCustomer.setFollowUpId(""+ii);
                    modelClassOfCustomer.setIcons(com.ezcorporate.R.drawable.calendar);
                    list.add(modelClassOfCustomer);
                    for (int i=0; i<jsonArray.length();i++){
                        ii=ii+7;
                        modelClassOfCustomer=new ModelClassOfLeads();
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        modelClassOfCustomer.setInqId(jsonObject1.getString("task_id"));
                        String name=jsonObject1.getString("task_name");
                        modelClassOfCustomer.setName(name);
                        modelClassOfCustomer.setSubject(jsonObject1.getString("task_mode"));
                       // modelClassOfCustomer.setFollowUpId(""+ii);
                        modelClassOfCustomer.setIcons(com.ezcorporate.R.drawable.telephone);
                        if(name.equals("Call")){
                            modelClassOfCustomer.setIcons(com.ezcorporate.R.drawable.telephone);
                        }else if(name.equals("EMail")){
                            modelClassOfCustomer.setIcons(com.ezcorporate.R.drawable.email);
                        }else{
                            modelClassOfCustomer.setIcons(com.ezcorporate.R.drawable.generaltask);
                        }
                        list.add(modelClassOfCustomer);
                    }
                    TabViewPager();
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
        SingletonVolley.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}