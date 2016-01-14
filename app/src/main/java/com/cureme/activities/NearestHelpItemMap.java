package com.cureme.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cureme.CureMeApplication;
import com.cureme.R;
import com.cureme.maps.Place;
import com.cureme.maps.PlacesList;
import com.cureme.maps.PopupAdapter;
import com.cureme.utils.AlertDialogManager;
import com.cureme.utils.EMailUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearestHelpItemMap extends FragmentActivity implements OnInfoWindowClickListener {

	PlacesList nearPlaces;
	
	double latitude;
	double longitude;

	String title = "";
	
	private LatLng geoPoint;

	boolean isNearestHelp ;

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
		setContentView(R.layout.activity_nearest_help_item_map);
		// Show the Up button in the action bar.
		setupActionBar();
		
		AdView adView = (AdView)this.findViewById(R.id.ad);
	    AdRequest adRequest = new AdRequest.Builder()
	    	.build();
	    adView.loadAd(adRequest);

		CureMeApplication.getInstance().trackScreenView(CureMeConstants.ID_NEARESTHELPMAP);

		GoogleMap map = ((SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map))
	               .getMap();
		if (map == null){
			Toast.makeText(getApplicationContext(), "Google Maps are not installed/supported on this device", Toast.LENGTH_LONG).show();
			return ;
		}
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		//map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
		//map.setMyLocationEnabled(true);
		
		// Getting intent data
		Intent i = getIntent();
		
		// Users current geo location
		String user_latitude = i.getStringExtra("user_latitude");
		String user_longitude = i.getStringExtra("user_longitude");
		isNearestHelp = i.getBooleanExtra("NearestHelp", true);
		title = i.getStringExtra("title");
		
		setTitle(title);
		// Nearplaces list
		nearPlaces = (PlacesList) i.getSerializableExtra("near_places");

		LatLng latLang = new LatLng(Double.parseDouble(user_latitude) , 
				Double.parseDouble(user_longitude));
		
		map.addMarker(new MarkerOptions()
					.position(latLang)
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_red))
					.title("You")
					.snippet("This is You !!! | "));
		
		// These values are used to get map boundary area
		// The area where you can see all the markers on screen
		int minLat = Integer.MAX_VALUE;
		int minLong = Integer.MAX_VALUE;
		int maxLat = Integer.MIN_VALUE;
		int maxLong = Integer.MIN_VALUE;
		
		// check for null in case it is null
		if (nearPlaces.results != null) {
			// loop through all the places
			for (Place place : nearPlaces.results) {
				latitude = place.geometry.location.lat; // latitude
				longitude = place.geometry.location.lng; // longitude

				geoPoint = new LatLng((latitude),
						(longitude));

				map.addMarker(new MarkerOptions()
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_blue))
							.position(geoPoint)
							.title(place.name)
							.snippet(place.toString()));
				
				// calculating map boundary area
				minLat  = (int) Math.min( geoPoint.latitude, minLat );
			    minLong = (int) Math.min( geoPoint.longitude, minLong);
			    maxLat  = (int) Math.max( geoPoint.latitude, maxLat );
			    maxLong = (int) Math.max( geoPoint.longitude, maxLong );
			}
			
		}

		map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
		map.setOnInfoWindowClickListener(this);
		if (isNearestHelp == true){
			map.moveCamera(CameraUpdateFactory.newLatLng(latLang));
		} else 
		{
			map.moveCamera(CameraUpdateFactory.newLatLng(geoPoint));
		}
		// Adjusting the zoom level so that you can see all the markers on map
		map.animateCamera(CameraUpdateFactory.zoomTo(15));


	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.grid_item, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
		case R.id.action_back:
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			upIntent.putExtra("near_places", nearPlaces);
			upIntent.putExtra("NearestHelp", isNearestHelp);
			upIntent.putExtra("user_latitude", Double.toString(geoPoint.latitude));
			upIntent.putExtra("user_longitude", Double.toString(geoPoint.longitude));
			upIntent.putExtra("title", title);

			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				// This activity is NOT part of this app's task, so create a new
				// task
				// when navigating up, with a synthesized back stack.
				TaskStackBuilder.create(this)
				// Add all of this activity's parents to the back stack
						.addNextIntentWithParentStack(upIntent)
						// Navigate up to the closest parent
						.startActivities();
			} else {
				// This activity is part of this app's task, so simply
				// navigate up to the logical parent activity.
				NavUtils.navigateUpTo(this, upIntent);
			}
			return true ;
		case R.id.action_contact_us:
			AlertDialogManager contactUsDlg = new AlertDialogManager();
			contactUsDlg.showContactUsDialog(NearestHelpItemMap.this);
			return true;
		case R.id.action_refer_friend:
			Intent email = EMailUtils.sendEmail();
			startActivity(Intent.createChooser(email, "Choose an Email client :"));
			return true ;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Intent intent = null;
		String snippet = marker.getSnippet();
		String[] placeDetails = snippet.split("\\|");
		if (placeDetails[1].length() > 0){
			intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+placeDetails[1]));
			startActivity(intent);
		}
	}

}
