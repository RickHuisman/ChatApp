package com.example.rickh.chatapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.rickh.chatapp.R;
import com.example.rickh.chatapp.adapters.UserListAdapter;
import com.example.rickh.chatapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class AddFriendActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private ImageView mBackImage;

    private RecyclerView mUserListView;
    private ProgressBar mProgressBar;

    private EditText mSearchField;

    private UserListAdapter mAdapter;

    private String mCurrentUser;

    private ArrayList<User> mAllUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        mAllUserList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        mUserListView = findViewById(R.id.user_list);
        mProgressBar = findViewById(R.id.progressBar);

        mSearchField = findViewById(R.id.search_edit_text);
        mSearchField.addTextChangedListener(searchTextChanged);

        mBackImage = findViewById(R.id.back_image);
        mBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getLists();
    }

    TextWatcher searchTextChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            searchForUser(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void searchForUser(String searchText) {
        ArrayList<User> searchedList = new ArrayList<>();

        if (!mAllUserList.isEmpty()) {
            for (User user : mAllUserList) {
                if (user.getmUserName().toLowerCase().contains(searchText.toLowerCase())) {
                    searchedList.add(user);
                }
            }

            mAdapter.filterList(searchedList);
        }
    }

    private void getLists() {
        setUpProgressBar();

        mUserListView = findViewById(R.id.user_list);
        mUserListView.setHasFixedSize(true);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get all users
        db
                .collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // TODO set onFailure
                            return;
                        }

                        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                            final ArrayList<User> allUsersList = new ArrayList<>();

                            for (QueryDocumentSnapshot document: queryDocumentSnapshots) {

                                String username = (String) document.get("username");
                                String imageDownloadUrl = (String) document.get("downloadUrl");
                                String userUid = (String) document.get("userUid");

                                if (!userUid.equals(mCurrentUser))
                                    allUsersList.add(new User(imageDownloadUrl, username, userUid, false, false, false));

                            }

                            // Get friend list
                            db
                                    .collection("users")
                                    .document(mCurrentUser)
                                    .collection("friends")
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                                // TODO set onFailure
                                                return;
                                            }
                                            final ArrayList<String> friendList = new ArrayList<>();

                                            for (QueryDocumentSnapshot document: queryDocumentSnapshots) {

                                                String friendUid = document.getId();

                                                friendList.add(friendUid);

                                            }

                                            // Get received friend requests
                                            db
                                                    .collection("friend_requests")
                                                    .document(mCurrentUser)
                                                    .collection("received_requests")
                                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                            if (e != null) {
                                                                // TODO set onFailure
                                                                return;
                                                            }

                                                            final ArrayList<String> recFriendReq = new ArrayList<>();

                                                            for (QueryDocumentSnapshot document: queryDocumentSnapshots) {

                                                                String friendReqUid = document.getId();

                                                                recFriendReq.add(friendReqUid);

                                                            }

                                                            // Get send friend requests
                                                            db
                                                                    .collection("friend_requests")
                                                                    .document(mCurrentUser)
                                                                    .collection("sent_requests")
                                                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                                            if (e != null) {
                                                                                // TODO set onFailure
                                                                                return;
                                                                            }

                                                                            ArrayList<String> sentFriendReq = new ArrayList<>();

                                                                            for (QueryDocumentSnapshot document: queryDocumentSnapshots) {

                                                                                String friendReqUid = document.getId();

                                                                                sentFriendReq.add(friendReqUid);

                                                                            }

                                                                            iterateLists(allUsersList, friendList, recFriendReq, sentFriendReq);

                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });

                        } else {
                            // TODO data == null
                        }
                    }
                });
    }

    private void setUpProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void iterateLists(ArrayList<User> allUserList, ArrayList<String> friendList, ArrayList<String> recFriendReq, ArrayList<String> sentFriendReq) {
        mAllUserList = allUserList;

        // Check if allUserList contains a friend
        if (!friendList.isEmpty()) {
            for (int i = 0; i < friendList.size(); i++) {
                for (User anAllUserList : allUserList) {
                    if (anAllUserList.getUserUid().equals(friendList.get(i))) {
                        anAllUserList.setFriend(true);
                    }
                }
            }
        } else {
            for (User user: allUserList) {
                user.setFriend(false);
            }
        }

        // Check if allUserList contains a received request
        if (!recFriendReq.isEmpty()) {
            for (int i = 0; i < recFriendReq.size(); i++) {
                for (User anAllUserList : allUserList) {
                    if (anAllUserList.getUserUid().equals(recFriendReq.get(i))) {
                        anAllUserList.setReceivedFriendRequest(true);
                    }
                }
            }
        } else {
            for (User user: allUserList) {
                user.setReceivedFriendRequest(false);
            }
        }

        // Check if allUserList contains a sent request
        if (!sentFriendReq.isEmpty()) {
            for (int i = 0; i < sentFriendReq.size(); i++) {
                for (User anAllUserList : allUserList) {
                    if (anAllUserList.getUserUid().equals(sentFriendReq.get(i))) {
                        anAllUserList.setSendFriendRequest(true);
                    }
                }
            }
        } else {
            for (User user: allUserList) {
                user.setSendFriendRequest(false);
            }
        }

        setUpAdapter(allUserList);
    }

    private void setUpAdapter(ArrayList<User> userList) {
        mAdapter = new UserListAdapter(this, userList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mUserListView.setLayoutManager(layoutManager);
        mUserListView.setAdapter(mAdapter);

        setProgresBarInvisible();
    }

    private void setProgresBarInvisible() {
        mProgressBar.setVisibility(View.GONE);
    }
}