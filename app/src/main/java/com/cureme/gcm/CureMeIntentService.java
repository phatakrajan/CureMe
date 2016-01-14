package com.cureme.gcm;



import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.cureme.R;
import com.cureme.activities.CureMeConstants;
import com.cureme.activities.MainActivity;
import com.cureme.utils.ListTagHandler;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class CureMeIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static final String TAG = "CureMe GCM";
    
	public CureMeIntentService() {
		super("CureMeIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
            	String message = intent.getStringExtra("CureMe");
                sendNotification(message);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        CureMeBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
	private void sendNotification(String msg) {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Log.i(TAG, "Inside SendNotification");
		try {
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra(CureMeConstants.MESSAGE, msg);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);

			String[] lines = msg.split(System.getProperty("line.separator"));
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this)
					.setSmallIcon(R.drawable.store_icon)
					.setContentTitle("Tip of the day")
					.setAutoCancel(true)
					.setContentText(
							Html.fromHtml(lines[0], null, new ListTagHandler()));
					

			Uri alarmSound = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			mBuilder.setSound(alarmSound);

			mBuilder.setVibrate(null);
			mBuilder.setContentIntent(contentIntent);

			mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			
		} catch (Exception e) {
			Log.i(TAG, e.getMessage());
		}

	}
	
}
