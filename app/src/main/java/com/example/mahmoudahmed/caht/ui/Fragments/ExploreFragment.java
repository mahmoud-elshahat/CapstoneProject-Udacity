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
import android.widget.Toast;

import com.example.mahmoudahmed.caht.Models.Client;
import com.example.mahmoudahmed.caht.R;
import com.example.mahmoudahmed.caht.ui.Activities.MainActivity;
import com.example.mahmoudahmed.caht.ui.Adapters.ExploreAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.cryse.widget.persistentsearch.PersistentSearchView;

import java.util.ArrayList;


public class ExploreFragment extends Fragment {

    TextView empty;
    DatabaseReference databaseReference;
    PersistentSearchView searchView;
    ExploreAdapter exploreAdapter;
    ProgressBar progressBar;
    private View rootView;
    private ArrayList<Client> clients;
    private RecyclerView recyclerView;

    public ExploreFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.explore_fragment, container, false);
        }

        empty = (TextView) rootView.findViewById(R.id.empty);
        searchView = (PersistentSearchView) rootView.findViewById(R.id.searchView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        exploreAdapter = new ExploreAdapter(getActivity());

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        recyclerView.setLayoutManager(llm);


        if (isOnline()) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getKey().equals("friendsIds")) {
                        ArrayList<String> friends = (ArrayList<String>) dataSnapshot.getValue();
                        MainActivity.current.setFriendsIds(friends);
                        exploreAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                    if (dataSnapshot.getKey().equals("friendsIds")) {
                        ArrayList<String> friends = (ArrayList<String>) dataSnapshot.getValue();
                        MainActivity.current.setFriendsIds(friends);
                        exploreAdapter.notifyDataSetChanged();
                    }
                    if (dataSnapshot.getKey().equals("sentRequests")) {
                        ArrayList<String> sentRequests = (ArrayList<String>) dataSnapshot.getValue();
                        MainActivity.current.setSentRequests(sentRequests);
                        exploreAdapter.notifyDataSetChanged();
                    }
                    if (dataSnapshot.getKey().equals("receivedRequests")) {
                        ArrayList<String> receivedRequests = (ArrayList<String>) dataSnapshot.getValue();
                        MainActivity.current.setReceivedRequests(receivedRequests);
                        exploreAdapter.notifyDataSetChanged();
                    }

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getKey().equals("sentRequests")) {
                        MainActivity.current.setSentRequests(new ArrayList<String>());
                        exploreAdapter.addedFriends = new ArrayList<>();
                        exploreAdapter.notifyDataSetChanged();
                    }
                    if (dataSnapshot.getKey().equals("receivedRequests")) {
                        MainActivity.current.setReceivedRequests(new ArrayList<String>());
                        exploreAdapter.addedFriends = new ArrayList<>();
                        exploreAdapter.notifyDataSetChanged();

                    }

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        searchView.setSearchListener(new PersistentSearchView.SearchListener() {
            @Override
            public void onSearchCleared() {

            }

            @Override
            public void onSearchTermChanged(String term) {
                //filtering items here

                if (clients == null || clients.size() == 0 || term.equals(" ")) {
                    return;
                }

                ArrayList<Client> tempClients = new ArrayList<>();
                for (int i = 0; i < clients.size(); i++) {
                    if (clients.get(i).username.toLowerCase().contains(term.toLowerCase().trim())) {
                        tempClients.add(clients.get(i));
                    }
                }

                if (tempClients.size() == 0) {
                    empty.setText(R.string.search_empty + term);
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.GONE);
                }
                exploreAdapter.setClients(tempClients);
                exploreAdapter.notifyDataSetChanged();


            }

            @Override
            public void onSearch(String query) {
            }

            @Override
            public void onSearchEditOpened() {

            }

            @Override
            public void onSearchEditClosed() {

            }

            @Override
            public boolean onSearchEditBackPressed() {
                return false;
            }

            @Override
            public void onSearchExit() {

            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                if (size == 0) {
                    Toast.makeText(getActivity(), R.string.no_users, Toast.LENGTH_LONG).show();
                    return;
                }
                clients = new ArrayList<Client>();
                for (DataSnapshot request : dataSnapshot.getChildren()) {
                    Client client = request.getValue(Client.class);
                    if (client != null && FirebaseAuth.getInstance().getCurrentUser() != null) {
                        if (!client.getId().contentEquals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            clients.add(client);
                        }

                    }
                }

                exploreAdapter.setClients(clients);
                recyclerView.setAdapter(exploreAdapter);
                progressBar.setVisibility(View.GONE);

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
