package org.jembi.rad.mqttdemo.database;

/**
 * Created by Jembi Health Systems on 2017/09/19.
 *
 *  Allows the Database to return data from an AsyncTask without blocking the thread.
 */

public interface DatabaseResult<T> {
    public void processResult(T result);
    public void processException(Exception e);
}
