package com.example.news.newsfeed;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.news.newsfeed.model.FeedItem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashutosh on 18/08/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "newsFeedManager";

    // Contacts table name
    private static final String TABLE_NEWS = "newsFeed";
    // Contacts Table Columns names
    private static final String NEWS_ID = "id";
    private static final String NEWS_HEADLINE = "headline";
    private static final String NEWS_DATELINE = "dateline";
    private static final String NEWS_PHOTO = "photo";
    private static final String NEWS_STORY = "story";
    private static final String NEWS_WEBURL = "webUrl";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NEWS + "("
                + NEWS_ID + " INTEGER PRIMARY KEY," + NEWS_HEADLINE + " TEXT,"
                + NEWS_DATELINE + " TEXT,"
                + NEWS_PHOTO + " TEXT,"
                + NEWS_STORY + " TEXT,"
                + NEWS_WEBURL + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);

        // Create tables again
        onCreate(db);
    }

    void clearTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NEWS,null,null);
    }
    void addNewsFeed(FeedItem feedItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NEWS_ID, feedItem.getNewsItemId());
        values.put(NEWS_HEADLINE, feedItem.getHeadLine());
        values.put(NEWS_DATELINE, feedItem.getDateLine());
        values.put(NEWS_PHOTO, feedItem.getImage());
        values.put(NEWS_STORY, feedItem.getStory());
        values.put(NEWS_WEBURL, feedItem.getWebURL());

        // Inserting Row

        db.insert(TABLE_NEWS, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<FeedItem> fetchItem() {

        String selectQuery = "SELECT  * FROM " + TABLE_NEWS;
        ArrayList<FeedItem> feedList = new ArrayList<FeedItem>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FeedItem item = new FeedItem();
                item.setNewsItemId(cursor.getString(cursor.getColumnIndex(NEWS_ID)));
                item.setHeadLine(cursor.getString(cursor.getColumnIndex(NEWS_HEADLINE)));
                item.setDateLine(cursor.getString(cursor.getColumnIndex(NEWS_DATELINE)));
                item.setImage(cursor.getString(cursor.getColumnIndex(NEWS_PHOTO)));
                item.setStory(cursor.getString(cursor.getColumnIndex(NEWS_STORY)));
                item.setWebURL(cursor.getString(cursor.getColumnIndex(NEWS_WEBURL)));

                feedList.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return feedList;
    }
}
