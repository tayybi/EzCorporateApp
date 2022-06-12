package com.ezcorporate.VirtualCallSystem.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezcorporate.Authentications.CheckConnectivity;
import com.ezcorporate.Others.ModelClassOfCustomer;
import com.ezcorporate.R;
import com.ezcorporate.REM.Transfer;
import com.ezcorporate.VirtualCallSystem.CallsDialog;
import com.ezcorporate.VirtualCallSystem.PendingLogsList;

import java.util.ArrayList;
import java.util.List;

public class RecyclerPendingLogsList extends RecyclerView.Adapter<RecyclerPendingLogsList.TransferViewHolde> {

    public List<ModelClassOfCallLogs> list;
    ModelClassOfCustomer modelClassOfCustomer;
    Context context;
    public RecyclerPendingLogsList(Context context, List<ModelClassOfCallLogs> list) {
        this.context = context;
        this.list=list;
    }

    @NonNull
    @Override
    public TransferViewHolde onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View mView = inflater.inflate(R.layout.custom_recyler_pending_logs_list, parent, false);
        return new TransferViewHolde(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TransferViewHolde holder, final int position) {

        holder.tvNamePhone.setText(list.get(position).getCallerNamePhone());
        holder.tvDate.setText(list.get(position).getCallDate());
        holder.tvTypeDuration.setText(list.get(position).getCallerType()+":  "+list.get(position).getCallerDuration()+" sec");
        if(list.get(position).getCallerType().equals("Outgoing")){
            holder.ivType.setImageResource(R.drawable.outgoing);
        }else if(list.get(position).getCallerType().equals("Incoming")){
            holder.ivType.setImageResource(R.drawable.incoming);
        }else{
            holder.ivType.setImageResource(R.drawable.missed);
        }
        holder.tvComment.setText(list.get(position).getCallerComments());
        holder.linSetClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckConnectivity.checkInternetConnection(context)){

                Intent intent=new Intent(context,CallsDialog.class);
                String bdId=list.get(position).getCallerMode();
                String bdcontact=list.get(position).getCallerNamePhone();
                intent.putExtra("phone_no",bdcontact);
                intent.putExtra("db_id",bdId);
                context.startActivity(intent);
                }else {
                    Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show();
                }
             }
        });

    }
    public void filterList(ArrayList<ModelClassOfCallLogs> filteredList) {
        list=new ArrayList<>();
        list.addAll(filteredList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TransferViewHolde extends RecyclerView.ViewHolder {

        TextView tvNamePhone,tvDate,tvTypeDuration,tvComment;
        ImageView ivType;
        LinearLayout linSetClick;

        public TransferViewHolde(View itemView) {
            super(itemView);
            tvNamePhone=itemView.findViewById(R.id.tv_namephone);
            tvDate=itemView.findViewById(R.id.tv_date);
            tvTypeDuration=itemView.findViewById(R.id.tv_type_duration);
            tvComment=itemView.findViewById(R.id.tv_comment);
            ivType=itemView.findViewById(R.id.iv_type);
            linSetClick=itemView.findViewById(R.id.lin_set_click);

        }
    }
}
