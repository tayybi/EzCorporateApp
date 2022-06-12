package com.ezcorporate.Assignments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ezcorporate.Assignments.Fragments.ListOfAssignments;
import com.ezcorporate.Assignments.RecyclerViewAssignment.MessageHistoryAdapter;
import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.Others.ModelClassOfAssigment;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ezcorporate.Assignments.AssignmentBoard.assignmentBoard;
import static com.ezcorporate.Assignments.Fragments.ListOfAssignments.listofAssignment;

public class AssignmentDetail extends AppCompatActivity {
    String URL_PREFIX_DOMAIN="";
    String USERID="";
    ImageView ivGoBack,ivSetting,ivSendMessage;
    EditText edtWritemessage;
    Dialog progressDialog;
    TextView titleofAssign;
    Context context;
    String LISTID;
    Dialog dialog;
    String funcId="";
    ListView lvMsgHistory;
    MessageHistoryAdapter adapter;
    ModelClassOfAssigment modelClassOfAssigment;
    SwipeRefreshLayout swipeRefreshLayout;
    List<ModelClassOfAssigment> listDD,listHistory;
    String dataType;
    String assignName;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_detail_view);
        try {
            LISTID=getIntent().getExtras().getString("INQID");
            dataType=getIntent().getExtras().getString("dataType");
            assignName=getIntent().getExtras().getString("assigname");

            Log.i("myinid",LISTID+"="+assignName);
            URL_PREFIX_DOMAIN=SharedPrefManager.getInstance(getApplicationContext()).getUrl();
            USERID=SharedPrefManager.getInstance(getApplicationContext()).getUId();

            Log.i("myurl",URL_PREFIX_DOMAIN+"/"+USERID);
            init();
            setListener();
            getAssignmentDetail(LISTID);
        }catch (Exception e){
            Log.i("Exception",""+e);
        }

    }

    private void init() {
        listDD=new ArrayList<>();
        listHistory=new ArrayList<>();
        context=this;
        lvMsgHistory=findViewById(R.id.lv_history);
        ivGoBack = findViewById(com.ezcorporate.R.id.go_back);
        ivSendMessage = findViewById(R.id.iv_send_reassign);
        edtWritemessage = findViewById(R.id.edt_write_message);
        titleofAssign = findViewById(com.ezcorporate.R.id.headingOfeditmanger);
        titleofAssign.setText(assignName);
        ivSetting = findViewById(com.ezcorporate.R.id.iv_setting);
    if (dataType.equals(AssignmentBoard.TODO)) {
        ivSetting.setVisibility(View.GONE);
        }
        swipeRefreshLayout=findViewById(com.ezcorporate.R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onRefresh() {

                try {
                    getAssignmentDetail(LISTID);
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

//basic info

    }

    private void setListener() {

        ivGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(com.ezcorporate.R.anim.in_left, com.ezcorporate.R.anim.out_right);
            }
        });
        ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String meg=edtWritemessage.getText().toString();
                if(!meg.equals("")){
                    updateAssignFunc(USERID,LISTID,"HistoryUpdate",meg);
                }
            }
        });
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {


                    final PopupMenu popup = new PopupMenu(context, ivSetting);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.assignmentmenue);

                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.lm_complete_user:
                                    getFunctionalityList("CompleteAssignmt");
                                    return true;
                                case R.id.lm_assign_new:
                                    getFunctionalityList("UserAssign");
                                    return true;
                                case R.id.lm_change_periority:
                                    getFunctionalityList("ChangePeriority");
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();

                }
        });

        lvMsgHistory.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0);
            }
        });
    }

    public void getAssignmentDetail(final String inqId) {
        progressDialog=CheckConnectivity.dialogForProgres(this);
        final StringRequest stringRequest1 = new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN + ServiceUrls.URL_ASSIGN_HISTORY, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    listHistory.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("asgnmt_history");
                    for (int i=0; i<jsonArray.length();i++){
                        modelClassOfAssigment=new ModelClassOfAssigment();
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        modelClassOfAssigment.setDescrep(jsonObject1.getString("assign_comments"));
                        modelClassOfAssigment.setDeadline(jsonObject1.getString("assign_aomdate"));
                        modelClassOfAssigment.setName(jsonObject1.getString("assign_hcmby"));
                        listHistory.add(modelClassOfAssigment);
                    }
                    setadapterData(listHistory);
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
                Log.i("error", "" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("assignmt_id",inqId );
                return params;
            }
        };
        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest1);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updateAssignFunc(final String userid, final String assignId, final String assignFuncMode, final String funcModeId) {
        progressDialog=CheckConnectivity.dialogForProgres(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN + ServiceUrls.URL_ASSIGN_UPDATE_LIST_FUNC, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("assgnmt_update");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String msg = jsonObject1.getString("message");
                        if (!(msg == null)) {
                            if(assignFuncMode.equals("CompleteAssignmt")){
                                listofAssignment.finish();
                                startActivity(new Intent(context,AssignmentBoard.class));
                                finish();
                                overridePendingTransition(com.ezcorporate.R.anim.in_left, com.ezcorporate.R.anim.out_right);
                                dialog.dismiss();
                                dialog.cancel();
                            }
                            if(assignFuncMode.equals("HistoryUpdate")){
                                edtWritemessage.setText("");
                                modelClassOfAssigment=new ModelClassOfAssigment();
                                modelClassOfAssigment.setDescrep(funcModeId);
                                modelClassOfAssigment.setDeadline(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                                modelClassOfAssigment.setName(SharedPrefManager.getInstance(context).getUName());
                                listHistory.add(modelClassOfAssigment);
                                //adapter.notifyDataSetChanged();
                                setadapterData(listHistory);
                            }else if(assignFuncMode.equals("UserAssign")||assignFuncMode.equals("ChangePeriority")){
                                dialog.dismiss();
                                dialog.cancel();
                            }


                            Log.i("updateFunctionality", jsonObject1.getString("message"));
                        }

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
                Log.i("error", ""+ error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userid);
                params.put("asgnmt_id", assignId);
                params.put("asgnmt_func_mode", assignFuncMode);
                params.put("process_id", funcModeId);
                return params;
            }
        };
        SingletonVolley.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getFunctionalityList(final String type){
        progressDialog=CheckConnectivity.dialogForProgres(context);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN+ServiceUrls.URL_DD_USER_PRIOR, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    listDD.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray;
                    if(type.equals("CompleteAssignmt")) {
                        jsonArray=jsonObject.getJSONArray("asgnmt_completin");
                        for (int i=0; i<jsonArray.length();i++){
                            modelClassOfAssigment=new ModelClassOfAssigment();
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            modelClassOfAssigment.setName(jsonObject1.getString("compltin_name"));
                            modelClassOfAssigment.setId(jsonObject1.getString("compltin_id"));
                            listDD.add(modelClassOfAssigment);
                        }
                        showDialogForsetting(type);
                    }else if(type.equals("UserAssign")){
                        jsonArray=jsonObject.getJSONArray("asgnmt_users");
                        for (int i=0; i<jsonArray.length();i++){
                            modelClassOfAssigment=new ModelClassOfAssigment();
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            modelClassOfAssigment.setName(jsonObject1.getString("user_name"));
                            modelClassOfAssigment.setId(jsonObject1.getString("user_id"));
                            listDD.add(modelClassOfAssigment);
                        }
                        showDialogForsetting(type);
                    }
                    else if(type.equals("ChangePeriority")){
                        jsonArray=jsonObject.getJSONArray("asgnmt_periorty");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            modelClassOfAssigment = new ModelClassOfAssigment();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            modelClassOfAssigment.setName(jsonObject1.getString("priorty_name"));
                            modelClassOfAssigment.setId(jsonObject1.getString("priorty_id"));
                            listDD.add(modelClassOfAssigment);
                        }
                        showDialogForsetting(type);

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
        });
        SingletonVolley.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);

    }

    public void showDialogForsetting(final String s){
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(com.ezcorporate.R.layout.custom_listview_for_dialog);
        final ListView listView;
        TextView textView;
        final Button button;
        listView=dialog.findViewById(com.ezcorporate.R.id.function_list);
        button=dialog.findViewById(com.ezcorporate.R.id.function_ok);
        textView=dialog.findViewById(com.ezcorporate.R.id.function_title);
        textView.setText(s);
        if(!listDD.isEmpty()){
            List<String> stringArray=new ArrayList<>();
            for (int i = 0; i< listDD.size(); i++){
                stringArray.add(listDD.get(i).getName());
                Log.i("namelist",""+ listDD.get(i).getName());
            }

            final ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
            listView.setAdapter(modeAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String namee=parent.getItemAtPosition(position).toString();
                    for (int j = 0; j< listDD.size(); j++){
                        if (namee.equals(listDD.get(j).getName())){
                            funcId = listDD.get(j).getId();
                            Log.i("idoflist","function="+funcId);
                            break;
                        }
                    }
                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    if(!funcId.equals("")){

                        updateAssignFunc(USERID,LISTID,s,funcId);
                        Log.i("updtedata",USERID+"|"+LISTID+"|"+s+"|"+funcId);
                    }else{
                        Toast.makeText(context,"Choose Option",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();

        }else {
            Toast.makeText(context,"Empty",Toast.LENGTH_SHORT).show();
        }
    }

    public void setadapterData(List<ModelClassOfAssigment> list){
        adapter=new MessageHistoryAdapter(this, list);
        lvMsgHistory.setAdapter(adapter);
    }

}
