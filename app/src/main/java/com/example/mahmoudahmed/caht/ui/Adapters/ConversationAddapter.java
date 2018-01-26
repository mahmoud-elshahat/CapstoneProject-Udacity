package com.example.mahmoudahmed.caht.ui.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mahmoudahmed.caht.Models.Client;
import com.example.mahmoudahmed.caht.Models.Message;
import com.example.mahmoudahmed.caht.R;

import java.util.ArrayList;

public class ConversationAddapter extends BaseAdapter {
    public Context context;
    LayoutInflater inflater;
    private ArrayList<Message> messages;
    private Client client;

    public ConversationAddapter() {
    }

    public ConversationAddapter(Context context, ArrayList<Message> messages, Client client) {
        this.context = context;
        this.messages = messages;
        this.client = client;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (messages.get(i).fromSender) {
            view = inflater.inflate(R.layout.chat_item_sent, null);
        } else {
            view = inflater.inflate(R.layout.chat_item_rcv, null);
        }


        TextView message = (TextView) view.findViewById(R.id.content);
        message.setText(messages.get(i).getContent());

        TextView date = (TextView) view.findViewById(R.id.date);
        date.setText(messages.get(i).date);

        return view;
    }
}
