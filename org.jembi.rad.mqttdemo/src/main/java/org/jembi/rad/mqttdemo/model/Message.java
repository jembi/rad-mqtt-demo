package org.jembi.rad.mqttdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Message implements Parcelable {

    private Date datetime;
    private String message;

    public Message(Date datetime, String message) {
        this.datetime = datetime;
        this.message = message;
    }

    protected Message(Parcel in) {
        datetime = new Date(in.readLong());
        message = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message { " + "datetime=" + datetime + ", message='" + message + '\'' + " }";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(datetime.getTime());
        parcel.writeString(message);
    }
}