package com.example.kvo.bearchat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Set;

public class SignUpActivity extends AppCompatActivity {

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mPasswordConfirmField;
    private EditText mUsernameField;
    private Button mSignUpButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmailField = (EditText) findViewById(R.id.email_sign_up_text);
        mPasswordField = (EditText) findViewById(R.id.password_sign_up_text);
        mPasswordConfirmField = (EditText) findViewById(R.id.password_sign_up_confirm_text);
        mUsernameField = (EditText) findViewById(R.id.username_sign_up_text);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setOnClickForSignUpButton();

    }

    private void setOnClickForSignUpButton() {
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateForm()) {
                    return;
                }
                String email = mEmailField.getText().toString();
                String password = mPasswordField.getText().toString();
                final String username = mUsernameField.getText().toString();

                mDatabase.child("Users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String email = mEmailField.getText().toString();
                        String password = mPasswordField.getText().toString();
                        final String username = mUsernameField.getText().toString();
                        if (dataSnapshot.exists()) {
                            Log.e("USERNAME", "EXIISSTTSSS");
                            mUsernameField.setError("Username already exists.");


                        } else {
                            Log.e("USERNAME", "dono exists");
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d("hi", "createUserWithEmail:success");
                                                FirebaseUser user = mAuth.getCurrentUser();

                                                String uid = user.getUid();
                                                mDatabase.child("User_Lookup").child(uid).setValue(username);
                                                mDatabase.child("Users").child(username).setValue("hi");
                                                Intent landmarkFeedIntent = new Intent(SignUpActivity.this,
                                                        LandmarkFeedActivity.class);
                                                landmarkFeedIntent.putExtra("usernameString", username);
                                                SignUpActivity.this.startActivity(landmarkFeedIntent);
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w("hi", "createUserWithEmail:failure", task.getException());
                                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailField.setError("Invalid Email.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        String confirmPassword = mPasswordConfirmField.getText().toString();
        if (!password.equals(confirmPassword)) {
            mPasswordField.setError("Passwords don't match.");
            mPasswordConfirmField.setError("Passwords don't match.");
            valid = false;
        }

        String username = mUsernameField.getText().toString();
        if (TextUtils.isEmpty(username)) {
            mUsernameField.setError("Required.");
            valid = false;
        } else {
            mUsernameField.setError(null);
        }

        return valid;
    }
}
