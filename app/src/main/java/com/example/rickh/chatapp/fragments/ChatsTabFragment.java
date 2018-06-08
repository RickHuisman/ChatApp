package com.example.rickh.chatapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rickh.chatapp.R;

public class ChatsTabFragment extends Fragment {

    private View mView;

    public ChatsTabFragment() {

    }

    public static ChatsTabFragment newInstance() {
        return new ChatsTabFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_chats_tab, container, false);

        return mView;
    }
}
