package com.example.rickh.chatapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rickh.chatapp.R;
import com.example.rickh.chatapp.adapters.RequestsListAdapter;
import com.example.rickh.chatapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestsFragment extends Fragment {

    private static final String ARG_SELECTED_TAB = "section_number";

    private View mView;

    private FirebaseFirestore db;

    private int position;

    private RecyclerView mRequestsList;

    private RequestsListAdapter mAdapter;

    public FriendRequestsFragment() {
    }

    public static FriendRequestsFragment newInstance(int sectionNumber) {
        FriendRequestsFragment fragment = new FriendRequestsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SELECTED_TAB, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_friend_requests, container, false);

        db = FirebaseFirestore.getInstance();

        mRequestsList = mView.findViewById(R.id.requests_list);

        position = getArguments().getInt(ARG_SELECTED_TAB);

        if (position == 0) {

            CollectionReference sentReference = buildCollectionReference("sent_requests");
            buildDataBaseListener(sentReference);

        } else if (position == 1){
            CollectionReference received_requests = buildCollectionReference("received_requests");
            buildDataBaseListener(received_requests);
        }

        return mView;
    }

    private CollectionReference buildCollectionReference(String reference) {
        return db.collection("friend_requests")
                .document(FirebaseAuth.getInstance().getUid())
                .collection(reference);
    }

    private ListenerRegistration buildDataBaseListener(CollectionReference collectionReference) {
        return collectionReference
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        System.out.println(position + " - New Event");
                        List<DocumentSnapshot> requests = queryDocumentSnapshots.getDocuments();

                        final ArrayList<User> userList = new ArrayList<>();

                        if (requests.isEmpty()) {
                            createAdapter(userList);
                            System.out.println("Current data: null");
                        }

                        for (int i = 0; i < requests.size(); i++) {
                            // Get the other user's info
                            db
                                    .collection("users")
                                    .document(requests.get(i).getId())
                                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                            String username = documentSnapshot.get("username").toString();
                                            String imageDownloadUrl = documentSnapshot.get("downloadUrl").toString();
                                            String userUid = documentSnapshot.get("userUid").toString();

                                            if (position == 0) {
                                                userList.add(new User(imageDownloadUrl, username, userUid, false, true, false));
                                                createAdapter(userList);
                                            } else {
                                                userList.add(new User(imageDownloadUrl, username, userUid, false, false, true));
                                                createAdapter(userList);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void createAdapter(ArrayList<User> userList) {

        mRequestsList.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRequestsList.setLayoutManager(layoutManager);

        mAdapter = new RequestsListAdapter(getContext(), userList, position);

        mRequestsList.setAdapter(mAdapter);
    }
}
