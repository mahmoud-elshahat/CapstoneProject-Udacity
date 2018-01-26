package com.example.mahmoudahmed.caht.ui.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.mahmoudahmed.caht.Models.Client;
import com.example.mahmoudahmed.caht.R;
import com.example.mahmoudahmed.caht.data.DataProvider;

import java.util.ArrayList;


public class WidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ListProvider(this.getApplicationContext(), intent));
    }

    class ListProvider implements RemoteViewsFactory {
        Context mContext = null;
        int appWidgetId;

        ArrayList<Client> userFriends;
        RemoteViews views;

        ListProvider(Context context, Intent intent) {
            this.mContext = context;
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);

            populateListItem();

        }

        private void populateListItem() {
            userFriends = new ArrayList<>();
            String URL = "content://com.example.pc.movies.ContactProvider";
            Uri students = Uri.parse(URL);
            Cursor c = mContext.getContentResolver().query(students, null, null, null, null);
            if (c.moveToFirst()) {
                do {
                    Client temp = new Client();

                    temp.id = c.getString(c.getColumnIndex(DataProvider.USER_ID));

                    ArrayList<String> channels = new ArrayList<String>();
                    channels.add(c.getString(c.getColumnIndex(DataProvider.CHANNEL_ID)));
                    temp.setChannelsIds(channels);

                    temp.username = c.getString(c.getColumnIndex(DataProvider.NAME));

                    userFriends.add(temp);
                } while (c.moveToNext());


            }

        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {


        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return userFriends.size();
        }

        @Override
        public RemoteViews getViewAt(final int position) {
            views = new RemoteViews(
                    mContext.getPackageName(), R.layout.widget_list_item);

            views.setTextViewText(R.id.widget_user_name, userFriends.get(position).username);


            Bundle extras = new Bundle();
            extras.putInt("pos", position);
            extras.putParcelable("object", userFriends.get(position));
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            // Make it possible to distinguish the individual on-click
            // action of a given item
            views.setOnClickFillInIntent(R.id.main, fillInIntent);


            return views;
        }


        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
