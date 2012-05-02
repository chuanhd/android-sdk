package com.appota.test;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.appota.model.TopupChecker;
import com.appota.payment.AppotaClient;

/**
 * Service to check topup transaction
 * */

public class CheckTopupService extends Service {
	
	private String topUpId;
	private CheckThread thread;
	private volatile boolean isSuccess = false;
	TopupChecker topup;
	private AppotaClient client;
	private String endPoint;
	private String accessToken;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class CheckThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int count = 0;
			//checking timeout: 10 times
			while(!isSuccess && count < 10){
				try {
					//checking after every 5 seconds
					Thread.sleep(5000);
					count++;
					topup = client.checkTopup(topUpId, endPoint, accessToken);
					isSuccess = topup.isSuccess();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			/**
			 * after checking, fire a broadcast message*/
			Intent i = new Intent();
			i.setAction("topup.true");
			Bundle b = new Bundle();
			b.putSerializable("result", topup);
			i.putExtras(b);
			sendBroadcast(i);
			stopSelf();
		}
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		topUpId = intent.getStringExtra("TOPUP_ID");
		accessToken = intent.getStringExtra("ACCESS_TOKEN_STR");
		thread = new CheckThread();
		thread.start();
		return super.onStartCommand(intent, flags, startId);
	}



	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		client = new AppotaClient();
		endPoint = Constants.CHECK_TOPUP_ENDPOINT;
	}

}
