package com.cureme.activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cureme.R;
import com.cureme.data.CureMeDataSource;
import com.cureme.data.CureMeGroup;
import com.cureme.data.CureMeItem;
import com.cureme.utils.AlertDialogManager;
import com.cureme.utils.EMailUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class CureMeItemActivity extends FragmentActivity implements TabListener {

	private ViewPager mPager;

	private ActionBar mActionBar;

	private CureMePagerAdapter mPagerAdapter;

	List<CureMeItemFragment> fragmentList = new ArrayList<CureMeItemFragment>();

	String title;

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
		setContentView(R.layout.activity_cure_me_item);
		// Show the Up button in the action bar.
		setupActionBar();

		AdView adView = (AdView)this.findViewById(R.id.ad);
	    AdRequest adRequest = new AdRequest.Builder()
	    	.build();
	    adView.loadAd(adRequest);
		
		mPager = (ViewPager) super.findViewById(R.id.pager);
		try {
			Intent intent = getIntent();
			title = intent.getStringExtra(CureMeConstants.TITLE);
			int startPosition = intent.getIntExtra(
					CureMeConstants.START_POSITION, 0);

			setTitle(title);

			mActionBar = getActionBar();

			// Specify that tabs should be displayed in the action bar.
			mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			CureMeGroup group = CureMeDataSource.GetGroup(title);
			List<CureMeItem> items = group.getItems();

			if (mPager != null) {

				// However, if we're being restored from a previous state,
				// then we don't need to do anything and should return or else
				// we could end up with overlapping fragments.
				/*if (savedInstanceState != null) {
					return;
				}*/

				for (CureMeItem item : items) {
					// Create an instance of ExampleFragment
					CureMeItemFragment itemFrag = new CureMeItemFragment();

					// Context context = getApplicationContext();
					Bundle args = new Bundle();
					args.putString(CureMeConstants.DESCRIPTION,
							item.getContent());
					args.putString(CureMeConstants.IMAGEPATH,
							item.getImagePath());
					args.putString(CureMeConstants.TITLE, item.get_title());
					itemFrag.setArguments(args);
					fragmentList.add(itemFrag);

					mActionBar.addTab(mActionBar.newTab()
							.setText(item.get_title()).setTabListener(this));

				}

				mPagerAdapter = new CureMePagerAdapter(
						getSupportFragmentManager(), fragmentList);
				mPager.setAdapter(mPagerAdapter);
				mPager.setCurrentItem(startPosition);
				mPager.setOnPageChangeListener(new OnPageChangeListener() {

					@Override
					public void onPageScrollStateChanged(int arg0) {

					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {

					}

					@Override
					public void onPageSelected(int position) {
						mActionBar.setSelectedNavigationItem(position);

					}
				});
				mActionBar.setSelectedNavigationItem(startPosition);
			}
		} catch (Exception e) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Exception : " + e.getStackTrace(), Toast.LENGTH_LONG);
			toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL, 0, 0);
			toast.show();
		}
	}

	/**
	 * Set up the {@link ActionBar}, if the API is available.
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
			upIntent.putExtra(CureMeConstants.CLICKED_GROUP, title);
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
			contactUsDlg.showContactUsDialog(CureMeItemActivity.this);
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
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {

		mPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

}
