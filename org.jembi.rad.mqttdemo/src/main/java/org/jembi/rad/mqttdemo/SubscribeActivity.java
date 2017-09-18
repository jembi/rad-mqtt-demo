package org.jembi.rad.mqttdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.jembi.rad.mqttdemo.database.MessageDBOpenHelper;
import org.jembi.rad.mqttdemo.model.Message;
import org.jembi.rad.mqttdemo.service.MessageService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SubscribeActivity extends AppCompatActivity {

    private RecyclerView messageView;
    private MessageViewAdapter messageAdapter;
    private MessageDBOpenHelper dbOpenHelper;

    private BroadcastReceiver alertReceiver = null;
    private BroadcastReceiver messageReceiver = null;
    private BroadcastReceiver connectionReceiver = null;

    private MenuItem offlineIcon;
    private Boolean connected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set up messages content screen
        messageView = (RecyclerView) findViewById(R.id.messages);
        messageAdapter = new MessageViewAdapter(new ArrayList<Message>());
        messageView.setAdapter(messageAdapter);
        dbOpenHelper = new MessageDBOpenHelper(this);
        List<Message> previousMessages = dbOpenHelper.getPreviousMessages();
        if(!previousMessages.isEmpty()) {
            for(Message message : previousMessages)  {
                messageAdapter.addMessage(message);
            }
        } else {
            messageAdapter.addMessage(new Message(new Date(), "Welcome to the RAD MQTT Demo App"));
        }

        // set up broadcast receiver to receive notifications about new alerts
        alertReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                displayAlert(intent.getStringExtra(MessageService.EVENT_ALERT_MESSAGE));
            }
        };

        // set up broadcast receiver to receive notifications about change in connection status
        connectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                changeConnectionStatus(intent.getExtras().getBoolean(MessageService.EVENT_BROKER_CONNECTION_STATUS));
            }
        };

        // set up broadcast receiver to receive notifications about new messages
        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                displayMessage((Message)intent.getParcelableExtra(MessageService.EVENT_MESSAGE_CONTENT));
            }
        };

        // set up crash handler to log unhandled exceptions
        // Note: in a production app, this should be a service like Crashalytics which will
        // allow you to monitor exceptions thrown on client devices
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.e("LOG", "App crashed!! Exception:", ex);
            }
        });

        // schedule message service
        Intent service = new Intent(getApplicationContext(), MessageService.class);
        getApplicationContext().startService(service);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(alertReceiver, new IntentFilter(MessageService.EVENT_ALERT));
        localBroadcastManager.registerReceiver(connectionReceiver, new IntentFilter(MessageService.EVENT_BROKER_CONNECTION));
        localBroadcastManager.registerReceiver(messageReceiver, new IntentFilter(MessageService.EVENT_MESSAGE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subscribe, menu);
        offlineIcon = menu.findItem(R.id.offline_icon);
        changeConnectionStatus(connected); // to ensure the latest status is displayed
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(SubscribeActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayMessage(Message message) {
        Log.i("LOG", "MQTT message incoming: " + message.getMessage());
        messageAdapter.addMessage(message);
        dbOpenHelper.insertMessage(message);
        messageView.smoothScrollToPosition(messageAdapter.getItemCount());
    }

    private void displayAlert(String alert) {
        Log.i("LOG", "Alert received: " + alert);
        //Snackbar.make(findViewById(android.R.id.content), alert, Snackbar.LENGTH_LONG)
        //        .setAction("Action", null).show();
    }

    private void changeConnectionStatus(Boolean status) {
        Log.i("LOG", "Connection status: " + status);
        connected = status;
        if (status == null) {
            Log.i("LOG", "Could not determine the current connection status");
            return;
        }
        if (offlineIcon == null) {
            Log.e("LOG", "offlineIcon has not yet been initialised!");
            return;
        }
        if (status == Boolean.TRUE) {
            offlineIcon.setVisible(false);
        } else if (status == Boolean.FALSE) {
            offlineIcon.setVisible(true);
        }
    }
}
