package com.appota.test;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.appota.model.SMSOption;
import com.appota.model.SMSTopup;

public class SMSPaymentActivity extends Activity implements OnClickListener{
	
	private ProgressDialog progressDialog;
	private SMSTopup smsTopup;
	private Spinner spnSMSValue; 
	private ArrayAdapter<SMSOption> smsSpinnerAdapter;
	private Runnable background;
	private Bundle received = null;
	private Button btnCancel;
	private Button btnCharge;
	private List<SMSOption> options;
	private String sendNumber;
	private String syntax;
	private boolean isSuccess;
	//private final String topupEndPoint = "https://api.appota.com/payment/topup";
	private String accessToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_topup);
		spnSMSValue = (Spinner) findViewById(R.id.spn_sms_value);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCharge = (Button) findViewById(R.id.btn_sendsms);
		btnCancel.setOnClickListener(this);
		btnCharge.setOnClickListener(this);
		received = this.getIntent().getExtras();
		background = new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				smsTopup = (SMSTopup) received.getSerializable("SMS_OPTIONS");
				accessToken = received.getString("ACCESS_TOKEN_STR");
				runOnUiThread(runnable);
			}
		};
		
		Thread thread = new Thread(background);
		thread.start();
		progressDialog = ProgressDialog.show(SMSPaymentActivity.this, "Please wait...", "Retrieving data...", true);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_cancel:
			finish();
			break;
		case R.id.btn_sendsms:
			sendSMS(sendNumber, syntax);
			System.out.println("Send SMS: " + syntax + " to " + sendNumber);
			Toast.makeText(getApplicationContext(),R.string.toast_card_charge_pending, Toast.LENGTH_SHORT).show();
			Intent i = new Intent(SMSPaymentActivity.this, CheckTopupService.class);
			i.putExtra("TOPUP_ID", smsTopup.getTopupId());
			i.putExtra("ACCESS_TOKEN_STR", accessToken);
			startService(i);
			finish();
			break;
		default:
			break;
		}
	}
	
	private Runnable runnable = new Runnable() {
		
		public void run() {
			// TODO Auto-generated method stub
			options = smsTopup.getSmsOptions();
			smsSpinnerAdapter = new ArrayAdapter<SMSOption>(getApplicationContext(), android.R.layout.simple_spinner_item, options);
			smsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spnSMSValue.setAdapter(smsSpinnerAdapter);
			spnSMSValue.setOnItemSelectedListener(new OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> arg0, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					SMSOption selected = options.get(position);
					sendNumber = selected.toString().split(":")[0].trim();
					syntax = selected.getSyntax();
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			progressDialog.dismiss();
		}
	};
	
	private void sendSMS(String number, String textMessage){
		SmsManager manager = SmsManager.getDefault();
		manager.sendTextMessage(number, null, textMessage, null, null);
		Toast.makeText(getApplicationContext(), "Your sms transaction has been sent.", Toast.LENGTH_SHORT).show();
	}

}
