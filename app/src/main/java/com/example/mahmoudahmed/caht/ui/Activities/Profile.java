package com.example.mahmoudahmed.caht.ui.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahmoudahmed.caht.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {


    public static final int PICK_IMAGE = 1;
    CircleImageView imageView;
    TextView email, name;
    Bitmap bitmap;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        MobileAds.initialize(this, "ca-app-pub-7194295347174365~1178240560");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        email = (TextView) findViewById(R.id.profile_user_email);
        if (user.getEmail() == null || user.getEmail().equals("")) {
            email.setText(R.string.not_provided);
        } else {
            email.setText(user.getEmail());
        }


        name = (TextView) findViewById(R.id.profile_user_name);
        if (user.getDisplayName() == null || user.getDisplayName().equals("")) {
            name.setText(R.string.not_provided);
        } else {
            name.setText(user.getDisplayName());
        }

        String path = "images/" + user.getUid();

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference islandRef = mStorageRef.child(path);

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                if (bmp == null) {
                    String imageUrl = "https://findicons.com/files/icons/1580/devine_icons_part_2/128/account_and_control.png";
                    new DownloadBitmap().execute(imageUrl);

                } else {
                    imageView.setImageBitmap(bmp);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Toast.makeText(Profile.this, R.string.error, Toast.LENGTH_SHORT).show();
                String imageUrl = "https://findicons.com/files/icons/1580/devine_icons_part_2/128/account_and_control.png";
                new DownloadBitmap().execute(imageUrl);
            }
        });

        imageView = (CircleImageView) findViewById(R.id.profile);


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
                imageView.setImageBitmap(bitmap);

                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                String path = "images/" + FirebaseAuth.getInstance().getCurrentUser().getUid();
                StorageReference imagesRef = mStorageRef.child(path);


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bytes = baos.toByteArray();

                UploadTask uploadTask = imagesRef.putBytes(bytes);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(Profile.this, R.string.photo_error, Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(Profile.this, R.string.photo_done, Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


    public class DownloadBitmap extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                InputStream in = new java.net.URL(params[0]).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                return bitmap;

            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }
}