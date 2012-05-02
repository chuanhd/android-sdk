package com.appota.test;

/**
 * Declare application constants variable
 * Warning: In-App Purchase only
 */
public class Constants {
	
	// enter your clientKey, clientSecret here (the two keys that you receive after register app)
	public static final String CLIENT_KEY = "";	
	public static final String CLIENT_SECRET = "";
	
	/**
	 * Android preference to save AccessToken
	 */
	public static final String REFRESH_TOKEN_PREF = "refresh_token_pref";
	public static final String REFRESH_TOKEN_KEY = "refresh_token";
	public static final String ACCESS_TOKEN_PREF = "access_token_pref";
	public static final String ACCESS_TOKEN_KEY = "access_token";
	
	/**
	 * Define scope for your app
	 * See more at appota api documents
	 */
	public static final String USER_INFO_SCOPE = "user.info";
	public static final String USER_EMAIL_SCOPE = "user.email";
	public static final String USER_COMMENT_SCOPE = "user.comment";
	public static final String USER_CHARGE_SCOPE = "user.charge";
	public static final String USER_PAYMENT_SCOPE = "user.payment";
	public static final String INAPP_SCOPE = "inapp";
	

	/**
	 * Define endpoint APIs:
	 * topup APIs: allow user to purchase TYM 
	 * 
	 */
	public static final String SMS_ENDPOINT = "https://api.appota.com/payment/topup_sms";
	public static final String CHECK_TOPUP_ENDPOINT = "https://api.appota.com/payment/topup";
	public static final String CARD_ENDPOINT = "https://api.appota.com/payment/topup_card";
	public static final String PAYPAL_ENDPOINT = "https://api.appota.com/payment/topup_paypal";
	public static final String BANK_ENDPOINT = "https://api.appota.com/payment/topup_bank";


	/**
	 * In-app purchase endPoint:
	 * Directly purchase cash to your app
	 * 
	 */
	public static final String INAPP_SMS_ENDPOINT = "https://api.appota.com/payment/inapp_sms";
	public static final String INAPP_CARD_ENDPOINT = "https://api.appota.com/payment/inapp_card";
	public static final String INAPP_BANK_ENDPOINT = "https://api.appota.com/payment/inapp_bank";
	public static final String INAPP_PAYPAL_ENDPOINT = "https://api.appota.com/payment/inapp_paypal";
	public static final String CHECK_INAPP_ENDPOINT = "https://api.appota.com/payment/inapp";

}
