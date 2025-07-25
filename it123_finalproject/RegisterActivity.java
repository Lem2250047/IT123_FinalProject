package com.example.it123_finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    TextView btnLogIn;
    EditText inputUsername, inputPasswordSignUp, inputEmailSignUp, inputConfirmPassword;
    Button btnRegister;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        btnLogIn = findViewById(R.id.btn_LogIn);
        inputUsername = findViewById(R.id.inputUsername);
        inputPasswordSignUp = findViewById(R.id.inputPasswordSignUp);
        inputEmailSignUp = findViewById(R.id.inputEmailSignUp);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        CheckBox checkBoxBuyer = findViewById(R.id.checkBoxBuyer);
        CheckBox checkBoxSeller = findViewById(R.id.checkBoxSeller);
        btnRegister = findViewById(R.id.btn_Register);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStore = FirebaseFirestore.getInstance();

        checkBoxBuyer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxSeller.setChecked(false);
            }
        });

        checkBoxSeller.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxBuyer.setChecked(false);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformAuth(checkBoxBuyer, checkBoxSeller);
            }
        });

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void PerformAuth(CheckBox checkBoxBuyer, CheckBox checkBoxSeller) {
        String email = inputEmailSignUp.getText().toString();
        String password = inputPasswordSignUp.getText().toString();
        String username = inputUsername.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();

        if (!email.matches(emailPattern)) {
            inputEmailSignUp.setError("Invalid email format");
        } else if (password.isEmpty() || password.length() < 7) {
            inputPasswordSignUp.setError("Password must be at least 7 characters");
        } else if (!password.equals(confirmPassword)) {
            inputConfirmPassword.setError("Passwords do not match");
        } else if (!checkBoxBuyer.isChecked() && !checkBoxSeller.isChecked()) {
            Toast.makeText(this, "Please select Buyer or Seller", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setMessage("Please wait while checking credentials.");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                        DocumentReference df = mStore.collection("Users").document(user.getUid());
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("Username", username);
                        userInfo.put("Email", email);
                        userInfo.put("Password", password);

                        // Set user type based on checkbox selection
                        if (checkBoxBuyer.isChecked()) {
                            userInfo.put("isBuyer", "1");
                        } else if (checkBoxSeller.isChecked()) {
                            userInfo.put("isSeller", "1");
                        }

                        df.set(userInfo);

                        sendUserToNextActivity();
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registration Failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}