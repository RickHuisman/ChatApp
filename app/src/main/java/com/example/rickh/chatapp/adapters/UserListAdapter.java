package com.example.rickh.chatapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
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
import java.util.Map;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    private Context context;

    private View mView;

    private ArrayList<User> mUserList;

    private FirebaseFirestore db;
    private String mCurrentUserUID;

    public static class UserListViewHolder extends RecyclerView.ViewHolder {
        public ImageView mProfileImageView, mRemoveFriendImage;
        public TextView mUserNameTextView;
        public MaterialButton mSendRequestButton, mCancelRequestButton;
        public View mDivider;
        public ProgressBar mProgressBar;

        public UserListViewHolder(View itemView) {
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

    public UserListAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.mUserList = userList;
    }

    @Override
    public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);

        db = FirebaseFirestore.getInstance();
        mCurrentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return new UserListViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final UserListViewHolder holder, int position) {
        User currentUser = mUserList.get(holder.getAdapterPosition());

        Glide.with(context)
                .asBitmap()
                .apply(RequestOptions.circleCropTransform())
                .load(mUserList.get(position).getmUserProfilePicture())
                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.mProfileImageView.setImageBitmap(resource);
                    }
                });

        holder.mUserNameTextView.setText(currentUser.getmUserName());

        holder.mProgressBar.setVisibility(View.GONE);

        if (holder.getAdapterPosition() == getItemCount() - 1)
            holder.mDivider.setVisibility(View.GONE);

        setVisButtons(holder, currentUser);

        if (holder.mSendRequestButton != null) {
            sentFriendRequest(
                    holder,
                    currentUser,
                    holder.getAdapterPosition());
        }

        if (holder.mCancelRequestButton != null) {
            if (currentUser.isReceivedFriendRequest()) {
                // received
                acceptFriendRequest(holder, currentUser, holder.getAdapterPosition());
            } else if (currentUser.isSendFriendRequest()) {
                // sent
                cancelFriendRequest(holder, currentUser, holder.getAdapterPosition());
            }
        }

        if (holder.mRemoveFriendImage != null) {
            if (currentUser.isReceivedFriendRequest()) {
                removeRequest(holder, currentUser, holder.getAdapterPosition());
            } else if(currentUser.isFriend()) {
                removeFriend(holder, currentUser, holder.getAdapterPosition());
            }
        }
    }

    private void buildSuccessSnackBar(String text) {
        Snackbar.make(mView, text, Snackbar.LENGTH_SHORT).setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();
    }

    private void buildErrorSnackBar(String text) {
        Snackbar.make(mView, text, Snackbar.LENGTH_SHORT).show();
    }

    public void filterList(ArrayList<User> filteredList) {
        mUserList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    private void setVisButtons(UserListViewHolder holder, User currentUser) {
        if (currentUser.isFriend()) {
            // User is friend - visible removeFriendImage
            holder.mRemoveFriendImage.setVisibility(View.VISIBLE);
            holder.mSendRequestButton.setVisibility(View.GONE);
            holder.mCancelRequestButton.setVisibility(View.GONE);
        } else if (currentUser.isSendFriendRequest()) {
            // User sent friend request - visible cancelRequestButton
            holder.mCancelRequestButton.setVisibility(View.VISIBLE);
            holder.mSendRequestButton.setVisibility(View.GONE);
            holder.mRemoveFriendImage.setVisibility(View.GONE);
        } else if (currentUser.isReceivedFriendRequest()) {
            // User received friend request - visible cancelRequest
            holder.mCancelRequestButton.setVisibility(View.VISIBLE);
            holder.mCancelRequestButton.setText("ACCEPT REQUEST");
            holder.mSendRequestButton.setVisibility(View.GONE);
            holder.mRemoveFriendImage.setVisibility(View.VISIBLE);
        }else {
            // User isn't a friend - visible sentRequest
            holder.mSendRequestButton.setVisibility(View.VISIBLE);
            holder.mCancelRequestButton.setVisibility(View.GONE);
            holder.mRemoveFriendImage.setVisibility(View.GONE);
        }
    }

    private void removeRequest(final UserListViewHolder holder, final User user, final int position) {
        holder.mRemoveFriendImage.setOnClickListener(new View.OnClickListener() {
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
            }
        });
    }

    private void acceptFriendRequest(final UserListViewHolder holder, final User user, final int position) {
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

    private void setUserFriend(final UserListViewHolder holder, final User user, final int position) {
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
                        buildErrorSnackBar("Failed to accept friend request from " + user.getUserUid());
                    }
                });
    }

    private void cancelFriendRequest(final UserListViewHolder holder, final User user, final int position) {
        // remove own request
        holder.mCancelRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                                user.setSendFriendRequest(false);
                                                notifyItemChanged(position);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                buildErrorSnackBar("Failed to cancel friend request to " + user.getUserUid());
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                buildErrorSnackBar("Failed to cancel friend request to " + user.getUserUid());
                            }
                        });
            }
        });
    }

    private void sentFriendRequest(final UserListViewHolder holder, final User user, final int position) {
        holder.mSendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mProgressBar.setVisibility(View.VISIBLE);
                db
                        .collection("friend_requests")
                        .document(mCurrentUserUID)
                        .collection("sent_requests")
                        .document(user.getUserUid())
                        .set(mapSender("sent"))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                db
                                        .collection("friend_requests")
                                        .document(user.getUserUid())
                                        .collection("received_requests")
                                        .document(mCurrentUserUID)
                                        .set(mapSender("received"))
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                holder.mProgressBar.setVisibility(View.GONE);
                                                user.setSendFriendRequest(true);
                                                notifyItemChanged(position);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                buildErrorSnackBar("Failed to send a friend request to " + user.getmUserName());
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                buildErrorSnackBar("Failed to send a friend request to " + user.getmUserName());
                            }
                        });
            }
        });
    }

    private void removeFriend(final UserListViewHolder holder, final User currentUser, final int position) {
        holder.mRemoveFriendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mProgressBar.setVisibility(View.VISIBLE);

                db
                        .collection("users")
                        .document(mCurrentUserUID)
                        .collection("friends")
                        .document(currentUser.getUserUid())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                db
                                        .collection("users")
                                        .document(currentUser.getUserUid())
                                        .collection("friends")
                                        .document(mCurrentUserUID)
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                holder.mProgressBar.setVisibility(View.GONE);
                                                notifyItemChanged(position);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                buildErrorSnackBar("Failed to remove friend: " + currentUser.getUserUid());
                                            }
                                        });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                buildErrorSnackBar("Failed to remove friend: " + mUserList.get(position).getmUserName() + ", "+ mUserList.get(holder.getAdapterPosition()).getUserUid());
                            }
                        });
            }
        });
    }

    private Map<String, Object> mapSender(String requestType) {
        Map<String, Object> mapSender = new HashMap<>();
        mapSender.put("request_type", requestType);

        return mapSender;
    }
}