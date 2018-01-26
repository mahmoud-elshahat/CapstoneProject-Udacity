package com.example.mahmoudahmed.caht.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DataProvider extends ContentProvider {

    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String CHANNEL_ID = "channel_id";
    public static final String NAME = "name";
    static final String PROVIDER_NAME = "com.example.pc.movies.ContactProvider";
    static final String URL1 = "content://" + PROVIDER_NAME + "/users";
    public static final Uri CONTENT_URL1 = Uri.parse(URL1);
    static final UriMatcher uriMatcher;


    static final int USERS = 1;
    static final int USERS_ID = 2;
    static final String DATABASE_NAME = "udacity";
    static final String TABLE_NAME_USERS = "users";
    static final int DATABASE_VERSION = 1;
    static final String QUERY1 =
            "CREATE TABLE " + TABLE_NAME_USERS +

                    "(id INTEGER PRIMARY KEY , " +
                    "user_id TEXT NOT NULL ," +
                    "name TEXT NOT NULL ," +
                    "channel_id TEXT NOT NULL ) ";

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "users", USERS);
        uriMatcher.addURI(PROVIDER_NAME, "users/#", USERS_ID);

    }

    private SQLiteDatabase sql;

    @Override
    public boolean onCreate() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        sql = dbHelper.getWritableDatabase();
        return sql != null;

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(TABLE_NAME_USERS);

        switch (uriMatcher.match(uri)) {
            case USERS:
                qb.setProjectionMap(null);
                break;

            case USERS_ID:
                qb.appendWhere(ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }

        Cursor c = qb.query(sql, projection, selection,
                selectionArgs, null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        throw new IllegalArgumentException("Unsupported URI: " + uri);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = sql.insert(TABLE_NAME_USERS, "", values);

        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URL1, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri + "  ");

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(QUERY1);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXIST " + TABLE_NAME_USERS);
            onCreate(db);

        }
    }
}