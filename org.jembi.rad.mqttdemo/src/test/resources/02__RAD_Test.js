var Thread = Java.type("java.lang.Thread");
var topic = 'org/jembi/rad/mqttdemo';

function execute(action) {
    out("Test Script: " + action.getName());
    publish('Vaccination Appointment Reminder: Saturday 23 September 2017 for Simon');
    Thread.sleep(15000);
    publish('Vaccination Appointment Reminder: your appointment is in 3 days');
    Thread.sleep(15000);
    publish('Vaccination Appointment Reminder: your appointment is tomorrow - Saturday 23 September 2017');
    Thread.sleep(15000);
    publish('Thank you for attending your appointment. Simon has received the BCG (TB) vaccination. Your next appointment will be on Saturday 21 October 2017.');
    action.setExitCode(0);
    action.setResultText("done.");
    out("Test Script: Done");
    return action;
}

function publish(message) {
    out("publishing message: " + message);
    mqttManager.publish(topic, message);
}

function out(message){
     output.print(message);
}
