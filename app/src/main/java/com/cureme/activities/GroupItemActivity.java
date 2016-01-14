package com.cureme.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cureme.CureMeApplication;
import com.cureme.R;
import com.cureme.data.CureMeCommon;
import com.cureme.data.CureMeDataSource;
import com.cureme.gcm.CureMeGcmRegister;
import com.cureme.utils.AlertDialogManager;
import com.cureme.utils.EMailUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

public class GroupItemActivity extends Activity implements OnItemClickListener {

	ListView listView;

	long lastPress;
	
	private InterstitialAd interstitial;

	private long INTERSTITIAL_DISPLAY_LENGHT = 2000;

	private AdView mAdView;

    @Override
    public void onStart() {
      super.onStart();
    }
    
    @Override
    public void onStop() {
      super.onStop();
    }
    
	// Invoke displayInterstitial() when you are ready to display an interstitial.
	  public void displayInterstitial() {
	    if (interstitial.isLoaded()) {
	      interstitial.show();
	    }
	  }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//showSplashScreen();
		setContentView(R.layout.main);
		
		mAdView = (AdView)this.findViewById(R.id.ad);
	    AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	    		.build();
		mAdView.loadAd(adRequest);

	    AdRequest adRequestInt = new AdRequest.Builder()
    	.build();

	    // Create the interstitial.
	    interstitial = new InterstitialAd(this);
	    interstitial.setAdUnitId(getResources().getString(R.string.admob_int_id));
	    interstitial.loadAd(adRequestInt);

		interstitial.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				requestNewInterstitial();
			}
		});

		requestNewInterstitial();

		CureMeDataSource dataSrc = new CureMeDataSource();

		groups = dataSrc.getGroups();

		listView = (ListView) findViewById(R.id.listView);

		@SuppressWarnings("unchecked")
		List<CureMeCommon> listOfGroups = (List<CureMeCommon>) groups;
		CureMeAdapter adapter = new CureMeAdapter(this, R.layout.main,
				listOfGroups);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
		/* New Handler to start the Menu-Activity 
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                displayInterstitial();
            }
        }, INTERSTITIAL_DISPLAY_LENGHT );
	}


	private void requestNewInterstitial() {
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.build();

		interstitial.loadAd(adRequest);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		/*
		 * if (regStatus.getStatus() == AsyncTask.Status.RUNNING){
		 * Toast.makeText(getApplicationContext(),
		 * "Application performing work in background !!",
		 * Toast.LENGTH_SHORT).show(); return ; }
		 */
		TextView tv = (TextView) view.findViewById(R.id.title);
		String listName = tv.getText().toString();

		/*
		 * if (listName.equalsIgnoreCase(CureMeConstants.NEAREST_HELP)){ Intent
		 * intent = new Intent(GroupItemActivity.this,
		 * NearestHelpActivity.class); startActivity(intent); } else
		 */
		// {
		Intent intent = new Intent(GroupItemActivity.this,
				GroupDetailActivity.class);
		intent.putExtra(CureMeConstants.CLICKED_GROUP, listName);

		displayInterstitial();
		startActivity(intent);
		// }
	}

	List<? extends CureMeCommon> groups;

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_contact_us:
			AlertDialogManager contactUsDlg = new AlertDialogManager();
			contactUsDlg.showContactUsDialog(GroupItemActivity.this);
			return true;
		case R.id.action_refer_friend:
			Intent email = EMailUtils.sendEmail();
			startActivity(Intent.createChooser(email, "Choose an Email client :"));
			return true ;
		default:
			return super.onOptionsItemSelected(item);
		}
		// return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
	    long currentTime = System.currentTimeMillis();
	    if(currentTime - lastPress > 3000){
	        Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
	        lastPress = currentTime;
	    }else{
	        super.onBackPressed();
	    }
	}
	
    @Override
    protected void onResume() {
        super.onResume();
		if (mAdView != null) {
			mAdView.resume();
		}

		CureMeApplication.getInstance().trackScreenView(CureMeConstants.ID_HOME);

        // Check device for Play Services APK.
        CureMeGcmRegister reg = new CureMeGcmRegister(getApplicationContext(),this);
        reg.register();
    }

	/** Called before the activity is destroyed */
	@Override
	public void onDestroy() {
		if (mAdView != null) {
			mAdView.destroy();
		}
		super.onDestroy();
	}

	/** Called when leaving the activity */
	@Override
	public void onPause() {
		if (mAdView != null) {
			mAdView.pause();
		}
		super.onPause();
	}
}
