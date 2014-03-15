package com.app.uber.googleimage;

import views.activities.GoogleImageSearch;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class SplashScreenActivity extends Activity {
	
	 Handler handler = new Handler();
	 Runnable runnable = new Runnable() {

		public void run() {
			 Intent i;
			
			 i = new Intent(SplashScreenActivity.this, GoogleImageSearch.class);		 
			// i = new Intent(SplashScreenActivity.this, MainActivity.class);
			 startActivity(i);
			 SplashScreenActivity.this.finish();
	     }
	  };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        
	    handler.postDelayed(runnable, 3000);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash_screen, menu);
        return true;
    }
    
}
