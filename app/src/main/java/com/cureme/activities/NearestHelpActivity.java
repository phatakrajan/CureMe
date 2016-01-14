package com.cureme.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cureme.CureMeApplication;
import com.cureme.R;
import com.cureme.data.CureMeGroup;
import com.cureme.maps.ConnectionDetector;
import com.cureme.maps.GPSTracker;
import com.cureme.maps.GooglePlaces;
import com.cureme.maps.Place;
import com.cureme.maps.PlaceDetails;
import com.cureme.maps.PlacesList;
import com.cureme.utils.AlertDialogManager;
import com.cureme.utils.EMailUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NearestHelpActivity extends FragmentActivity implements OnItemClickListener {

	// Places Listview
	ListView lv;

	// ListItems data
	ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String,String>>();

	// GPS Location
	GPSTracker gps;

	// Progress dialog
	ProgressDialog pDialog;

	// Connection detector class
	ConnectionDetector cd;
	boolean isInternetPresent;
	
	boolean isNearestHelp ;

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	public GooglePlaces googlePlaces;

	// Places List
	PlacesList nearPlaces;

	Button btnShowOnMap;
	
	double latitude ;
	double longitude ;
	
	String title ;

	private String entityType;
	
	int startPosition ;
	
	// KEY Strings
	public static String KEY_REFERENCE = "reference"; // id of the place
	public static String KEY_NAME = "name"; // name of the place
	public static String KEY_VICINITY = "vicinity"; // Place area name

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
        setContentView(R.layout.activity_nearest_help);
		
		AdView adView = (AdView)this.findViewById(R.id.ad);
	    AdRequest adRequest = new AdRequest.Builder()
	    	.build();
	    adView.loadAd(adRequest);

		CureMeApplication.getInstance().trackScreenView(CureMeConstants.ID_NEARESTHELP);

		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		isInternetPresent = cd.isConnectingToInternet();
		
		// Set to true when the location based on current location is found
		isNearestHelp = true ;
		
		if (!isInternetPresent) {
			// Internet Connection is not present
			alert.showAlertDialog(NearestHelpActivity.this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		// creating GPS Class object
		gps = new GPSTracker(this);

		// check if GPS location can get
		if (gps.canGetLocation()) {
			Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
		} else {
			// Can't get user's current location
			alert.showAlertDialog(NearestHelpActivity.this, "GPS Status",
					"Couldn't get location information. Please enable GPS",
					false);
			// stop executing code by return
			return;
		}
		try{
		ImageView icon = (ImageView) findViewById(R.id.icon);
		icon.setImageDrawable(CureMeGroup.getImageDrawable(this,
				CureMeConstants.PIC_NEARESTHELP));
		} catch (Exception e){
			
		}
		// Getting listview
		lv = (ListView) findViewById(R.id.list);
		lv.setOnItemClickListener(this);
		
		// button show on map
		btnShowOnMap = (Button) findViewById(R.id.btn_show_map);

		startPosition = getIntent().getIntExtra(
				CureMeConstants.START_POSITION, 0);

		if (startPosition == CureMeConstants.PHARMACY ){
			title = "Pharmacy" ;
			entityType = "pharmacy";
		} else if (startPosition == CureMeConstants.DOCTORS){
			title = "Doctors";
			entityType = "doctor";
		} else if (startPosition == CureMeConstants.HOSPITALS){
			title = "Hospitals";
			entityType = "hospital";
		}
		
		setTitle(title);
		// calling background Async task to load Google Places
		// After getting places from Google all the data is shown in listview
		if (getIntent().getExtras().size() > 1 || savedInstanceState != null)
		{
			if (getIntent().getExtras().size() > 1) {
				Intent i = getIntent();
				nearPlaces = (PlacesList) i.getSerializableExtra("near_places");
				isNearestHelp = i.getBooleanExtra("NearestHelp", true);

				// Users current geo location
				String user_latitude = i.getStringExtra("user_latitude");
				String user_longitude = i.getStringExtra("user_longitude");

				latitude = Double.parseDouble(user_latitude);
				longitude = Double.parseDouble(user_longitude);

				title = i.getStringExtra("title");
			} else {
				nearPlaces = (PlacesList) savedInstanceState
						.getSerializable("near_places");
				latitude = savedInstanceState.getDouble("user_latitude");
				longitude = savedInstanceState.getDouble("user_longitude");
				title = savedInstanceState.getString("title");
				isNearestHelp = savedInstanceState.getBoolean("NearestHelp");
			}
			setTitle(title);
			
			if (nearPlaces != null) {
				if (nearPlaces.results != null) {
					// loop through each place
					for (Place p : nearPlaces.results) {
						HashMap<String, String> map = new HashMap<String, String>();

						// Place reference won't display in listview - it will
						// be hidden
						// Place reference is used to get "place full details"
						map.put(KEY_REFERENCE, p.reference);

						// Place name
						map.put(KEY_NAME, p.name);

						// adding HashMap to ArrayList
						placesListItems.add(map);
					}
					// list adapter
					ListAdapter adapter = new SimpleAdapter(
							NearestHelpActivity.this, placesListItems,
							R.layout.list_item, new String[] { KEY_REFERENCE,
									KEY_NAME }, new int[] { R.id.reference,
									R.id.name });

					// Adding data into listview
					lv.setAdapter(adapter);
				}
			} else {
				new LoadPlaces().execute();
			}

		} else {
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();
	        //title = getResources().getString(R.string.title_activity_nearest_help);
			setTitle(title);
			new LoadPlaces().execute();
		}
		/** Button click event for shown on map */
		btnShowOnMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (placesListItems.isEmpty() == true){
					Toast.makeText(getApplicationContext(), "No Items to display", Toast.LENGTH_LONG).show();
					return ;
				}
				Intent i = new Intent(getApplicationContext(),
						NearestHelpItemMap.class);
				// Sending user current geo location
				i.putExtra("user_latitude", Double.toString(latitude));
				i.putExtra("user_longitude", Double.toString(longitude));
				i.putExtra("NearestHelp", isNearestHelp);
				i.putExtra("title", getTitle());
				// passing near places to map activity
				i.putExtra("near_places", nearPlaces);
				// staring activity
				startActivity(i);
			}
		});
		
		
    }
	
	@Override
	public void onPause() {
	    super.onPause();

	    if(pDialog != null)
	    	pDialog.dismiss();
	    pDialog = null;
	}

	/**
	 * Background Async Task to Load Google places
	 * */
	class LoadPlaces extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(NearestHelpActivity.this);
			pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading "+title+"..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... args) {
			// creating Places class object
			googlePlaces = new GooglePlaces();

			try {
				// Separeate your place types by PIPE symbol "|"
				// If you want all types places make it as null
				// Check list of types supported by google
				//
				//String types = "hospital"; // Listing places only cafes,
											// restaurants

				// Radius in meters - increase this value if you don't find any
				// places
				double radius = 5000; // 5000 meters

				// get nearest places
				if (args.length == 0){
				nearPlaces = googlePlaces.search(gps.getLatitude(),
						gps.getLongitude(), radius, entityType);
				} else 
				{
					nearPlaces = googlePlaces.search(Double.valueOf(args[0]),
							Double.valueOf(args[1]), radius, entityType);
				}
				
				for (Place p : nearPlaces.results) {

					try {
						PlaceDetails details = googlePlaces
								.getPlaceDetails(p.reference);
						if (details != null) {
							String statusDetail = details.status;
							if (statusDetail.equals("OK")) {
								if (details.result != null) {
									p.name = details.result.name;
									p.formatted_address = details.result.formatted_address;
									p.formatted_phone_number = details.result.formatted_phone_number;
								}

							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		
		/**
		 * After completing background task Dismiss the progress dialog
		 * and show the data in UI
		 * Always use runOnUiThread(new Runnable()) to update UI from background
		 * thread, otherwise you will get error
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			if (pDialog != null) {
				pDialog.dismiss();
			}
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					// Clear the list before performing any operations
					placesListItems.clear();

					/**
					 * Updating parsed Places into LISTVIEW
					 * */
					// Get json response status
					String status = "";
					if (nearPlaces != null) {
						status = nearPlaces.status;
					} else {
						alert.showAlertDialog(NearestHelpActivity.this, "Error",
								"Sorry unknown error occured. Check internet connectivity",
								false);
						return ;
					}
					
					// Check for all possible status
					if(status.equals("OK")){
						// Successfully got places details
						if (nearPlaces.results != null) {
							// loop through each place
							for (Place p : nearPlaces.results) {
								HashMap<String, String> map = new HashMap<String, String>();
								
								// Place reference won't display in listview - it will be hidden
								// Place reference is used to get "place full details"
								map.put(KEY_REFERENCE, p.reference);
								
								// Place name
								map.put(KEY_NAME, p.name);
								
								// adding HashMap to ArrayList
								placesListItems.add(map);
								
							}
							// list adapter
							ListAdapter adapter = new SimpleAdapter(NearestHelpActivity.this, placesListItems,
					                R.layout.list_item,
					                new String[] { KEY_REFERENCE, KEY_NAME}, new int[] {
					                        R.id.reference, R.id.name });
							
							// Adding data into listview
							lv.setAdapter(adapter);
						}
					}
					else if(status.equals("ZERO_RESULTS")){
						// Zero results found
						alert.showAlertDialog(NearestHelpActivity.this, title,
								"Sorry no Items found.",
								false);
					}
					else if(status.equals("UNKNOWN_ERROR"))
					{
						alert.showAlertDialog(NearestHelpActivity.this, "Error",
								"Sorry unknown error occured.",
								false);
					}
					else if(status.equals("OVER_QUERY_LIMIT"))
					{
						alert.showAlertDialog(NearestHelpActivity.this, "Error",
								"Sorry query limit to google places is reached",
								false);
					}
					else if(status.equals("REQUEST_DENIED"))
					{
						alert.showAlertDialog(NearestHelpActivity.this, "Error",
								"Sorry error occured. Request is denied",
								false);
					}
					else if(status.equals("INVALID_REQUEST"))
					{
						alert.showAlertDialog(NearestHelpActivity.this, "Error",
								"Sorry error occured. Invalid Request",
								false);
					}
					else
					{
						alert.showAlertDialog(NearestHelpActivity.this, "Error",
								"Sorry error occured.",
								false);
					}
				}
			});

		}

	}
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nearest_help, menu);
        return true;
    }


	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(NearestHelpActivity.this);
		
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.popup, null);
	    alertDialog.setView(dialogView);
		
		Place place = nearPlaces.results.get(position);
		
		alertDialog.setIcon(R.drawable.store_logo);
		alertDialog.setTitle(getTitle());
		
		
		// Setting OK Button
        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                }
        });
 
 
        //AlertDialog dialog = alertDialog.create();
        //dialog.setContentView(R.layout.popup);
        
        TextView title = (TextView)dialogView.findViewById(R.id.title);
        title.setText(place.name);
        
        TextView address = (TextView)dialogView.findViewById(R.id.snippet);
        address.setText(place.formatted_address);
        
        TextView phone = (TextView)dialogView.findViewById(R.id.phone);
        phone.setText(place.formatted_phone_number);

        // Showing Alert Messaget
        alertDialog.show();
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putSerializable("near_places", nearPlaces);
		// Users current geo location
		bundle.putDouble("user_latitude",latitude);
		bundle.putDouble("user_longitude",longitude);

		bundle.putString("title", title);
		bundle.putBoolean("NearestHelp", isNearestHelp);
		bundle.putInt(CureMeConstants.START_POSITION, startPosition);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		nearPlaces = (PlacesList) savedInstanceState
				.getSerializable("near_places");
		latitude = savedInstanceState.getDouble("user_latitude");
		longitude = savedInstanceState.getDouble("user_longitude");
		title = savedInstanceState.getString("title");
		isNearestHelp = savedInstanceState.getBoolean("NearestHelp");
		
		startPosition = savedInstanceState.getInt(CureMeConstants.START_POSITION);

	}
	/**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()) {
		
		case android.R.id.home:
		case R.id.action_back:
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			upIntent.putExtra(CureMeConstants.CLICKED_GROUP, CureMeConstants.NEAREST_HELP);
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
    	case R.id.action_search_by_city:
			SearchHospitalsByCity();
			return true;
		case R.id.action_contact_us:
			AlertDialogManager contactUsDlg = new AlertDialogManager();
			contactUsDlg.showContactUsDialog(NearestHelpActivity.this);
			return true;
		case R.id.action_refer_friend:
			Intent email = EMailUtils.sendEmail();
			startActivity(Intent.createChooser(email, "Choose an Email client :"));
			return true ;

		default:
			return super.onOptionsItemSelected(item);
		}
    }


	private void SearchHospitalsByCity() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Select City");

		final Spinner input = new Spinner(this);
		String[] cityArray = getResources().getStringArray(R.array.city_array);
		ArrayAdapter<String> cities = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, cityArray);
		input.setAdapter(cities);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String city = input.getSelectedItem().toString();
				String title = (String) getTitle();
				Address address = getLatitudeAndLongitudeFromGoogleMapForAddress(city);
				if (address != null){
					isNearestHelp = false ;
					title = title + " - " + city ;
					setTitle(title);
					new LoadPlaces().execute(Double.toString(address.getLatitude()),Double.toString(address.getLongitude()));
				} else {
					Toast.makeText(getApplicationContext(), "Place couldn't be found at this momemt. Check internet connectivity or retry after some time !!", Toast.LENGTH_LONG).show();
				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// canceled
					}
				});
		alert.show();
	}
	
	public Address getLatitudeAndLongitudeFromGoogleMapForAddress(String searchedAddress){

	    Geocoder coder = new Geocoder(this);
	    List<Address> address;
	    try 
	    {
	        address = coder.getFromLocationName(searchedAddress,5);
	        if (address == null) {
	            return null ;
	        }
	        Address location = address.get(0);

	        return location;

	    }
	    catch(Exception e)
	    {
	        Log.d("Address Error", "Address Not Found");
	        return null;
	    }
	}
    
}
