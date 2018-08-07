package com.example.rickh.chatapp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.rickh.chatapp.R;
import com.example.rickh.chatapp.adapters.CreateChatListAdapter;
import com.example.rickh.chatapp.models.Contact;
import com.example.rickh.chatapp.models.ContactChip;
import com.example.rickh.chatapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;

public class SelectContactsFragment extends Fragment implements CreateChatListAdapter.AdapterCallback {

    private View mView;
    private LinearLayout mChipContainer;
    private CreateChatListAdapter mAdapter;

    private ArrayList<Contact> mContactList;
    private ArrayList<Contact> mSelectedContactsList;
    private ArrayList<ContactChip> mChipList;

    private SelectContactsFragment.NextFragment mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_select_contacts, container, false);

        setUpToolbar();
        getContacts();
        setUpFab();

        mSelectedContactsList = new ArrayList<>();
        mChipList = new ArrayList<>();

        mChipContainer = mView.findViewById(R.id.chip_container_layout);

        return mView;
    }

    private void setUpToolbar() {
        Toolbar toolbar = mView.findViewById(R.id.toolbar);

        ImageView backImage = mView.findViewById(R.id.back_image);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        toolbar.inflateMenu(R.menu.menu_select_contacts);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                MenuItem mSearch = item;
                SearchView searchView = (SearchView) item.getActionView();
                searchView.setMaxWidth(Integer.MAX_VALUE);

                ImageView test = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
                test.setColorFilter(R.color.colorTextHint);

                SearchView mSearchView = (SearchView) mSearch.getActionView();
                mSearchView.setQueryHint("Search");

                mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        filter(newText);
                        return true;
                    }
                });
                return false;
            }
        });
    }

    private void setUpFab() {
        FloatingActionButton nextFragmentFab = mView.findViewById(R.id.quick_action_fab);
        nextFragmentFab.setOnClickListener(onFabClick);
    }

    private void setUpRecyclerView(ArrayList<Contact> contactsList) {
        RecyclerView mRecyclerView = mView.findViewById(R.id.contacts_list);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());

        mAdapter = new CreateChatListAdapter(this, getContext(), contactsList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void getContacts() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final CollectionReference contactsRef = db.collection("users").document(currentUser).collection("friends");

        contactsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                mContactList = new ArrayList<>();

                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                        String contactUid = queryDocumentSnapshots.getDocuments().get(i).get("userUid").toString();

                        final int finalI = i;
                        db
                                .collection("users")
                                .document(contactUid)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        String username = (String) task.getResult().get("username");
                                        String imageDownloadUrl = (String) task.getResult().get("downloadUrl");
                                        String userUid = (String) task.getResult().get("userUid");

                                        if (!userUid.equals(currentUser))
                                            mContactList.add(new Contact(finalI, userUid, imageDownloadUrl, username));

                                        setUpRecyclerView(mContactList);
                                    }
                                });
                    }
                } else {
                    setUpRecyclerView(mContactList);
                }
            }
        });
    }

    View.OnClickListener onFabClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mSelectedContactsList.size() > 0) {
                mCallback.fabClicked(mSelectedContactsList);
            } else {
                showSnackBar();
            }
        }
    };

    public void showSnackBar() {
        Snackbar snackbar = Snackbar.make(mView.findViewById(R.id.container), "Make sure to select atleast one contact", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void filter(String text) {
        ArrayList<Contact> filteredList = new ArrayList<>();

        for (Contact contact : mContactList) {
            if (contact.getmContactName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(contact);
            }
        }

        mAdapter.filterList(filteredList);
    }

    private void createChip(final String imageURL, int contactId, String text) {
        final ContactChip chip = new ContactChip(getContext(), contactId);

        chip.setChipIconEnabled(true);
        chip.setTextAppearanceResource(R.style.ChatApp_Chip_Text);
        chip.setChipBackgroundColorResource(R.color.colorAccent);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.circleCrop();
        requestOptions.placeholder(R.drawable.ic_placeholder_24dp);

        Glide
                .with(getContext())
                .asDrawable()
                .apply(requestOptions)
                .load(imageURL)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        chip.setChipIcon(resource);
                    }
                });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        int marginPx = convertDptoPx(5);
        lp.setMargins(
                marginPx,
                marginPx,
                marginPx,
                marginPx);
        chip.setLayoutParams(lp);

        chip.setChipText(text);

        mChipContainer.addView(chip);
        mChipList.add(chip);
    }

    private int convertDptoPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (SelectContactsFragment.NextFragment) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onRowSelected(int position) {
        String imageURL = mContactList.get(position).getmContactProfilePicture();
        String title = mContactList.get(position).getmContactName();
        Contact contact = mContactList.get(position);

        if (!mSelectedContactsList.contains(contact)) {
            mSelectedContactsList.add(contact);
            createChip(imageURL, position, title);
        } else {
            for(Iterator<Contact> iterator = mSelectedContactsList.iterator(); iterator.hasNext(); ) {
                if(iterator.next().getId() == position)
                    iterator.remove();
            }
            for(Iterator<ContactChip> iterator = mChipList.iterator(); iterator.hasNext(); ) {
                if(iterator.next().getContactId() == position) {
                    for (ContactChip contactChip : mChipList) {
                        if (contactChip.getContactId() == position) {
                            mChipContainer.removeView(mChipList.get(mChipList.indexOf(contactChip)));
                            break;
                        }
                    }
                    iterator.remove();
                }
            }
        }
    }

    public interface NextFragment {
        void fabClicked(ArrayList<Contact> selectedContacts);
    }
}
