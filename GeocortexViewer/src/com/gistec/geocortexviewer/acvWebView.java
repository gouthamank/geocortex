package com.gistec.geocortexviewer;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class acvWebView extends Activity {
	private WebView mWebView;
	private ProgressBar pb_webview;

	@Override
	public void onBackPressed() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			getBackToLinksMenu();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		((WebView) findViewById(R.id.webview)).saveState(outState);
	}
	//trying to save state when orientation change, failed so far, 
	//have locked orientation to portrait till I fix this
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		((WebView) findViewById(R.id.webview)).restoreState(savedInstanceState);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.acv_webview);
		getActionBar().setTitle("");
		getActionBar().setBackgroundDrawable( //am aware that the BG is blocky and bad res, am looking for a better res logo
				getResources().getDrawable(R.drawable.logo_ncsi3));
		getActionBar().setDisplayShowHomeEnabled(false);
		String address = (String) getIntent().getExtras().get("address");

		invalidateOptionsMenu();
		mWebView = (WebView) findViewById(R.id.webview);
		pb_webview = (ProgressBar) findViewById(R.id.pb_webview);

		mWebView.setWebViewClient(new WebViewClient());
		mWebView.loadUrl(address);
		mWebView.getSettings().setBuiltInZoomControls(false);
		mWebView.getSettings().setGeolocationEnabled(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				pb_webview.setProgress(newProgress);
				if (newProgress < 100
						&& (pb_webview.getVisibility() == ProgressBar.GONE)) {
					pb_webview.setVisibility(ProgressBar.VISIBLE);
				}
				if (newProgress >= 100)
					pb_webview.setVisibility(ProgressBar.GONE);
			}

			@Override
			public void onGeolocationPermissionsShowPrompt(String origin,
					Callback callback) {
				super.onGeolocationPermissionsShowPrompt(origin, callback);
				callback.invoke(origin, true, false);
			}

		});

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ab_btn_refresh:
			mWebView.reload();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void getBackToLinksMenu() {
		finish();
		overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_down);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ab_webview, menu);
		return true;
	}

	public Intent getParentActivityIntent() {
		getBackToLinksMenu();
		return null;
	}

}
