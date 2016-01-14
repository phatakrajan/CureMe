package com.cureme.gcm;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class CureMeBroadcastReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setClass(context, CureMeIntentService.class)));
        setResultCode(Activity.RESULT_OK);
	}

}
