package com.appota.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.appota.model.PaypalTopup;
import com.appota.payment.AppotaClient;

public class PaypalPaymentActivity extends Activity implements OnClickListener{

	private EditText editPaypalValue;
	private Button btnCancel;
	private Button btnCharge;
	private PaypalTopup topup;
	//private final String paypalEndPoint = "https://api.appota.com/payment/topup_paypal";
	private String accessToken;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paypal_topup);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCharge = (Button) findViewById(R.id.btn_paypal_charge);
		editPaypalValue = (EditText) findViewById(R.id.edit_paypal_value);
		accessToken = getIntent().getStringExtra("ACCESS_TOKEN_STR");
		btnCancel.setOnClickListener(this);
		btnCharge.setOnClickListener(this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_cancel:
			finish();
			break;
		case R.id.btn_paypal_charge:
			topup = charge(Double.valueOf(editPaypalValue.getText().toString()), Constants.PAYPAL_ENDPOINT, accessToken);
			Intent intent = new Intent(PaypalPaymentActivity.this, PaypalActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("PAYPAL", topup);
			intent.putExtras(bundle);
			startActivity(intent);
			finish();
		default:
			break;
		}
	}
	
	private PaypalTopup charge(double amount, String endPoint, String accessToken){
		AppotaClient client = new AppotaClient();
		PaypalTopup topup = client.paypalPayment(amount, endPoint, accessToken);
		return topup;
	}

}
