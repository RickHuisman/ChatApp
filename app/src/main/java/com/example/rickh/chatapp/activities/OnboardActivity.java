package com.example.rickh.chatapp.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.rickh.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class OnboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboard);

        MaterialButton mSignUpButton = findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(signUpCLick);

        TextView mSignInText = findViewById(R.id.sign_in_text);
        mSignInText.setOnClickListener(signInClick);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = authListener;
    }

    View.OnClickListener signUpCLick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
        }
    };

    View.OnClickListener signInClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    };

    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (firebaseAuth.getCurrentUser() != null)
                startHomeActivity();
        }
    };

    private void startHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
        this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }
}
