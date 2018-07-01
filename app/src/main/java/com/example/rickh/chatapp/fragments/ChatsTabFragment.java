package com.example.rickh.chatapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.rickh.chatapp.R;
import com.example.rickh.chatapp.adapters.ChatListAdapter;
import com.example.rickh.chatapp.models.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

public class ChatsTabFragment extends Fragment {

    private View mView;

    private RecyclerView mChatListRv;

    private ChatListAdapter mAdapter;

    private ArrayList<Chat> mChatList;

    public ChatsTabFragment() {

    }

    public static ChatsTabFragment newInstance() {
        return new ChatsTabFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_chats_tab, container, false);

        mChatListRv = mView.findViewById(R.id.chats_list);

        getChats();

        return mView;
    }

    private void getChats() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String currentUserUid = FirebaseAuth.getInstance().getUid();
        CollectionReference chatRef = db.collection("/users/" + currentUserUid + "/chats");

        chatRef
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            // TODO add failure
                            return;
                        }

                        if (e != null) {
                            // TODO add error
                            return;
                        }

                        mChatList = new ArrayList<>();

                        for (final DocumentSnapshot document: queryDocumentSnapshots) {
                            db
                                    .collection("chats")
                                    .document(document.get("chatUid").toString())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task != null) {
                                                Chat chat = task.getResult().toObject(Chat.class).setId(document.getId());
                                                mChatList.add(chat);
                                                setUpList();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // TODO add failure
                                        }
                                    });
                        }
                    }
                });
    }

    private void setUpList() {
        mAdapter = new ChatListAdapter(getContext(), mChatList);

        mChatListRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mChatListRv.setAdapter(mAdapter);
    }
}
