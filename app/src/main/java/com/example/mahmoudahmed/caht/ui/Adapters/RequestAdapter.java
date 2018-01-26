package com.example.mahmoudahmed.caht.ui.Adapters;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
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
import com.example.mahmoudahmed.caht.data.DataProvider;
import com.example.mahmoudahmed.caht.ui.Activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RecyclerHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<Client> clients;
    DatabaseReference mDatabase;
    ProgressDialog progressDialog;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

    public RequestAdapter(Context context, ArrayList<Client> clients) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.clients = clients;
    }

    public RequestAdapter(Context context) {
        this.context = context;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }

    @Override
    public RequestAdapter.RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_request, null);
        RequestAdapter.RecyclerHolder recyclerHolder = new RequestAdapter.RecyclerHolder(view);
        return recyclerHolder;
    }

    @Override
    public void onBindViewHolder(final RequestAdapter.RecyclerHolder holder, final int position) {


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

        // Main button action
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = ProgressDialog.show(context, context.getResources().getString(R.string.processing), R.string.add
                        + clients.get(position).username );
                progressDialog.setCancelable(false);
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(clients.get(position).getId());
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Client client = dataSnapshot.getValue(Client.class);
                        //remove request and add user to friend list

                        client.getSentRequests().remove(MainActivity.current.getId());

                        if (client.getFriendsIds() == null)
                            client.setFriendsIds(new ArrayList<String>());
                        client.getFriendsIds().add(MainActivity.current.getId());

                        if (client.getChannelsIds() == null)
                            client.setChannelsIds(new ArrayList<String>());
                        client.getChannelsIds().add((MainActivity.current.getId() + client.getId()));

                        databaseReference.child(client.getId()).setValue(client).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (MainActivity.current.getFriendsIds() == null)
                                        MainActivity.current.setFriendsIds(new ArrayList<String>());


                                    if (MainActivity.current.getChannelsIds() == null)
                                        MainActivity.current.setChannelsIds(new ArrayList<String>());
                                    MainActivity.current.getChannelsIds().add((MainActivity.current.getId() + client.getId()));

                                    MainActivity.current.getReceivedRequests().remove(client.getId());
                                    MainActivity.current.getFriendsIds().add(client.getId());

                                    databaseReference.child(MainActivity.current.getId()).setValue(MainActivity.current);

                                    Toast.makeText(context, R.string.you_and + client.username + R.string.friends, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    clients.remove(position);
                                    notifyItemRemoved(position);

                                    ContentValues values = new ContentValues();
                                    values.put(DataProvider.USER_ID, client.getId());
                                    values.put(DataProvider.NAME,
                                            client.username);
                                    values.put(DataProvider.CHANNEL_ID,
                                            (MainActivity.current.getId() + client.getId()));

                                    context.getContentResolver().insert(
                                            DataProvider.CONTENT_URL1, values);

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
        });


        holder.igonre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = ProgressDialog.show(context, context.getResources().getString(R.string.processing)
                        , R.string.igonre
                        + clients.get(position).username);
                progressDialog.setCancelable(false);

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(clients.get(position).getId());
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Client client = dataSnapshot.getValue(Client.class);
                        //remove request and add user to friend list
                        client.getSentRequests().remove(MainActivity.current.getId());
                        databaseReference.child(client.getId()).setValue(client).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    MainActivity.current.getReceivedRequests().remove(client.getId());
                                    databaseReference.child(MainActivity.current.getId()).setValue(MainActivity.current);
                                    Toast.makeText(context, R.string.deleted, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
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
        });

    }


    @Override
    public int getItemCount() {
        return clients.size();
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircleImageView userImage;
        Button add, igonre;

        public RecyclerHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.request_user_name);
            userImage = (CircleImageView) itemView.findViewById(R.id.request_user_image);
            add = (Button) itemView.findViewById(R.id.request_user_confirm);
            igonre = (Button) itemView.findViewById(R.id.request_user_ignore);

        }
    }
}
