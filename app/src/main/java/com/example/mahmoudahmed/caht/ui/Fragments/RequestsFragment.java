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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mahmoudahmed.caht.Models.Client;
import com.example.mahmoudahmed.caht.R;
import com.example.mahmoudahmed.caht.ui.Activities.MainActivity;
import com.example.mahmoudahmed.caht.ui.Adapters.RequestAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RequestsFragment extends Fragment {

    DatabaseReference firebaseDatabase;
    ProgressBar progressBar;
    TextView emptyView;
    RecyclerView recyclerView;
    ArrayList<Client> clientsThatSentRequests;
    ArrayList<Client> allClients;
    private View rootView;
    private DatabaseReference databaseReference;
    private RequestAdapter requestAdapter;

    public RequestsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.request_fragment, container, false);

        }
        requestAdapter = new RequestAdapter(getActivity());
        allClients = new ArrayList<>();
        emptyView = (TextView) rootView.findViewById(R.id.empty);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);


        if (isOnline()) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getKey().equals("receivedRequests")) {

                        ArrayList<String> receivedRequests = (ArrayList<String>) dataSnapshot.getValue();
                        clientsThatSentRequests = new ArrayList<>();
                        for (int i = 0; i < allClients.size(); i++) {
                            for (int j = 0; j < receivedRequests.size(); j++) {
                                if (receivedRequests.get(j).equals(allClients.get(i).getId())) {
                                    clientsThatSentRequests.add(allClients.get(i));
                                    MainActivity.current.setReceivedRequests(new ArrayList<String>());
                                    MainActivity.current.getReceivedRequests().add(allClients.get(i).getId());
                                }
                            }
                        }
                        requestAdapter.setClients(clientsThatSentRequests);
                        requestAdapter.notifyDataSetChanged();
                        emptyView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                    if (dataSnapshot.getKey().equals("receivedRequests")) {
                        ArrayList<String> receivedRequests = (ArrayList<String>) dataSnapshot.getValue();
                        clientsThatSentRequests = new ArrayList<>();
                        for (int i = 0; i < allClients.size(); i++) {
                            for (int j = 0; j < receivedRequests.size(); j++) {
                                if (receivedRequests.get(j).equals(allClients.get(i).getId())) {
                                    clientsThatSentRequests.add(allClients.get(i));
                                }
                            }

                        }
                        requestAdapter.setClients(clientsThatSentRequests);
                        requestAdapter.notifyDataSetChanged();

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


        // Get received requests and view it
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MainActivity.current = dataSnapshot.getValue(Client.class);


                final ArrayList<String> clientsIds = MainActivity.current.getReceivedRequests();
                firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        clientsThatSentRequests = new ArrayList<>();
                        for (DataSnapshot request : dataSnapshot.getChildren()) {
                            Client client = request.getValue(Client.class);
                            allClients.add(client);
                            if (client != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
                                if (clientsIds != null && client != null) {
                                    if (clientsIds.contains(client.getId())) {
                                        clientsThatSentRequests.add(client);
                                    }
                                }

                            }
                        }

                        //view data
                        requestAdapter = new RequestAdapter(getActivity(), clientsThatSentRequests);
                        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(llm);
                        recyclerView.setAdapter(requestAdapter);

                        //hide loading bar
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        emptyView.setText(R.string.error);
                        emptyView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });


                if (MainActivity.current.getReceivedRequests() == null) {
                    emptyView.setText(R.string.no_requests);
                    emptyView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return rootView;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}
