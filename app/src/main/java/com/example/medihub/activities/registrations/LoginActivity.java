package com.example.medihub.activities.registrations;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medihub.R;
import com.example.medihub.database.RegistrationRequestsReference;
import com.example.medihub.database.UsersReference;
import com.example.medihub.enums.RequestStatus;
import com.example.medihub.enums.UserRole;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button buttonCreateAccount;
    private Button buttonLogin;
    private EditText textEmail;
    private EditText textPassword;
    private RequestStatus registrationStatus = null;
    private FirebaseDatabase firebaseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseDB = FirebaseDatabase.getInstance();

        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openRegistrationOptionActivity();
            }
        });

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userEmail = textEmail.getText().toString();
                String userPassword = textPassword.getText().toString();

                if (userEmail.isEmpty() && userPassword.isEmpty() == false) {

                    Toast.makeText(LoginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;

                } else if (userPassword.isEmpty() && !userEmail.isEmpty() == false) {

                    Toast.makeText(LoginActivity.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;

                } else if (userPassword.isEmpty() && userEmail.isEmpty()) {

                    Toast.makeText(LoginActivity.this, "Enter email and password", Toast.LENGTH_SHORT).show();
                    return;

                }

                mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    checkUser();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Email or Password is Incorrect",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }


    public void checkUser() {
        String userId = mAuth.getUid();
        UsersReference usersReference = new UsersReference();
        DatabaseReference userRef = usersReference.get(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String strRole = snapshot.child("role").getValue(String.class);
                    UserRole role = UserRole.valueOf(strRole);

                    if (role == UserRole.admin) {  // user is admin, log in
                        openWelcomeActivity();
                    } else {
                        checkRegistrationRequest();
                    }
                } else {
                    checkRegistrationRequest();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkRegistrationRequest() {
        String userId = mAuth.getUid();
        RegistrationRequestsReference registrationRequestsReference = new RegistrationRequestsReference();
        DatabaseReference registrationRequestRef = registrationRequestsReference.get(userId);

        registrationRequestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String rStatus = snapshot.child("status").getValue(String.class);
                    registrationStatus = RequestStatus.valueOf(rStatus);

                    if(registrationStatus== RequestStatus.approved){
                        openWelcomeActivity();
                    }
                    else if(registrationStatus== RequestStatus.pending){
                        Toast.makeText(getApplicationContext(), "The current registration has not been approved yet. Status: " + registrationStatus.toString(), Toast.LENGTH_SHORT).show();
                    }
                    else if(registrationStatus== RequestStatus.declined){
                        Toast.makeText(getApplicationContext(), "The current registration has been rejected. You can contact the Administrator by phone at 1011011001", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "couldn't find registration request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void openRegistrationOptionActivity() {
        Intent intent = new Intent(this, RegistrationOptionActivity.class);
        startActivity(intent);
    }

    public void openWelcomeActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}
