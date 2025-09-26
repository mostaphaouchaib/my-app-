package com.spacester.tweetsterupdate.emailAuth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.spacester.tweetsterupdate.MainActivity;
import com.spacester.tweetsterupdate.R;
import com.spacester.tweetsterupdate.activity.Policy;
import com.spacester.tweetsterupdate.activity.Terms;

import java.util.Objects;

public class EmailActivity extends AppCompatActivity {

    EditText password;
    Button sign;
    ProgressBar progress;
    EditText mEmail;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        mAuth = FirebaseAuth.getInstance();

        //Progress
        ProgressBar progressBar = findViewById(R.id.pb);
         progress = findViewById(R.id.pb_two);

         TextView forgot= findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmailActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

         TextView textView = findViewById(R.id.textView);
         TextView textView2 = findViewById(R.id.textView2);

        //Back
        ImageView back = findViewById(R.id.imageView);
        back.setOnClickListener(v -> onBackPressed());

        //EditText
         mEmail = findViewById(R.id.editText);

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

         password = findViewById(R.id.password);
         sign = findViewById(R.id.sign);

        //Continue Button
        Button next = findViewById(R.id.next);
        next.setOnClickListener(v -> {

            String email = mEmail.getText().toString().trim();
            progressBar.setVisibility(View.VISIBLE);

            if (email.isEmpty()){

                progressBar.setVisibility(View.GONE);

                Toast.makeText(this, "Enter Your Email Address", Toast.LENGTH_SHORT).show();

            }else {

                //Check if email exist
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            Query query = ref.orderByChild("email").equalTo(email);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){

                                        progressBar.setVisibility(View.GONE);
                                        mEmail.setVisibility(View.GONE);
                                        next.setVisibility(View.GONE);
                                        password.setVisibility(View.VISIBLE);
                                        sign.setVisibility(View.VISIBLE);
                                        forgot.setVisibility(View.VISIBLE);
                                        textView2.setText("Enter Your Password");
                                        textView.setText("Hi");

                                        sign.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                login();
                                                progress.setVisibility(View.VISIBLE);
                                            }
                                        });

                                    }else {

                                        progressBar.setVisibility(View.GONE);
                                        //Email not exist
                                        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                                        intent.putExtra("email", email);
                                        startActivity(intent);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressBar.setVisibility(View.GONE);

                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {

                            progressBar.setVisibility(View.GONE);
                            //Email not exist
                            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

    }

    private void login() {
        String mPassword = password.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        if (mPassword.isEmpty()){
            progress.setVisibility(View.GONE);
            Toast.makeText(this, "Enter Your Password", Toast.LENGTH_SHORT).show();

        }else {

            mAuth.signInWithEmailAndPassword(email,mPassword).addOnCompleteListener(EmailActivity.this, task -> {

                if (task.isSuccessful()){
                    progress.setVisibility(View.GONE);
                    Intent intent = new Intent(EmailActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }else {
                    progress.setVisibility(View.GONE);
                    String msg = Objects.requireNonNull(task.getException()).getMessage();
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}