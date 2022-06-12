package com.ezcorporate.Assignments.RecyclerViewAssignment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ezcorporate.Assignments.AssignmentDetail;
import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.CRM.InqueryDetailView;
import com.ezcorporate.CRM.RecyclerViewCRM.ModelClassOfLeads;
import com.ezcorporate.Others.ModelClassOfAssigment;
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

import static com.ezcorporate.Assignments.AssignmentBoard.assignmentBoard;
import static com.ezcorporate.Assignments.Fragments.ListOfAssignments.listofAssignment;
import static com.ezcorporate.CRM.Fragments.TaskListFragment.taskListFragment;

public class RecyclerAssigList extends RecyclerView.Adapter<RecyclerAssigList.TransferViewHolde> {

    public List<ModelClassOfAssigment> list;
    String URL_PREFIX_DOMAIN;
    String USERID;
    String dataType;
    String listName;
    List<ModelClassOfAssigment> listDD;
    Context context;



    public RecyclerAssigList(Context context, List<ModelClassOfAssigment> list,String listname) {
        this.context = context;
        this.list = list;
        this.listName=listname;
        URL_PREFIX_DOMAIN=SharedPrefManager.getInstance(context).getUrl();
        USERID=SharedPrefManager.getInstance(context).getUId();
        listDD = new ArrayList<>();
    }

    @NonNull
    @Override
    public TransferViewHolde onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View mView = inflater.inflate(R.layout.custom_recyler_assignment_list, parent, false);
        return new TransferViewHolde(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TransferViewHolde holder, final int position) {
        holder.tvTitle.setText(list.get(position).getTitle());
        holder.tvDeadline.setText(list.get(position).getDeadline());
        holder.tvPriority.setText(list.get(position).getPriority());
        holder.tvAssignedbyto.setText(list.get(position).getAssignBy());
        holder.tvDetail.setText("ToDo:  "+list.get(position).getDescrep());
        holder.linSetClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pif = list.get(position).getId();
                Intent intent =new Intent(context,AssignmentDetail.class);
                intent.putExtra("INQID",pif);
                intent.putExtra("dataType",listName);
                intent.putExtra("assigname",list.get(position).getTitle());
                context.startActivity(intent);
                listofAssignment.overridePendingTransition(R.anim.in_right, R.anim.out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TransferViewHolde extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDeadline, tvPriority, tvAssignedbyto, tvDetail;
        LinearLayout linSetClick;

        public TransferViewHolde(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDeadline = itemView.findViewById(R.id.tv_deadline);
            tvPriority = itemView.findViewById(R.id.tv_priority);
            tvAssignedbyto = itemView.findViewById(R.id.tv_assign_by);
            tvDetail = itemView.findViewById(R.id.tv_detail);
            linSetClick = itemView.findViewById(R.id.lin_set_click);
        }
    }

}


