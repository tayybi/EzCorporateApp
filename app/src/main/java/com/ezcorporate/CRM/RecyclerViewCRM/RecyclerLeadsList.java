package com.ezcorporate.CRM.RecyclerViewCRM;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Button;
import android.widget.DatePicker;
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
import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.CRM.InqueryDetailView;
import com.ezcorporate.CRM.OpportunityList;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;
import com.ezcorporate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.Others.ServiceUrls;
import com.ezcorporate.Others.SingletonVolley;

import static com.ezcorporate.CRM.HoldsList.holdsList;
import static com.ezcorporate.CRM.LeadsList.leadlist;
import static com.ezcorporate.CRM.OpportunityList.opportunityList;

public class RecyclerLeadsList extends RecyclerView.Adapter<RecyclerLeadsList.TransferViewHolde> {

    public List<ModelClassOfLeads> list;
    String URL_PREFIX_DOMAIN;
    String USERID;
    Dialog progressDialog;
    Dialog dialog;
    String dataType;
    public static String LISTID;
    public static String OPPORTUNITY = "opportunity";
    public static String UNHOLDLEAD = "UnHoldLead";
    public static String UNHOLDOPPORTUNITY = "UnHoldOpp";
    public static String LEAD = "lead";
    int indexee;
    List<ModelClassOfLeads> leadsOption;
    ModelClassOfLeads modelClassOfLeads;
    String finalTaskid;
    private Context context;
    String funcId = "";

    public RecyclerLeadsList(Context context, List<ModelClassOfLeads> list, String dataType) {
        this.context = context;
        this.list = list;
        this.dataType = dataType;
        URL_PREFIX_DOMAIN = SharedPrefManager.getInstance(context.getApplicationContext()).getUrl();
        USERID = SharedPrefManager.getInstance(context.getApplicationContext()).getUId();

        leadsOption = new ArrayList<>();
        Log.i("checkmode", dataType);
    }

    @NonNull
    @Override
    public TransferViewHolde onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View mView = inflater.inflate(R.layout.custom_recyler_leads_list, parent, false);
        return new TransferViewHolde(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TransferViewHolde holder, final int position) {
        holder.tvName.setText(list.get(position).getName());
        holder.tvGroupName.setText(list.get(position).getGroupName());
        holder.tvSubjectName.setText(list.get(position).getSubject());
        holder.tvDateTime.setText(list.get(position).getDate());
        holder.ivCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = list.get(position).getMobileNo();
                if (!mobile.equals("")) {
                    makeCall(mobile);
                } else {
                    Toast.makeText(context, list.get(position).getMobileNo(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        holder.linSetClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pif = list.get(position).getInqId();
                Intent intent = new Intent(context, InqueryDetailView.class);
                intent.putExtra("INQID", pif);
                intent.putExtra("dataType", dataType);
                context.startActivity(intent);
                if (dataType.equals(OPPORTUNITY)) {
                    opportunityList.overridePendingTransition(R.anim.in_right, R.anim.out_left);
                } else if (dataType.equals(LEAD)) {
                    leadlist.overridePendingTransition(R.anim.in_right, R.anim.out_left);
                } else if (dataType.equals(UNHOLDOPPORTUNITY) || dataType.equals(UNHOLDLEAD)) {
                    holdsList.overridePendingTransition(R.anim.in_right, R.anim.out_left);
                }

                Log.i("inqidee", pif);
            }
        });

        holder.ivSetting.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                LISTID = list.get(position).getInqId();
                indexee = position;
                Log.i("idoflist", "LEADID=" + LISTID);
                //creating a popup menu

                if (dataType.equals(UNHOLDLEAD) || dataType.equals(UNHOLDOPPORTUNITY)) {  //////////////////  unhold
                    final PopupMenu popup = new PopupMenu(context, holder.ivSetting);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.holdmenue);

                    //adding click listener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.unhold_menue:
                                    if (dataType.equals(UNHOLDLEAD)) {
                                        updteLeadFunctionality(USERID, LISTID, dataType, "");
                                    } else if (dataType.equals(UNHOLDOPPORTUNITY)) {
                                        updteOppFunctionality(USERID, LISTID, dataType, "");
                                    }

                                    //updateUnholdLed(USERID, LISTID, dataType);
                                    Log.i("updtdata", USERID + "|" + LISTID + "|" + dataType);
                                    return true;
                                default:
                                    return false;

                            }
                        }
                    });
                    //displaying the popup
                    popup.show();
                    /////
                } else if (dataType.equals(OPPORTUNITY)) {  //////////////////opportunity
                    ///
                    getTasksList();
                    final PopupMenu popup = new PopupMenu(context, holder.ivSetting);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.oppertunitymenue);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.oopm_hold:
                                    getFunctionalityList("HoldOpp");
                                    return true;
                                case R.id.oopm_assign_new:
                                    getFunctionalityList("UserAssign");
                                    return true;
                                case R.id.oopm_add_new_task:
                                    showDialogForNewTask(list.get(position).getSubject(), "NewTask");
                                    return true;
                                case R.id.oopm_decline:
                                    getFunctionalityList("DeclineOpp");
                                    return true;
                                case R.id.oopm_convert_opp:
                                    getFunctionalityList("StageUpgrade");
                                    return true;
                                case R.id.oopm_forcast:
                                    getFunctionalityList("OppForecast");
                                    return true;
                                case R.id.oopm_priority:
                                    getFunctionalityList("OppPriority");
                                    return true;
                                case R.id.oopm_success:
                                    updteOppFunctionality(USERID, LISTID, "OppSuccess", "");
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();
                } else if (dataType.equals(LEAD)) {///////////////////leads
                    getTasksList();
                    final PopupMenu popup = new PopupMenu(context, holder.ivSetting);
                    //inflating menu from xml resource
                    popup.inflate(R.menu.leadsmenue);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.lm_hold:
                                    getFunctionalityList("HoldLead");
                                    return true;
                                case R.id.lm_assign_new:
                                    getFunctionalityList("UserAssign");
                                    return true;
                                case R.id.lm_add_new_task:
                                    showDialogForNewTask(list.get(position).getSubject(), "NewTask");
                                    return true;
                                case R.id.lm_decline:
                                    getFunctionalityList("DeclineLead");
                                    return true;
                                case R.id.lm_convert_opp:
                                    getFunctionalityList("ConvertToOpp");
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    //displaying the popup
                    popup.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TransferViewHolde extends RecyclerView.ViewHolder {

        TextView tvName, tvGroupName, tvSubjectName, tvDateTime;
        ImageView ivSetting, ivCalls;
        LinearLayout linSetClick;

        public TransferViewHolde(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvGroupName = itemView.findViewById(R.id.tv_groupname);
            tvSubjectName = itemView.findViewById(R.id.tv_subject);
            tvDateTime = itemView.findViewById(R.id.tv_datetime);
            ivSetting = itemView.findViewById(R.id.iv_setting_dots);
            ivCalls = itemView.findViewById(R.id.iv_calls);
            linSetClick = itemView.findViewById(R.id.lin_set_click);

        }
    }

    public void removeAt(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
        funcId = "";
    }

    public void filterList(ArrayList<ModelClassOfLeads> filteredList) {
        list = new ArrayList<>();
        list.addAll(filteredList);
        notifyDataSetChanged();
    }

    public void showDialogForNewTask(final String name, final String type) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_task_update_new);
        final TextView tvUpdateTime, tvUpdateDate, tvNextTime, tvNextDate, tvCancel, tvDone, tvTitle, tvName;
        final EditText etComment, etTodo;
        final int year, month, day, hour, minute;
        Calendar calendar;
        Spinner spinTask;
        LinearLayout linUpdateTime, linUpdateDate, linNextTime, linNextDate, linUpdate;
        spinTask = dialog.findViewById(R.id.sp_task);
        linNextDate = dialog.findViewById(R.id.lin_ndate);
        linNextTime = dialog.findViewById(R.id.lin_ntime);
        linUpdate = dialog.findViewById(R.id.lin_update);
        linUpdateDate = dialog.findViewById(R.id.lin_update_date);
        linUpdateTime = dialog.findViewById(R.id.lin_update_time);
        etComment = dialog.findViewById(R.id.et_comments);
        etTodo = dialog.findViewById(R.id.et_todo);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        tvUpdateDate = dialog.findViewById(R.id.tv_update_date);
        tvUpdateTime = dialog.findViewById(R.id.tv_update_time);
        tvNextDate = dialog.findViewById(R.id.tv_ndate);
        tvNextTime = dialog.findViewById(R.id.tv_ntime);
        tvUpdateTime.setText(setTimeAmPm(hour, minute));
        tvUpdateDate.setText(day + "-" + (month + 1) + "-" + year);
        tvTitle = dialog.findViewById(R.id.tv_title);
        tvName = dialog.findViewById(R.id.tv_name);
        tvName.setText(name);
        tvDone = dialog.findViewById(R.id.tv_done);
        tvCancel = dialog.findViewById(R.id.tv_cancel);

        if (type.equals("NewTask")) {
            linUpdate.setVisibility(View.GONE);
            tvTitle.setText(type);

        } else if (type.equals("UpdateTask")) {
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
                try {
                    itemName = parent.getItemAtPosition(position).toString();
                    if (!itemName.equals("Choose Task")) {
                        for (int i = 0; i < leadsOption.size(); i++) {
                            if (itemName.equals(leadsOption.get(i).getName())) {
                                finalTaskid = leadsOption.get(i).getInqId();
                                Log.i("taskid", finalTaskid);
                                break;
                            }
                        }
                    } else {
                        finalTaskid = "";
                        Log.i("taskid", finalTaskid);
                    }
                } catch (Exception e) {
                    Log.i("Exception", "" + e);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String todoo = etTodo.getText().toString();
                String followuodate = tvNextDate.getText().toString();
                String followuotime = tvNextTime.getText().toString();
                // String commentoo=etComment.getText().toString();
                //  String activeodatee=tvUpdateDate.getText().toString()+" "+tvUpdateTime.getText().toString();
                if (todoo.equals("")) {
                    etTodo.setError("");
                    etTodo.setFocusable(true);
                } else if (finalTaskid.equals("")) {
                    Toast.makeText(context, "Choose Task", Toast.LENGTH_SHORT).show();
                } else if (followuodate.equals("")) {
                    Toast.makeText(context, "Set DATE/TIME", Toast.LENGTH_SHORT).show();
                } else if (followuotime.equals("")) {
                    Toast.makeText(context, "Set DATE/TIME", Toast.LENGTH_SHORT).show();
                } else {
                    String dateTime = followuodate + " " + followuotime;
                    updteTasks(USERID, LISTID, finalTaskid, type, todoo, "", "", dateTime, "");
                    Log.i("updtedata", USERID + "|" + LISTID + "|" + finalTaskid + "|" + type + "|" + todoo + "|" + dateTime);
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog.cancel();
            }
        });
        linNextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    tvNextDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                }

                            }, year, month, day);
                    datePickerDialog.show();
                } catch (Exception e) {
                    Log.i("Exception", "" + e);
                }

            }
        });
        linNextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String timeSet;
                    TimePickerDialog datePickerDialog = new TimePickerDialog(context,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    tvNextTime.setText(setTimeAmPm(hourOfDay, minute));
                                }
                            }, hour, minute, false);
                    datePickerDialog.show();
                } catch (Exception e) {
                    Log.i("Exception", "" + e);
                }

            }
        });
        linUpdateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    tvUpdateDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                                }

                            }, year, month, day);
                    datePickerDialog.show();
                } catch (Exception e) {
                    Log.i("Exception", "" + e);
                }

            }
        });
        linUpdateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String timeSet;
                    TimePickerDialog datePickerDialog = new TimePickerDialog(context,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    tvUpdateTime.setText(setTimeAmPm(hourOfDay, minute));
                                }
                            }, hour, minute, false);
                    datePickerDialog.show();
                } catch (Exception e) {
                    Log.i("Exception", "" + e);
                }

            }
        });

        dialog.show();
    }

    public void showDialogForsetting(final String s) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_listview_for_dialog);
        final ListView listView;
        TextView textView;
        final Button button;
        listView = dialog.findViewById(R.id.function_list);
        button = dialog.findViewById(R.id.function_ok);
        textView = dialog.findViewById(R.id.function_title);
        textView.setText(s);
        if (!leadsOption.isEmpty()) {
            List<String> stringArray = new ArrayList<>();
            for (int i = 0; i < leadsOption.size(); i++) {
                stringArray.add(leadsOption.get(i).getName());
                Log.i("namelist", "" + leadsOption.get(i).getName());
            }

            final ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, stringArray);
            listView.setAdapter(modeAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String namee = parent.getItemAtPosition(position).toString();
                    for (int j = 0; j < leadsOption.size(); j++) {
                        if (namee.equals(leadsOption.get(j).getName())) {
                            funcId = leadsOption.get(j).getInqId();
                            Log.i("idoflist", "function=" + funcId);
                            break;
                        }
                    }
                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    if (!funcId.equals("")) {

                        if (dataType.equals(OPPORTUNITY)) {
                            updteOppFunctionality(USERID, LISTID, s, funcId);
                            Log.i("updtedata", USERID + "|" + LISTID + "|" + s + "|" + funcId);
                        } else if (dataType.equals(LEAD)) {
                            updteLeadFunctionality(USERID, LISTID, s, funcId);
                            Log.i("updtedata", USERID + "|" + LISTID + "|" + s + "|" + funcId);
                        }
                    } else {
                        Toast.makeText(context, "Choose Option", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();

        } else {
            Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updteTasks(final String userid, final String inqId, final String taskid, final String updatemode,
                           final String todo, final String comments, final String followupid, final String followupdate,
                           final String activitydatetime) {
        progressDialog = CheckConnectivity.dialogForProgres(context);
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

    public String setTimeAmPm(int hourOfDay, int minute) {
        String time;
        if (hourOfDay >= 0 && hourOfDay < 12) {
            time = hourOfDay + ":" + minute + " AM";
            return time;
        } else {
            if (hourOfDay == 12) {
                time = hourOfDay + ":" + minute + " PM";
                return time;
            } else {
                hourOfDay = hourOfDay - 12;
                time = hourOfDay + ":" + minute + " PM";
                return time;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getTasksList() {
        progressDialog = CheckConnectivity.dialogForProgres(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_PREFIX_DOMAIN + ServiceUrls.URL_DROP_DOWN_LISTS, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("inq_task");
                    leadsOption.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        modelClassOfLeads = new ModelClassOfLeads();
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
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
                Log.i("error", "" + error);
            }
        });
        SingletonVolley.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getFunctionalityList(final String type) {
        progressDialog = CheckConnectivity.dialogForProgres(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_PREFIX_DOMAIN + ServiceUrls.URL_FUNCTIONS_LIST, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (type.equals("UserAssign")) {
                        leadsOption.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("dd_user");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            modelClassOfLeads = new ModelClassOfLeads();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            modelClassOfLeads.setName(jsonObject1.getString("user_name"));
                            modelClassOfLeads.setInqId(jsonObject1.getString("user_id"));
                            leadsOption.add(modelClassOfLeads);
                        }
                        showDialogForsetting(type);
                    } else if (type.equals("ConvertToOpp") || type.equals("StageUpgrade")) {
                        leadsOption.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("dd_inq_stage");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            modelClassOfLeads = new ModelClassOfLeads();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            modelClassOfLeads.setName(jsonObject1.getString("stage_name"));
                            modelClassOfLeads.setInqId(jsonObject1.getString("stage_id"));
                            leadsOption.add(modelClassOfLeads);
                        }
                        showDialogForsetting(type);
                    } else if (type.equals("HoldLead") || type.equals("HoldOpp")) {
                        leadsOption.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("dd_hold_reasone");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            modelClassOfLeads = new ModelClassOfLeads();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            modelClassOfLeads.setName(jsonObject1.getString("holdr_name"));
                            modelClassOfLeads.setInqId(jsonObject1.getString("holdr_id"));
                            leadsOption.add(modelClassOfLeads);
                        }
                        showDialogForsetting(type);
                    } else if (type.equals("DeclineLead") || type.equals("DeclineOpp")) {
                        leadsOption.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("dd_close_reasone");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            modelClassOfLeads = new ModelClassOfLeads();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            modelClassOfLeads.setName(jsonObject1.getString("closer_name"));
                            modelClassOfLeads.setInqId(jsonObject1.getString("closer_id"));
                            leadsOption.add(modelClassOfLeads);
                        }
                        showDialogForsetting(type);
                    } else if (type.equals("OppForecast")) {
                        leadsOption.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("dd_forecast");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            modelClassOfLeads = new ModelClassOfLeads();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            modelClassOfLeads.setName(jsonObject1.getString("forcast_name"));
                            modelClassOfLeads.setInqId(jsonObject1.getString("forcast_id"));
                            leadsOption.add(modelClassOfLeads);
                        }
                        showDialogForsetting(type);
                    } else if (type.equals("OppPriority")) {
                        leadsOption.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("dd_priority");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            modelClassOfLeads = new ModelClassOfLeads();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            modelClassOfLeads.setName(jsonObject1.getString("priority_name"));
                            modelClassOfLeads.setInqId(jsonObject1.getString("priority_id"));
                            leadsOption.add(modelClassOfLeads);
                        }
                        showDialogForsetting(type);
                    }
//                    else if(type.equals("OppSuccess")){
//                        progressDialog.dismiss();
//                        updteOppFunctionality(USERID,LISTID,type,"");
//                    }

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
        });
        SingletonVolley.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updteLeadFunctionality(final String userid, final String inqId, final String functionMode, final String functionModeId) {
        progressDialog = CheckConnectivity.dialogForProgres(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN + ServiceUrls.URL_LEAD_FUN_UPDATE, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("crm_update");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String msg = jsonObject1.getString("message");
                        if (!(msg == null)) {

                            if (functionMode.equals(UNHOLDLEAD)) {
                                removeAt(indexee);
                            } else {
                                removeAt(indexee);
                                dialog.dismiss();
                                dialog.cancel();
                            }

//                            context.startActivity(new Intent(context, LeadsList.class));
//                            ((LeadsList)context).finish();
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
                params.put("inq_func_mode", functionMode);
                params.put("selection_id", functionModeId);
                return params;
            }
        };
        SingletonVolley.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void updteOppFunctionality(final String userid, final String inqId, final String functionMode, final String functionModeId) {
        progressDialog = CheckConnectivity.dialogForProgres(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PREFIX_DOMAIN + ServiceUrls.URL_OPP_FUN_UPDATE, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                Log.i("response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("crm_update");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        String msg = jsonObject1.getString("message");
                        if (!(msg == null)) {

                            if (functionMode.equals(UNHOLDOPPORTUNITY)) {
                                removeAt(indexee);
                            } else if (functionMode.equals("OppSuccess")) {
                                removeAt(indexee);
                            } else if (functionMode.equals("OppForecast") || functionMode.equals("OppPriority") || functionMode.equals("StageUpgrade")) {
                                dialog.dismiss();
                                dialog.cancel();
                            } else {
                                removeAt(indexee);
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
                Log.i("error", "" + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userid);
                params.put("inq_id", inqId);
                params.put("inq_func_mode", functionMode);
                params.put("selection_id", functionModeId);
                return params;
            }
        };
        SingletonVolley.getInstance(context.getApplicationContext()).addToRequestQueue(stringRequest);

    }

    protected void makeCall(String phone) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE},0);
        }
        else
        {
            String d = "tel:" + phone ;
            Intent phoneIntent = new Intent(Intent.ACTION_CALL);
            phoneIntent.setData(Uri.parse(d));
            context.startActivity(phoneIntent);
        }
    }
}
