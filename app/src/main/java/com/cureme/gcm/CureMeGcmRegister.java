package com.cureme.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.cureme.activities.GroupItemActivity;
import com.cureme.utils.HttpUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

public class CureMeGcmRegister {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    
	private static final String CUREME_GCM_REG_URL = "http://cureme-appid.appspot.com/register?";
    //private static final String CUREME_GCM_REG_URL = "http://10.0.2.2:8080/register?";

	/** Global instance of the HTTP transport. */
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /**
     * Tag used on log messages.
     */
    static final String TAG = "CureMeGCM";

    GoogleCloudMessaging gcm;

    RegisterInBackground regStatus ;

    String regid;
    Context context;
    Activity activity;

    public CureMeGcmRegister(Context con, Activity act){
    	context = con ;
    	activity = act ;
    }
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "1004669118337";
    
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
    	regStatus = new RegisterInBackground();
    	regStatus.execute(null, null, null);
    }
    
    class RegisterInBackground extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                
				for (int i = 0; i < 5; ++i) {
					try {
						regid = gcm.register(SENDER_ID);
						break ;
					} catch (Exception e) {
						Thread.sleep(100);	
					}
				}
				
				if (regid.isEmpty()){
					return "";
				}
                msg = "Device registered, registration ID = " + regid;

                Log.i(TAG,msg);
                // You should send the registration ID to your server over HTTP, so it
                // can use GCM/HTTP or CCS to send messages to your app.
                sendRegistrationIdToBackend(regid);

                // Persist the regID - no need to register again.
                storeRegistrationId(context, regid);
            } catch (Exception ex) {
                msg = "Error :" + ex.getMessage();
                Log.i(TAG,msg);
                clearSharedPreferences(context);
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
            return msg;
        }

        /**
         * Stores the registration ID and the app versionCode in the application's
         * {@code SharedPreferences}.
         *
         * @param context application's context.
         * @param regId registration ID
         */
        private void storeRegistrationId(Context context, String regId) {
            final SharedPreferences prefs = getGcmPreferences(context);
            int appVersion = getAppVersion(context);
            Log.i(TAG, "Saving regId on app version " + appVersion);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PROPERTY_REG_ID, regId);
            editor.putInt(PROPERTY_APP_VERSION, appVersion);
            editor.commit();
        }
        
		/**
         * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
         * messages to your app. Not needed for this demo since the device sends upstream messages
         * to a server that echoes back the message using the 'from' address in the message.
		 * @param regid 
		 * @throws Exception 
         */
        private void sendRegistrationIdToBackend(String regid) throws Exception {
        	Log.i(TAG,"Sending Reg Id to Background");
			HttpRequestFactory httpRequestFactory = HttpUtils.createRequestFactory(HTTP_TRANSPORT);
			HttpRequest request = httpRequestFactory
					.buildGetRequest(new GenericUrl(CUREME_GCM_REG_URL));
			request.getUrl().put("regId", regid);
			HttpResponse resp = request.execute();
			String response = resp.parseAsString();
			Log.i(TAG,response);
        }
		@Override
        protected void onPostExecute(String msg) {
            //mDisplay.append(msg + "\n");
        }

    }
	/**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
    	Log.i(TAG,"Getting registration Id");
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        Log.i(TAG,registrationId);
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            clearSharedPreferences(context);
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(GroupItemActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    
    private void clearSharedPreferences(Context context) {
    	SharedPreferences preferences = context.getSharedPreferences(GroupItemActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    	SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
    
    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices() {
        /*int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                activity.finish();
            }
            return false;
        }*/
        return true;
    }
    
    public void register(){
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(context);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
            	Log.i(TAG,"Register in Background");
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

    }
    

}
