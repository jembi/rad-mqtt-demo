package org.jembi.rad.mqttdemo.database;

/**
 * Allows the Database to return data from an AsyncTask without blocking the thread.
 *
 * [T] is the return type of the AsyncTask.
 */

public interface DatabaseResult<T> {
    /**
     * Process the data returned by the AsyncTask
     * @param result T, the specified generic Task Return Type
     */
    public void processResult(T result);

    /**
     * Process any error that occurs during the execution of the Task
     * @param e Exception
     */
    public void processException(Exception e);
}
