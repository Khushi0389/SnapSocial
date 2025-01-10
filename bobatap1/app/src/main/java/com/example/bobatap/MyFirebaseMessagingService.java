package com.example.bobatap;

import android.content.Intent;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            // Handle data payload of the message, if any.
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            // Notify the ChatActivity about the new message.
            sendNotificationToChat(remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            // Handle notification payload of the message, if any.
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            // You can choose to display a notification to the user here.
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // Send the new registration token to your server or associate it with the user.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // Implement the logic to send the token to your server here.
    }

    private void sendNotificationToChat(Map<String, String> messageData) {
        Intent intent = new Intent("new_message");
        intent.putExtra("message", messageData.get("message"));

        // Send a local broadcast to notify the ChatActivity about the new message.
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

