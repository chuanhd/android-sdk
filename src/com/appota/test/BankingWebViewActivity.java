package com.appota.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebView;

import com.appota.model.BankingWebViewClient;

public class BankingWebViewActivity extends Activity{

	private WebView webView;
	private ProgressDialog progressDialog;
	private Runnable r;
	private String url;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bank_webview);
		webView = (WebView) findViewById(R.id.webview);
		webView.setWebViewClient(new BankingWebViewClient());
		
		r = new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				Bundle bundle = getIntent().getExtras();
				url = bundle.getString("URL").replace("\\", "");
				runOnUiThread(updateUI);
			}
		};
		
		Thread thread = new Thread(r);
		thread.start();
		progressDialog = ProgressDialog.show(BankingWebViewActivity.this, "Loading...", "Please wait...", true);
	}
	
	private Runnable updateUI = new Runnable() {
		
		public void run() {
			// TODO Auto-generated method stub
			webView.loadUrl(url);
			progressDialog.dismiss();
		}
	};
	
	

}
