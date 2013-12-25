package com.gistec.geocortexviewer;

import java.util.prefs.Preferences;

import com.afollestad.cardsui.Card;
import com.afollestad.cardsui.Card.CardMenuListener;
import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardBase;
import com.afollestad.cardsui.CardListView;
import com.afollestad.cardsui.CardListView.CardClickListener;

import android.animation.Animator.AnimatorListener;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class acvLinksMenu extends Activity implements CardClickListener{
	private CardListView cardsList;
	private int preference_webmethod;
	private boolean preference_showsplash;
	private SharedPreferences preferences;
	private RadioGroup rg_webmethod;
	private RadioButton rb_internalB, rb_externalB;
	private CheckBox cb_showsplash;
	private ObjectAnimator fadein_links;
	private TextView tv_disclaimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acv_linksmenu);

		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.ab_background));
		getActionBar().setIcon(R.drawable.ic_app);

		tv_disclaimer = (TextView) findViewById(R.id.tv_disclaimer);
		cardsList = (CardListView) findViewById(R.id.cardslist_linksmenu);

		// set user preferences
		setUpPreferences();

		// hide links
		cardsList.setAlpha(0);

		// perform animations
		performAnimations();

		// initialise cards
		CardAdapter<Card> cardsAdapter = new CardAdapter<Card>(this)
				.setAccentColorRes(R.color.gcv_red);
		cardsList.setAdapter(cardsAdapter);

		// fill in card details
		cardsAdapter.add(new Card("NCSI",
				"National Center for Statistics and Information, Oman")
				.setThumbnail(this, R.drawable.img_logo_client1));

	}

	private void setUpPreferences() {
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		preference_webmethod = preferences.getInt("webmethod", -1);
		if (preference_webmethod == -1) {
			if ((Build.MODEL.contentEquals("GT-N7100") || Build.MODEL
					.contentEquals("GT-I9300"))
					&& (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN || Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1)) {
				preferences.edit().putInt("webmethod", 1).apply();
			} else
				preferences.edit().putInt("webmethod", 0).apply();
			preferences.edit().putBoolean("showsplash", true).apply();
			preferences
					.edit()
					.putString(
							"url",
							"http://restdemos.geocortex.com/Geocortex/Html5Viewer/Index.html?viewerConfigUri=http://restdemos.geocortex.com/Geocortex/Essentials/Sandbox/REST/sites/omncsi/viewers/html5Viewer/virtualdirectory/Resources/Config/Default/handheld.json.js")
					.apply();
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
			boolean currentShowSplash = preferences.getBoolean("showsplash",
					true);
			AlertDialog.Builder builder_settingsDialog = new AlertDialog.Builder(
					acvLinksMenu.this);
			LayoutInflater inflater_settingsDialog = LayoutInflater.from(acvLinksMenu.this);
			View dialogView_settingsDialog = inflater_settingsDialog.inflate(R.layout.dialog_settings, null);
			builder_settingsDialog.setView(dialogView_settingsDialog);

			cb_showsplash = (CheckBox) dialogView_settingsDialog
					.findViewById(R.id.cb_showsplash);
			rb_internalB = (RadioButton) dialogView_settingsDialog
					.findViewById(R.id.rb_internalB);
			rb_externalB = (RadioButton) dialogView_settingsDialog
					.findViewById(R.id.rb_externalB);
			if (currentWebMethod == 0)
				rb_internalB.setChecked(true);
			else
				rb_externalB.setChecked(true);
			if ((Build.MODEL.contentEquals("GT-N7100") || Build.MODEL
					.contentEquals("GT-I9300"))
					&& (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN || Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1))
				rb_internalB.setEnabled(false);

			if (currentShowSplash)
				cb_showsplash.setChecked(true);

			builder_settingsDialog.setTitle("Settings");
			builder_settingsDialog.setPositiveButton("OK",
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
			builder_settingsDialog.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			AlertDialog dialog_settings = builder_settingsDialog.create();
			dialog_settings.setCanceledOnTouchOutside(false);
			dialog_settings.show();
			break;
			
		case R.id.ab_btn_editURL:
			AlertDialog.Builder builder_editURLDialog = new AlertDialog.Builder(acvLinksMenu.this);
			LayoutInflater inflater_editURLDialog = LayoutInflater.from(acvLinksMenu.this);
			View dialogView_editURLDialog = inflater_editURLDialog.inflate(R.layout.dialog_edit, null);
			final String origURL = "http://restdemos.geocortex.com/Geocortex/Html5Viewer/Index.html?viewerConfigUri=http://restdemos.geocortex.com/Geocortex/Essentials/Sandbox/REST/sites/omncsi/viewers/html5Viewer/virtualdirectory/Resources/Config/Default/handheld.json.js";
			final EditText et_editURL = (EditText) dialogView_editURLDialog
					.findViewById(R.id.et_editURL);
			et_editURL.setText(preferences.getString("url", "Enter URL"));

			builder_editURLDialog.setNeutralButton("Reset to Default",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).setTitle("Edit URL").setView(dialogView_editURLDialog);

			final AlertDialog dialog_editURL = builder_editURLDialog.create();
			dialog_editURL.setOnShowListener(new DialogInterface.OnShowListener() {
				Toast toast;

				@Override
				public void onShow(DialogInterface dialog) {
					dialog_editURL.getButton(AlertDialog.BUTTON_NEUTRAL)
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									et_editURL.setText(origURL);
								}
							});
					dialog_editURL.getButton(AlertDialog.BUTTON_POSITIVE)
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									String url = et_editURL.getText().toString();
									if (!(url.startsWith("http://") || url
											.startsWith("https://"))) {
										InputMethodManager imm = (InputMethodManager) getSystemService(acvLinksMenu.INPUT_METHOD_SERVICE);
										imm.hideSoftInputFromWindow(
												et_editURL.getWindowToken(), 0);
										if (toast != null)
											toast.cancel();
										toast = Toast
												.makeText(
														getApplicationContext(),
														"Oops! Check your URL, it should begin with http:// or https://",
														Toast.LENGTH_LONG);
										toast.show();
									} else {
										preferences.edit().putString("url", url)
												.apply();
										dialog_editURL.dismiss();
									}
								}
							});
				}
			});
			dialog_editURL.setCanceledOnTouchOutside(false);
			dialog_editURL.show();
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void performAnimations() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		ObjectAnimator fadein_links = ObjectAnimator.ofFloat(cardsList,
				"alpha", 0f, 1f).setDuration(2000);
		ObjectAnimator fadein_text = ObjectAnimator.ofFloat(tv_disclaimer,
				"alpha", 0f, 1f).setDuration(2000);

		ObjectAnimator translate_links = ObjectAnimator.ofFloat(cardsList,
				"translationY", (float) (dm.heightPixels * 1.3), 0)
				.setDuration(1500);
		fadein_links.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
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
		AnimatorSet set = new AnimatorSet();
		// set.playTogether(fadein_links, translate_links, fadein_text);
		set.playTogether(fadein_links, fadein_text);
		set.start();
	}

	private void setCardListener() {
		cardsList.setOnCardClickListener(new CardClickListener() {

			@Override
			public void onCardClick(int index, CardBase card, View view) {
				switch (index) {
				case 0:
					loadWebView(preferences.getString("url", ""));
					// loadWebView("http://www.google.com");
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
