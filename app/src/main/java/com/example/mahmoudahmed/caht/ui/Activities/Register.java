package com.example.mahmoudahmed.caht.ui.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahmoudahmed.caht.Models.Client;
import com.example.mahmoudahmed.caht.R;
import com.example.mahmoudahmed.caht.Utilities.SignUpValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;

public class Register extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    public static String TAG = "Firebase";
    CircleImageView profile;
    Bitmap bitmap;
    private TextView login;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private EditText name;
    private EditText email;
    private EditText pwd;
    private EditText confirmPwd;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private SignUpValidation validation;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        profile = (CircleImageView) findViewById(R.id.profile);

        mStorageRef = FirebaseStorage.getInstance().getReference();


        login = (TextView) findViewById(R.id.already_user);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivity = new Intent(getApplicationContext(), Login.class);
                loginActivity.addFlags(FLAG_ACTIVITY_NO_HISTORY);
                startActivity(loginActivity);
            }
        });


        TextView tx = (TextView) findViewById(R.id.signText);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/simple.ttf");
        tx.setTypeface(custom_font);


        mAuth = FirebaseAuth.getInstance();


        name = (EditText) findViewById(R.id.fullName);
        email = (EditText) findViewById(R.id.userEmailId);
        pwd = (EditText) findViewById(R.id.password);
        confirmPwd = (EditText) findViewById(R.id.confirmPassword);

        progressBar = (ProgressBar) findViewById(R.id.proBar);
    }

    public void signUp(View v) {

        final String emailId = email.getText().toString().trim();
        final String username = name.getText().toString().trim();
        final String password = pwd.getText().toString().trim();
        final String confirmPassword = confirmPwd.getText().toString().trim();

        validation = new SignUpValidation(emailId, password, confirmPassword, username);


        TextInputLayout til = (TextInputLayout) findViewById(R.id.textInput);


        if (validation.isEmptyEmail())
            email.setError(getResources().getString(R.string.empty));
        if (validation.isEmptyName())
            name.setError(getResources().getString(R.string.empty));
        if (validation.isEmptyPassword())
            pwd.setError(getResources().getString(R.string.empty));
        if (validation.isEmptyName() || validation.isEmptyEmail() || validation.isEmptyPassword())
            return;

        //Password Length
        if (!validation.isAcceptablePassword()) {
            pwd.setError(getResources().getString(R.string.constaint1));
            return;
        }
        //email validation
        if (!validation.isValidEmail()) {
            email.setError(getResources().getString(R.string.constaint2));
            return;
        }
        //Password matching
        if (!validation.isMatch()) {
            confirmPwd.setError(getResources().getString(R.string.constaint3));
            return;
        }

        if (bitmap == null) {
            Toast.makeText(this, R.string.photo, Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(emailId, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), R.string.error2,
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference("users");
                            Client user = new Client(userId, emailId, username);
                            databaseReference.child(userId).setValue(user);

                            String path = "images/" + userId;

                            FirebaseUser xUser = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name.getText().toString())
                                    .setPhotoUri(Uri.parse(path))
                                    .build();
                            xUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                            }
                                        }
                                    });


                            StorageReference imagesRef = mStorageRef.child(path);


                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] bytes = baos.toByteArray();

                            UploadTask uploadTask = imagesRef.putBytes(bytes);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(Register.this, R.string.photo3, Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            });


                        }
                    }
                });
    }

    public void selectPicture(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {

            if (data == null) {
                Toast.makeText(this, R.string.photo2, Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Uri uri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                profile.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}


