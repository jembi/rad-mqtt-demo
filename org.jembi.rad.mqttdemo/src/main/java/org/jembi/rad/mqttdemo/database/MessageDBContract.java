package org.jembi.rad.mqttdemo.database;

import android.provider.BaseColumns;

/**
 * Defines the Message Database structure and SQL statements to create and drop the table
 */
public final class MessageDBContract {

    public MessageDBContract() {
    }

    public static class MessageEntry implements BaseColumns {
        public static final String TABLE_NAME ="message";
        public static final String COLUMN_NAME_MESSAGE_TEXT ="text";
        public static final String COLUMN_NAME_DATE ="date";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + MessageEntry.TABLE_NAME;

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + MessageEntry.TABLE_NAME  + " (" +
                MessageEntry._ID + " INTEGER PRIMARY KEY, " +
                MessageEntry.COLUMN_NAME_MESSAGE_TEXT + " TEXT, " +
                MessageEntry.COLUMN_NAME_DATE + " NUMERIC )";
    }
}


