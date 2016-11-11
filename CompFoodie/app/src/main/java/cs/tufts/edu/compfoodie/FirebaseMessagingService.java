package cs.tufts.edu.compfoodie;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;

import static android.R.id.message;

/**
 * Created by ngapham on 11/10/16.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("MESSAGE", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("MESSAGE", "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("MESSAGE", "Message Notification Body: " + remoteMessage.getNotification().getTitle());
            //Toast.makeText(this, "ORDER READY! remoteMessage.getNotification().getTitle()", Toast.LENGTH_SHORT).show();
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}