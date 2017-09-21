# MQTT DEMO APPLICATION


## MQTT

MQTT stands for Message Queue Telemetry Transport and is a publish/subscribe messaging protocol designed for lightweight
machine-to-machine/Internet of Things (IoT) communications. It was originally developed by IBM and is now an open standard.
It is useful for connections with remote locations where a small code footprint is required and/or network bandwidth is at a minimum.
When a device or service publishes data to a topic, all of the devices subscribed will automatically get the updated information.

See documentation on [Messaging Protocols](https://docs.google.com/document/d/1AmpT4td30J9jhD9ZksCafrKAt1aIqrTfTQ5dmp_lDjk)

## Demo app

This demo application was adapted from the [Paho Android Service Example app](https://github.com/eclipse/paho.mqtt.android).
It is an MQTT client library written in Java for developing applications on Android. This app uses the
Paho Android Service which provides a library for implementing MQTT.


The demo app is a simple MQTT subscriber that receives messages on a configurable MQTT topic.

### Versions

The libraries/frameworks used:

 - Android: platform 24
 - Paho MQTT Android Service: 1.1.1

### Getting started

#### Step 1: Install Android Studio

To get started, it is recommended that you download [Android Studio](http://developer.android.com/tools/studio/index.html).
If you don't wish to download Android Studio, you will need to install the Android SDK manually.

#### Step 2: Download the source code from Github

```
git clone https://github.com/jembi/rad-mqtt-demo.git
```

Note: You can also chose to create a new Android Studio project directly from Github and skip the 
next step.

#### Step 3: Import the project into Android Studio

In Android Studio, select File -> New -> Import Project. Then select the directory in which the
project was cloned (step above).

The project should be automatically built, but if it isn't, run Build -> Clean Project or
Build -> Rebuild Project.

Check that there aren't any errors in the Event Log and that the Gradle build finished with no errors.

### Testing the app

In Android Studio, a "Configuration" should have been created automatically for "org.jembi.rad.mqttdemo".

To run the app, you can press the green "play"  button on the toolbar, use Shift-F10, or select 
Run -> Run  "org.jembi.rad.mqttdemo".

It's quickest to run the app on an actual device rather than an [emulator](https://developer.android.com/studio/run/emulator.html), but either should work.

#### Running on a device

1. Plugin in the device via USB
2. Enable Developer Mode on the device:
  * go to Settings > More > About and tap "Build number" 7 times
  * go to Settings > More > Developer Options and enable "USB debugging"
  * See more: [Configure On-Device Developer Options](https://developer.android.com/studio/debug/dev-options.html)
3. Press Shift-F10 (or run using the green play button on the toolbar)
4. Select your device from the list in the popup (under Connected Devices). If your device is not listed, ensure that it is 
properly connected and that USB debugging has been enabled (a popup is sometimes displayed on the device).

#### Running on an emulator

1. Go to Tools -> Android -> AVD Manager
2. Click the "Create Virtual Device" button
3. Choose your target device and target Android version (this will probably require you to download an image)
4. Finish the create AVD wizard
5. Press Shift-F10 (or run using the green play button on the toolbar)
6. Select your AVD from the list of Available Virtual Devices and click OK

#### Using the app

The app is simple to use. It displays and stores messages published to the topic. The broker, topic,
 and client id can be configured in the Settings.
 
The app is able to run in the background, so it can receive messages even if the app is not running,
 or is running in the background. A notification will be displayed if the app receives a message when
 it is not open. The notification can be configured in the Settings.

#### Publishing MQTT messages

Using an MQTT application like [MQTT.fx](http://www.mqttfx.org/), publish a message to the topic 
"org/jembi/rad/mqttdemo" using the Eclipse IOT broker (iot.eclipse.org).

The message should be delivered to all apps running the demo app. If the app is currently open on
the device, a message will just be added to the top of the message list. If the app is not currently
running, or is in the background, a message notification will be created.

Note: If the app MQTT settings have been changed (i.e. the broker or topic), then use the same
topic and broker in your MQTT application.

See the [MQTT.fx script](org.jembi.rad.mqttdemo/src/test/resources/02__RAD_Test.js) which can be used
to automate a test with the app. 

1. Copy this script into ~/MQTT-FX/scripts
2. Restart the MQTT.fx application
3. Connect to the Eclipse IOT broker (iot.eclipse.org)
4. Go to the Scripts tab
5. Select "RAD Test" from the drop down
6. Click Execute
 
### Troubleshooting 

Things to note:
1. If the MQTT settings are changed, the app will need to be restarted for these to take effect.
2. The client id must be unique otherwise the clients will be disconnected.
3. The publisher must use the same broker and topic as the client.