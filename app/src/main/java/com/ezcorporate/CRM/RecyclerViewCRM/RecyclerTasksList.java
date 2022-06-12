package com.ezcorporate.CRM.RecyclerViewCRM;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.CRM.Fragments.TaskListFragment;
import com.ezcorporate.CRM.InqueryDetailView;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;

import static com.ezcorporate.CRM.Fragments.TaskListFragment.taskListFragment;
import static com.ezcorporate.CRM.HoldsList.holdsList;
import static com.ezcorporate.CRM.LeadsList.leadlist;
import static com.ezcorporate.CRM.OpportunityList.opportunityList;

public class RecyclerTasksList extends RecyclerView.Adapter<RecyclerTasksList.TransferViewHolde> {

    public List<ModelClassOfLeads> list;
    String URL_PREFIX_DOMAIN;
    String USERID;
    Dialog progressDialog;
    Dialog dialog;
    String dataType;
    String LISTID,FOLLOWUPID;
    List<ModelClassOfLeads> leadsOption;
    ModelClassOfLeads modelClassOfLeads;
    Context context;
    String finalTaskid;
    int indexes;
    String funcId;

    public RecyclerTasksList(Context context, List<ModelClassOfLeads> list) {
        this.context = context;
        this.list = list;
        URL_PREFIX_DOMAIN=SharedPrefManager.getInstance(context).getUrl();
        USERID=SharedPrefManager.getInstance(context).getUId();

        leadsOption = new ArrayList<>();
    }

    @NonNull
    @Override
    public TransferViewHolde onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View mView = inflater.inflate(R.layout.custom_recyler_task_list, parent, false);
        return new TransferViewHolde(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TransferViewHolde holder, final int position) {
        holder.tvName.setText(list.get(position).getName());
        holder.tvDayLeft.setText(list.get(position).getDayLeft());
        holder.tvDate.setText(list.get(position).getDate());
        holder.tvLeadOpp.setText(list.get(position).getLeadOpp());
        holder.tvToDo.setText("ToDo:  "+list.get(position).getTodo());
        // if(list.get(position).getDate()>)
      //  holder.linColor.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));

        holder.linSetClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pif = list.get(position).getInqId();
                FOLLOWUPID = list.get(position).getFollowUpId();
                Intent intent =new Intent(context,InqueryDetailView.class);
                intent.putExtra("INQID",pif);
                intent.putExtra("FOLLOWUPID",FOLLOWUPID);
                intent.putExtra("dataType",list.get(position).getLeadOpp());
                context.startActivity(intent);
                taskListFragment.overridePendingTransition(R.anim.in_right, R.anim.out_left);
            }
        });
        holder.ivSetting.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                getTasksList();
                LISTID = list.get(position).getInqId();
                indexes=position;
                FOLLOWUPID = list.get(position).getFollowUpId();
                Log.i("idoflist", "LEADID=" + LISTID);
                //creating a popup menu

                final PopupMenu popup = new PopupMenu(context, holder.ivSetting);
                //inflating menu from xml resource
                popup.inflate(R.menu.taskmenue);

                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.task_update:
                                showDialogForUpdateTask(list.get(position).getName(),"UpdateTask");
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();
                /////

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TransferViewHolde extends RecyclerView.ViewHolder {

        TextView tvName, tvDate, tvLeadOpp, tvDayLeft, tvToDo;
        ImageView ivSetting;
        LinearLayout linSetClick,linColor;

        public TransferViewHolde(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvLeadOpp = itemView.findViewById(R.id.tv_led_or_opp);
            tvDayLeft = itemView.findViewById(R.id.tv_dayleft);
            tvToDo = itemView.findViewById(R.id.tv_todo);
            ivSetting = itemView.findViewById(R.id.iv_setting_dots);
            linSetClick = itemView.findViewById(R.id.lin_set_click);
            linColor = itemView.findViewById(R.id.lin_color);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void showDialogForUpdateTask(final String name, final String type) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_task_update_new);
        final TextView tvUpdateTime,tvUpdateDate,tvNextTime,tvNextDate,tvCancel,tvDone,tvTitle,tvName;
        final EditText etComment,etTodo;
        final int year,month,day,hour,minute;
        Calendar calendar;
        Spinner spinTask;
        LinearLayout linUpdateTime,linUpdateDate,linNextTime,linNextDate,linUpdate;
        spinTask=dialog.findViewById(R.id.sp_task);
        linNextDate=dialog.findViewById(R.id.lin_ndate);
        linNextTime=dialog.findViewById(R.id.lin_ntime);
        linUpdate=dialog.findViewById(R.id.lin_update);
        linUpdateDate=dialog.findViewById(R.id.lin_update_date);
        linUpdateTime=dialog.findViewById(R.id.lin_update_time);
        etComment=dialog.findViewById(R.id.et_comments);
        etTodo=dialog.findViewById(R.id.et_todo);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour=calendar.get(Calendar.HOUR_OF_DAY);
        minute=calendar.get(Calendar.MINUTE);

        tvUpdateDate=dialog.findViewById(R.id.tv_update_date);
        tvUpdateTime=dialog.findViewById(R.id.tv_update_time);
        tvNextDate=dialog.findViewById(R.id.tv_ndate);
        tvNextTime=dialog.findViewById(R.id.tv_ntime);
        tvUpdateTime.setText(setTimeAmPm(hour,minute));
        tvUpdateDate.setText(day + "-" + (month + 1) + "-" + year);
        tvTitle=dialog.findViewById(R.id.tv_title);
        tvName=dialog.findViewById(R.id.tv_name);
        tvName.setText(name);
        tvDone=dialog.findViewById(R.id.tv_done);
        tvCancel=dialog.findViewById(R.id.tv_cancel);

       if(type.equals("NewTask")){
           linUpdate.setVisibility(View.GONE);
           tvTitle.setText(type);

       }else if(type.equals("UpdateTask")){
           linUpdate.setVisibility(View.VISIBLE);
           tvTitle.setText(type);
       }
            List<String> stringArray = new ArrayList<>();
            stringArray.add("Choose Task");
            for (int i = 0; i < leadsOption.size(); i++) {
                stringArray.add(leadsOption.get(i).getName());
                Log.i("namelist", "" + leadsOption.get(i).getName());
            }

            final ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, stringArray);
            modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinTask.setAdapter(modeAdapter);
            spinTask.setSelection(0);
            spinTask.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String itemName;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try{
                    itemName=parent.getItemAtPosition(position).toString();
                    if(!itemName.equals("Choose Task")){
                        for (int i=0;i<leadsOption.size();i++){
                            if(itemName.equals(leadsOption.get(i).getName())){
                                finalTaskid=leadsOption.get(i).getInqId();
                                Log.i("taskid",finalTaskid);
                                break;
                            }
                        }
                    }else {
                        finalTaskid="";
                        Log.i("taskid",finalTaskid);
                    }
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
            tvDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String todoo=etTodo.getText().toString();
                    String followNextdate=tvNextDate.getText().toString();
                    String followNexttime=tvNextTime.getText().toString();
                    String commentoo=etComment.getText().toString();
                    String updatedatee=tvUpdateDate.getText().toString();
                    String updateTime=tvUpdateTime.getText().toString();

                    if(commentoo.equals("")){
                        etComment.setError("");
                        etComment.setFocusable(true);
                    }else if(todoo.equals("")){
                        todoo="nill";
                        String UpdatedateTime=updatedatee+" "+updateTime;
                        updteTasks(USERID,LISTID,finalTaskid,type,todoo,commentoo,FOLLOWUPID,"",UpdatedateTime);
                        Log.i("updtedata", USERID + "|" + LISTID + "|" +finalTaskid+ "|" +type +"|" + todoo+"|"+commentoo+"|"+FOLLOWUPID+"|"+UpdatedateTime);
                    }else if(!todoo.equals("nill")){
                        if(finalTaskid.equals("")){
                            Toast.makeText(context,"Choose Task",Toast.LENGTH_SHORT).show();
                        }else if(followNextdate.equals("")){
                            Toast.makeText(context,"Set Next DATE/TIME",Toast.LENGTH_SHORT).show();
                        }else if(followNexttime.equals("")){
                            Toast.makeText(context,"Set Next DATE/TIME",Toast.LENGTH_SHORT).show();
                        }else {
                            String NextdateTime=followNextdate+" "+followNexttime;
                            String UpdatedateTime=updatedatee+" "+updateTime;
                            updteTasks(USERID,LISTID,finalTaskid,type,todoo,commentoo,FOLLOWUPID,NextdateTime,UpdatedateTime);
                            Log.i("updtedata", USERID + "|" + LISTID + "|" +finalTaskid+ "|" +type +"|" + todoo+"|"+commentoo+"|"+FOLLOWUPID+"|"+NextdateTime+"|"+UpdatedateTime);
                        }
                    }else {
                        String NextdateTime=followNextdate+" "+followNexttime;
                        String UpdatedateTime=updatedatee+" "+updateTime;
                        updteTasks(USERID,LISTID,finalTaskid,type,todoo,commentoo,FOLLOWUPID,NextdateTime,UpdatedateTime);
                        Log.i("updtedata", USERID + "|" + LISTID + "|" +finalTaskid+ "|" +type +"|" + todoo+"|"+commentoo+"|"+FOLLOWUPID+"|"+NextdateTime+"|"+UpdatedateTime);
                    }
                }
            });

        tvCancel.setOnClickListener(new View.OnClickListener()
        {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    dialog.cancel();
                }
            });
        linNextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    tvNextDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                }

                            }, year, month, day);
                    datePickerDialog.show();
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });
        linNextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    final String timeSet;
                    TimePickerDialog datePickerDialog = new TimePickerDialog(context,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    tvNextTime.setText(setTimeAmPm(hourOfDay,minute));
                                }
                            },hour,minute,false);
                    datePickerDialog.show();
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });
        linUpdateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    tvUpdateDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                }

                            }, year, month, day);
                    datePickerDialog.show();
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });
        linUpdateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    final String timeSet;
                    TimePickerDialog datePickerDialog = new TimePickerDialog(context,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    tvUpdateTime.setText(setTimeAmPm(hourOfDay,minute));
                                }
                            },hour,minute,false);
                    datePickerDialog.show();
                }catch (Exception e){
                    Log.i("Exception",""+e);
                }

            }
        });

            dialog.show();
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updteTasks(final String userid, final String inqId, final String taskid, final String updatemode,
                           final String todo, final String comments, final String followupid, final String followupdate,
                           final String activitydatetime) {
        progressDialog=CheckConnectivity.dialogForProgres(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN + ServiceUrls.URL_UPDATE_TASKS_FUNCTIONALITY, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("task_update");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String msg = jsonObject1.getString("message");
                        if (!(msg == null)) {
                            removeAt(indexes);
                            dialog.dismiss();
                            dialog.cancel();
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
                Log.i("error", "" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userid);
                params.put("inq_id", inqId);
                params.put("task_id", taskid);
                params.put("UpdateMode", updatemode);
                params.put("to_do", todo);
                params.put("comments", comments);
                params.put("followupID", followupid);
                params.put("folwup_date", followupdate);
                params.put("activity_date", activitydatetime);
                return params;
            }
        };
        SingletonVolley.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);

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
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("inq_task");
                    leadsOption.clear();
                    for (int i=0; i<jsonArray.length();i++){
                        modelClassOfLeads=new ModelClassOfLeads();
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        modelClassOfLeads.setInqId(jsonObject1.getString("task_id"));
                        modelClassOfLeads.setName(jsonObject1.getString("task_name"));
                        leadsOption.add(modelClassOfLeads);
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

    public String setTimeAmPm(int hourOfDay,int minute){
        String time;
        if(hourOfDay>=0 && hourOfDay<12){
            time = hourOfDay + ":" + minute + " AM";
            return time;
        } else {
            if(hourOfDay == 12){
                time = hourOfDay + ":" + minute + " PM";
                return time;
            } else{
                hourOfDay = hourOfDay -12;
                time = hourOfDay + ":" + minute + " PM";
                return time;
            }
        }
    }

//    public String datechecker(String data){
//        String year = null;
//                String month,day;
//        Character c;
//        for (int i=0;i<data.length();i++){
//
//            if(data.charAt(i)!= '-'){
//
//            }else {
//                year =year+""+data.charAt(i);
//            }
//        }
//    }
}
