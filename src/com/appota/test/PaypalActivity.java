package com.appota.test;

import java.math.BigDecimal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appota.model.PaypalTopup;
import com.appota.model.ResultDelegate;
import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalInvoiceData;
import com.paypal.android.MEP.PayPalInvoiceItem;
import com.paypal.android.MEP.PayPalPayment;

public class PaypalActivity extends Activity implements OnClickListener {

	private static final int server = PayPal.ENV_LIVE;
	protected static final int INITIALIZE_SUCCESS = 0;
	protected static final int INITIALIZE_FAILURE = 1;
	private static final String appID = "APP-3NF54453B0123992M";
	private static final int request = 1;
	private LinearLayout layoutPayment;
	private CheckoutButton btnCheckout;
	private PaypalTopup topup;
	ProgressDialog progressDialog;
	
	Handler hRefresh = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
		    	case INITIALIZE_SUCCESS:
		    		setupButtons();
		    		progressDialog.dismiss();
		            break;
		    	case INITIALIZE_FAILURE:
		    		showFailure();
		    		progressDialog.dismiss();
		    		break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Thread libraryInitializationThread = new Thread() {
			public void run() {
				initLibrary();
				Bundle bundle = getIntent().getExtras();
				topup = (PaypalTopup) bundle.getSerializable("PAYPAL");
				// The library is initialized so let's create our CheckoutButton and update the UI.
				if (PayPal.getInstance().isLibraryInitialized()) {
					System.out.println("setup button");
					hRefresh.sendEmptyMessage(INITIALIZE_SUCCESS);
				}
				else {
					System.out.println("fail");
					hRefresh.sendEmptyMessage(INITIALIZE_FAILURE);
				}
			}
		};
		libraryInitializationThread.start();
		drawUI();
		progressDialog = ProgressDialog.show(this, "Loading...", "Please wait...", true);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == btnCheckout) {
			// Use our helper function to create the simple payment.
			PayPalPayment payment = getPayment();
			// Use checkout to create our Intent.
			Intent checkoutIntent = PayPal.getInstance().checkout(payment,
					this, new ResultDelegate());
			// Use the android's startActivityForResult() and pass in our
			// Intent. This will start the library.
			startActivityForResult(checkoutIntent, request);
		}
	}

	private void drawUI() {

		LinearLayout content = new LinearLayout(this);
		content.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		content.setGravity(Gravity.CENTER_HORIZONTAL);
		content.setOrientation(LinearLayout.VERTICAL);
		content.setPadding(10, 10, 10, 10);
		content.setBackgroundColor(Color.TRANSPARENT);

		layoutPayment = new LinearLayout(this);
		layoutPayment.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		layoutPayment.setGravity(Gravity.CENTER_HORIZONTAL);
		layoutPayment.setOrientation(LinearLayout.VERTICAL);
		layoutPayment.setPadding(0, 5, 0, 5);

		content.addView(layoutPayment);
		setContentView(content);
	}

	public void setupButtons() {
		PayPal pp = PayPal.getInstance();
		// Get the CheckoutButton. There are five different sizes. The text on
		// the button can either be of type TEXT_PAY or TEXT_DONATE.
		btnCheckout = pp.getCheckoutButton(this, PayPal.BUTTON_194x37, CheckoutButton.TEXT_PAY);
		// You'll need to have an OnClickListener for the CheckoutButton. For
		// this application, MPL_Example implements OnClickListener and we
		// have the onClick() method below.
		btnCheckout.setOnClickListener(this);
		// The CheckoutButton is an android LinearLayout so we can add it to our
		// display like any other View.
		layoutPayment.addView(btnCheckout);

		// Show our labels and the preapproval EditText.
	}

	public void showFailure() {
		Toast.makeText(getApplicationContext(),
				"Could not initialize the PayPal library.", Toast.LENGTH_SHORT);
	}

	private void initLibrary() {
		System.out.println("init library");
		PayPal pp = PayPal.getInstance();
		// If the library is already initialized, then we don't need to
		// initialize it again.
		if (pp == null) {
			// This is the main initialization call that takes in your Context,
			// the Application ID, and the server you would like to connect to.
			pp = PayPal.initWithAppID(this, appID, server);

			// -- These are required settings.
			pp.setLanguage("en_US"); // Sets the language for the library.
			// --

			// -- These are a few of the optional settings.
			// Sets the fees payer. If there are fees for the transaction, this
			// person will pay for them. Possible values are FEEPAYER_SENDER,
			// FEEPAYER_PRIMARYRECEIVER, FEEPAYER_EACHRECEIVER, and
			// FEEPAYER_SECONDARYONLY.
			pp.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER);
			// Set to true if the transaction will require shipping.
			pp.setShippingEnabled(true);
			// Dynamic Amount Calculation allows you to set tax and shipping
			// amounts based on the user's shipping address. Shipping must be
			// enabled for Dynamic Amount Calculation. This also requires you to
			// create a class that implements PaymentAdjuster and Serializable.
			pp.setDynamicAmountCalculationEnabled(false);
			// --
		}
	}

	private PayPalPayment getPayment() {
		// Create a basic PayPalPayment.
		PayPalPayment payment = new PayPalPayment();
		// Sets the currency type for this payment.
		payment.setCurrencyType(topup.getForm().getCurrencyCode());
		// Sets the recipient for the payment. This can also be a phone number.
		payment.setRecipient(topup.getForm().getBusiness());
		// Sets the amount of the payment, not including tax and shipping
		// amounts.
		payment.setSubtotal(new BigDecimal(topup.getForm().getAmount()));
		// Sets the payment type. This can be PAYMENT_TYPE_GOODS,
		// PAYMENT_TYPE_SERVICE, PAYMENT_TYPE_PERSONAL, or PAYMENT_TYPE_NONE.
		payment.setPaymentType(PayPal.PAYMENT_TYPE_GOODS);

		// PayPalInvoiceData can contain tax and shipping amounts. It also
		// contains an ArrayList of PayPalInvoiceItem which can
		// be filled out. These are not required for any transaction.
		PayPalInvoiceData invoice = new PayPalInvoiceData();
		// Sets the tax amount.
		invoice.setTax(new BigDecimal("0.00"));
		// Sets the shipping amount.
		invoice.setShipping(new BigDecimal(topup.getForm().getNoShopping()));

		// PayPalInvoiceItem has several parameters available to it. None of
		// these parameters is required.
		PayPalInvoiceItem item1 = new PayPalInvoiceItem();
		// Sets the name of the item.
		item1.setName("Nạp tiền vào AppStore.vn");
		// Sets the ID. This is any ID that you would like to have associated
		// with the item.
		item1.setID("87239");
		// Sets the total price which should be (quantity * unit price). The
		// total prices of all PayPalInvoiceItem should add up
		// to less than or equal the subtotal of the payment.
		item1.setTotalPrice(new BigDecimal("1.00"));
		// Sets the unit price.
		item1.setUnitPrice(new BigDecimal("1.00"));
		// Sets the quantity.
		item1.setQuantity(1);
		// Add the PayPalInvoiceItem to the PayPalInvoiceData. Alternatively,
		// you can create an ArrayList<PayPalInvoiceItem>
		// and pass it to the PayPalInvoiceData function setInvoiceItems().
		invoice.getInvoiceItems().add(item1);

		// Sets the PayPalPayment invoice data.
		payment.setInvoiceData(invoice);
		// Sets the merchant name. This is the name of your Application or
		// Company.
		payment.setMerchantName("Nạp tiền vào AppStore.vn");
		// Sets the Custom ID. This is any ID that you would like to have
		// associated with the payment.
		payment.setCustomID("8873482296");
		// Sets the Instant Payment Notification url. This url will be hit by
		// the PayPal server upon completion of the payment.
		payment.setIpnUrl("http://www.exampleapp.com/ipn");
		// Sets the memo. This memo will be part of the notification sent by
		// PayPal to the necessary parties.
		payment.setMemo("Nạp tiền vào AppStore.vn");
		return payment;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode != request){
			return;
		}
		btnCheckout.updateButton();
	}

	
}
