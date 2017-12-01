package org.projects.shoppinglist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView emailField;
    private TextView passwordField;
    private DatabaseReference mDatabase;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        setContentView(R.layout.login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field);
        user = mAuth.getCurrentUser();
        if(user != null)
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userId", user.getUid());
            startActivity(intent);
        }
    }

    public void signInAction(View view)
    {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        final LoginActivity activity = this;
        if(email.isEmpty() || password.isEmpty())
        {
            Context context = getApplicationContext();
            CharSequence text = "Email or password missing";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Auth", "signInWithEmail:success");
                            user = mAuth.getCurrentUser();
                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.putExtra("userId", user.getUid());
                            startActivity(intent);
                        } else {
                            Log.w("Auth", "signInWithEmail:failure", task.getException());
                            Context context = getApplicationContext();
                            CharSequence text = "Could not log you in.";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                });
    }

    public void signUpAction(View view)
    {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        final LoginActivity activity = this;
        if(email.isEmpty() || password.isEmpty())
        {
            Context context = getApplicationContext();
            CharSequence text = "Email or password missing";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Auth", "createUserWithEmail:success");
                            user = mAuth.getCurrentUser();
                            mDatabase.child(user.getUid()).setValue(user.getEmail());
                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.putExtra("userId", user.getUid());
                            startActivity(intent);
                        } else {
                            Log.w("Auth", "createUserWithEmail:failure", task.getException());
                            Context context = getApplicationContext();
                            CharSequence text = task.getException().getMessage();
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                });
    }

}
