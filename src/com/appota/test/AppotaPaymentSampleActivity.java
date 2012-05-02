package com.appota.test;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.appota.dialogactivity.LoginDialogActivity;
import com.appota.model.AccessToken;
import com.appota.model.DefaultAppotaOauth;
import com.appota.model.SMSTopup;
import com.appota.model.TopupChecker;
import com.appota.payment.AppotaClient;

public class AppotaPaymentSampleActivity extends Activity implements OnClickListener {
	
	protected Button btnPhoneCard;
	protected Button btnSMS;
	protected Button btnBanking;
	protected Button btnPaypal;
	protected ImageButton btnClose;
	private AccessToken accessToken = null;
	private String accessTokenStr = null;
	private final int REQUEST_CODE = 1;
	private AppotaClient client;
	private SharedPreferences pref;
	private Editor editor;
	private TopupChecker checker;
	private TopupReceiver topupReceiver;
	private boolean isRequireUser = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topup_options);
		findViews();
		topupReceiver = new TopupReceiver();
		IntentFilter intentFilter = new IntentFilter();
	    intentFilter.addAction("topup.true");
	    registerReceiver(topupReceiver, intentFilter);
		btnBanking.setOnClickListener(this);
		btnPaypal.setOnClickListener(this);
		btnPhoneCard.setOnClickListener(this);
		btnSMS.setOnClickListener(this);
		client = new AppotaClient();
		
		//create a sharedPreference to store access token
		pref = getSharedPreferences(Constants.ACCESS_TOKEN_PREF, 0);
		/*editor = pref.edit();
		editor.clear();
		editor.commit();*/
		accessTokenStr = pref.getString(Constants.ACCESS_TOKEN_KEY, "");
	}

	public void findViews(){
		btnBanking = (Button) findViewById(R.id.btn_banking);
		btnPaypal = (Button) findViewById(R.id.btn_paypal);
		btnPhoneCard = (Button) findViewById(R.id.btn_carrier_card);
		btnSMS = (Button) findViewById(R.id.btn_sms);
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_carrier_card:
			phoneCardPayment();
			break;
		case R.id.btn_sms:
			smsPayment(intent);
			break;
		case R.id.btn_banking:
			bankingPayment(intent);
			break;
		case R.id.btn_paypal:
			paypalPayment(intent);
			break;
		default:
			break;
		}
	}

	
	//topup by phone card
	public void phoneCardPayment(){
		//check if access token is null, let user login first to get access token
		/*
		 * isRequireUser: define the app need to use user information or not
		 * if the app only use inapp scope, then no need user information
		 * 
		 */
		if(accessTokenStr.equals("") && isRequireUser == true){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setCancelable(true);
	        builder.setMessage("Login first");
	        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	
	    			List<String> scopes = new ArrayList<String>();
	    			//add all scopes you need to an array list
	    			scopes.add(Constants.USER_INFO_SCOPE);
	    			scopes.add(Constants.USER_PAYMENT_SCOPE);
	    			
	    			//create an instance of DefautAppotaOauth, contains some constant fields already
	    			//we don't have to assign or set anything to it
	    			DefaultAppotaOauth defOauth = new DefaultAppotaOauth();
	    			
	    			//get request token endpoint and send to Login Activity
	    			String requestTokenUrl = client.requestToken(Constants.CLIENT_KEY, scopes, defOauth);
	    			
	    			//start activity for login
	    			//LoginDialogActivity from ap.jar library file, you should declare it in AndroidManifest.xml file
	    			Intent i = new Intent(AppotaPaymentSampleActivity.this, LoginDialogActivity.class);
	    			i.putExtra("REQ_URL", requestTokenUrl);
	    			i.putExtra("CLIENT_KEY", Constants.CLIENT_KEY);
	    			i.putExtra("CLIENT_SECRET", Constants.CLIENT_SECRET);
	    			
	    			//get accesstoken from Login Activity after user finish authorization
	    			startActivityForResult(i, REQUEST_CODE);
	            }
	        });
	        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                return;
	            }
	        });
	        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
	            public void onCancel(DialogInterface dialog) {
	                return;
	            }
	        });
	        builder.show();
			
		} else {
			//if access token has value already, start payments activity
			Intent i = new Intent(AppotaPaymentSampleActivity.this, PhoneCardPaymentActivity.class);
			i.putExtra("ACCESS_TOKEN_STR", accessTokenStr);
			startActivity(i);
		}
	}
	
	/*
	 * end of Phone card payment,
	 * we do the same thing with 3 remain payment method*/
	

	
	//topup by sms
	public void smsPayment(Intent i){
		
		if(accessTokenStr.equals("") && isRequireUser == true){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setCancelable(true);
	        builder.setMessage("Login first");
	        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	
	    			List<String> scopes = new ArrayList<String>();
	    			scopes.add(Constants.USER_INFO_SCOPE);
	    			scopes.add(Constants.USER_PAYMENT_SCOPE);
	    			String requestTokenUrl = client.requestToken(Constants.CLIENT_KEY, scopes, new DefaultAppotaOauth());
	    			Intent i = new Intent(AppotaPaymentSampleActivity.this, LoginDialogActivity.class);
	    			i.putExtra("REQ_URL", requestTokenUrl);
	    			startActivityForResult(i, REQUEST_CODE);
	            }
	        });
	        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                return;
	            }
	        });
	        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
	            public void onCancel(DialogInterface dialog) {
	                return;
	            }
	        });
	        builder.show();
			
		} else {
			SMSTopup topup = client.smsPayment(Constants.SMS_ENDPOINT, accessTokenStr);
			i = new Intent(AppotaPaymentSampleActivity.this, SMSPaymentActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("SMS_OPTIONS", topup);
			bundle.putString("ACCESS_TOKEN_STR", accessTokenStr);
			i.putExtras(bundle);
			startActivity(i);
		}
		
	}
	
	
	//topup by bank
	public void bankingPayment(Intent i){
		if(accessTokenStr.equals("") && isRequireUser == true){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setCancelable(true);
	        builder.setMessage("Login first");
	        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	
	    			List<String> scopes = new ArrayList<String>();
	    			scopes.add(Constants.USER_INFO_SCOPE);
	    			scopes.add(Constants.USER_PAYMENT_SCOPE);
	    			String requestTokenUrl = client.requestToken(Constants.CLIENT_KEY, scopes, new DefaultAppotaOauth());
	    			Intent i = new Intent(AppotaPaymentSampleActivity.this, LoginDialogActivity.class);
	    			i.putExtra("REQ_URL", requestTokenUrl);
	    			startActivityForResult(i, REQUEST_CODE);
	            }
	        });
	        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                return;
	            }
	        });
	        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
	            public void onCancel(DialogInterface dialog) {
	                return;
	            }
	        });
	        builder.show();
		} else {
			i = new Intent(AppotaPaymentSampleActivity.this, BankingPaymentActivity.class);
			i.putExtra("ACCESS_TOKEN_STR", accessTokenStr);
			startActivity(i);
		}
		
	}
	
	
	
	//topup by paypal
	public void paypalPayment(Intent i){
		if(accessTokenStr.equals("") && isRequireUser == true){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setCancelable(true);
	        builder.setMessage("Login first");
	        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	
	    			List<String> scopes = new ArrayList<String>();
	    			scopes.add(Constants.USER_INFO_SCOPE);
	    			scopes.add(Constants.USER_PAYMENT_SCOPE);
	    			String requestTokenUrl = client.requestToken(Constants.CLIENT_KEY, scopes, new DefaultAppotaOauth());
	    			Intent i = new Intent(AppotaPaymentSampleActivity.this, LoginDialogActivity.class);
	    			i.putExtra("REQ_URL", requestTokenUrl);
	    			startActivityForResult(i, REQUEST_CODE);
	            }
	        });
	        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                return;
	            }
	        });
	        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
	            public void onCancel(DialogInterface dialog) {
	                return;
	            }
	        });
	        builder.show();
		} else {
			i = new Intent(AppotaPaymentSampleActivity.this, PaypalPaymentActivity.class);
			i.putExtra("ACCESS_TOKEN_STR", accessToken.getToken());
			startActivity(i);
		}
		
	}

	
	//get access token and save it to sharedPreference
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode == REQUEST_CODE){
			accessToken = (AccessToken) data.getExtras().get("ACCESS_TOKEN");
			accessTokenStr = accessToken.getToken();
			editor = pref.edit();
			editor.putString(Constants.ACCESS_TOKEN_KEY, accessTokenStr);
			editor.commit();
		}
	}
	
	private class TopupReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			checker = (TopupChecker) arg1.getExtras().getSerializable("result");
			if (checker.isSuccess()) {
				Toast.makeText(getApplicationContext(),checker.getMessage() + " TYM: " + checker.getTym(), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(),checker.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(topupReceiver);
	}

}