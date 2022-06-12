package com.ezcorporate.Assignments;
import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import com.ezcorporate.Assignments.Fragments.PagerAdapterAss;
import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.CRM.Fragments.PagerAdapterooo;
import com.ezcorporate.CRM.HoldsList;
import com.ezcorporate.CRM.Inquery;
import com.ezcorporate.CRM.LeadsList;
import com.ezcorporate.CRM.OpportunityList;
import com.ezcorporate.CRM.RecyclerViewCRM.ModelClassOfLeads;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AssignmentBoard extends AppCompatActivity {
    String URL_PREFIX_DOMAIN="";
    String USERID="";
    Context context;
    TabLayout tabLayout;
    ImageView ivGoBack;
    ViewPager viewPager;
    Dialog progressDialog;
    public static AssignmentBoard assignmentBoard;
    public static String TODO="ToDo";
    public static String ALLOCATE="Allocate";
    public static boolean countfragment;
    ModelClassOfLeads modelClassOfCustomer;
    public static boolean isAppRunning;
    List<ModelClassOfLeads> list;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_board);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA
                ,Manifest.permission.SYSTEM_ALERT_WINDOW}, 10);

        URL_PREFIX_DOMAIN=SharedPrefManager.getInstance(getApplicationContext()).getUrl();
        USERID=SharedPrefManager.getInstance(getApplicationContext()).getUId();
        assignmentBoard=AssignmentBoard.this;
        this.context=AssignmentBoard.this;
        Log.i("userid",USERID);
        try{
            init();
            setListener();
            try {
                getFragmentList();
            }catch (Exception e){
                Log.i("Exception",""+e);
            }

            FloatingActionButton fab = (FloatingActionButton) findViewById(com.ezcorporate.R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(context, AddAssignments.class));
                    overridePendingTransition(R.anim.in_right,R.anim.out_left);
                }
            });
        }catch (Exception e){
            Log.i("Exception",""+e);
        }
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
            final PagerAdapterAss adapter = new PagerAdapterAss(context,getSupportFragmentManager(),list);
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

    public void getFragmentList(){

                        modelClassOfCustomer=new ModelClassOfLeads();
                        modelClassOfCustomer.setName(TODO);
                        modelClassOfCustomer.setIcons(R.drawable.todo);
                        list.add(modelClassOfCustomer);

                        modelClassOfCustomer=new ModelClassOfLeads();
                        modelClassOfCustomer.setName(ALLOCATE);
                        modelClassOfCustomer.setIcons(R.drawable.assigned);
                        list.add(modelClassOfCustomer);
                             TabViewPager();
                    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isAppRunning=false;
        finish();
    }
}