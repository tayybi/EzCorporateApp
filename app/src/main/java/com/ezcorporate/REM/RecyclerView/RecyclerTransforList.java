package com.ezcorporate.REM.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ezcorporate.CRM.RecyclerViewCRM.ModelClassOfLeads;
import com.ezcorporate.Others.ModelClassOfCustomer;
import com.ezcorporate.R;
import com.ezcorporate.REM.Transfer;

import java.util.ArrayList;
import java.util.List;

import com.ezcorporate.Others.ModelClassOfCustomer;

public class RecyclerTransforList extends RecyclerView.Adapter<RecyclerTransforList.TransferViewHolde> {

    public List<ModelClassOfCustomer> list;
    ModelClassOfCustomer modelClassOfCustomer;
    Context context;
    public RecyclerTransforList(Context context, List<ModelClassOfCustomer> list) {
        this.context = context;
        this.list=list;
    }

    @NonNull
    @Override
    public TransferViewHolde onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View mView = inflater.inflate(R.layout.custom_recyler_transfor_list, parent, false);
        return new TransferViewHolde(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TransferViewHolde holder, final int position) {
        holder.tvPlotName.setText(list.get(position).getItemName());
        holder.tvFrom.setText(list.get(position).getNameF());
        holder.tvTo.setText(list.get(position).getNameT());
        holder.linSetClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,Transfer.class);
                String pif=list.get(position).getAgrementId();
                intent.putExtra("agrementId",pif);
                Log.i("productid",pif);
                context.startActivity(intent);

               // overridePendingTransition(R.anim.in_left,R.anim.out_right);    //ask umar
             }
        });

    }
    public void filterList(ArrayList<ModelClassOfCustomer> filteredList) {
        list=new ArrayList<>();
        list.addAll(filteredList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TransferViewHolde extends RecyclerView.ViewHolder {

        TextView tvPlotName,tvFrom,tvTo;
        LinearLayout linSetClick;

        public TransferViewHolde(View itemView) {
            super(itemView);
            tvPlotName=itemView.findViewById(R.id.tv_plot_name);
            tvFrom=itemView.findViewById(R.id.tv_from_name);
            tvTo=itemView.findViewById(R.id.tv_to_name);
            linSetClick=itemView.findViewById(R.id.lin_set_click);

        }
    }
}
