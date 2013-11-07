package com.gistec.geocortexviewer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardBase;
import com.afollestad.cardsui.CardListView;
import com.afollestad.cardsui.CardListView.CardClickListener;

import android.R.integer;
import android.animation.Animator.AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class acvLinksMenu extends Activity implements CardClickListener {
	private ImageView img_logo;
	private CardListView cardsList;
	private int preference_webmethod;
	private boolean preference_showsplash;
	private SharedPreferences preferences;
	private RadioGroup rg_webmethod;
	private RadioButton rb_internalB, rb_externalB;
	private CheckBox cb_showsplash;
	private ObjectAnimator fadein_links;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acv_linksmenu);
		getActionBar().hide();
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(
						android.R.color.holo_blue_dark)));

		cardsList = (CardListView) findViewById(R.id.cardslist_linksmenu);
		img_logo = (ImageView) findViewById(R.id.img_logo);
		// set user preferences
		setUpPreferences();

		// hide links
		img_logo.setVisibility(ImageView.GONE);
		cardsList.setAlpha(0);

		// perform animations
		performAnimations();
		
		// initialise cards
		CardAdapter<Card> cardsAdapter = new CardAdapter<Card>(this)
				.setAccentColorRes(android.R.color.holo_blue_dark);
		cardsList.setAdapter(cardsAdapter);

		// fill in card details
		cardsAdapter.add(new Card("NCSI",
				"National Center for Statistics and Information, Oman")
				.setPopupMenu(-1, null).setThumbnail(this,
						R.drawable.img_logo_client1));

	}

	private void setUpPreferences() {
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		preference_webmethod = preferences.getInt("webmethod", -1);
		if (preference_webmethod == -1) {
			preferences.edit().putInt("webmethod", 0).apply();
			preferences.edit().putBoolean("showsplash", true).apply();
		}
		preference_webmethod = preferences.getInt("webmethod", -1);
		preference_showsplash = preferences.getBoolean("showsplash", true);
	}

	@Override
	public void onCardClick(int index, CardBase card, View view) {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ab_linksmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ab_btn_settings:
			int currentWebMethod = preferences.getInt("webmethod", -1);
			;
			boolean currentShowSplash = preferences.getBoolean("showsplash",
					true);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					acvLinksMenu.this);
			LayoutInflater inflater = LayoutInflater.from(acvLinksMenu.this);
			View dialogView = inflater.inflate(R.layout.dialog_settings, null);
			builder.setView(dialogView);

			cb_showsplash = (CheckBox) dialogView
					.findViewById(R.id.cb_showsplash);
			rb_internalB = (RadioButton) dialogView
					.findViewById(R.id.rb_internalB);
			rb_externalB = (RadioButton) dialogView
					.findViewById(R.id.rb_externalB);
			if (currentWebMethod == 0)
				rb_internalB.setChecked(true);
			else
				rb_externalB.setChecked(true);

			if (currentShowSplash)
				cb_showsplash.setChecked(true);

			builder.setTitle("Settings");
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							preferences.edit().remove("webmethod").apply();
							preferences.edit().remove("showsplash").apply();
							int preferredWebMethod;
							if (rb_internalB.isChecked()) {
								preferredWebMethod = 0;
							} else {
								preferredWebMethod = 1;
							}

							preferences.edit()
									.putInt("webmethod", preferredWebMethod)
									.apply();
							preferences
									.edit()
									.putBoolean("showsplash",
											cb_showsplash.isChecked()).apply();
						}
					});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			builder.show();
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	private void performAnimations() {
		ObjectAnimator fadein_logo = ObjectAnimator.ofFloat(img_logo, "alpha",
				0f, 1f).setDuration(3000);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		ObjectAnimator translateUp_logo = ObjectAnimator.ofFloat(img_logo, "y",
				-dm.heightPixels).setDuration(1800);
		ObjectAnimator fadein_links = ObjectAnimator.ofFloat(cardsList,
				"alpha", 0f, 1f).setDuration(1000);
		AnimatorSet set = new AnimatorSet();
		fadein_links.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				getActionBar().show();
				setCardListener();

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
		if (preferences.getBoolean("showsplash", true)) {
			img_logo.setVisibility(ImageView.VISIBLE);
			img_logo.setAlpha((float)0);
			set.playSequentially(fadein_logo, translateUp_logo, fadein_links);
		} else {
			set.play(fadein_links);
		}
		set.start();
	}

	private void setCardListener() {
		// set card click listener
		cardsList.setOnCardClickListener(new CardClickListener() {

			@Override
			public void onCardClick(int index, CardBase card, View view) {
				switch (index) {
				case 0:
					loadWebView("http://restdemos.geocortex.com/Geocortex/Html5Viewer/Index.html?viewerConfigUri=http://restdemos.geocortex.com/Geocortex/Essentials/Sandbox/REST/sites/omncsi/viewers/html5Viewer/virtualdirectory/Resources/Config/Default/handheld.json.js");
					//loadWebView("http://www.google.com");
					break;
				}
			}

			private void loadWebView(String url) {
				int webmethod = preferences.getInt("webmethod", 0);
				if (webmethod == 0) {
					Intent mIntent = new Intent(acvLinksMenu.this,
							acvWebView.class);
					Bundle mBundle = new Bundle();
					mBundle.putString("address", url);
					mIntent.putExtras(mBundle);
					startActivity(mIntent);
					overridePendingTransition(R.anim.slide_in_up,
							android.R.anim.fade_out);
				} else if (webmethod == 1) {
					Intent mIntent = new Intent(Intent.ACTION_VIEW);
					mIntent.setData(Uri.parse(url));
					startActivity(mIntent);
				}
			}
		});
	}
}
