package com.ezcorporate.Assignments.RecyclerViewAssignment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ezcorporate.Authentications.SharedPrefManager;
import com.ezcorporate.Others.ModelClassOfAssigment;
import com.ezcorporate.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageHistoryAdapter extends BaseAdapter {

    List<ModelClassOfAssigment> list;
    LayoutInflater messageInflater;
    TextView messageBody,messageDate;
    Context context;
    public MessageHistoryAdapter(Context ctx,List<ModelClassOfAssigment> list) {
        super();
        this.list = list;
        this.context = ctx;
    }

    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public int getViewTypeCount() {

        if(getCount() > 0){
            return getCount();
        }else{
            return super.getViewTypeCount();
        }
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public View getView(int position, View view, ViewGroup parent) {
        messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        ModelClassOfAssigment bpj=list.get(position);

        if(list.get(position).getName().equals(SharedPrefManager.getInstance(context).getUName())){
            view = messageInflater.inflate(R.layout.custom_my_message, null);

            messageBody=(TextView)view.findViewById(R.id.message_body);
            messageDate=(TextView)view.findViewById(R.id.message_date);
            messageBody.setText(bpj.getDescrep());
            messageDate.setText(bpj.getDeadline());

        }else {
            view = messageInflater.inflate(R.layout.custom_their_message, null);

            messageBody=(TextView)view.findViewById(R.id.message_body);
            messageDate=(TextView)view.findViewById(R.id.message_date);
            messageBody.setText(bpj.getDescrep());
            messageDate.setText(bpj.getDeadline());
        }
        return view;
    };
}
