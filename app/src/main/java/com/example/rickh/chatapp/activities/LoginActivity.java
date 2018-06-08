package com.example.rickh.chatapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.example.rickh.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ImageView mCloseImage;
    private MaterialButton mSignInButton;

    private TextInputEditText mEmailInput, mPasswordInput;
    private TextInputLayout mEmailLayout, mPasswordLayout;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mCloseImage = findViewById(R.id.close_image);
        mCloseImage.setOnClickListener(closeClick);

        mSignInButton = findViewById(R.id.login_button);
        mSignInButton.setOnClickListener(signInClick);

        mEmailInput = findViewById(R.id.email_edit_text);
        mPasswordInput = findViewById(R.id.password_edit_text);

        mEmailLayout = findViewById(R.id.email_text_input_layout);
        mPasswordLayout = findViewById(R.id.password_text_input_layout);
    }

    View.OnClickListener closeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    View.OnClickListener signInClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email = mEmailInput.getText().toString();
            String password = mPasswordInput.getText().toString();

            if (validateForm(email, password)) {
                login(email, password);
            }
        }
    };

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (!isValidEmail(email)) {
            if (TextUtils.getTrimmedLength(email) == 0) {
                setHelperError(mEmailLayout, "Error: Make sure to fill in the email field");
                valid = false;
            } else {
                setHelperError(mEmailLayout, "Error: Email is not valid");
                valid = false;
            }
        } else {
            resetHelperError(mEmailLayout);
        }

        if (TextUtils.getTrimmedLength(password) < 6) {
            setHelperError(mPasswordLayout, "Error: At least 6 characters");
            valid = false;
        } else {
            resetHelperError(mPasswordLayout);
        }

        return valid;
    }

    private void resetHelperError(TextInputLayout inputLayout) {
        inputLayout.setHelperTextEnabled(false);
    }

    private boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void setHelperError(TextInputLayout inputLayout, String error) {
        inputLayout.setHelperTextEnabled(true);
        inputLayout.setHelperTextTextAppearance(R.style.TextAppearance_Design_Error);
        inputLayout.setHelperText(error);
    }

    private void login(String email, String password) {
        createProgressDialog();

        mAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            startHomeActivity();
                        } else {
                            hideSoftKeyboard();
                            showLoginFailedSnackBar();

                            TextInputLayout emailLabel = findViewById(R.id.email_text_input_layout);
                            TextInputLayout passwordLabel = findViewById(R.id.password_text_input_layout);

                            setHelperError(emailLabel, "Error: Make sure you filled in the right email");
                            setHelperError(passwordLabel, "Error: Make sure you filled in the right password");
                        }
                    }
                });
    }

    private void startHomeActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }

    private void createProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Login into your account...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void hideSoftKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showLoginFailedSnackBar() {
        Snackbar loginFailed = Snackbar.make(findViewById(R.id.container), "Login failed", Snackbar.LENGTH_SHORT);
        loginFailed.show();
    }
}
