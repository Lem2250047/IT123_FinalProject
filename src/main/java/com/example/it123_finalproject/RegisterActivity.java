package com.example.it123_finalproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class RegisterActivity extends AppCompatActivity {

    TextView btnLogIn;
    EditText inputUsername, inputPasswordSignUp, inputEmailSignUp, inputConfirmPassword;
    Button btnRegister;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

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
        btnRegister = findViewById(R.id.btn_Register);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCredentials();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformAuth();
            }
        });
    }

    private void PerformAuth() {
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
                        sendUserToNextActivity();
                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registration Failed: " + task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void checkCredentials() {
        String username = inputUsername.getText().toString();
        String email = inputEmailSignUp.getText().toString();
        String password = inputPasswordSignUp.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();

        if (username.isEmpty() || username.length() < 7) {
            showError(inputUsername, "Username not valid");
        } else if (email.isEmpty() || !email.contains("@")) {
            showError(inputEmailSignUp, "Invalid email");
        } else if (password.isEmpty() || password.length() < 7) {
            showError(inputPasswordSignUp, "Passwords must be 7 characters");
        } else if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
            showError(inputConfirmPassword, "Passwords do not match");
        } else {
            Toast.makeText(this, "Call Registration Method", Toast.LENGTH_SHORT).show();
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