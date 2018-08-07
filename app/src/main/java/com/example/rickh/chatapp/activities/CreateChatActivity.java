package com.example.rickh.chatapp.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.rickh.chatapp.R;
import com.example.rickh.chatapp.fragments.CreateGroupChatFragment;
import com.example.rickh.chatapp.fragments.SelectContactsFragment;
import com.example.rickh.chatapp.models.Contact;

import java.util.ArrayList;

public class CreateChatActivity extends AppCompatActivity implements SelectContactsFragment.NextFragment {

    private FragmentManager mFragManager;
    private ArrayList<Contact> mSelectedContactsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);
        goToFragment(0);
    }

    private void goToFragment(int key) {
        switch (key) {
            case 0:
                startFragment(new SelectContactsFragment(), false);
                break;
            case 1:

//                if (mSelectedContactsList.size() == 1) {
//                    // Create a private chat
//                    this.finish();
//                    return;
//                }

                Bundle arguments = new Bundle();
                arguments.putSerializable("selectedContacts", mSelectedContactsList);

                CreateGroupChatFragment createGroupChatFragment = new CreateGroupChatFragment();
                createGroupChatFragment.setArguments(arguments);
                startFragment(createGroupChatFragment, true);
                break;
        }
    }

    private void startFragment(Fragment frag, boolean addToBackStack) {
        mFragManager = getFragmentManager();
        FragmentTransaction transaction = mFragManager.beginTransaction();
        transaction.replace(R.id.frame_container, frag);

        if (addToBackStack) transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mFragManager.getBackStackEntryCount() > 0) {
            mFragManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void fabClicked(ArrayList<Contact> selectedContacts) {
        this.mSelectedContactsList = selectedContacts;
        goToFragment(1);
    }
}
