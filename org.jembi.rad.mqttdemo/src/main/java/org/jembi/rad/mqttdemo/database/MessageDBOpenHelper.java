package org.jembi.rad.mqttdemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import org.jembi.rad.mqttdemo.RadMQTTDemoApplication;
import org.jembi.rad.mqttdemo.model.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.jembi.rad.mqttdemo.database.MessageDBContract.*;
import static org.jembi.rad.mqttdemo.database.MessageDBContract.MessageEntry.COLUMN_NAME_DATE;
import static org.jembi.rad.mqttdemo.database.MessageDBContract.MessageEntry.COLUMN_NAME_MESSAGE_TEXT;
import static org.jembi.rad.mqttdemo.database.MessageDBContract.MessageEntry.SQL_CREATE_TABLE;
import static org.jembi.rad.mqttdemo.database.MessageDBContract.MessageEntry.SQL_DROP_TABLE;
import static org.jembi.rad.mqttdemo.database.MessageDBContract.MessageEntry.TABLE_NAME;

/**
 * Class that handles the Message Database interaction. It is used to manage creating, upgrading,
 * downgrading of the database. Also opening of the database connection, and running specific operations
 * to insert and query data.
 */
public class MessageDBOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Message.db";
    private SQLiteDatabase database;
    private DatabaseResult<List<Message>> databaseResult;

    public MessageDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DROP_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }

    /**
     * Retrieves the messages stored in the database asynchronously
     * @param databaseResult DatabaseResult, a callback for the List of Messages that is returned
     */
    public void getPreviousMessages(DatabaseResult<List<Message>> databaseResult) {
            new GetPreviousMessagesTask(databaseResult).execute();
    }

    /**
     * Adds a new message to the database
     * @param message Message to insert
     */
    public void insertMessage(Message message) {
        new InsertMessageTask().execute(message);
    }

    /**
     * Class that handles the asynchronous task to query messages from the database
     */
    private class GetPreviousMessagesTask extends AsyncTask<Void, Void, List<Message>> {

        private DatabaseResult<List<Message>> callback;

        public GetPreviousMessagesTask(DatabaseResult<List<Message>> callback) {
            this.callback = callback;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(RadMQTTDemoApplication.LOG_TAG, "Retrieving previous messages");
            openDBConnection();

        }

        @Override
        protected List<Message> doInBackground(Void... params) {
            List<Message> messages = new ArrayList<>();
            Cursor cursor = null;
            try {
                String[] projection = {MessageEntry.COLUMN_NAME_DATE, MessageEntry.COLUMN_NAME_MESSAGE_TEXT};
                String selectionCriteria = MessageEntry.COLUMN_NAME_DATE + " <  ?";
                String[] selectionArgs = {String.valueOf(new Date().getTime())};
                String groupBy = MessageEntry.COLUMN_NAME_MESSAGE_TEXT;

                cursor = database.query( MessageEntry.TABLE_NAME,
                        projection, selectionCriteria, selectionArgs, groupBy, null, null);
                messages = readFromCursor(cursor);
            } catch (Exception e) {
                callback.processException(e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return messages;
        }

        @Override
        protected void onPostExecute(List<Message> messages) {
            super.onPostExecute(messages);
            callback.processResult(messages);
        }

        private List<Message> readFromCursor(Cursor cursor) throws IllegalArgumentException {
            List<Message> messages = new ArrayList<>();
            while (cursor.moveToNext()) {
                Long date = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_DATE));
                String message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_MESSAGE_TEXT));
                messages.add(new Message(new Date(date), message));
            }
            return messages;
        }
    }

    /**
     * Class that handles the asynchronous task to insert message
     */
    private class InsertMessageTask extends AsyncTask<Message, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            openDBConnection();
            Log.i(RadMQTTDemoApplication.LOG_TAG, "Saving message");

        }

        /**
         *
         * Checks that the received message is not a duplicate of
         * the last received message before saving
         * @return
         */
        @Override
        protected Void doInBackground(Message... messages) {
            Cursor cursor = null;
            try {
                ContentValues contentValues = createContentValues(messages[0]);
                String sql = "SELECT " + MessageEntry.COLUMN_NAME_MESSAGE_TEXT + " FROM " + MessageEntry.TABLE_NAME +
                        " WHERE " + MessageEntry._ID + " = (SELECT MAX(_id) FROM " + MessageEntry.TABLE_NAME + ")" +
                        " AND " + MessageEntry.COLUMN_NAME_MESSAGE_TEXT + " LIKE \"%" + messages[0].getMessage() + "%\"";
                
                cursor = database.rawQuery(sql, null);
                if (cursor.getCount() <= 0) {
                    database.insert(TABLE_NAME, null, contentValues);
                }
            } catch (Exception ex) {
                Log.e(RadMQTTDemoApplication.LOG_TAG, "Could not perform insert message due to error: " + ex.getStackTrace());
            } finally {
                if(cursor != null) {
                    cursor.close();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(RadMQTTDemoApplication.LOG_TAG, "Message saved");
        }

        private ContentValues createContentValues(Message message) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME_MESSAGE_TEXT, message.getMessage());
            contentValues.put(COLUMN_NAME_DATE, message.getDatetime().getTime());
            return contentValues;
        }

    }

    private void openDBConnection() {
        if(database == null) {
            database = this.getWritableDatabase();
        }
    }
}