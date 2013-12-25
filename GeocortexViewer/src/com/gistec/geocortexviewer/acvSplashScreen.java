package com.gistec.geocortexviewer;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class acvSplashScreen extends Activity implements OnClickListener {
	private ImageButton ib_english, ib_arabic;
	private RelativeLayout rl_splashscreen;

	private String stringURL = "http://restdemos.geocortex.com/Geocortex/Html5Viewer/Index.html?viewerConfigUri=http://restdemos.geocortex.com/Geocortex/Essentials/Sandbox/REST/sites/omncsi/viewers/html5Viewer/virtualdirectory/Resources/Config/Default/handheld.json.js";
	private boolean shouldShowURLList = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acv_splashscreen);
		getActionBar().hide();
		
		rl_splashscreen = (RelativeLayout) findViewById(R.id.rl_splashscreen);
		ib_english = (ImageButton) findViewById(R.id.ib_english);
		ib_arabic = (ImageButton) findViewById(R.id.ib_arabic);
		if(shouldShowURLList){
		ib_english.setOnClickListener(this);
		ib_arabic.setOnClickListener(this);
		}
		else{
			ib_english.setEnabled(false);
			ib_arabic.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent mIntent = new Intent(Intent.ACTION_VIEW);
					mIntent.setData(Uri.parse(stringURL));
					startActivity(mIntent);
				}
			});
		}
		
		
		performAnimations();
		
		
	}

	private void performAnimations() {
		ObjectAnimator fadein_splashscreen = ObjectAnimator.ofFloat(
				rl_splashscreen, "alpha", 0f, 1f).setDuration(1500);
		fadein_splashscreen.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {
				ib_english.setClickable(false);
				ib_arabic.setClickable(false);
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				ib_english.setClickable(true);
				ib_arabic.setClickable(true);
			}

			@Override
			public void onAnimationCancel(Animator arg0) {

			}
		});
		fadein_splashscreen.start();
	}

	@Override
	public void onClick(View v) {
		startActivity(new Intent(acvSplashScreen.this, acvLinksMenu.class));
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

}
