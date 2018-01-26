package com.example.mahmoudahmed.caht.ui.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.mahmoudahmed.caht.Models.Client;
import com.example.mahmoudahmed.caht.R;
import com.example.mahmoudahmed.caht.ui.Adapters.FragmentsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    public static Client current;
    TabLayout tabLayout;
    private FragmentsAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        mSectionsPagerAdapter = new FragmentsAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);


        if(FirebaseAuth.getInstance().getCurrentUser()== null)
        {
            startActivity(new Intent(this,Login.class));
            finish();
        }
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final ProgressDialog dialog = ProgressDialog.show(this, getResources().getString(R.string.processing)
                , getResources().getString(R.string.get_data));
        dialog.setCancelable(false);



        if (isOnline()) {
            //get current user info
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(id);
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    current = dataSnapshot.getValue(Client.class);
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    dialog.dismiss();
                }
            });
        } else {
            final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                    .coordinatorLayout);

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout,
                            R.string.error, Snackbar.LENGTH_LONG);
            snackbar.show();
            dialog.dismiss();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile) {
            Intent intent = new Intent(getApplicationContext(), Profile.class);
            startActivity(intent);
        }
        if (id == R.id.logout_menu) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            finish();
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(0);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
