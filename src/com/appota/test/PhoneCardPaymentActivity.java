package com.appota.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.appota.payment.AppotaClient;

public class PhoneCardPaymentActivity extends Activity implements
		OnClickListener {

	private Spinner spnCardType;
	private EditText editCardCode;
	private EditText editSerial;
	private Button btnCancel;
	private Button btnCharge;
	private String topupId;
	private String accessToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_topup);
		findViews();
		btnCancel.setOnClickListener(this);
		btnCharge.setOnClickListener(this);
		accessToken = getIntent().getStringExtra("ACCESS_TOKEN_STR");
	}

	public void findViews() {
		spnCardType = (Spinner) findViewById(R.id.spn_card_type);
		editCardCode = (EditText) findViewById(R.id.edit_card_code);
		editSerial = (EditText) findViewById(R.id.edit_card_serial);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCharge = (Button) findViewById(R.id.btn_charge);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_charge:
			topupId = charge(editCardCode.getText().toString(), editSerial.getText().toString(), spnCardType.getSelectedItem().toString(), Constants.CARD_ENDPOINT, accessToken);
			
			//after get topupId, run a service to check if the transaction was success or not after a period of time.
			Toast.makeText(getApplicationContext(),R.string.toast_card_charge_pending, Toast.LENGTH_SHORT).show();
			Intent i = new Intent(PhoneCardPaymentActivity.this, CheckTopupService.class);
			i.putExtra("TOPUP_ID", topupId);
			i.putExtra("ACCESS_TOKEN_STR", accessToken);
			startService(i);
			finish();
			break;
		case R.id.btn_cancel:
			finish();
			break;
		default:
			break;
		}
	}

	private String charge(String code, String serial, String vendor,
			String endPoint, String accessToken) {
		AppotaClient client = new AppotaClient();
		String topupId = client.cardPayment(code, serial, vendor, endPoint,
				accessToken);
		return topupId;
	}

	/*private class TopupReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			isSuccess = arg1.getBooleanExtra("success", false);
			if (isSuccess) {
				Toast.makeText(getApplicationContext(),R.string.toast_card_charge_success, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(),R.string.toast_card_charge_failed, Toast.LENGTH_SHORT).show();
			}
		}

	}*/

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//unregisterReceiver(receiver);
	}

}
