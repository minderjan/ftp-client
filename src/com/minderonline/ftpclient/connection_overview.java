package com.minderonline.ftpclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.minderonline.ftpclient.R;
import com.minderonline.ftpclient.db.DBAdapter;
import com.minderonline.ftpclient.db.ConnectionDBAdapter;
import com.minderonline.ftpclient.ftp.ftp_functions;
import com.minderonline.ftpclient.general.ConnectionAdapter;
import com.minderonline.ftpclient.general.FtpAnswer;
import com.minderonline.ftpclient.preferences.MainPreferencesActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class connection_overview extends Activity {

	public SharedPreferences prefs = null;
	Thread tr_connecting;
	Context thisApp = this;
	public static Boolean CancelThread = false;
	String FtpReply[] = null;

	// Ad Things
	private Boolean EnableAds;
	private AdView adView;
	private String MY_AD_UNIT_ID = "a14fb5401aa2c42";

	private ProgressDialog progdialog;

	/** Database Variabels **/
	final DBAdapter db = new DBAdapter(this);
	final ConnectionDBAdapter dbcon = new ConnectionDBAdapter(this);

	// Ftp Answer
	ConnectAsyncTask ctctask;
	FtpAnswer fa = new FtpAnswer();
	
	// Ftp Disconnecting 
	public static Boolean FtpDisconnect = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.connections_overview);

		registerForContextMenu(findViewById(R.id.lv_conn_list));

		
		
		// First Boot of Application
		if (isFirstBoot()) {

			// Toast tst = Toast.makeText(getApplicationContext(), "First Boot",
			// Toast.LENGTH_SHORT);
			// tst.show();
			db.open();
			dbcon.open();
			dbcon.close();
			db.close();
			prefs = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			Editor edit = prefs.edit();
			edit.putBoolean("IsFirstBoot", false);
			edit.commit();
		}

		// Fills the Listview
		fillConnectionList();

		// Check the Setting Variables

		// get data from settings activity in this case the language
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);

		// Getting the Enable Ads Variable
		EnableAds = settings.getBoolean("pref_key_enable_ads", true);

		if (EnableAds) {
			// Ad Things
			// Create the adView
			adView = new AdView(this, AdSize.BANNER, MY_AD_UNIT_ID);
			// Lookup your LinearLayout assuming it’s been given
			// the attribute android:id="@+id/mainLayout"
			LinearLayout layout = (LinearLayout) findViewById(R.id.adplaceholder);

			// Add the adView to it
			layout.addView(adView);

			// Initiate a generic request to load it with an ad
			AdRequest adreq = new AdRequest();
			// Add a Test Device
			adreq.addTestDevice("A8FD8C9F9DDEE57E90064D71D57B23AA");

			adView.loadAd(adreq);

		}

	}

	/** __________Private Functions______ **/

	private Boolean isFirstBoot() {
		Boolean isFirstBoot = true;

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		isFirstBoot = prefs.getBoolean("IsFirstBoot", true);

		return isFirstBoot;
	}

	/** Function to Fill the Listview and the Onclick Listener **/
	private void fillConnectionList() {

		dbcon.open();

		Cursor AllConnections = dbcon.getAllConnections();
		Log.i("DB Collumns", Integer.toString(AllConnections.getColumnCount()));
		Log.i("DB count", Integer.toString(AllConnections.getCount()));

		ArrayList<String> titleArray = new ArrayList<String>();
		ArrayList<String> hostArray = new ArrayList<String>();

		if (AllConnections.moveToFirst()) {
			for (int i = 0; i < AllConnections.getCount(); i++) {
				titleArray.add(AllConnections.getString(1));
				hostArray.add(AllConnections.getString(3));
				Log.i("DB Host", AllConnections.getString(3));
				AllConnections.moveToNext();
			}
		}

		AllConnections.close();

		Log.i("Array Count", Integer.toString(titleArray.size()));

		dbcon.close();

		final ListView list = (ListView) findViewById(R.id.lv_conn_list);
		LinearLayout lv_empty = (LinearLayout) findViewById(R.id.lv_conn_empty);
		list.setEmptyView(lv_empty);
		ConnectionAdapter adapter = new ConnectionAdapter(this, titleArray,
				hostArray);
		list.setAdapter(adapter);
		// Set onclick function on items
		list.setClickable(true);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int Position, long arg3) {
				// TODO Auto-generated method stub

				Object o = list.getItemAtPosition(Position);

				// Edit Record from DataBase
				dbcon.open();

				Cursor AllConnections = dbcon.getAllConnections();

				ArrayList<Integer> ConidArray = new ArrayList<Integer>();

				if (AllConnections.moveToFirst()) {
					for (int i = 0; i < AllConnections.getCount(); i++) {
						ConidArray.add(AllConnections.getInt(0));

						AllConnections.moveToNext();
					}
				}

				AllConnections.close();

				int CurrentConnId = ConidArray.get(Position);

				Cursor OneConnection = dbcon.getConnection(CurrentConnId);
				ftpConnectedActivity.HOST = OneConnection.getString(3);
				ftpConnectedActivity.PORT = Integer.parseInt(OneConnection
						.getString(4));
				ftpConnectedActivity.FTPMODE = Integer.parseInt(OneConnection
						.getString(2));
				ftpConnectedActivity.USERNAME = OneConnection.getString(5);
				ftpConnectedActivity.PASSWORD = OneConnection.getString(6);
				dbcon.close();

//				createCancelProgressDialog(getString(R.string.ftp_connecting),
//						ftpConnectedActivity.HOST, "Abbrechen");

				// Starting the Asynctask
				ctctask = new ConnectAsyncTask();
				ctctask.execute(1);

				// New Thread to Connect with the FTP Server

				// tr_connecting = new Thread(new Runnable() {
				// public void run() {
				//
				// fa = ftp_functions.ftpConnect(
				// ftpConnectedActivity.HOST,
				// ftpConnectedActivity.USERNAME,
				// ftpConnectedActivity.PASSWORD,
				// ftpConnectedActivity.PORT);
				//
				// Log.i("FTP", "After the Function is Called");
				//
				// // Network Error
				//
				// if (fa.getIsConnected()) {
				// // Login Error
				// if (fa.getIsLogin()) {
				// Log.i("FTP", "Send 1 to handler");
				// handler.sendEmptyMessage(1);
				// } else {
				// handler.sendEmptyMessage(3);
				// }
				// } else {
				// handler.sendEmptyMessage(2);
				// }
				//
				// }
				// });

			}

		});
	}

	private void createCancelProgressDialog(String title, String message,
			String buttonText) {

		progdialog = new ProgressDialog(this);
		progdialog.setTitle(title);
		progdialog.setMessage(message);
		progdialog.setButton("Abbrechen",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						// Verbindung Abbrechen
						ctctask.cancel(true);

					}
				});

		progdialog.show();
	}

	public class ConnectAsyncTask extends AsyncTask<Object, Boolean, FtpAnswer> {

		@Override
		protected FtpAnswer doInBackground(Object... params) {
			if (!isCancelled()) {
				FtpAnswer ftan = ftp_functions.ftpConnect(
						ftpConnectedActivity.HOST,
						ftpConnectedActivity.USERNAME,
						ftpConnectedActivity.PASSWORD,
						ftpConnectedActivity.PORT);

				return ftan;
			} else {
				return null;
			}

		}

		@Override
		protected void onPostExecute(FtpAnswer Response) {
			try {

				if (!isCancelled()) {

					// Handel the Response
					if (Response.getIsConnected()) {
						if (Response.getIsLogin()) {

							progdialog.dismiss();
							Log.i("FTP", "Start Intent");

							Intent intent = new Intent(
									connection_overview.this,
									ftpConnectedActivity.class);
							startActivity(intent);

						} else {
							progdialog.dismiss();

							ShowErrorMessage(
									"Login Fehler: " + Response.getLoginCode(),
									Response.getLoginError());
						}
					} else {
						progdialog.dismiss();

						ShowErrorMessage("Verbindung Fehlgeschlagen",
								"Die Verbinung zu\n'ftp://"
										+ ftpConnectedActivity.HOST + ":"
										+ ftpConnectedActivity.PORT
										+ "'\nkonnte nicht hergestellt werden.");
					}
				}
			} catch (Exception e) {
				Log.d("Error: ", " " + e.getMessage());
			}

			// super.onPostExecute(response);
		}

		@Override
		protected void onCancelled() {

		}

		@Override
		protected void onPreExecute() {
			// super.onPreExecute();
			 createCancelProgressDialog(getString(R.string.ftp_connecting),
						ftpConnectedActivity.HOST, "Abbrechen");

		}

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:

				progdialog.dismiss();
				Log.i("FTP", "Start Intent");

				Intent intent = new Intent(connection_overview.this,
						ftpConnectedActivity.class);
				startActivity(intent);

				break;
			case 2:

				progdialog.dismiss();

				ShowErrorMessage("Verbindung Fehlgeschlagen",
						"Die Verbinung zu\n'ftp://" + ftpConnectedActivity.HOST
								+ ":" + ftpConnectedActivity.PORT
								+ "'\nkonnte nicht hergestellt werden.");

				break;
			case 3:

				progdialog.dismiss();

				ShowErrorMessage("Login Fehler: " + fa.getLoginCode(),
						fa.getLoginError());

				break;
			}
		}
	};

	private void ShowErrorMessage(String _Title, String _Message) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(_Title);
		alertDialog.setMessage(_Message);

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				// here you can add functions

			}
		});

		// alertDialog.setIcon(R.drawable.ic_launcher_ftp);
		alertDialog.show();

	}

	@Override
	public void onDestroy() {
		if (EnableAds) {
			adView.destroy();
		}

		super.onDestroy();
	}

	/** Show the Editing Dialog **/
	private void ShowAlertDialog(final Boolean IsUpdate,
			final Cursor OneConnection) {

		// Inflate the Dialog Custom Layout
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.add_connection_dialog, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// alertDialogBuilder.setCustomTitle(promptsView);
		// View customTitle = getLayoutInflater().inflate(R.layout.dialog_title,
		// null);
		// alertDialogBuilder.setCustomTitle(customTitle);
		alertDialogBuilder.setView(promptsView);

		// Get the Elements of the Custom Layout
		// Connection Name
		final EditText connectionname = (EditText) promptsView
				.findViewById(R.id.edt_add_conn_conn_name);

		// Port
		final EditText port = (EditText) promptsView
				.findViewById(R.id.edt_add_conn_port);

		// Host
		final EditText host = (EditText) promptsView
				.findViewById(R.id.edt_add_conn_host);

		// Username
		final EditText username = (EditText) promptsView
				.findViewById(R.id.edt_add_conn_username);

		// Password
		final EditText password = (EditText) promptsView
				.findViewById(R.id.edt_add_conn_password);

		// FTP-Mode
		final Spinner ftp_mode = (Spinner) promptsView
				.findViewById(R.id.sp_add_conn_ftp_mode);

		// Fill Spinner
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, new String[] { "FTP",
						"FTPS (Sercure - FTP)" });
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner spinner = (Spinner) promptsView
				.findViewById(R.id.sp_add_conn_ftp_mode);

		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				int CurrentFtpModePos = spinner.getSelectedItemPosition();
				if (CurrentFtpModePos == 0) {

					port.setText("21");
					port.setHint("21");
				}
				if (CurrentFtpModePos == 1) {

					port.setText("990");
					port.setHint("990");
				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		}

		);

		// Update Method
		if (IsUpdate) {
			// spinner.setSelected(false);
			// spinner.setSelection(OneConnection.getInt(2));

			spinner.setSelection(OneConnection.getInt(2), true);

			connectionname.setText(OneConnection.getString(1));
			host.setText(OneConnection.getString(3));

			username.setText(OneConnection.getString(5));
			password.setText(OneConnection.getString(6));

			port.setText(String.valueOf(OneConnection.getInt(4)));
			port.setHint(String.valueOf(OneConnection.getInt(4)));

		} else {
			port.setText(String.valueOf(21));

		}

		if (IsUpdate) {
			// set dialog message
			alertDialogBuilder
					.setCancelable(false)

					.setTitle(getString(R.string.men_edt_con))
					.setPositiveButton(getString(R.string.gen_save),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Check if all the requiered fields are
									// filled

									if (port.getText().toString().equals("")
											|| host.getText().toString()
													.equals("")
											|| connectionname.getText()
													.toString().equals("")) {

										Toast.makeText(
												thisApp,
												getString(R.string.add_conn_error_msg),
												Toast.LENGTH_LONG
														+ Toast.LENGTH_SHORT)
												.show();

									} else {
										// Save the Items to the Database

										String UserName = username.getText()
												.toString();
										if (UserName.equals("")) {
											UserName = "Anonymous";
										}
										dbcon.open();

										dbcon.updateConnection(OneConnection
												.getLong(0), connectionname
												.getText().toString(), spinner
												.getSelectedItemPosition(),
												Integer.parseInt(port.getText()
														.toString()), host
														.getText().toString(),
												UserName, password.getText()
														.toString());
										dbcon.close();
										fillConnectionList();
									}

								}
							})
					.setNegativeButton(getString(R.string.gen_cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});

		} else {
			// set dialog message
			alertDialogBuilder
					.setCancelable(false)
					.setTitle(getString(R.string.men_add_con))
					.setPositiveButton(getString(R.string.gen_save),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									if (port.getText().toString().equals("")
											|| host.getText().toString()
													.equals("")
											|| connectionname.getText()
													.toString().equals("")) {
										Toast.makeText(
												thisApp,
												getString(R.string.add_conn_error_msg),
												Toast.LENGTH_LONG
														+ Toast.LENGTH_SHORT)
												.show();
									} else {

										// Save the Items to the Database
										String UserName = username.getText()
												.toString();
										if (UserName.equals("")) {
											UserName = "Anonymous";
										}
										dbcon.open();

										dbcon.insertConnection(connectionname
												.getText().toString(), ftp_mode
												.getSelectedItemPosition(),
												host.getText().toString(),
												Integer.parseInt(port.getText()
														.toString()), UserName,
												password.getText().toString());

										dbcon.close();

										fillConnectionList();
									}

								}
							})
					.setNegativeButton(getString(R.string.gen_cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
		}

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show the Alert dialog
		alertDialog.show();

	}

	/** __________System-Functions____________-__ **/

	/** Erstellt ein Kontextmenu für jedes Element in der Listview **/
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.lv_conn_list) {

			menu.setHeaderTitle(getString(R.string.conn_ctx_menu_title));
			menu.add(Menu.NONE, 123, Menu.NONE,
					getString(R.string.conn_ctx_menu_edit));
			menu.add(Menu.NONE, 124, Menu.NONE,
					getString(R.string.conn_ctx_menu_remove));
		}

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/** Kontextmnenu item selected **/
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();
		int Position = info.position;
		Cursor AllConnections;
		int CurrentConnId;
		switch (menuItemIndex) {

		case 123:

			// Edit Record from DataBase
			dbcon.open();

			AllConnections = dbcon.getAllConnections();

			ArrayList<Integer> ConidArray = new ArrayList<Integer>();

			if (AllConnections.moveToFirst()) {
				for (int i = 0; i < AllConnections.getCount(); i++) {
					ConidArray.add(AllConnections.getInt(0));

					AllConnections.moveToNext();
				}
			}

			AllConnections.close();

			CurrentConnId = ConidArray.get(Position);

			Cursor OneConnection = dbcon.getConnection(CurrentConnId);

			ShowAlertDialog(true, OneConnection);

			dbcon.close();

			break;
		case 124:

			// Delete Record from DataBase
			dbcon.open();

			AllConnections = dbcon.getAllConnections();

			ArrayList<Integer> idArray = new ArrayList<Integer>();

			if (AllConnections.moveToFirst()) {
				for (int i = 0; i < AllConnections.getCount(); i++) {
					idArray.add(AllConnections.getInt(0));

					AllConnections.moveToNext();
				}
			}

			AllConnections.close();

			CurrentConnId = idArray.get(Position);

			dbcon.deleteConnection(CurrentConnId);

			dbcon.close();
			fillConnectionList();

			break;
		}

		return true;
	}

	/** Hauptmenü der Activity hinzufügen */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.connection_overview, menu);

		// MenuItem item = menu.add(getString(R.id.men_add_con));
		// item.setIcon(R.drawable.ic_action_add);

		return super.onCreateOptionsMenu(menu);
	}

	/** Hauptmenü - change Wlan state **/
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (isWlanEnabled(getApplicationContext())) {
			menu.findItem(R.id.men_wlan).setTitle(
					getString(R.string.men_wlan_disable));
		} else {
			menu.findItem(R.id.men_wlan).setTitle(
					getString(R.string.men_wlan_enable));
		}

		return true;
	}

	/** Hauptmenü on Item Selected */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.men_add_con:

			ShowAlertDialog(false, null);

			return true;
		case R.id.men_wlan:

			WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

			if (wifi.isWifiEnabled()) {
				wifi.setWifiEnabled(false);
				Toast.makeText(getBaseContext(),
						getString(R.string.tst_wlan_disabled),
						Toast.LENGTH_SHORT).show();

			} else {
				wifi.setWifiEnabled(true);
				Toast.makeText(getBaseContext(),
						getString(R.string.tst_wlan_enabeld),
						Toast.LENGTH_SHORT).show();

			}
			return true;
		case R.id.men_settings:

			Intent i = new Intent(connection_overview.this,
					MainPreferencesActivity.class);
			startActivity(i);

			return true;

		default:
			return super.onOptionsItemSelected(item);

		}
	}

	/** Check if Wifi is enabled and connected **/
	private static boolean isWlanEnabled(Context ctx) {
		Boolean isEnabled = false;
		WifiManager wifimanager = (WifiManager) ctx
				.getSystemService(Context.WIFI_SERVICE);
		if (wifimanager.isWifiEnabled()) {
			isEnabled = true;
		} else {
			isEnabled = false;
		}
		return isEnabled;

	}

}