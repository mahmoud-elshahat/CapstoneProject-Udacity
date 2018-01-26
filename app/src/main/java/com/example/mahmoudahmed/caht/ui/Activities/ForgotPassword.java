package com.example.mahmoudahmed.caht.ui.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mahmoudahmed.caht.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    Button submit;
    TextView result;
    ProgressBar progressBar;
    private EditText email;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        auth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.registered_emailid_forgot);
        result = (TextView) findViewById(R.id.result);
        progressBar = (ProgressBar) findViewById(R.id.forgot_par);

        submit = (Button) findViewById(R.id.forgotBtn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString().trim();

                if (userEmail.isEmpty()) {
                    email.setError(getResources().getString(R.string.empty));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(userEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    result.setText(R.string.forgot_email_right);

                                } else {
                                    result.setText(R.string.forgot_email_wrong);
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });

            }
        });
    }

}
