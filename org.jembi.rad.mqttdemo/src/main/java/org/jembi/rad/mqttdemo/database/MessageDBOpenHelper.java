package org.jembi.rad.mqttdemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.jembi.rad.mqttdemo.model.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.database.sqlite.SQLiteDatabase.*;
import static org.jembi.rad.mqttdemo.database.MessageDBContract.*;
import static org.jembi.rad.mqttdemo.database.MessageDBContract.MessageEntry.COLUMN_NAME_DATE;
import static org.jembi.rad.mqttdemo.database.MessageDBContract.MessageEntry.COLUMN_NAME_MESSAGE_TEXT;
import static org.jembi.rad.mqttdemo.database.MessageDBContract.MessageEntry.SQL_CREATE_TABLE;
import static org.jembi.rad.mqttdemo.database.MessageDBContract.MessageEntry.SQL_DROP_TABLE;
import static org.jembi.rad.mqttdemo.database.MessageDBContract.MessageEntry.TABLE_NAME;

/**
 * Created by Jembi Health Systems on 2017/09/13.
 */

public class MessageDBOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Message.db";
    private SQLiteDatabase database;

    public MessageDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        open();
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

    public void insertMessage( Message message) {
        open();
        ContentValues contentValues = createContentValues(message);
        database.insert(TABLE_NAME, null, contentValues);
    }

    public List<Message> getPreviousMessages() {
        String[] projection = {MessageEntry.COLUMN_NAME_DATE, MessageEntry.COLUMN_NAME_MESSAGE_TEXT};
        String selectionCriteria = MessageEntry.COLUMN_NAME_DATE + " <  ?";
        String[] selectionArgs = {String.valueOf(new Date().getTime())};
        String sortOrder = MessageEntry.COLUMN_NAME_DATE + " DESC";
        open();
        Cursor cursor = database.query(MessageEntry.TABLE_NAME,
                projection, selectionCriteria, selectionArgs, null, null, sortOrder);

        List<Message> messages = readFromCursor(cursor);

        return messages;
    }

    private List<Message> readFromCursor(Cursor cursor) {
        List<Message> messages = new ArrayList<>();
        while (cursor.moveToNext()) {
            Long date = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_NAME_DATE));
            String message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_MESSAGE_TEXT));
            messages.add(new Message(new Date(date), message));
        }
        return messages;
    }


    private void open() {
        if(database == null) {
            database = this.getWritableDatabase();
        }
    }

    private ContentValues createContentValues(Message message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_MESSAGE_TEXT, message.getMessage());
        contentValues.put(COLUMN_NAME_DATE, message.getDatetime().getTime());
        return contentValues;
    }

}
