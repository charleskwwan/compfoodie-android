package cs.tufts.edu.compfoodie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;

import java.lang.reflect.Array;

import static android.R.id.message;
import static android.app.PendingIntent.FLAG_ONE_SHOT;

/**
 * Created by ngapham on 11/10/16.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("MESSAGE", "HELLO");
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("MESSAGE", "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String message = remoteMessage.getNotification().getTitle();
            Log.d("MESSAGE", "Message Notification Body: " + message);

            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{loginIntent}, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setContentTitle("Order Alert");
            notificationBuilder.setContentText(message);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            notificationBuilder.setContentIntent(pendingIntent);
            notificationBuilder.setVibrate(new long[0]);

            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        }
    }
}