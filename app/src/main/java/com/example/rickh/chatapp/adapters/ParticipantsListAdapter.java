package com.example.rickh.chatapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.rickh.chatapp.R;
import com.example.rickh.chatapp.models.Contact;

import java.util.ArrayList;

public class ParticipantsListAdapter extends RecyclerView.Adapter<ParticipantsListAdapter.ParticipantsListViewHolder> {

    private Context context;
    private ArrayList<Contact> mContactList;

    public static class ParticipantsListViewHolder extends RecyclerView.ViewHolder {
        public ImageView mProfileImageView, mRemoveFriendImage;
        public TextView mUserNameTextView;
        public MaterialButton mSendRequestButton, mCancelRequestButton;
        public ProgressBar mProgressBar;
        public View mDivider;

        public ParticipantsListViewHolder(View itemView) {
            super(itemView);
            mProfileImageView = itemView.findViewById(R.id.user_profile_image);
            mRemoveFriendImage = itemView.findViewById(R.id.remove_friend_image);
            mUserNameTextView = itemView.findViewById(R.id.user_name_text);
            mSendRequestButton = itemView.findViewById(R.id.send_request_button);
            mCancelRequestButton = itemView.findViewById(R.id.cancel_request_button);
            mProgressBar = itemView.findViewById(R.id.progressbar);
            mDivider = itemView.findViewById(R.id.divider_view);
        }
    }

    public ParticipantsListAdapter(Context context, ArrayList<Contact> contactList) {
        this.context = context;
        this.mContactList = contactList;
    }

    @NonNull
    @Override
    public ParticipantsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ParticipantsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ParticipantsListViewHolder holder, int position) {
        final Contact currentContact = mContactList.get(holder.getAdapterPosition());

        Glide
                .with(context)
                .asBitmap()
                .apply(RequestOptions.circleCropTransform())
                .load(currentContact.getmContactProfilePicture())
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.mProfileImageView.setImageBitmap(resource);
                    }
                });

        holder.mRemoveFriendImage.setVisibility(View.GONE);
        holder.mSendRequestButton.setVisibility(View.GONE);
        holder.mCancelRequestButton.setVisibility(View.GONE);
        holder.mProgressBar.setVisibility(View.GONE);

        if (holder.getAdapterPosition() == getItemCount() - 1)
            holder.mDivider.setVisibility(View.GONE);

        holder.mUserNameTextView.setText(currentContact.getmContactName());
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }
}
