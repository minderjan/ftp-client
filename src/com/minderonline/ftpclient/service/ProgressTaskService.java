package com.minderonline.ftpclient.service;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.minderonline.ftpclient.ProgressActivity;
import com.minderonline.ftpclient.R;

import android.R.bool;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class ProgressTaskService extends Service {

	private Timer timer = new Timer();

	private String TAG = "FtpService";
	Notification noti;
	NotificationManager nm;
	int STATUS_BAR_NOTIFICATION = 1;
	int mCount = 0;

	public static final String BROADCAST_ACTION = "com.minderonline.ftpclient.service.ProgressTaskService";
	private final Handler handler = new Handler();
	Intent intent;

	public void onCreate() {

		Log.i(TAG, "Service onCreate..");
		super.onCreate();

		intent = new Intent(BROADCAST_ACTION);

	}

	// @Override
	// public void onStart(Intent intent, int startId) {
	// handler.removeCallbacks(sendUpdatesToUI);
	// handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
	//
	// }

	// private Runnable sendUpdatesToUI = new Runnable() {
	// public void run() {
	// DisplayLoggingInfo();
	// handler.postDelayed(this, 10000); // 10 seconds
	// }
	// };

	// private void DisplayLoggingInfo() {
	// Log.d(TAG, "entered DisplayLoggingInfo");
	//
	// intent.putExtra("time", new Date().toLocaleString());
	// intent.putExtra("counter", "50");
	// sendBroadcast(intent);
	// }

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "Service onStartCommand..");
		ShowNotification();

		startservice();

		return super.onStartCommand(intent, flags, startId);

	}

	private void ShowNotification() {
		// String ns = this.NOTIFICATION_SERVICE;
		// NotificationManager mNotificationManager = (NotificationManager)
		// getSystemService(ns);
		//
		// int icon = R.drawable.ic_launcher_ftp;
		// CharSequence tickerText = "Download  started..";
		// long when = System.currentTimeMillis();
		//
		// Notification notification = new Notification(icon, tickerText, when);
		//
		// Context context = getApplicationContext();
		// CharSequence contentTitle = "My notification";
		// CharSequence contentText = "Hello World!";
		// Intent notificationIntent = new Intent(this, ProgressActivity.class);
		// PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		// notificationIntent, 0);
		//
		// notification.setLatestEventInfo(context, contentTitle, contentText,
		// contentIntent);
		//
		// final int HELLO_ID = 1;
		//
		// mNotificationManager.notify(HELLO_ID, notification);

		nm = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		CharSequence tickerText = "Downloading..";
		long when = System.currentTimeMillis();
		noti = new Notification(R.drawable.ic_launcher_ftp, tickerText, when);

		Intent notiIntent = new Intent(getApplicationContext(),
				ProgressActivity.class);
		PendingIntent pi = PendingIntent.getActivity(getApplicationContext(),
				0, notiIntent, 0);
		noti.flags |= Notification.FLAG_NO_CLEAR;
		CharSequence title = "Downloading initializing...";
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.nofity);
		contentView.setImageViewResource(R.id.status_icon,
				R.drawable.ic_launcher_ftp);
		contentView.setTextViewText(R.id.status_text, title);
		contentView.setProgressBar(R.id.status_progress, 100, 0, false);
		noti.contentView = contentView;
		noti.contentIntent = pi;
		nm.notify(STATUS_BAR_NOTIFICATION, noti);

	}

	private void startservice() {

		timer.scheduleAtFixedRate(new TimerTask() {

			public void run() {
				Log.i(TAG, "Service scheduleAtFixedRate..");
				// Do whatever you want to do every “INTERVAL”

				handler.post(new Runnable() {
					// @Override
					public void run() {
						mCount = mCount + 1;
						if (mCount < 100) {
						

							noti.contentView.setProgressBar(
									R.id.status_progress, 100, mCount % 100,
									false);

							CharSequence title = "Downloading: " + mCount % 100
									+ "%";
							noti.contentView.setTextViewText(R.id.status_text,
									title);
							nm.notify(STATUS_BAR_NOTIFICATION, noti);
							// Toast.makeText(getApplicationContext(),
							// "Service still alive..", Toast.LENGTH_SHORT)
							// .show();

							// Update GUI

							intent.putExtra("counter",
									Integer.toString(mCount % 100));
							sendBroadcast(intent);
						} else {
							noti.contentView.setProgressBar(
									R.id.status_progress, 100, 100, false);
							noti.contentView.setTextViewText(R.id.status_text,
									"Download finished");
							noti.flags |= Notification.FLAG_AUTO_CANCEL;
							nm.notify(STATUS_BAR_NOTIFICATION, noti);
							intent.putExtra("counter", Integer.toString(100));
							sendBroadcast(intent);
							timer.cancel();
						}

					}
				});

			}

		}, 0, 100);

		;
	}

	public void onDestroy() {

		Log.i(TAG, "Stopping..");

		if (timer != null) {

			timer.cancel();

		}

		nm.cancelAll();
		// handler.removeCallbacks(sendUpdatesToUI);

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
