package android.example.com.squawker.fcm;

import android.content.ContentValues;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Sergio on 22/03/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

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
    }
}
// example server data:
//{"to":"","data":{"author":"TestAccount","message":"Venmo knausgaard actually distillery 8-bit ethical","date":1521736350167,"authorKey":"key_test"}}

//{"to":"eAbZ8L3lnz8:APA91bF5Xt03b09QMU4oBwAdpI-74tTzm2uF_1fo2q0CRr_-VmKB_nh28zB90TyrYfdHW7HXppN6S8KZJDWqL-DvKCxINm7nPy8Dr_qZ8wtMyo2zhwU0VLpXGeS6uqd_jRvKgK3P3JWX","data":{"author":"TestAccount","message":"Fap tilde butcher keffiyeh, helvetica master cleanse readymade, keffiyeh leggings semiotics la croix kale chips biodiesel quinoa affogato, heirloom irony bushwick street art tumeric tofu knausgaard actually distillery 8-bit ethical","date":1521737207342,"authorKey":"key_test"}}