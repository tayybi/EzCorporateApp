package com.ezcorporate.Assignments.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ezcorporate.Assignments.AssignmentBoard;
import com.ezcorporate.CRM.CRMDashBoard;
import com.ezcorporate.CRM.RecyclerViewCRM.ModelClassOfLeads;
import com.ezcorporate.R;

import java.util.List;

public class PagerAdapterAss extends FragmentStatePagerAdapter {
    List<ModelClassOfLeads> list;
    Context context;
    public PagerAdapterAss(Context context, FragmentManager fm, List<ModelClassOfLeads> list) {
        super(fm);
        this.context=context;
        this.list = list;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        if(AssignmentBoard.countfragment) {
                ListOfAssignments tab1 = new ListOfAssignments();
                Bundle bundle = new Bundle();
                bundle.putString("name", list.get(position).getName());
                tab1.setArguments(bundle);
                Log.i("position", list.get(position).getName());
                AssignmentBoard.countfragment=false;
                return tab1;
        }else {
            Log.i("position","null fragment");
            Fragment fragment=new Fragment();
            return fragment;
        }

    }

    public View getTabView(int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_tab_layout, null);
        TextView tvTaskname = (TextView) v.findViewById(R.id.tv_tab_txt);
        tvTaskname.setText(list.get(position).getName());
        ImageView img = (ImageView) v.findViewById(R.id.iv_tab_icon);
        img.setImageResource(list.get(position).getIcons());
        return v;
    }

//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return super.getPageTitle(position);
//    }

    @Override
    public int getCount() {
        return list.size();
    }
}