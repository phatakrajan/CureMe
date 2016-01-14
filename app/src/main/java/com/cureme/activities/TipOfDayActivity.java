package com.cureme.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cureme.CureMeApplication;
import com.cureme.R;
import com.cureme.gcm.CureMeGcmRegister;
import com.cureme.utils.ListTagHandler;
import com.cureme.utils.URLImageParser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class TipOfDayActivity extends Activity {

	String message = null ;
	TextView tip_message ;
	
    @Override
    public void onStart() {
      super.onStart();
    }
    @Override
    public void onStop() {
      super.onStop();
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tip_of_day);
		
		AdView adView = (AdView)this.findViewById(R.id.ad);
	    AdRequest adRequest = new AdRequest.Builder()
	    	.build();
	    adView.loadAd(adRequest);


		CureMeApplication.getInstance().trackScreenView(CureMeConstants.ID_TIPOFDAY);
	    
		Intent intent = getIntent();
		if (savedInstanceState != null) {
			message = savedInstanceState.getString(CureMeConstants.MESSAGE);
		}
		else {
			message = intent.getStringExtra(CureMeConstants.MESSAGE);
		}

		tip_message = (TextView) findViewById(R.id.message);
		URLImageParser imageParser = new URLImageParser(tip_message, this);
		tip_message.setText(Html.fromHtml(message , imageParser, new ListTagHandler()));
		tip_message.setMovementMethod(LinkMovementMethod.getInstance());

	}
	
	public void goBackHome(View view){
		Intent intent = new Intent(TipOfDayActivity.this,
				GroupItemActivity.class);
		finish();
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tip_of_day, menu);
		return true;
	}
	

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
		case R.id.action_home:
			Intent intent = new Intent(TipOfDayActivity.this,
					GroupItemActivity.class);
			finish();
			startActivity(intent);
			return true;
		default:
			return true;
		}
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(TipOfDayActivity.this,
				GroupItemActivity.class);
		finish();
		startActivity(intent);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putString(CureMeConstants.MESSAGE, message);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		message = savedInstanceState.getString(CureMeConstants.MESSAGE);
	}

    @Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        CureMeGcmRegister reg = new CureMeGcmRegister(getApplicationContext(),this);
        reg.checkPlayServices();
    }

}
