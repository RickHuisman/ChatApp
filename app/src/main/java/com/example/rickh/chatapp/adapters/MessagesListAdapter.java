package com.example.rickh.chatapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rickh.chatapp.R;
import com.example.rickh.chatapp.models.Message;

import java.util.ArrayList;

public class MessagesListAdapter extends RecyclerView.Adapter<MessagesListAdapter.MessagesListViewHolder> {

    private Context mContext;

    private ArrayList<Message> mMessagesList;

    public static class MessagesListViewHolder extends RecyclerView.ViewHolder {
//        public ImageView mChatIconImage;
//        public TextView mTitleText, mLastMessageText, mLastMessageTimeText;
//        public View mDivider;
//
        TextView mMessageText;

        public MessagesListViewHolder(View itemView) {
            super(itemView);
            mMessageText = itemView.findViewById(R.id.message_text_layout);
//            mChatIconImage = itemView.findViewById(R.id.chat_icon_image);
//            mTitleText = itemView.findViewById(R.id.chat_title_text);
//            mLastMessageText = itemView.findViewById(R.id.last_message_text);
//            mLastMessageTimeText = itemView.findViewById(R.id.last_message_time_text);
//            mDivider = itemView.findViewById(R.id.divider_view);
        }
    }

    public MessagesListAdapter(Context context, ArrayList<Message> mMessagesList) {
        this.mContext = context;
        this.mMessagesList = mMessagesList;
    }

    @Override
    public MessagesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);

        return new MessagesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MessagesListViewHolder holder, int position) {
        final Message message = mMessagesList.get(holder.getAdapterPosition());

        holder.mMessageText.setText(message.getMessageText());

//        if (chat.getChatIconUrl() != null)
//            Glide
//                    .with(mContext)
//                    .load("https://www.gravatar.com/avatar/4181551b7cb8eeb7cfa7f0f3d50f3ded?s=48&d=identicon&r=PG&f=1")
//                    .into(holder.mChatIconImage);
//
////            Glide
////                    .with(mContext)
////                    .asBitmap()
////                    .apply(RequestOptions.circleCropTransform())
////                    .load(mChatList.get(position).getChatIconUrl())
////                    .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
////                        @Override
////                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
////                            holder.mChatIconImage.setImageDrawable("https://www.gravatar.com/avatar/4181551b7cb8eeb7cfa7f0f3d50f3ded?s=48&d=identicon&r=PG&f=1");
////                        }
////                    });
//
//        if (holder.getAdapterPosition() == getItemCount() - 1)
//            holder.mDivider.setVisibility(View.GONE);
//
//        if (chat.getTitle() != null)
//            holder.mTitleText.setText(chat.getTitle());
//
//        if (chat.getLastMessage() != null)
//            holder.mLastMessageText.setText(chat.getLastMessage());
//
//        if (chat.getLastMessageTime() != null)
//            holder.mLastMessageTimeText.setText(chat.getLastMessageTime());
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mContext.startActivity(new Intent(mContext, ChatActivity.class).putExtra("chatInfo", chat));
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }
}