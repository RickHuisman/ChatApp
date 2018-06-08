package com.example.rickh.chatapp.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.rickh.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String mCurrentUser;
    private FirebaseFirestore mDatabase;

    private ImageView mCloseImage, mDoneImage, mSelectProfileImage;
    private TextInputEditText mEmailInput, mUsernameInput, mPasswordInput;
    private TextInputLayout mEmailLayout, mUsernameLayout, mPasswordLayout;

    private static final int PICK_IMAGE = 100;
    private Uri imageUri;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        mCloseImage = findViewById(R.id.close_image);
        mCloseImage.setOnClickListener(closeClick);

        mDoneImage = findViewById(R.id.done_image);
        mDoneImage.setOnClickListener(doneClick);

        mSelectProfileImage = findViewById(R.id.select_profile_image);
        mSelectProfileImage.setOnClickListener(selectProfileImageClick);

        mEmailInput = findViewById(R.id.email_edit_text);
        mUsernameInput = findViewById(R.id.username_edit_text);
        mPasswordInput = findViewById(R.id.password_edit_text);

        mEmailLayout = findViewById(R.id.email_text_input_layout);
        mUsernameLayout = findViewById(R.id.username_text_input_layout);
        mPasswordLayout = findViewById(R.id.password_text_input_layout);
    }

    View.OnClickListener closeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    View.OnClickListener doneClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email = mEmailInput.getText().toString();
            String username = mUsernameInput.getText().toString();
            String password = mPasswordInput.getText().toString();

            if (validateForm(email, username, password)) {
                if (imageUri == null) {
                    buildConformationDialog(email, username, password);
                } else {
                    createAccount(email, username, password);
                }
            }
        }
    };

    View.OnClickListener selectProfileImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            openGallery();
        }
    };

    private void buildConformationDialog(final String email, final String username, final String password) {
        AlertDialog.Builder confromationDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.ChatApp_Theme_CustomDialog));
        confromationDialog.setTitle("Create account without selecting a profile picture?");
        confromationDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                createAccount(email, username, password);
            }
        });
        confromationDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                dialogInterface.cancel();
            }
        });

        confromationDialog.create();
        confromationDialog.show();
    }

    private boolean validateForm(String email, String username, String password) {
        boolean valid = true;

        if (!isValidEmail(email)) {
            setHelperError(mEmailLayout, "Error: Email is not valid");
            valid = false;
        } else {
            resetHelperError(mEmailLayout);
        }

        if (TextUtils.getTrimmedLength(username) <= 1) {
            setHelperError(mUsernameLayout, "Error: At least 2 characters");
            valid = false;
        } else {
            resetHelperError(mUsernameLayout);
        }

        if (TextUtils.getTrimmedLength(password) < 6) {
            setHelperError(mPasswordLayout, "Error: At least 6 characters");
            valid = false;
        } else {
            resetHelperError(mPasswordLayout);
        }

        return valid;
    }

    private boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void resetHelperError(TextInputLayout inputLayout) {
        inputLayout.setHelperTextEnabled(false);
    }

    private void setHelperError(TextInputLayout inputLayout, String error) {
        inputLayout.setHelperTextEnabled(true);
        inputLayout.setHelperTextTextAppearance(R.style.TextAppearance_Design_Error);
        inputLayout.setHelperText(error);
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void buildProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating a new account...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void createAccount(final String email, final String username, String password) {
        buildProgressDialog();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        mAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mCurrentUser = mAuth.getCurrentUser().getUid();

                            if (!(imageUri == null)) {
                                StorageReference randomImageRef = storageReference.child("userprofileimages/" + UUID.randomUUID());

                                randomImageRef
                                        .putFile(imageUri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                mDatabase.collection("users").document(mCurrentUser)
                                                        .set(userMap(email, username, taskSnapshot.getUploadSessionUri().toString(), mCurrentUser))
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                startHomeActivity();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                failedRegister();
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                failedRegister();
                                            }
                                        });
                            } else {
                                mDatabase.collection("users").document(mCurrentUser)
                                        .set(userMap(email, username, "null", mCurrentUser))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                startHomeActivity();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                failedRegister();
                                            }
                                        });
                            }
                        } else {
                            failedRegister();
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                setHelperError(mPasswordLayout, "Error: Password is to short");
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                setHelperError(mEmailLayout, "Error: Invalid email");
                            } catch(FirebaseAuthUserCollisionException e) {
                                setHelperError(mEmailLayout, "Error: User already exists");
                            } catch(Exception e) {
                                Log.e("CreateAccountDialog", e.getMessage());
                            }
                        }
                    }
                });
    }

    private void startHomeActivity() {
        startActivity(new Intent(this, MainActivity.class));
        progressDialog.cancel();
        finishAffinity();
    }

    private void failedRegister() {
        progressDialog.dismiss();
        hideSoftKeyboard();
        showRegisterFailedSnackBar();
    }

    private void showRegisterFailedSnackBar() {
        Snackbar.make(findViewById(R.id.container), "Failed to create a account", Snackbar.LENGTH_SHORT).show();
    }

    private void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } else {
        }
    }

    private Map<String, Object> userMap(String email, String username, String imageUrl, String userUid) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("username", username);
        user.put("downloadUrl", imageUrl);
        user.put("userUid", userUid);

        return user;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            ImageView profileImage = findViewById(R.id.profile_image);
            Glide
                    .with(this)
                    .load(imageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);
        }
    }
}
