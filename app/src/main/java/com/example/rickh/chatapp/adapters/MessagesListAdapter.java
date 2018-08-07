package com.example.rickh.chatapp.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rickh.chatapp.R;
import com.example.rickh.chatapp.models.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;

public class MessagesListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private ArrayList<Message> mMessagesList;

    static class MessagesListViewHolder extends RecyclerView.ViewHolder {

        ImageView mUserImage;
        TextView mMessageText, mDateText;
        ConstraintLayout mContainer;
        CardView mContainerMessage;

        MessagesListViewHolder(View itemView) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.user_image);
            mMessageText = itemView.findViewById(R.id.message_text);
            mDateText = itemView.findViewById(R.id.date_text);
            mContainer = itemView.findViewById(R.id.container_message);
            mContainerMessage = itemView.findViewById(R.id.container_message_text);
        }
    }

    public MessagesListAdapter(Context context, ArrayList<Message> messagesList) {
        this.mContext = context;
        this.mMessagesList = messagesList;

        Collections.sort(mMessagesList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);

            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);

            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessagesList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = (Message) mMessagesList.get(position);

        String currentUserId = FirebaseAuth.getInstance().getUid();

        if (message.getUserId().equals(currentUserId)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {

        ImageView mUserImage;
        TextView mMessageText, mDateText;
        ConstraintLayout mContainer;
        CardView mContainerMessage;

        SentMessageHolder(View itemView) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.user_image);
            mMessageText = itemView.findViewById(R.id.message_text);
            mDateText = itemView.findViewById(R.id.date_text);
            mContainer = itemView.findViewById(R.id.container_message);
            mContainerMessage = itemView.findViewById(R.id.container_message_text);
        }

        void bind(Message message) {
            if (!message.getMessageText().isEmpty())
                mMessageText.setText(message.getMessageText());

            if (message.getTime() != null)
                mDateText.setText(message.getTime().toString());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        TextView mMessageText, mDateText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            mMessageText = itemView.findViewById(R.id.message_text);
            mDateText = itemView.findViewById(R.id.date_text);
        }

        void bind(Message message) {
            if (!message.getMessageText().isEmpty())
                mMessageText.setText(message.getMessageText());

            if (message.getTime() != null)
                mDateText.setText(message.getTime().toString());
        }
    }
}