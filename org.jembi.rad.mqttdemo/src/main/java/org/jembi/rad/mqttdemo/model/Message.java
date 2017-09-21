package org.jembi.rad.mqttdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Class that is used to persist and display a message that is received on the configured topic
 */
public class Message implements Parcelable {

    private Date datetime;
    private String message;
    private int qos;
    private boolean isRetained;
    private boolean isDuplicate;

    public Message(Date datetime, String message) {
        this.datetime = datetime;
        this.message = message;
    }

    /**
     * The Creator is used to deserialise the Message from a Parcel, which is used
     * when the Message is passed as an extra in an Intent that is send as a Broadcast
     * from the MessageService to the SubscribeActivity.
     */
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

    public Message(Date datetime, int qos, boolean isRetained, boolean isDuplicate, String message) {
        this.datetime = datetime;
        this.qos = qos;
        this.isDuplicate = isDuplicate;
        this.isRetained = isRetained;
        this.message = message;
    }

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

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isRetained() {
        return isRetained;
    }

    public void setRetained(boolean retained) {
        isRetained = retained;
    }

    public boolean isDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(boolean duplicate) {
        isDuplicate = duplicate;
    }

    protected Message(Parcel in) {
        datetime = new Date(in.readLong());
        message = in.readString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (datetime != null ? !datetime.equals(message1.datetime) : message1.datetime != null)
            return false;
        return message != null ? message.equals(message1.message) : message1.message == null;
    }

    @Override
    public int hashCode() {
        int result = datetime != null ? datetime.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
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