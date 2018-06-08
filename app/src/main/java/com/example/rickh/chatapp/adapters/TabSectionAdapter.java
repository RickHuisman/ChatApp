package com.example.rickh.chatapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.rickh.chatapp.fragments.ChatsTabFragment;
import com.example.rickh.chatapp.fragments.ContactsTabFragment;

public class TabSectionAdapter extends FragmentPagerAdapter {

    public TabSectionAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ChatsTabFragment.newInstance();
            case 1:
                return ContactsTabFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
