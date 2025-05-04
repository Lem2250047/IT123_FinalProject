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

import com.example.it123_finalproject.UserTypeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    TextView btnSignUp;
    EditText inputPasswordLogin, inputEmailLogin;
    Button btnLogin;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        btnSignUp = findViewById(R.id.btn_SignUp);
        inputEmailLogin = findViewById(R.id.inputEmailLogin);
        inputPasswordLogin = findViewById(R.id.inputPasswordLogin);
        btnLogin = findViewById(R.id.LoginBtn);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCredentials();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, UserTypeActivity.class));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performauth();
            }
        });
    }

    private void performauth() {
        String email = inputEmailLogin.getText().toString();
        String password = inputPasswordLogin.getText().toString();

        if (!email.matches(emailPattern)) {
            inputEmailLogin.setError("Invalid email format");
        } else if (password.isEmpty() || password.length() < 7) {
            inputPasswordLogin.setError("Password must be at least 7 characters");
        } else {
            progressDialog.setMessage("Please wait while checking credentials.");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void checkCredentials() {
        String email = inputEmailLogin.getText().toString();
        String password = inputPasswordLogin.getText().toString();

        if (email.isEmpty() || !email.contains("@")) {
            showError(inputEmailLogin, "Invalid email");
        } else if (password.isEmpty() || password.length() < 7) {
            showError(inputPasswordLogin, "Password must be at least 7 characters");
        } else {
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
        }
    }

    private void showError(EditText input, String message) {
        input.setError(message);
        input.requestFocus();
    }
}