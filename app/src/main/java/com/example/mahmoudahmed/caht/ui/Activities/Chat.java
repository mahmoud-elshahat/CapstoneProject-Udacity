package com.example.mahmoudahmed.caht.ui.Activities;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.mahmoudahmed.caht.Models.Client;
import com.example.mahmoudahmed.caht.Models.Message;
import com.example.mahmoudahmed.caht.R;
import com.example.mahmoudahmed.caht.ui.Adapters.ConversationAddapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Chat extends AppCompatActivity {
    String channelId;
    private ListView list;
    private EditText text;
    private Client client;
    private ArrayList<Message> messages;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        text = (EditText) findViewById(R.id.message);
        client = getIntent().getExtras().getParcelable("object");

        channelId = getIntent().getExtras().getString("channel_id");

        getSupportActionBar().setTitle(client.username);


        messages = new ArrayList<>();

        ConversationAddapter conversationAddapter = new ConversationAddapter(getApplicationContext(), messages, client);
        list = (ListView) findViewById(R.id.chatList);
        list.setAdapter(conversationAddapter);

        if (isOnline()) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child(channelId);
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    Message message = dataSnapshot.getValue(Message.class);
                    add_new_message(message);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
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
        } else {

            String URL = "content://com.example.pc.movies.ContactProvider/messages";
            Uri students = Uri.parse(URL);
            Cursor c = getContentResolver().query(students, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    Message message = new Message();
                    add_new_message(message);
                } while (c.moveToNext());


            }
        }

    }

    private void add_new_message(Message message) {

        if (message != null) {

            if (message.getReciver().contentEquals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                if (message.getSender().contentEquals(client.getId())) {
                    message.fromSender = false;
                    messages.add(message);

                }
            } else if (message.getSender().contentEquals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                if (message.getReciver().contentEquals(client.getId())) {
                    message.fromSender = true;
                    messages.add(message);
                }
            }
            ((BaseAdapter) list.getAdapter()).notifyDataSetChanged();

        }
    }

    public void sendMessage(View view) {
        String content = text.getText().toString();
        if (content.isEmpty()) {
            return;
        }
        Message message = new Message();

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String currentDateTimeString = sdf.format(d);

        text.setText("");

        message.setSender(FirebaseAuth.getInstance().getCurrentUser().getUid());
        message.setReciver(client.getId());
        message.setContent(content);
        message.date = currentDateTimeString;

        databaseReference = FirebaseDatabase.getInstance().getReference(channelId);
        String id = databaseReference.push().getKey();
        databaseReference.child(id).setValue(message);

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
