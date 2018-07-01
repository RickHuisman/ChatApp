package com.example.rickh.chatapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.rickh.chatapp.R;
import com.example.rickh.chatapp.models.Chat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    Chat chat;

    EditText mMessageInput;
    String message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chat = (Chat) getIntent().getExtras().getSerializable("chatInfo");

        TextView toolbarTitleText = findViewById(R.id.toolbar_title_text);
        toolbarTitleText.setText(chat.getTitle());

        mMessageInput = findViewById(R.id.message_input);

        FloatingActionButton sendMessageFab = findViewById(R.id.send_message_fab);
        sendMessageFab.setOnClickListener(sendMessageClick);

        setUpChatIcon();
    }

    View.OnClickListener sendMessageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sendMessage();
        }
    };

    private void sendMessage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference sendMessageRef =
                db.collection("chats")
                .document(chat.getId())
                .collection("messages")
                .document();


        String currentUser = FirebaseAuth.getInstance().getUid();

        Date currentTime = Calendar.getInstance().getTime();

        message = mMessageInput.getText().toString();

        if (TextUtils.isEmpty(message)) {
            return;
        } else {

            // groupId
                // message id, m0
                    // message text
                    // user
                    // time the message was send

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("messageText", message);
            messageMap.put("userId", currentUser);
            messageMap.put("time", currentTime);

            sendMessageRef
                    .set(messageMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    private void setUpChatIcon() {
        ImageView chatIconImage = findViewById(R.id.chat_icon_image);

        if (!TextUtils.isEmpty(chat.getChatIconUrl())) {
            Glide
                    .with(this)
                    .load(chat.getChatIconUrl())
                    .into(chatIconImage);
        } else {
            chatIconImage.setImageDrawable(getDrawable(R.drawable.ic_person_add_white_24dp));
        }

        chatIconImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO chat info fragment
            }
        });
    }
}
