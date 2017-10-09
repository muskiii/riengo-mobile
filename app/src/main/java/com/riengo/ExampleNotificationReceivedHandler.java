package com.riengo;


import android.util.Log;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

/**
 * Yepes: Esto parece que debería funcionar, pero no lo hace
 * https://documentation.onesignal.com/docs/android-native-sdk#section-receiving-notifications
 *
 */

public class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
    @Override
    public void notificationReceived(OSNotification notification) {
        Log.i("OneSignalExample","llegó una notificacion de Onesignal!");
        JSONObject data = notification.payload.additionalData;
        String customKey;

        if (data != null) {
            customKey = data.optString("customkey", null);
            if (customKey != null)
                Log.i("OneSignalExample", "customkey set with value: " + customKey);
        }
    }
}