package com.example.rickh.chatapp.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.rickh.chatapp.R;
import com.example.rickh.chatapp.activities.HomeActivity;
import com.example.rickh.chatapp.adapters.ParticipantsListAdapter;
import com.example.rickh.chatapp.models.Contact;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class CreateGroupChatFragment extends Fragment {

    private View mView;

    private FirebaseFirestore db;

    private RecyclerView mParticipantsView;
    private ArrayList<Contact> mParticipantsList;
    private ParticipantsListAdapter mAdapter;

    private TextInputEditText mGroupNameText;
    private MaterialButton mCreateChatButton;
    private ImageView mGroupPhotoImage;
    private LinearLayout mGroupPictureContainer;

    private String mGroupName;

    private ProgressDialog mProgressDialog;

    private static final int PICK_IMAGE = 100;
    private Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_create_group_chat, container, false);

        db = FirebaseFirestore.getInstance();

        Bundle bundle = getArguments();
        mParticipantsList = (ArrayList<Contact>) bundle.getSerializable("selectedContacts");

        mCreateChatButton = mView.findViewById(R.id.create_chat_button);
        mCreateChatButton.setOnClickListener(createChatClick);
        mCreateChatButton.setEnabled(false);

        mGroupNameText = mView.findViewById(R.id.group_name_edit_text);
        mGroupNameText.addTextChangedListener(groupNameListener);

        mGroupPictureContainer = mView.findViewById(R.id.group_picture_container);
        mGroupPictureContainer.setOnClickListener(pictureContainerClick);

        mGroupPhotoImage = mView.findViewById(R.id.group_photo_image);

        setUpParticipantsView();

        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();

            Glide
                    .with(getContext())
                    .load(imageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(mGroupPhotoImage);

            validateForm(mGroupName);
        }
    }

    View.OnClickListener createChatClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            createChat();
        }
    };

    View.OnClickListener pictureContainerClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openGallery();
        }
    };

    TextWatcher groupNameListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mGroupName = charSequence.toString();
            validateForm(mGroupName);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private boolean validateForm(String groupName) {
        boolean validated = true;

        if (TextUtils.isEmpty(groupName)) {
            validated = false;
        }

        if (imageUri == null) {
            validated = false;
        }

        if (validated)
            mCreateChatButton.setEnabled(true);
        else
            mCreateChatButton.setEnabled(false);

        return validated;
    }

    private void createChat() {
        if (!validateForm(mGroupName)) {
            return;
        }

        createProgressDialog();

        final HashMap<String, Object> baseChatMap = new HashMap<>();
        baseChatMap.put("title", mGroupName);

        mParticipantsList.add(new Contact(123, FirebaseAuth.getInstance().getUid(), "", ""));

        final HashMap<String, Object> membersMap = new HashMap<>();
        for (int i = 0; i < mParticipantsList.size(); i++) {
            membersMap.put("m" + i, mParticipantsList.get(i).getUserUid());
        }

        baseChatMap.put("members", membersMap);

        final DocumentReference chatReference = db.collection("chats").document();

        chatReference
                .set(baseChatMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        for (int i = 0; i < mParticipantsList.size(); i++) {
                            HashMap<String, String> chatMap = new HashMap<>();
                            chatMap.put("chatUid", chatReference.getId());

                            db
                                    .collection("users")
                                    .document(mParticipantsList.get(i).getUserUid())
                                    .collection("chats")
                                    .document(chatReference.getId())
                                    .set(chatMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mProgressDialog.cancel();
                                            startActivity(new Intent(getContext(), HomeActivity.class));
                                            getActivity().finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void createProgressDialog() {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void setUpParticipantsView() {
        mParticipantsView = mView.findViewById(R.id.participants_list);

        mParticipantsView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());

        mAdapter = new ParticipantsListAdapter(getContext(), mParticipantsList);

        mParticipantsView.setLayoutManager(mLayoutManager);
        mParticipantsView.setAdapter(mAdapter);
    }
}
