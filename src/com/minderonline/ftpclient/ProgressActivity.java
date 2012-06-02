package com.minderonline.ftpclient;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.minderonline.ftpclient.service.ProgressTaskService;

public class ProgressActivity extends Activity {

	private Intent intent;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.progress_dialog);

		// Fullscreen
		WindowManager wm = getWindowManager();

		int x = wm.getDefaultDisplay().getWidth();
		int y = wm.getDefaultDisplay().getHeight();

		getWindow().setLayout(x - 100, LayoutParams.WRAP_CONTENT);

		// Start Download
		Log.i("FTP", "StartServiceFunction");
		intent = new Intent(this, ProgressTaskService.class);
		//startDownload(null, null);

	}

	private void startDownload(String _Source, String _Destination) {

		// Start the Download Service
		Log.i("FTP", "Start SErvice");

		// startService(new Intent(this, ProgressTaskService.class));

	}

	@Override
	public void onDestroy() {
		Log.i("FTP", "onPuase()");
		//NotificationManager nm = (NotificationManager) this
		//		.getSystemService(Context.NOTIFICATION_SERVICE);

	//	nm.cancelAll();
	//	stopService(new Intent(this, ProgressTaskService.class));
		//unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI(intent);
		}
	};

	@Override
	public void onResume() {
		Log.i("FTP", "onResume");
		super.onResume();
		if (!isServiceRunning()) {
			startService(intent);
			registerReceiver(broadcastReceiver, new IntentFilter(
					ProgressTaskService.BROADCAST_ACTION));
		}
		else
		{
			registerReceiver(broadcastReceiver, new IntentFilter(
					ProgressTaskService.BROADCAST_ACTION));
		}
	}

	private boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (ProgressTaskService.BROADCAST_ACTION.equals(service.service
					.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onPause() {
		Log.i("FTP", "onPuase()");
		super.onPause();
		unregisterReceiver(broadcastReceiver);
		// stopService(intent);
	}

	private void updateUI(Intent intent) {
		
		String counter = intent.getStringExtra("counter");
		
		if(Integer.valueOf(counter) < 100)
		{

		ProgressBar pbBar = (ProgressBar) findViewById(R.id.pb_prog_dialog);
		pbBar.setProgress(Integer.valueOf(counter));

		TextView txtCounter = (TextView) findViewById(R.id.txt_counter_status);
		
		txtCounter.setText(counter + "%");
		}
		else
		{
			ProgressBar pbBar = (ProgressBar) findViewById(R.id.pb_prog_dialog);
			pbBar.setProgress(Integer.valueOf(100));
			TextView txtCounter = (TextView) findViewById(R.id.txt_counter_status);
			
			txtCounter.setText("finished");
			
		}
	}
	
	public void bt_status_ok_click(View v)
	{
		// Hide DialogActivty
		
		this.finish();
		
	}

}
