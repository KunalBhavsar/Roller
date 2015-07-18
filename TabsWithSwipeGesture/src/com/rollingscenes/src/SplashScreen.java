package com.rollingscenes.src;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.rollingscenes.R;
import com.rollingscenes.src.utils.SharedPrefs;

public class SplashScreen extends Activity {
	// Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        ((ImageView)findViewById(R.id.imgLogo)).startAnimation(
        		AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate));
        ((TextView)findViewById(R.id.app_name_text)).startAnimation(
        		AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in));
        
        new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
            	SharedPrefs sPrefs = SharedPrefs.getInstance(getApplicationContext());
            	if (sPrefs.isLoggedIn()) {
        			Intent intent = new Intent(getApplicationContext(),
        					EventListActivity.class);
        			startActivity(intent);
        		}
            	else {
            		Intent intent = new Intent(getApplicationContext(),
        					SocialLoginActivity.class);
        			startActivity(intent);
            	}
 
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
