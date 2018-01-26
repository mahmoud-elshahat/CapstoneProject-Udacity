package com.example.mahmoudahmed.caht.ui.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mahmoudahmed.caht.Models.Client;
import com.example.mahmoudahmed.caht.R;
import com.example.mahmoudahmed.caht.ui.Activities.Chat;
import com.example.mahmoudahmed.caht.ui.Activities.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.RecyclerHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<String> clientsIds;

    ArrayList<Client> clients;

    DatabaseReference reference;

    public ChatListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        clients = new ArrayList<>();
    }

    public void setClientsIds(ArrayList<String> clientsIds) {
        this.clientsIds = clientsIds;
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.chat_list_item, null);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);
        return recyclerHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerHolder holder, final int position) {

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(clientsIds.get(position));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Client client = dataSnapshot.getValue(Client.class);


                holder.userName.setText(client.username);

                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                String path = "images/" + client.getId();
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

                clients.add(client);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(context, Chat.class);
                    i.putExtra("object", clients.get(position));
                    i.putExtra("channel_id", MainActivity.current.getChannelsIds().get(position));
                    context.startActivity(i);

                } catch (NullPointerException n) {

                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return clientsIds.size();
    }


    public class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView userName;
        CircleImageView userImage;

        public RecyclerHolder(View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.user_name);
            userImage = (CircleImageView) itemView.findViewById(R.id.user_image);
        }
    }
}

