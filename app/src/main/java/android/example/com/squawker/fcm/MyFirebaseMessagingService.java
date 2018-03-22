package android.example.com.squawker.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Sergio on 22/03/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final int NOTIFICATION_ID = 111;
    private static final String CHANNEL_ID = "message_channel_id";

    private static PendingIntent getPendingIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("Sergio>", this + " onMessageReceived\nremoteMessage= " + remoteMessage);
        if (remoteMessage == null) return;

        Map<String, String> data = remoteMessage.getData();
        if (data == null) return;

        ContentValues values = new ContentValues();
        values.put(SquawkContract.COLUMN_AUTHOR, data.get("author"));
        values.put(SquawkContract.COLUMN_MESSAGE, data.get("message"));
        values.put(SquawkContract.COLUMN_DATE, data.get("date"));
        values.put(SquawkContract.COLUMN_AUTHOR_KEY, data.get("authorKey"));

        getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI, values);

        Context context = getApplicationContext();


        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android Oreo devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.test)
                .setContentTitle(data.get("author"))
                .setContentText(data.get("message"))
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(context.getResources(), R.drawable.asser)))
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
                .setColorized(true)
                .setContentIntent(getPendingIntent(context))
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.quackquack_soundbible))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        // COMPLETED (12) Trigger the notification by calling notify on the NotificationManager.
        // Pass in a unique ID of your choosing for the notification and notificationBuilder.build()
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());


    }

}
// example server data:
//{"to":"","data":{"author":"TestAccount","message":"Venmo knausgaard actually distillery 8-bit ethical","date":1521736350167,"authorKey":"key_test"}}

//{"to":"eAbZ8L3lnz8:APA91bF5Xt03b09QMU4oBwAdpI-74tTzm2uF_1fo2q0CRr_-VmKB_nh28zB90TyrYfdHW7HXppN6S8KZJDWqL-DvKCxINm7nPy8Dr_qZ8wtMyo2zhwU0VLpXGeS6uqd_jRvKgK3P3JWX","data":{"author":"TestAccount","message":"Fap tilde butcher keffiyeh, helvetica master cleanse readymade, keffiyeh leggings semiotics la croix kale chips biodiesel quinoa affogato, heirloom irony bushwick street art tumeric tofu knausgaard actually distillery 8-bit ethical","date":1521737207342,"authorKey":"key_test"}}