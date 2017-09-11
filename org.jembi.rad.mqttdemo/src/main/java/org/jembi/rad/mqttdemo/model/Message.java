package org.jembi.rad.mqttdemo.model;

import java.util.Date;

public class Message {

    private Date datetime;
    private String message;

    public Message(Date datetime, String message) {
        this.datetime = datetime;
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
}
