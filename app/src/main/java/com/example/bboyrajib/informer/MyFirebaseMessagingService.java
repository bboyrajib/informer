package com.example.bboyrajib.informer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.logging.Handler;

/**
 * Created by bboyrajib on 12/04/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

   // SharedPreferences prefs;
  //  SharedPreferences.Editor editor=prefs.edit();





    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       // Log.e(TAG, "From: " + remoteMessage.getFrom());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        /*if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            Log.e(TAG, "Notification From: " + remoteMessage.getFrom());
            sendNotification((remoteMessage.getNotification().getTitle()),remoteMessage.getNotification().getBody());
           // SharedPreferences.Editor editor=prefs.edit();
          //  editor.putString("topic",remoteMessage.getNotification().getTitle());
          //  editor.putString("message",remoteMessage.getNotification().getBody());
          //  editor.commit();
            String current_room=prefs.getString("room",null);
            if(current_room.equalsIgnoreCase(remoteMessage.getNotification().getTitle())) {
                updateMyActivity(this, remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
                return;
            }
           // sendMessageToActivity(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
            //handleNotification(remoteMessage.getNotification().getBody());
        }*/

        // Check if message contains a data payload.
       if (remoteMessage.getData().size() > 0) {
           Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                //JSONObject json = new JSONObject(remoteMessage.getData().toString());
                //handleDataMessage(json);
                Map<String,String> data=remoteMessage.getData();
                String title=data.get("title");
                String body=data.get("body");
              //  editor.putString("bg",null).commit();
                if(!NotificationUtils.isAppIsInBackground(getApplicationContext())) {


                    String current_room = prefs.getString("room", null);

                    if (current_room.equalsIgnoreCase(title)) {
                        updateMyActivity(this, body, title);


                    }
                    else
                        sendNotification(title, body);
                    SharedPreferences.Editor editor=prefs.edit();
                    editor.putString("messages","").commit();
                    editor.putString("bg",body).commit();
                    editor.putString("room",title).commit();
                    Intent result=new Intent(getApplicationContext(),MainActivity.class);
                    result.putExtra("room",title);
                    result.putExtra("message",body);
                    startActivity(result);
                }
                else{
                    //Log.d("TAG",title);

                    String c_room=prefs.getString("room",null);
                    if(c_room.equalsIgnoreCase(title)) {

                        sendNotification(title, body);
                        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("bg", body).commit();
                        resultIntent.putExtra("message", body);
                        resultIntent.putExtra("room", title);
                        startActivity(resultIntent);
                    }
                    else {
                        sendNotification(title, body);
                        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("bg", body).commit();
                        editor.putString("room",title).commit();
                        editor.putString("messages","").commit();
                        resultIntent.putExtra("message", body);
                        resultIntent.putExtra("room", title);
                        startActivity(resultIntent);
                    }
                }
                //updateMyActivity(this,body,title);data.get("title");
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
            return;
        }
    }

    private void sendNotification(String title,String body){

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(1)
                .setSound(alarmSound)
                .setWhen(System.currentTimeMillis()).setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setLights(Color.RED, 3000, 3000)
                .setAutoCancel(true);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("room",title);
        notificationIntent.putExtra("message",body);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
                //0);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());

        //updateMyActivity(this,body,title);

    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

          /*  Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);*/


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

   /* private void sendMessageToActivity(String topic, String msg) {
        Intent intent = new Intent(MyFirebaseMessagingService.this,MainActivity.class);
        // You can also include some extra data.
        intent.putExtra("message", msg);
        intent.putExtra("topic", topic);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }*/

    static void updateMyActivity(Context context, String message,String topic) {

        Intent intent = new Intent("unique_name");

        //put whatever data you want to send, if any
        intent.putExtra("message", message);
        intent.putExtra("topic",topic);

        //send broadcast
        context.sendBroadcast(intent);
    }



}
