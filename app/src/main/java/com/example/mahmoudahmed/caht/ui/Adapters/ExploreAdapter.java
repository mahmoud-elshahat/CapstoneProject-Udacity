package com.example.mahmoudahmed.caht.ui.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mahmoudahmed.caht.Models.Client;
import com.example.mahmoudahmed.caht.R;
import com.example.mahmoudahmed.caht.ui.Activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MahmoudAhmed on 1/6/2018.
 */

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.RecyclerHolder> {

    public ArrayList<Integer> addedFriends;
    Context context;
    LayoutInflater inflater;
    ArrayList<Client> clients;
    ProgressDialog progressDialog;
    private DatabaseReference mDatabase;

    public ExploreAdapter(Context context) {
        this.context = context;
        addedFriends = new ArrayList<>();
        inflater = LayoutInflater.from(context);
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }

    @Override
    public ExploreAdapter.RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item_explore, null);
        ExploreAdapter.RecyclerHolder recyclerHolder = new ExploreAdapter.RecyclerHolder(view);
        return recyclerHolder;
    }

    @Override
    public void onBindViewHolder(final ExploreAdapter.RecyclerHolder holder, final int position) {


        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        String path = "images/" + clients.get(position).getId();
        StorageReference imagesRef = mStorageRef.child(path);
        imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri.toString()).into(holder.userImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });


        holder.userName.setText(clients.get(position).username);


        if (MainActivity.current.getSentRequests() != null && (addedFriends.contains(position)
                || MainActivity.current.getSentRequests().contains(clients.get(position).getId()))) {
            holder.add.setText(R.string.cancle);
            holder.add.setBackgroundResource(R.drawable.rounded_button_ignore);
            holder.add.setTextColor(Color.BLACK);

        } else if (MainActivity.current.getFriendsIds() != null &&
                MainActivity.current.getFriendsIds().contains(clients.get(position).getId())) {
            holder.add.setText(R.string.frineds);
            holder.add.setBackgroundResource(R.drawable.rounded_button_added);
            holder.add.setTextColor(Color.WHITE);
            holder.add.setEnabled(false);
        } else {
            holder.add.setText(R.string.add);
            holder.add.setBackgroundResource(R.drawable.rounded_button_confirm);
            holder.add.setTextColor(Color.WHITE);
        }

        // Main button action
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //case add user "send request"
                if (holder.add.getText().toString().toLowerCase().equals(context.getResources().getString(R.string.add))) {
                    progressDialog = ProgressDialog.show(context, context.getResources().getString(R.string.processing)
                            , R.string.add  + clients.get(position).username);

                    //get added user id to add it
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(clients.get(position).getId());
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //  //add this request to other user
                            Client requestedClient = dataSnapshot.getValue(Client.class);

                            if (requestedClient.getReceivedRequests() == null)
                                requestedClient.setReceivedRequests(new ArrayList<String>());
                            requestedClient.getReceivedRequests().add(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            // added requested user to this to make it historical
                            if (MainActivity.current.getSentRequests() == null)
                                MainActivity.current.setSentRequests(new ArrayList<String>());
                            MainActivity.current.getSentRequests().add(requestedClient.getId());
                            //write it to database ..
                            mDatabase.setValue(requestedClient).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mDatabase = FirebaseDatabase.getInstance().getReference().child("users")
                                                .child(MainActivity.current.getId());
                                        mDatabase.setValue(MainActivity.current);

                                        Toast.makeText(context, R.string.sent, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                        addedFriends.add(position);
                                        holder.add.setText(R.string.cancle);
                                        holder.add.setBackgroundResource(R.drawable.rounded_button_ignore);
                                        holder.add.setTextColor(Color.BLACK);
                                    } else {
                                        Toast.makeText(context, R.string.some_error, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(context, R.string.some_error, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });

                } else {

                    progressDialog = ProgressDialog.show(context, context.getResources().getString(R.string.processing)
                            , R.string.cancle + clients.get(position).username);
                    //get added user id to add it

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(clients.get(position).getId());
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //  //remove this request from other user
                            Client requestedClient = dataSnapshot.getValue(Client.class);
                            requestedClient.getReceivedRequests().remove(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            // remove requested user from this to make it historical
                            MainActivity.current.getSentRequests().remove(requestedClient.getId());
                            //write it to database ..
                            mDatabase.setValue(requestedClient).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mDatabase = FirebaseDatabase.getInstance().getReference().child("users")
                                                .child(MainActivity.current.getId());
                                        mDatabase.setValue(MainActivity.current);

                                        Toast.makeText(context, R.string.sent, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                        addedFriends.remove(Integer.valueOf(position));
                                        holder.add.setText(R.string.add);
                                        holder.add.setBackgroundResource(R.drawable.rounded_button_confirm);
                                        holder.add.setTextColor(Color.WHITE);
                                    } else {
                                        Toast.makeText(context, R.string.some_error, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(context, R.string.some_error, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });

                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return clients.size();
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircleImageView userImage;
        Button add;

        public RecyclerHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.request_user_name);
            userImage = (CircleImageView) itemView.findViewById(R.id.request_user_image);
            add = (Button) itemView.findViewById(R.id.request_user_confirm);

        }
    }
}
