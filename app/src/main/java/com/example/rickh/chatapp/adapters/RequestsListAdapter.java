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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class RequestsListAdapter extends RecyclerView.Adapter<RequestsListAdapter.RequestsListViewHolder> {

    private Context context;
    private FirebaseFirestore db;
    private ArrayList<User> mUserList;
    private int mTypeRequest;
    private String mCurrentUserUID;

    public static class RequestsListViewHolder extends RecyclerView.ViewHolder {
        public ImageView mProfileImageView, mRemoveFriendImage;
        public TextView mUserNameTextView;
        public MaterialButton mSendRequestButton, mCancelRequestButton;
        public ProgressBar mProgressBar;
        public View mDivider;

        public RequestsListViewHolder(View itemView) {
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

    public RequestsListAdapter(Context context, ArrayList<User> userList, int typeRequest) {
        this.context = context;
        this.mUserList = userList;
        this.mTypeRequest = typeRequest;
    }

    @Override
    public RequestsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);

        db = FirebaseFirestore.getInstance();
        mCurrentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return new RequestsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RequestsListViewHolder holder, int position) {
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
        if (mTypeRequest == 1) {
            holder.mCancelRequestButton.setVisibility(View.VISIBLE);
            holder.mCancelRequestButton.setText("ACCEPT REQUEST");
        }

        acceptRequest(holder, mUserList.get(holder.getAdapterPosition()), holder.getAdapterPosition());

        removeRequest(holder, mUserList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
    }

    private void acceptRequest(final RequestsListViewHolder holder, final User user, final int position) {
        holder.mCancelRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mProgressBar.setVisibility(View.VISIBLE);

                db
                        .collection("friend_requests")
                        .document(mCurrentUserUID)
                        .collection("received_requests")
                        .document(user.getUserUid())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                db
                                        .collection("friend_requests")
                                        .document(user.getUserUid())
                                        .collection("sent_requests")
                                        .document(mCurrentUserUID)
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                setUserFriend(holder, user, position);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                System.out.println(e.getMessage());
                                            }
                                        });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println(e.getMessage());
                            }
                        });
            }
        });
    }

    private void setUserFriend(final RequestsListViewHolder holder, final User user, final int position) {
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("userUid", user.getUserUid());

        db
                .collection("users")
                .document(mCurrentUserUID)
                .collection("friends")
                .document(user.getUserUid())
                .set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        HashMap<String, String> currentUserMap = new HashMap<>();
                        currentUserMap.put("userUid", mCurrentUserUID);

                        db
                                .collection("users")
                                .document(user.getUserUid())
                                .collection("friends")
                                .document(mCurrentUserUID)
                                .set(currentUserMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        holder.mProgressBar.setVisibility(View.GONE);
                                        mUserList.get(position).setFriend(true);
                                        notifyItemChanged(position);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                    }
                });
    }

    private void removeRequest(final RequestsListViewHolder holder, final User user, final int position) {
        holder.mRemoveFriendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mTypeRequest) {
                    case 0:
                        holder.mProgressBar.setVisibility(View.VISIBLE);

                        db
                                .collection("friend_requests")
                                .document(mCurrentUserUID)
                                .collection("sent_requests")
                                .document(user.getUserUid())
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        db
                                                .collection("friend_requests")
                                                .document(user.getUserUid())
                                                .collection("received_requests")
                                                .document(mCurrentUserUID)
                                                .delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        holder.mProgressBar.setVisibility(View.GONE);
                                                        mUserList.get(position).setReceivedFriendRequest(false);
                                                        notifyItemChanged(position);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        System.out.println(e.getMessage());
                                                    }
                                                });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                });
                        break;
                    case 1:
                        holder.mProgressBar.setVisibility(View.VISIBLE);

                        db
                                .collection("friend_requests")
                                .document(mCurrentUserUID)
                                .collection("received_requests")
                                .document(user.getUserUid())
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        db
                                                .collection("friend_requests")
                                                .document(user.getUserUid())
                                                .collection("sent_requests")
                                                .document(mCurrentUserUID)
                                                .delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        holder.mProgressBar.setVisibility(View.GONE);
                                                        mUserList.get(position).setReceivedFriendRequest(false);
                                                        notifyItemChanged(position);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        System.out.println(e.getMessage());
                                                    }
                                                });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                });
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }
}