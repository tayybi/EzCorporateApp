package com.ezcorporate.CRM.Fragments;

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

import com.ezcorporate.CRM.CRMDashBoard;
import com.ezcorporate.CRM.RecyclerViewCRM.ModelClassOfLeads;
import com.ezcorporate.R;

import java.util.List;

import com.ezcorporate.CRM.RecyclerViewCRM.ModelClassOfLeads;

public class PagerAdapterooo extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    List<ModelClassOfLeads> list;
    Context context;
    public PagerAdapterooo(Context context,FragmentManager fm, int NumOfTabs, List<ModelClassOfLeads> list) {
        super(fm);
        this.context=context;
        this.list = list;
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        if(CRMDashBoard.countfragment) {
            if (list.get(position).getName().equals("Today")) {
                Today today = new Today();
                Log.i("position", "" + list.get(position).getName());
                CRMDashBoard.countfragment=false;
                return today;
            } else {
                TaskListFragment tab1 = new TaskListFragment();
                Bundle bundle = new Bundle();
                bundle.putString("taskid", list.get(position).getInqId());
                tab1.setArguments(bundle);
                Log.i("position", list.get(position).getName() + "|" + list.get(position).getInqId());
                CRMDashBoard.countfragment=false;
                return tab1;
            }
        }else {
            Log.i("position","null fragment");
            Fragment fragment=new Fragment();
            return fragment;
        }

    }

    public View getTabView(int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_tab_layout, null);
        TextView tvTaskname = (TextView) v.findViewById(R.id.tv_tab_txt);
        TextView tvNotificationNo = (TextView) v.findViewById(R.id.tv_notification_no);
        tvTaskname.setText(list.get(position).getName());
        tvNotificationNo.setText(list.get(position).getFollowUpId());
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