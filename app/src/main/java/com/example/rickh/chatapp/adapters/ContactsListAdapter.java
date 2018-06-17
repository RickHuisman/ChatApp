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
import com.example.rickh.chatapp.models.User;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

public class ContactsListAdapter extends RecyclerView.Adapter<ContactsListAdapter.ContactsListViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter,
        FastScrollRecyclerView.MeasurableAdapter {

    private Context context;

    private ArrayList<User> mUserList;

    @Override
    public int getItemViewType(int position) {
        return R.layout.item_user;
    }

    @Override
    public int getViewTypeHeight(RecyclerView recyclerView, @Nullable RecyclerView.ViewHolder viewHolder, int viewType) {
        return 0;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return mUserList.get(position).getmUserName();
    }

    public static class ContactsListViewHolder extends RecyclerView.ViewHolder {
        public ImageView mProfileImageView, mRemoveFriendImage;
        public TextView mUserNameTextView;
        public MaterialButton mSendRequestButton, mCancelRequestButton;
        public ProgressBar mProgressBar;
        public View mDivider;

        public ContactsListViewHolder(View itemView) {
            super(itemView);
            mProfileImageView = itemView.findViewById(R.id.chat_icon_image);
            mRemoveFriendImage = itemView.findViewById(R.id.remove_friend_image);
            mUserNameTextView = itemView.findViewById(R.id.chat_title_text);
            mSendRequestButton = itemView.findViewById(R.id.send_request_button);
            mCancelRequestButton = itemView.findViewById(R.id.cancel_request_button);
            mProgressBar = itemView.findViewById(R.id.progressbar);
            mDivider = itemView.findViewById(R.id.divider_view);
        }
    }

    public ContactsListAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.mUserList = userList;
    }

    @Override
    public ContactsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);

        return new ContactsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactsListViewHolder holder, int position) {
        Glide
                .with(context)
                .asBitmap()
                .apply(RequestOptions.circleCropTransform())
                .load(mUserList.get(position).getmUserProfilePicture())
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.mProfileImageView.setImageBitmap(resource);
                    }
                });

        if (holder.getAdapterPosition() == getItemCount() - 1)
            holder.mDivider.setVisibility(View.GONE);

        holder.mUserNameTextView.setText(mUserList.get(holder.getAdapterPosition()).getmUserName());
        holder.mProgressBar.setVisibility(View.GONE);
        holder.mSendRequestButton.setVisibility(View.GONE);
        holder.mCancelRequestButton.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }
}