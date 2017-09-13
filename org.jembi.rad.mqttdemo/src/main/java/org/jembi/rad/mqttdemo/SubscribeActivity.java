package org.jembi.rad.mqttdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jembi.rad.mqttdemo.model.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class SubscribeActivity extends AppCompatActivity {

    private static int qos = 1;
    private static boolean cleanSession = false;
    private static boolean automaticReconnect = true;

    private SharedPreferences preferences;
    private MqttAndroidClient mqttAndroidClient;
    private RecyclerView messageView;
    private MessageViewAdapter messageAdapter;

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
        messageAdapter.addMessage(new Message(new Date(), "Welcome to the RAD MQTT Demo App"));

        // get connection details from preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String serverUri =  preferences.getString(this.getString(R.string.server_uri_label), this.getString(R.string.server_uri));
        String clientId =  preferences.getString(this.getString(R.string.client_id_label), null);

        if (clientId == null) {
            // set the clientId if it hasn't been set already (i.e. probably the first time using the application)
            clientId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(this.getString(R.string.client_id_label), clientId);
            editor.commit();
        }

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                goOnline();

                if (reconnect) {
                    displayMessage("Reconnected to : " + serverURI);
                    if (cleanSession) {
                        subscribeToTopic();
                    }
                } else {
                    displayMessage("Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                displayMessage("The Connection was lost.");
                goOffline();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                // this callback is unnecessary because we will request a separate callback after subscription to a topic
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // we aren't sending messages, so no need to implement this callback method
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(automaticReconnect);
        mqttConnectOptions.setCleanSession(cleanSession);


        try {
            IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("LOG", "Exception while connecting", exception);
                    displayMessage("Failed to connect to: " + serverUri);
                }
            });


        } catch (MqttException ex){
            Log.e("LOG", "Exception while connecting", ex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subscribe, menu);
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

    public void subscribeToTopic(){
        final String subscriptionTopic = preferences.getString(this.getString(R.string.topic_label), this.getString(R.string.topic_name));
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, qos, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    displayMessage("Subscribed to: " + subscriptionTopic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    displayMessage("Failed to subscribe to: " + subscriptionTopic);
                }
            });

            mqttAndroidClient.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // message Arrived!
                    String messageContent = new String(message.getPayload());
                    messageAdapter.addMessage(new Message(new Date(), messageContent));
                    messageView.smoothScrollToPosition(messageAdapter.getItemCount());
                }
            });

        } catch (MqttException ex){
            Log.e("LOG", "Exception while subscribing", ex);
        }
    }


    private void displayMessage(String mainText){
        Log.i("LOG", mainText);
        Snackbar.make(findViewById(android.R.id.content), mainText, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void goOffline() {
        findViewById(R.id.offline_icon).setVisibility(View.VISIBLE);
    }

    private void goOnline() {
        findViewById(R.id.offline_icon).setVisibility(View.INVISIBLE);
    }
}
