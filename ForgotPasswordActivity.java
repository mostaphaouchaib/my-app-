package com.spacester.tweetsterupdate.emailAuth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.spacester.tweetsterupdate.R;
import com.spacester.tweetsterupdate.activity.Policy;
import com.spacester.tweetsterupdate.activity.Terms;

import java.util.Objects;

@SuppressWarnings("deprecation")
public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Progress
        ProgressBar progressBar = findViewById(R.id.pb);

        //Back
        ImageView back = findViewById(R.id.imageView);
        back.setOnClickListener(v -> onBackPressed());

        //EditText
        EditText mEmail = findViewById(R.id.editText);

                        TextView privacy = findViewById(R.id.privacy);
        TextView terms = findViewById(R.id.terms);

        privacy.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Policy.class);
            startActivity(intent);
        });
        terms.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Terms.class);
            startActivity(intent);
        });

        //Continue Button
        Button next = findViewById(R.id.next);
        next.setOnClickListener(v -> {
            String email = mEmail.getText().toString().trim();
            progressBar.setVisibility(View.VISIBLE);
            if (email.isEmpty()){
                progressBar.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), "Enter Your Email Address", Toast.LENGTH_SHORT).show();
            }else{

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(this, "Reset link is sent on your email address", Toast.LENGTH_SHORT).show();


                        new Handler().postDelayed(() -> {

                            Intent intent = new Intent(getApplicationContext(), EmailActivity.class);
                            startActivity(intent);
                            finish();

                        }, 2000);

                    } else {
                        progressBar.setVisibility(View.GONE);
                        String msg = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}