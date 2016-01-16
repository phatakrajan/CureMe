package com.cureme.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

import com.cureme.R;

public class MainActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGHT = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        String message = "";
        final Intent mainIntent ;
		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().size() > 0) {
				message = getIntent().getStringExtra(CureMeConstants.MESSAGE);
                if (message != null) {
                    mainIntent = new Intent(MainActivity.this,
                            TipOfDayActivity.class);
                    mainIntent.putExtra(CureMeConstants.MESSAGE, message);
                }else{
                    mainIntent = new Intent(MainActivity.this,
                            GroupItemActivity.class);
                }

				/*try {
					ShortcutBadger.setBadge(getApplicationContext(), 0);
				} catch (ShortcutBadgeException e) {
					// TODO Auto-generated catch block
				}*/
			} else {
				mainIntent = new Intent(MainActivity.this,
						GroupItemActivity.class);
			}
		}else {
			mainIntent = new Intent(MainActivity.this,
                    GroupItemActivity.class);
		}

        /* New Handler to start the Menu-Activity 
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                
                MainActivity.this.startActivity(mainIntent);
                MainActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGHT);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
}
