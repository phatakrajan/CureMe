package com.cureme.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cureme.CureMeApplication;
import com.cureme.R;
import com.cureme.data.CureMeCommon;
import com.cureme.data.CureMeDataSource;
import com.cureme.data.CureMeGroup;
import com.cureme.data.CureMeItem;
import com.cureme.utils.AlertDialogManager;
import com.cureme.utils.EMailUtils;
import com.cureme.xml.XMLParser;
import com.cureme.xml.XmlModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class GroupDetailActivity extends Activity implements OnItemClickListener {

	ListView listView;
	ImageView image;
	TextView desc;
	List<XmlModel> myData = null;
	CureMeGroup group ;
	AlertDialog pDialog ;
	
	Dialog emailDialog ;

	private AdView mAdView;

	private InterstitialAd interstitial;

	private long INTERSTITIAL_DISPLAY_LENGHT = 2000;

    @Override
    public void onStart() {
      super.onStart();
    }

	/** Called before the activity is destroyed */
	@Override
	public void onDestroy() {
		if (mAdView != null) {
			mAdView.destroy();
		}
		super.onDestroy();
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
		setContentView(R.layout.activity_group_detail);
		
		// Show the Up button in the action bar.
		setupActionBar();

		mAdView = (AdView)this.findViewById(R.id.ad);
	    AdRequest adRequest = new AdRequest.Builder()
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

		Intent intent = getIntent();
		String clickedGroup = intent
				.getStringExtra(CureMeConstants.CLICKED_GROUP);
		
		setTitle(clickedGroup);

		CureMeApplication.getInstance().trackScreenView(clickedGroup);

		String fileName = "";
		if (clickedGroup.equalsIgnoreCase(CureMeConstants.FIRST_AID)) {
			fileName = "xml/FirstAid.xml";
		} else if (clickedGroup.equalsIgnoreCase(CureMeConstants.SELF_CURE)) {
			fileName = "xml/SelfCure.xml";
		} else if (clickedGroup
				.equalsIgnoreCase(CureMeConstants.SEASONAL_PROBLEM)) {
			fileName = "xml/SeasonalProblems.xml";
		} else if (clickedGroup.equalsIgnoreCase(CureMeConstants.GRANNY_TIPS)) {
			fileName = "xml/GrannyTips.xml";
		} else if (clickedGroup
				.equalsIgnoreCase(CureMeConstants.CONTACT_DOCTOR)) {
			fileName = "xml/ContactDoctor.xml";
		} else if (clickedGroup.equalsIgnoreCase(CureMeConstants.NEAREST_HELP)) {
			fileName = "xml/NearestHelp.xml";
		}

		group = CureMeDataSource.GetGroup(clickedGroup);
		try {
			if (group.getItems().size() == 0) {
				InputStream is = getResources().getAssets().open(fileName);
				XMLParser parser = new XMLParser();
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser sp = factory.newSAXParser();
				XMLReader reader = sp.getXMLReader();
				reader.setContentHandler(parser);
				reader.parse(new InputSource(is));

				myData = parser.list;
				if (myData != null) {
					for (XmlModel xmlRowData : myData) {
						if (xmlRowData != null) {
							String title = xmlRowData.getTitle();
							String element = xmlRowData.getElement();
							String imagePath = xmlRowData.getImagePath();
							String description = xmlRowData.getDescription();
							String content = xmlRowData.getContent();

							group.getItems().add(
									new CureMeItem(element, title, description,
											imagePath, content, group));
						}
					}
				}

			}

			image = (ImageView) findViewById(R.id.icon);
			image.setImageDrawable(CureMeGroup.getImageDrawable(this,
					group.getImagePath()));
			
			desc = (TextView) findViewById(R.id.title);
			desc.setText(group.get_description());
			
			List<? extends CureMeCommon> items = group.getItems();
			listView = (ListView) findViewById(R.id.listView);
			
			@SuppressWarnings("unchecked")
			List<CureMeCommon> listOfItems = (List<CureMeCommon>) items;
			CureMeAdapter adapter = new CureMeAdapter(this, R.layout.main, listOfItems);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);

					/* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
                /* Create an Intent that will start the Menu-Activity. */
					displayInterstitial();
				}
			}, INTERSTITIAL_DISPLAY_LENGHT);
			
		} catch (Exception e) {
			Toast toast = Toast.makeText(
					getApplicationContext(),
					"Clicked Item :- " + clickedGroup + "Exception : "
							+ e.getMessage(), Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL, 0, 0);
			toast.show();
		}

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
		displayInterstitial();
		final Context context = view.getContext();
        Intent intent ;
        if (group.get_title().equalsIgnoreCase(CureMeConstants.CONTACT_DOCTOR)){
        	//intent= new Intent (GroupDetailActivity.this,NearestHelpActivity.class);
        	if (position == CureMeConstants.EMAIL){
            	emailDialog = new Dialog(context);
        		emailDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
        		emailDialog.setContentView(R.layout.activity_contact_doctor);
        		emailDialog.setTitle("Contact a Doctor");
        		emailDialog.setCanceledOnTouchOutside(false);
        		
        		// Add action buttons
        		
        		Button btnEmail = (Button)emailDialog.findViewById(R.id.email);
                
        		btnEmail.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {


                		EditText nameField = (EditText) emailDialog.findViewById(R.id.EditTextName);
                		String name = nameField.getText().toString();
                		if (name.length() == 0){
                			nameField.setError("Name is mandatory !!");
                			return ;
                		}
                		
                		EditText ageField = (EditText) emailDialog.findViewById(R.id.age);
                		String age = ageField.getText().toString();
                		if (age.length()==0){
                			ageField.setError("Enter Age details");
                			return ;
                		}
                		
                		RadioGroup radioSexGroup = (RadioGroup) emailDialog.findViewById(R.id.radioSex);
                		// get selected radio button from radioGroup
                		int selectedId = radioSexGroup.getCheckedRadioButtonId();

                		// find the radiobutton by returned id
                		RadioButton radioSexButton = (RadioButton) emailDialog.findViewById(selectedId);
                		
                		EditText durationField = (EditText) emailDialog.findViewById(R.id.duration);
                		String duration = durationField.getText().toString();
                		if (duration.length()==0){
                			durationField.setError("Enter duration of complain");
                			return ;
                		}

                		EditText complainField = (EditText) emailDialog.findViewById(R.id.complain);
                		String complain = complainField.getText().toString();
                		if (complain.length()==0){
                			complainField.setError("Enter complain details");
                			return ;
                		}

                		EditText medicationField = (EditText) emailDialog.findViewById(R.id.medication);
                		String medication = medicationField.getText().toString();

                		StringBuilder builder = new StringBuilder();
                		builder.append(CureMeConstants.HEADER_START+ name +CureMeConstants.HEADER_END + CureMeConstants.BREAK);
                		builder.append(CureMeConstants.BOLD_START + "Age - "+ CureMeConstants.BOLD_END + age + CureMeConstants.BREAK);
                		builder.append(CureMeConstants.BOLD_START + "Sex - "+ CureMeConstants.BOLD_END + radioSexButton.getText() + CureMeConstants.BREAK);
                		builder.append(CureMeConstants.BOLD_START + "Duration of complain - "+ CureMeConstants.BOLD_END + duration + CureMeConstants.BREAK);
                		builder.append(CureMeConstants.BOLD_START + "Complain Details - "+ CureMeConstants.BOLD_END + complain + CureMeConstants.BREAK);

                		String to = "Cure.Me@live.com";
                		String subject = "Contact a doctor";
                		String message = builder.toString();

                		Intent email = new Intent(Intent.ACTION_SEND);
                		email.setType("text/html");
                		email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
                		email.putExtra(Intent.EXTRA_SUBJECT, subject);
                		email.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(message));

                		//need this to prompts email client only
                		email.setType("message/rfc822");

                		
                		startActivity(Intent.createChooser(email, "Choose an Email client"));
                    
                		emailDialog.dismiss();					
					}
				});        	
        		
        		Button btnCancel = (Button)emailDialog.findViewById(R.id.cancel);
        		
        		btnCancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						emailDialog.dismiss();
						
					}
				});
        		
        		emailDialog.show();
        		emailDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.store_logo);
			} else if (position == CureMeConstants.SKYPE) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						GroupDetailActivity.this);
				builder.setTitle("Call a doctor");
				builder.setIcon(R.drawable.store_logo);
				builder.setMessage("Have you already booked an appointment? If yes, click Proceed");
				builder.setPositiveButton("Proceed",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								initiateSkypeUri(context,
										"skype:live:Cure.Me?call&video=true");

							}

							/**
							 * Initiate the actions encoded in the specified
							 * URI.
							 */
							public void initiateSkypeUri(Context myContext,
									String mySkypeUri) {

								// Make sure the Skype for Android client is
								// installed
								if (!isSkypeClientInstalled(myContext)) {
									goToMarket(myContext);
									return;
								}

								// Create the Intent from our Skype URI
								Uri skypeUri = Uri.parse(mySkypeUri);
								Intent myIntent = new Intent(
										Intent.ACTION_VIEW, skypeUri);

								// Restrict the Intent to being handled by the
								// Skype for Android client only
								myIntent.setComponent(new ComponentName(
										"com.skype.raider",
										"com.skype.raider.Main"));
								myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

								// Initiate the Intent. It should never fail
								// since we've already established the
								// presence of its handler (although there is an
								// extremely minute window where that
								// handler can go away...)
								myContext.startActivity(myIntent);

								return;
							}

							/**
							 * Determine whether the Skype for Android client is
							 * installed on this device.
							 */
							public boolean isSkypeClientInstalled(
									Context myContext) {
								PackageManager myPackageMgr = myContext
										.getPackageManager();
								try {
									myPackageMgr.getPackageInfo(
											"com.skype.raider",
											PackageManager.GET_ACTIVITIES);
								} catch (PackageManager.NameNotFoundException e) {
									return (false);
								}
								return (true);
							}

							/**
							 * Install the Skype client through the market: URI
							 * scheme.
							 */
							public void goToMarket(Context myContext) {
								Uri marketUri = Uri
										.parse("market://details?id=com.skype.raider");
								Intent myIntent = new Intent(
										Intent.ACTION_VIEW, marketUri);
								myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								myContext.startActivity(myIntent);

								return;
							}
						});
				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}

						});
				pDialog = builder.create();
				builder.show();
			}
        	
        } 
        else if (group.get_title().equalsIgnoreCase(
				CureMeConstants.NEAREST_HELP)) {
			intent = new Intent(GroupDetailActivity.this,
					NearestHelpActivity.class);
    		intent.putExtra(CureMeConstants.START_POSITION, position);
			startActivity(intent);
		} 
        else {
        	intent= new Intent (GroupDetailActivity.this,CureMeItemActivity.class);
        	intent.putExtra(CureMeConstants.TITLE, group.get_title());
    		intent.putExtra(CureMeConstants.START_POSITION, position);
    		startActivity(intent);
        }
		
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
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
	public void onPause() {
	    super.onPause();

	    if(pDialog != null)
	    	pDialog.dismiss();
	    pDialog = null;

		if (mAdView != null) {
			mAdView.pause();
		}

	    if(emailDialog != null)
	    	emailDialog.dismiss();
	    emailDialog = null;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
		case R.id.action_back:
			Intent upIntent = NavUtils.getParentActivityIntent(this);
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
			return true;
		case R.id.action_contact_us:
			AlertDialogManager contactUsDlg = new AlertDialogManager();
			contactUsDlg.showContactUsDialog(GroupDetailActivity.this);
			return true;
		case R.id.action_refer_friend:
			Intent email = EMailUtils.sendEmail();
			startActivity(Intent.createChooser(email, "Choose an Email client :"));
			return true ;

		default:
			return super.onOptionsItemSelected(item);			
		}
	}

}
