package org.jembi.rad.mqttdemo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Android Application class for the app. It is the the base class and contains references to all other
 * components (e.g. activities or services). It is instantiated before any other class when the process
 * is created.
 *
 * It is not required and should be used very carefully, it is primarily used for initialization of
 * global state before the first Activity is displayed.
 *
 * This Application implementation is used to implement the Activity Lifecycle callbacks to monitor
 * if the app is being used. This is useful to know whether or not to send status notifications when
 * a message arrives.
 */
public class RadMQTTDemoApplication extends Application {

    public static final String LOG_TAG = "MQTTDemo";

    private static RadMQTTDemoApplication instance;
    private static boolean appInForeground = false;

    /**
     * Provides a handle on the Application singleton
     * @return RadMQTTDemoApplication
     */
    public static RadMQTTDemoApplication getInstance() {
        return instance;
    }

    /**
     * Indicates if the app is currently being displayed to the user (or being used by the user).
     * @return true if the app is in the foreground, on top of all other apps
     */
    public boolean isAppInForeground() {
        return appInForeground;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.i(RadMQTTDemoApplication.LOG_TAG, "onActivityResumed");
                appInForeground = true;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.i(RadMQTTDemoApplication.LOG_TAG, "onActivityPaused");
                appInForeground = false;
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
        Log.i(RadMQTTDemoApplication.LOG_TAG, "Created RAD MQTT Demo Application");
    }
}
