package com.example.rickh.chatapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rickh.chatapp.R;
import com.example.rickh.chatapp.activities.FriendRequestsActivity;
import com.example.rickh.chatapp.adapters.ContactsListAdapter;
import com.example.rickh.chatapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

public class ContactsTabFragment extends Fragment {

    private View mView;

    private ContactsListAdapter mAdapter;

    private FastScrollRecyclerView mContactsList;

    private ConstraintLayout mFriendsRequestsContainer;

    private ArrayList<User> mListContacts;

    public ContactsTabFragment() {

    }

    public static ContactsTabFragment newInstance() {
        return new ContactsTabFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_contacts_tab, container, false);

        mContactsList = mView.findViewById(R.id.contacts_list);

        mFriendsRequestsContainer = mView.findViewById(R.id.friend_requests_container);
        mFriendsRequestsContainer.setOnClickListener(friendsRequestsClick);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final CollectionReference contactsRef = db.collection("users").document(mCurrentUser).collection("friends");

        contactsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.out.println("Listen failed. " + e);
                    return;
                }

                mListContacts = new ArrayList<>();

                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot document: queryDocumentSnapshots) {
                        String contactUid = document.get("userUid").toString();

                        db
                                .collection("users")
                                .document(contactUid)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        String username = (String) task.getResult().get("username");
                                        String imageDownloadUrl = (String) task.getResult().get("downloadUrl");
                                        String userUid = (String) task.getResult().get("userUid");

                                        if (!userUid.equals(mCurrentUser))
                                            mListContacts.add(new User(imageDownloadUrl, username, userUid, false, false, false));

                                        setUpAdapter(mListContacts);
                                    }
                                });
                    }
                } else {
                    setUpAdapter(mListContacts);
                }
            }
        });

        return mView;
    }

    View.OnClickListener friendsRequestsClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(getContext(), FriendRequestsActivity.class));
        }
    };

    private void setUpAdapter(ArrayList<User> userList) {
        if (!userList.isEmpty()) {
            mAdapter = new ContactsListAdapter(getContext(), userList);

            mContactsList.setLayoutManager(new LinearLayoutManager(getContext()));
            mContactsList.setAdapter(mAdapter);
        }
    }
}
