package org.jembi.rad.mqttdemo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 *
 */
public class RadMQTTDemoApplication extends Application {

    public static final String LOG_TAG = "MQTTDemo";

    private static RadMQTTDemoApplication instance;
    private static boolean appInForeground = false;

    public static RadMQTTDemoApplication getInstance() {
        return instance;
    }

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
