package com.ezcorporate.Authentications;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ezcorporate.Adpters.ExpandableListAdapter;
import com.ezcorporate.Assignments.AssignmentBoard;
import com.ezcorporate.CRM.CRMDashBoard;
import com.ezcorporate.CRM.Inquery;
import com.ezcorporate.Database.AllFields;
import com.ezcorporate.Database.DataBaseHelper;
import com.ezcorporate.DefinationInfoCustomer.CustomerVerification;
import com.ezcorporate.DefinationInfoCustomer.ExistingCustomer;
import com.ezcorporate.REM.ListForTransfor;
import com.ezcorporate.DefinationInfoCustomer.NewCustomer;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ezcorporate.DefinationInfoCustomer.CustomerVerification;
import com.ezcorporate.VirtualCallSystem.PendingLogsList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivDrawerMenu;
    String URL_PREFIX_DOMAIN="";
    String GetUid="";
    String GetUMac="";
    LinearLayout linLogOut;
    DrawerLayout mDrawerLayout;
    protected FrameLayout frameLayout;
    protected LinearLayout linearLayoutDrawer;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    /////Menue expandable
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    DataBaseHelper dataBaseHelper;
    TextView tvUserName;
    Dialog progressDialog;
    Cursor cursorGetMenue;
    Cursor cursorGetSubMenue;
    int index=0;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_custom_drawer);
        Log.i("Activitystates","oncreate");
        URL_PREFIX_DOMAIN=SharedPrefManager.getInstance(getApplicationContext()).getUrl();
        GetUid=SharedPrefManager.getInstance(getApplicationContext()).getUId();
        GetUMac=SharedPrefManager.getInstance(getApplicationContext()).getUMac();

        init();
        try {

            progressDialog=CheckConnectivity.dialogForProgres(MainActivity.this);
            syncMenue(GetUid,GetUMac);
            setListeners();
            drawerSetting();
        }catch (Exception e){
            Log.i("Exception",""+e);
        }

    }

    private void setListeners() {
        ivDrawerMenu.setOnClickListener(this);
        // Listview Group click listener
//
//        // Listview Group expanded listener
//        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//
//            @Override
//            public void onGroupExpand(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        listDataHeader.get(groupPosition) + " Expanded",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // Listview Group collasped listener
//        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//
//            @Override
//            public void onGroupCollapse(int groupPosition) {
//                Toast.makeText(getApplicationContext(),
//                        listDataHeader.get(groupPosition) + " Collapsed",
//                        Toast.LENGTH_SHORT).show();
//
//            }
//        });

//        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v,
//                                        int groupPosition, long id) {
//                // Toast.makeText(getApplicationContext(),
//                // "Group Clicked " + listDataHeader.get(groupPosition),
//                // Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                 //TODO Auto-generated method stub

//                Toast.makeText(getApplicationContext(),listDataHeader.get(groupPosition) + " : " + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                try {
                    onActivities(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

                return false;
            }
        });
        linLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                SharedPrefManager.getInstance(getApplicationContext()).setVCCValue("yes");
//                dataBaseHelper.deleteTable(AllFields.TABLE_OF_MAINMENUE);
//                dataBaseHelper.deleteTable(AllFields.TABLE_OF_SUBMENUE);
//                dataBaseHelper.deleteTable(AllFields.TABLE_DD_GROUP);
//                dataBaseHelper.deleteTable(AllFields.TABLE_DD_CAT);
//                dataBaseHelper.deleteTable(AllFields.TABLE_DD_SUB_CAT);
//                dataBaseHelper.deleteTable(AllFields.TABLE_DD_SOURCE);
//                dataBaseHelper.deleteTable(AllFields.TABLE_DD_TASK);
//                dataBaseHelper.deleteTable(AllFields.TABLE_DD_STAGE);
//                dataBaseHelper.deleteTable(AllFields.TABLE_DD_STKH);
//                dataBaseHelper.deleteTable(AllFields.TABLE_DD_USERS);
                MainActivity.this.deleteDatabase(AllFields.DATABASE_NAME);
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
                overridePendingTransition(R.anim.in_left,R.anim.out_right);
            }
        });
    }

    private void init() {
        dataBaseHelper=new DataBaseHelper(this);
        ivDrawerMenu = findViewById(R.id.ivDrawerIcon);
        frameLayout = (FrameLayout) findViewById(R.id.content_frame);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        linearLayoutDrawer = findViewById(R.id.linearLayoutDrawer);
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        linLogOut=findViewById(R.id.lin_logout);
        tvUserName=findViewById(R.id.tv_user_name);
        tvUserName.setText(SharedPrefManager.getInstance(getApplicationContext()).getUName());
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivDrawerIcon:
                if (!mDrawerLayout.isDrawerOpen(linearLayoutDrawer)) {
                    mDrawerLayout.openDrawer(linearLayoutDrawer);
                }
                break;
        }
    }

    private void drawerSetting() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,      /* host Activity */
                mDrawerLayout,     /* DrawerLayout object */
                R.mipmap.ic_launcher,     /* nav drawer image to replace 'Up' caret */
                R.string.open_drawer,       /* "open drawer" description for accessibility */
                R.string.close_drawer)      /* "close drawer" description for accessibility */ {
            @Override
            public void onDrawerClosed(View drawerView) {
//                getSupportActionBar().setTitle(listArray[position]);
                //Toast.makeText(MainActivity.this,"Closed",Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
              //  Toast.makeText(MainActivity.this,"Open",Toast.LENGTH_SHORT).show();
//                getSupportActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //Toast.makeText(MainActivity.this,"slide",Toast.LENGTH_SHORT).show();
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
             //   Toast.makeText(MainActivity.this,"changed",Toast.LENGTH_SHORT).show();
                super.onDrawerStateChanged(newState);
            }
        };
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(linearLayoutDrawer)) {
            mDrawerLayout.closeDrawer(linearLayoutDrawer);
        } else {
//            mDrawerLayout.openDrawer(linearLayoutDrawer);
            super.onBackPressed();
            finish();
                overridePendingTransition(R.anim.in_left,R.anim.out_right);
        }
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        setMenue();
    }

    public void setMenue()  {
        cursorGetMenue=dataBaseHelper.showTableData(AllFields.TABLE_OF_MAINMENUE);
        cursorGetSubMenue=dataBaseHelper.showTableData(AllFields.TABLE_OF_SUBMENUE);
        if(cursorGetMenue==null){
            Log.i("error", "dbMenueNull");
        }else {
            if (cursorGetMenue.moveToFirst()) {
                do {
                    String m_id=cursorGetMenue.getString(cursorGetMenue.getColumnIndex(AllFields.MM_ID));
                    String m_name=cursorGetMenue.getString(cursorGetMenue.getColumnIndex(AllFields.MM_NAME));
                    listDataHeader.add(m_name);
                    setSubMenue(m_id);
                }while (cursorGetMenue.moveToNext());
            }
        }
    }

    public void setSubMenue(String id){
        List<String> name1   = new ArrayList<String>();
        cursorGetSubMenue=dataBaseHelper.showTableData(AllFields.TABLE_OF_SUBMENUE);
        if(cursorGetSubMenue==null){
            Log.i("error", "dbMenueNull");
        }else {
            if (cursorGetSubMenue.moveToFirst()) {
                do {
                    String msm_id=cursorGetSubMenue.getString(cursorGetSubMenue.getColumnIndex(AllFields.MSM_ID));
                    String sm_id=cursorGetSubMenue.getString(cursorGetSubMenue.getColumnIndex(AllFields.SM_ID));
                    String m_link=cursorGetSubMenue.getString(cursorGetSubMenue.getColumnIndex(AllFields.SM_LINKS));
                    String m_name=cursorGetSubMenue.getString(cursorGetSubMenue.getColumnIndex(AllFields.SM_NAME));
                    if (id.equals(msm_id)){
                        Log.i("idees",id+""+sm_id);
                        name1.add(m_name);
                    }
                }while (cursorGetSubMenue.moveToNext());
                listDataChild.put(listDataHeader.get(index),name1);
                index++;
            }

        }
    }

    public void onActivities(String name){
        Cursor cursor=dataBaseHelper.showTableData(AllFields.TABLE_OF_SUBMENUE);
        if(cursor==null){
            Log.i("error", "dbMenueNull");
        }else {
            if (cursor.moveToFirst()) {
                do {
                    String m_link=cursor.getString(cursor.getColumnIndex(AllFields.SM_LINKS));
                    String m_name=cursor.getString(cursor.getColumnIndex(AllFields.SM_NAME));
                    if (name.equals(m_name)){
                        if(m_link.equals("transfer")) {
                            if(CheckConnectivity.checkInternetConnection(MainActivity.this)){
                                startActivity(new Intent(MainActivity.this, ListForTransfor.class));
                                overridePendingTransition(R.anim.in_right,R.anim.out_left);
                            }else {
                                Toast.makeText(MainActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }else if(m_link.equals("newcust")){
                            startActivity(new Intent(MainActivity.this, NewCustomer.class));
                            overridePendingTransition(R.anim.in_right,R.anim.out_left);
                            break;
                        }
                        else if(m_link.equals("custinfo")){
                            if(CheckConnectivity.checkInternetConnection(MainActivity.this)){
                                startActivity(new Intent(MainActivity.this, ExistingCustomer.class));
                                overridePendingTransition(R.anim.in_right,R.anim.out_left);
                            }else {
                                Toast.makeText(MainActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                            }
                            break;
                        } else if(m_link.equals("verifybio")){
                            if(CheckConnectivity.checkInternetConnection(MainActivity.this)){
                                startActivity(new Intent(MainActivity.this, CustomerVerification.class));
                                overridePendingTransition(R.anim.in_right,R.anim.out_left);
                            }else {
                                Toast.makeText(MainActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }else if(m_link.equals("newinquiry")){
                            startActivity(new Intent(MainActivity.this, Inquery.class));
                            overridePendingTransition(R.anim.in_right,R.anim.out_left);
                            break;
                        }else if(m_link.equals("inquirydb")){
                            if(CheckConnectivity.checkInternetConnection(MainActivity.this)){
                                startActivity(new Intent(MainActivity.this, CRMDashBoard.class));
                                overridePendingTransition(R.anim.in_right,R.anim.out_left);
                            }else {
                                Toast.makeText(MainActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }else if(m_link.equals("virtclcntr")){
                            startActivity(new Intent(MainActivity.this, PendingLogsList.class));
                                overridePendingTransition(R.anim.in_right,R.anim.out_left);
                            break;
                        }
                        else if(m_link.equals("asignmnts")){
                            if(CheckConnectivity.checkInternetConnection(MainActivity.this)){
                                startActivity(new Intent(MainActivity.this, AssignmentBoard.class));
                                overridePendingTransition(R.anim.in_right,R.anim.out_left);
                            }else {
                                Toast.makeText(MainActivity.this,"No Internet Connection",Toast.LENGTH_SHORT).show();
                            }break;
                        }
                    }
                }while (cursor.moveToNext());
            }

        }
    }

    public void syncMenue(final String uid, final String mac){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN+ServiceUrls.URL_MAIN_MENUE, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status=jsonObject.getString("MMenu");
                    Log.i("status", status);
                    if(status!=null){
                        dataBaseHelper.deleteTable(AllFields.TABLE_OF_MAINMENUE);
                        JSONArray jsonArray=jsonObject.getJSONArray("MMenu");
                        for (int i=0; i<jsonArray.length();i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String m_name = jsonObject1.getString("m_name");
                            String m_link = jsonObject1.getString("m_link");
                            String m_icon = jsonObject1.getString("m_icon");
                            String m_id = jsonObject1.getString("menu_id");
                            dataBaseHelper.insertMainMenueDetail(m_id,m_name,m_icon,m_link);
                            Log.i("mmenue", m_id+"/"+m_name+"/"+m_link);
                        }
                        syncSubMenue(GetUid,GetUMac);
                    }else {
                        Log.i("error", "stutus is empty");
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
                params.put("UserID", uid);
                params.put("MacAd", mac);
                return params;
            }
        };
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    public void syncSubMenue(final String uid, final String mac) {
        StringRequest stringRequest11=new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN+ServiceUrls.URL_SUB_MENUE, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status=jsonObject.getString("SMenu");
                    Log.i("status", status);
                    if(status!=null){
                        dataBaseHelper.deleteTable(AllFields.TABLE_OF_SUBMENUE);
                        JSONArray jsonArray=jsonObject.getJSONArray("SMenu");
                        for (int i=0; i<jsonArray.length();i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String m_name = jsonObject1.getString("sm_name");
                            String m_link = jsonObject1.getString("sm_link");
                            String m_icon = jsonObject1.getString("s_icon");
                            String sm_id = jsonObject1.getString("sm_id");
                            String mm_id = jsonObject1.getString("mm_id");
                            dataBaseHelper.insertSubMenueDetail(mm_id,sm_id,m_name,m_icon,m_link);
                            Log.i("smenue", mm_id+"/"+sm_id+"/"+m_name+"/"+m_link);
                        }
                        prepareListData();
                        listAdapter = new ExpandableListAdapter(MainActivity.this, listDataHeader, listDataChild);
                        expListView.setAdapter(listAdapter);

                    }else {
                        Log.i("error", "stutus is empty");
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
                params.put("UserID", uid);
                params.put("MacAd", mac);
                return params;
            }
        };
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest11);

    }

}
