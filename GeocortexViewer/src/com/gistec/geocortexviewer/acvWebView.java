package com.gistec.geocortexviewer;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;

public class acvWebView extends Activity {
	private WebView mWebView;
	private MenuItem ab_spinner_refresh;
	private FrameLayout mFrameLayout;
	private String address;

	@Override
	public void onBackPressed() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			NavUtils.navigateUpTo(acvWebView.this, new Intent(acvWebView.this,
					acvLinksMenu.class));
			overridePendingTransition(android.R.anim.fade_in,
					R.anim.slide_out_down);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.acv_webview);
		getActionBar().setTitle("");
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.ab_background));
		getActionBar().setDisplayShowHomeEnabled(true);
		getActionBar().setIcon(R.drawable.ic_app);

		address = (String) getIntent().getExtras().get("address");

		invalidateOptionsMenu();

		initUI();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ab_btn_refresh:
			mWebView.reload();
			break;
		case R.id.ab_btn_back:
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ab_webview, menu);
		ab_spinner_refresh = (MenuItem) menu.findItem(R.id.ab_spinner_refresh);
		ab_spinner_refresh.setActionView(R.layout.ab_refresh_spinner);
		ab_spinner_refresh.setVisible(true);
		return true;
	}

	public Intent getParentActivityIntent() {
		NavUtils.navigateUpTo(acvWebView.this, new Intent(acvWebView.this,
				acvLinksMenu.class));
		overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_down);

		return null;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mWebView.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mWebView.restoreState(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (mWebView != null)
			mFrameLayout.removeView(mWebView);
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.acv_webview);
		initUI();

	}

	private void initUI() {

		mFrameLayout = (FrameLayout) findViewById(R.id.framelayout);

		if (mWebView == null) {
			mWebView = new WebView(this);
			mWebView.setLayoutParams(new ViewGroup.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

			mWebView.setWebViewClient(new WebViewClient() {

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					if (ab_spinner_refresh != null)
						ab_spinner_refresh.setVisible(false);
				}

				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					if (ab_spinner_refresh != null)
						ab_spinner_refresh.setVisible(true);
				}

				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					super.onReceivedError(view, errorCode, description,
							failingUrl);
					Toast.makeText(
							acvWebView.this,
							"Unable to connect to server. Check URL and internet connectivity",
							Toast.LENGTH_LONG).show();
					NavUtils.navigateUpTo(acvWebView.this, new Intent(
							acvWebView.this, acvLinksMenu.class));
					overridePendingTransition(android.R.anim.fade_in,
							R.anim.slide_out_down);
				}

			});
			
			mWebView.loadUrl(address);
			mWebView.getSettings().setBuiltInZoomControls(false);
			mWebView.getSettings().setGeolocationEnabled(true);
			mWebView.getSettings().setDisplayZoomControls(false);
			mWebView.getSettings().setUseWideViewPort(true);
			mWebView.getSettings().setLoadWithOverviewMode(true);
			// mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.setWebChromeClient(new WebChromeClient() {

				@Override
				public void onGeolocationPermissionsShowPrompt(String origin,
						Callback callback) {
					super.onGeolocationPermissionsShowPrompt(origin, callback);
					callback.invoke(origin, true, false);
				}
			});
		}

		mFrameLayout.addView(mWebView);
	}


}
