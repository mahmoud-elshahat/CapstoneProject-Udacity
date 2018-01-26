package com.example.mahmoudahmed.caht.ui.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mahmoudahmed.caht.Models.Client;
import com.example.mahmoudahmed.caht.R;
import com.example.mahmoudahmed.caht.ui.Adapters.ChatListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatFragment extends Fragment {


    DatabaseReference databaseReference;
    ChatListAdapter chatListAdapter;
    ArrayList<String> userFriendsIds;
    private RecyclerView recyclerView;
    private View rootView;

    public ChatFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.chat_list_fragment, container, false);
        }

        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);

        chatListAdapter = new ChatListAdapter(getActivity());

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Client current = dataSnapshot.getValue(Client.class);
                userFriendsIds = current.getFriendsIds();
                if (userFriendsIds == null) {
                    userFriendsIds = new ArrayList<>();
                }
                chatListAdapter.setClientsIds(userFriendsIds);
                recyclerView.setAdapter(chatListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        if (isOnline()) {

            databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    if (dataSnapshot.getKey().equals("friendsIds")) {
                        ArrayList<String> friends = (ArrayList<String>) dataSnapshot.getValue();
                        userFriendsIds = friends;
                        chatListAdapter.notifyItemInserted(userFriendsIds.size() - 1);

                    }

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }


        return rootView;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
