package com.appota.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.appota.model.BankOption;
import com.appota.model.BankTopup;
import com.appota.payment.AppotaClient;

public class BankingPaymentActivity extends Activity implements OnClickListener{
	
	private EditText editBankValue;
	private BankTopup topup;
	private BankOption option;
	private Button btnCancel;
	private Button btnCharge;
	//private final String bankEndPoint = "https://api.appota.com/payment/topup_bank";
	private String accessToken;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.banking_topup);
		editBankValue = (EditText) findViewById(R.id.edit_bank_value);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCharge = (Button) findViewById(R.id.btn_banking_charge);
		btnCancel.setOnClickListener(this);
		btnCharge.setOnClickListener(this);
		accessToken = getIntent().getStringExtra("ACCESS_TOKEN_STR");
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_cancel:
			finish();
			break;
		case R.id.btn_banking_charge:
			
			//get banking topup information
			topup = charge(Double.valueOf(editBankValue.getText().toString()), Constants.BANK_ENDPOINT, accessToken);
			
			//go to smart link payment website
			Intent intent = new Intent(BankingPaymentActivity.this, BankingWebViewActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("URL", topup.getOption().getUrl());
			intent.putExtras(bundle);
			startActivity(intent);
			finish();
		default:
			break;
		}
	}
	
	private BankTopup charge(double amount, String endPoint, String accessToken){
		AppotaClient client = new AppotaClient();
		BankTopup topup = client.bankPayment(amount, endPoint, accessToken);
		return topup;
	}

}
