package com.example.mahmoudahmed.caht.ui.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.mahmoudahmed.caht.R;
import com.example.mahmoudahmed.caht.ui.Fragments.ChatFragment;
import com.example.mahmoudahmed.caht.ui.Fragments.ExploreFragment;
import com.example.mahmoudahmed.caht.ui.Fragments.RequestsFragment;


public class FragmentsAdapter extends FragmentPagerAdapter {

    Context context;

    public FragmentsAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new ChatFragment();
            case 1:
                return new RequestsFragment();
            case 2:
                return new ExploreFragment();

            //this case not going to be happen anyway
            default:
                return new RequestsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return context.getResources().getString(R.string.chat);
            case 1:
                return context.getResources().getString(R.string.requests);
            case 2:
                return context.getResources().getString(R.string.explore);
            default:
                return context.getResources().getString(R.string.chat);
        }

    }
}
