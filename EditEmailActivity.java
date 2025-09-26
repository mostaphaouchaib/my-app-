package com.spacester.tweetsterupdate.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.spacester.tweetsterupdate.NightMode;
import com.spacester.tweetsterupdate.R;

import java.util.HashMap;

public class EditEmailActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressBar progressBar;

    NightMode sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new NightMode(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);

         mAuth = FirebaseAuth.getInstance();

        //Progress
         progressBar = findViewById(R.id.pb);

        //Back
        ImageView back = findViewById(R.id.imageView);
        back.setOnClickListener(v -> onBackPressed());

        //SignUp
        EditText name = findViewById(R.id.editText);
        EditText username = findViewById(R.id.username);

        Button button = findViewById(R.id.signUp);

        button.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String newE = name.getText().toString().trim();
            String newP = username.getText().toString().trim();
            if (TextUtils.isEmpty(newE)){
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter your new Email")
                        .textColor(Color.WHITE)
                        .textBold()
                        .gravity(0)
                        .length(2000)
                        .solidBackground()
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (TextUtils.isEmpty(newP)){
                new StyleableToast
                        .Builder(getApplicationContext())
                        .text("Enter your password")
                        .textColor(Color.WHITE)
                        .textBold()
                        .length(2000)
                        .solidBackground()
                        .gravity(0)
                        .backgroundColor(getResources().getColor(R.color.colorPrimary))
                        .show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            Query emailQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(newE);
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getChildrenCount()>0){

                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text("Email already exist")
                                .textColor(Color.WHITE)
                                .textBold()
                                .length(2000)
                                .solidBackground()
                                .gravity(0)
                                .backgroundColor(getResources().getColor(R.color.colorPrimary))
                                .show();
                        progressBar.setVisibility(View.GONE);
                        return;
                    }
                    updateEmail(newE,newP);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

    }

    private void updateEmail(String newE, String newP) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(),newP ); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
            user1.updateEmail(newE);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("email", newE);
            progressBar.setVisibility(View.GONE);
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap);
            Toast.makeText(EditEmailActivity.this, "Updated", Toast.LENGTH_SHORT).show();
        }
        );
    }

}