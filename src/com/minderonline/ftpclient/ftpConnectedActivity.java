package com.minderonline.ftpclient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Path.FillType;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.minderonline.ftpclient.ftp.ftp_functions;
import com.minderonline.ftpclient.general.ConnectionAdapter;
import com.minderonline.ftpclient.general.FileManagerListAdapter;
import com.minderonline.ftpclient.general.FileOb;
import com.minderonline.ftpclient.general.TabListener;

public class ftpConnectedActivity extends FragmentActivity {

	// Public Variables
	public static String HOST = "";
	public static int PORT = 21;
	public static String USERNAME = "";
	public static String PASSWORD = "";
	public static int FTPMODE = 0;
	public static String FtpWorkingDir = "";

	private static ftpConnectedActivity instance;

	private static final int NUM_ITEMS = 2;
	private MyAdapter mAdapter;
	private ViewPager mPager;
	public ActionBar mActionBar;

	static MenuItem menuItemProgress;

	// Filexplorer things
	public static String ROOTDIR = "/sdcard/";

	public static List<FileOb> LocalFileList = new ArrayList<FileOb>();
	public static List<FileOb> RemoteFileList = new ArrayList<FileOb>();

	public static List<FileOb> LocalFileListTemp = new ArrayList<FileOb>();
	public static List<FileOb> RemoteFileListTemp = new ArrayList<FileOb>();

	// Select Mode
	public static Boolean isSelectingMod = false;

	// History Variables
	public static List<String> HistoryListLocal = new ArrayList<String>();
	public static List<String> HistoryListRemote = new ArrayList<String>();

	public static int CurrentHistoryPositionLocal;
	public static int CurrentHistoryPositionRemote;

	// Ad Things
	private Boolean EnableAds;
	private AdView adView;
	private String MY_AD_UNIT_ID = "a14fb5401aa2c42";

	// Network Monitoring
	private ConnectivityReceiver receiver = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Animation
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		// overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		setContentView(R.layout.ftp_connected);

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			ActionBar actionBar = this.getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);

			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		} else {
			// do something for phones running an SDK before froyo
		}

		// ConnectToFtp;
		initalizeTabs();

		// Register Reciver

		receiver = new ConnectivityReceiver();
		registerReceiver(receiver, new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION));

		// Initalise History
		HistoryListLocal.clear();
		HistoryListLocal.add(ROOTDIR);
		CurrentHistoryPositionLocal = 0;

		HistoryListRemote.clear();
		CurrentHistoryPositionRemote = 0;

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
			LinearLayout layout = (LinearLayout) findViewById(R.id.adplaceholder_connected);
			LinearLayout ln_bottombar = (LinearLayout) findViewById(R.id.ln_bottombar);

			layout.setVisibility(layout.VISIBLE);
			ln_bottombar.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));

			// Add the adView to it
			layout.addView(adView);

			// Initiate a generic request to load it with an ad
			AdRequest adreq = new AdRequest();
			// Add a Test Device
			adreq.addTestDevice("A8FD8C9F9DDEE57E90064D71D57B23AA");

			adView.loadAd(adreq);
		}

	}

	public static Context getContext() {
		return instance;
	}

	private void initalizeTabs() {

		try {

			mActionBar = getActionBar();
			mAdapter = new MyAdapter(getSupportFragmentManager());
			mAdapter.setActionBar(mActionBar);
			mPager = (ViewPager) findViewById(R.id.pager);
			mPager.setAdapter(mAdapter);

			mPager.setOnPageChangeListener(new OnPageChangeListener() {

				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub

				}

				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub

				}

				public void onPageSelected(int arg0) {
					// TODO Auto-generated method stub
					Log.d("ViewPager", "onPageSelected: " + arg0);
					mActionBar.getTabAt(arg0).select();

					if (mActionBar.getSelectedNavigationIndex() == 0) {
						// Local Tab
						if (CurrentHistoryPositionLocal == 0) {
							if (CurrentHistoryPositionRemote != 0) {
								// Hide the Bottombar
								LinearLayout ln = (LinearLayout) findViewById(R.id.ln_bottombar);
								Animation fadeOutAnimation = AnimationUtils
										.loadAnimation(getApplicationContext(),
												R.anim.fadeout);
								ln.setAnimation(fadeOutAnimation);
								if (ln.getVisibility() != ln.GONE) {
									ln.setVisibility(ln.GONE);
								}
							}

						} else {
							if (CurrentHistoryPositionRemote == 0) {

								// Show the Bottombar
								LinearLayout ln = (LinearLayout) findViewById(R.id.ln_bottombar);
								Animation fadeOutAnimation = AnimationUtils
										.loadAnimation(getApplicationContext(),
												R.anim.fadein);
								ln.setAnimation(fadeOutAnimation);
								if (ln.getVisibility() != ln.VISIBLE) {
									ln.setVisibility(ln.VISIBLE);
								}
							}

						}

					}
					if (mActionBar.getSelectedNavigationIndex() == 1) {
						// Remote Tab
						if (CurrentHistoryPositionRemote == 0) {
							if (CurrentHistoryPositionLocal != 0) {

								// Hide the Bottombar
								LinearLayout ln = (LinearLayout) findViewById(R.id.ln_bottombar);
								Animation fadeOutAnimation = AnimationUtils
										.loadAnimation(getApplicationContext(),
												R.anim.fadeout);
								ln.setAnimation(fadeOutAnimation);

								if (ln.getVisibility() != ln.GONE) {
									ln.setVisibility(ln.GONE);
								}
							}

						} else {
							if (CurrentHistoryPositionLocal == 0) {
								// Show the Bottombar
								LinearLayout ln = (LinearLayout) findViewById(R.id.ln_bottombar);
								Animation fadeOutAnimation = AnimationUtils
										.loadAnimation(getApplicationContext(),
												R.anim.fadein);
								ln.setAnimation(fadeOutAnimation);
								if (ln.getVisibility() != ln.VISIBLE) {
									ln.setVisibility(ln.VISIBLE);
								}
							}

						}

					}

				}

			});

			mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			mActionBar.setDisplayShowTitleEnabled(false);
			Tab tab = mActionBar
					.newTab()
					.setText(getString(R.string.ftp_tab_local))
					.setTabListener(
							new TabListener<android.app.Fragment>(this, 0 + "",
									mPager));
			mActionBar.addTab(tab);
			tab = mActionBar
					.newTab()
					.setText(getString(R.string.ftp_tab_remote))
					.setTabListener(
							new TabListener<android.app.Fragment>(this, 1 + "",
									mPager));
			mActionBar.addTab(tab);

			mActionBar.getTabAt(1).select();
		} catch (Exception e) {
			Log.e("ViewPager", e.toString());
		}
	}

	public static class MyAdapter extends FragmentPagerAdapter {
		ActionBar mActionBar;

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return NUM_ITEMS;
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			// mActionBar.getTabAt(position).select();
			Log.v("getItem", "Calling the LoadFragment()");
			return LoadFragment(position);
			// return null;
		}

		public void setActionBar(ActionBar bar) {
			Log.v("setActionBar", "Before");
			mActionBar = bar;
			Log.v("setActionBar", "After");
		}
	}

	public static void HideLoading(Context ctx) {

		Animation fadeOutAnimation = AnimationUtils.loadAnimation(ctx,
				R.anim.fadeout);
		ProgressBar pb = (ProgressBar) menuItemProgress.getActionView()
				.findViewById(R.id.menuItemRefreshBar);
		pb.setAnimation(fadeOutAnimation);
		pb.setVisibility(pb.INVISIBLE);
	}

	private static Fragment LoadFragment(int FrgId) {

		Log.v("LoadFragment", "Fragment Before Loading");

		Fragment returnFragment = null;
		Fragment frg_local = new TabLocalFragment();
		Fragment frg_remote = new TabRemoteFragment();

		switch (FrgId) {
		case 0:

			returnFragment = frg_local;
			break;

		case 1:
			returnFragment = frg_remote;
			break;

		}
		Log.v("LoadFragment", "Fragment After Loading");
		return returnFragment;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
			/**
			 * Intent intent = new Intent(this, connection_overview.class);
			 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * startActivity(intent);
			 **/

			ShowExitDialog();
			return true;

		case R.id.menuItemDownload:

			Animation fadeInAnimation = AnimationUtils.loadAnimation(this,
					R.anim.fadein);
			ProgressBar pb = (ProgressBar) this.menuItemProgress
					.getActionView().findViewById(R.id.menuItemRefreshBar);
			pb.setAnimation(fadeInAnimation);
			pb.setVisibility(pb.VISIBLE);
			Thread trd = new Thread() {
				@Override
				public void run() {
					try {

						// Do some work here
						sleep(2000);
						handler.sendEmptyMessage(1);

					} catch (Exception e) {
					}
				}
			};
			trd.start();

			return true;
		case R.id.menuItemCheckMode:

			if (isSelectingMod) {
				isSelectingMod = false;

				Animation fadeOutAnimation = AnimationUtils.loadAnimation(this,
						R.anim.push_up_out);
				LinearLayout ln = (LinearLayout) findViewById(R.id.ln_bottombar);

				ln.setAnimation(fadeOutAnimation);
				ln.setVisibility(ln.GONE);
			} else {
				isSelectingMod = true;
				Animation fadeOutAnimation = AnimationUtils.loadAnimation(this,
						R.anim.fadein);
				LinearLayout ln = (LinearLayout) findViewById(R.id.ln_bottombar);

				// ln.setAnimation(fadeOutAnimation);
				ln.setVisibility(ln.VISIBLE);

			}

			// Listviews aktualisieren

			TabLocalFragment.adapter.notifyDataSetChanged();
			TabRemoteFragment.adapter.notifyDataSetChanged();

			return true;

		case R.id.menuItemCreateFolder:
			// Inflate the Dialog Custom Layout
			LayoutInflater li = LayoutInflater.from(this);
			View promptsView = li.inflate(R.layout.dialog_input, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			// alertDialogBuilder.setCustomTitle(promptsView);
			// View customTitle =
			// getLayoutInflater().inflate(R.layout.dialog_title, null);
			// alertDialogBuilder.setCustomTitle(customTitle);
			alertDialogBuilder.setView(promptsView);
			final EditText FolderName = (EditText) promptsView
					.findViewById(R.id.edt_dialog_input);

			// set dialog message
			alertDialogBuilder
					.setCancelable(false)
					.setTitle(getString(R.string.men_add_folder))
					.setPositiveButton(getString(R.string.gen_save),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									// Get the Selected Navigation Tab
									if (mActionBar.getSelectedNavigationIndex() == 0) {
										// Local
										String CurrentPath = HistoryListLocal
												.get(CurrentHistoryPositionLocal);
										// create a File object for the parent
										// directory

										File NewFolderDirectory = new File(
												CurrentPath
														+ "/"
														+ FolderName.getText()
																.toString());
										// have the object build the directory
										// structure, if needed.
										NewFolderDirectory.mkdir();

										// Refresh the Listview
										CreateLocalFileList(CurrentPath);
										TabLocalFragment.adapter
												.notifyDataSetChanged();

									}
									if (mActionBar.getSelectedNavigationIndex() == 1) {
										// Remote

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

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show the Alert dialog
			alertDialog.show();

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:

				HideLoading(getApplicationContext());

				break;
			case 2:
				Animation anim = (Animation) AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.fadein);

				ProgressBar pb = (ProgressBar) ftpConnectedActivity.menuItemProgress
						.getActionView().findViewById(R.id.menuItemRefreshBar);
				pb.setAnimation(anim);
				pb.setVisibility(pb.VISIBLE);
				TabRemoteFragment.adapter.notifyDataSetChanged();
				HideLoading(getApplicationContext());
				HistoryListRemote.remove(CurrentHistoryPositionRemote - 1);
				CurrentHistoryPositionRemote = CurrentHistoryPositionRemote - 1;

				break;

			case 3:
				anim = (Animation) AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.fadein);

				pb = (ProgressBar) ftpConnectedActivity.menuItemProgress
						.getActionView().findViewById(R.id.menuItemRefreshBar);
				pb.setAnimation(anim);
				pb.setVisibility(pb.VISIBLE);
				// If remote is the Lastest folder
				TabRemoteFragment.adapter.notifyDataSetChanged();
				HideLoading(getApplicationContext());
				HistoryListRemote.clear();
				CurrentHistoryPositionRemote = 0;

				// Hide the Bottombar
				LinearLayout ln = (LinearLayout) findViewById(R.id.ln_bottombar);
				Animation fadeOutAnimation = AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.fadeout);
				ln.setAnimation(fadeOutAnimation);
				ln.setVisibility(ln.GONE);

				break;
			}
		}
	};

	public static void CreateLocalFileList(String dirPath) {

		LocalFileListTemp.clear();
		File f = new File(dirPath);

		File[] files = f.listFiles();

		for (int i = 0; i < files.length; i++)

		{

			File file = files[i];
			FileOb fl = new FileOb();
			fl.setFilePath(file.getPath());

			if (file.isDirectory()) {

				fl.isDirectory(true);
				fl.setFilename(file.getName());
			} else {
				fl.isDirectory(false);
				fl.setFilename(file.getName());

			}

			LocalFileListTemp.add(fl);
		}

		// Sort the Temp Array and Write it in the Original Array
		// This For schleife is for the Direcotries
		LocalFileList.clear();
		for (int i = 0; i < LocalFileListTemp.size(); i++) {

			if (LocalFileListTemp.get(i).isDirectory()) {
				LocalFileList.add(LocalFileListTemp.get(i));
			}
		}

		// this is for the Files in the Temp list

		for (int i = 0; i < LocalFileListTemp.size(); i++) {

			if (!LocalFileListTemp.get(i).isDirectory()) {
				LocalFileList.add(LocalFileListTemp.get(i));

			}

		}
	}

	/** ON back-Button pressed **/
	@Override
	public void onBackPressed() {
		if (mActionBar.getSelectedNavigationIndex() == 0) {
			if (CurrentHistoryPositionLocal == 0) {
				ShowExitDialog();
			} else {

				if (CurrentHistoryPositionLocal - 1 == 0) {
					// Navigate to SD card
					CreateLocalFileList(ROOTDIR);
					TabLocalFragment.adapter.notifyDataSetChanged();

					// Disable Bottombar and Backbutton
					// ImageButton imb_back = (ImageButton)
					// findViewById(R.id.imb_arrow_left);
					// imb_back.setImageResource(R.drawable.ic_action_arrow_left_disabled);
					// imb_back.setEnabled(false);

					LinearLayout ln = (LinearLayout) findViewById(R.id.ln_bottombar);
					Animation fadeOutAnimation = AnimationUtils.loadAnimation(
							this, R.anim.fadeout);
					ln.setAnimation(fadeOutAnimation);
					ln.setVisibility(ln.GONE);

					// Change Current Position
					CurrentHistoryPositionLocal = CurrentHistoryPositionLocal - 1;

					// Reset the History
					HistoryListLocal.clear();
					HistoryListLocal.add(ROOTDIR);
					CurrentHistoryPositionLocal = 0;

					// Get the Layout in the Scroll view
					LinearLayout ln_bottombar = (LinearLayout) findViewById(R.id.ln_bottombar_scroll);

					// Get the Current Index of this Button
					int childcount = ln_bottombar.getChildCount();

					// Remove the last Unused Buttons

					View bt_child = (Button) ln_bottombar
							.getChildAt(childcount - 1);
					ln_bottombar.removeView(bt_child);

				} else {
					// Navigate One Directory Back
					String NewPath = HistoryListLocal
							.get(CurrentHistoryPositionLocal - 1);
					CreateLocalFileList(NewPath);
					TabLocalFragment.adapter.notifyDataSetChanged();

					// Update the Current Position
					CurrentHistoryPositionLocal = CurrentHistoryPositionLocal - 1;

					// Get the Layout in the Scroll view
					LinearLayout ln_bottombar = (LinearLayout) findViewById(R.id.ln_bottombar_scroll);

					// Get the Current Index of this Button
					int childcount = ln_bottombar.getChildCount();

					// Remove the last Unused Buttons

					View bt_child = (Button) ln_bottombar
							.getChildAt(childcount - 1);
					ln_bottombar.removeView(bt_child);

				}

				// Überprüfen ob ein forward-navigate möglich ist
				if (ftpConnectedActivity.CurrentHistoryPositionLocal >= ftpConnectedActivity.HistoryListLocal
						.size() - 1) {

					// ImageButton imb = (ImageButton)
					// findViewById(R.id.imb_arrow_right);
					// imb.setEnabled(false);
					// imb.setImageResource(R.drawable.ic_action_arrow_right_disabled);

				} else {
					// ImageButton imb = (ImageButton)
					// findViewById(R.id.imb_arrow_right);
					// imb.setEnabled(true);
					// imb.setImageResource(R.drawable.ic_action_arrow_right);
				}

			}
		} else {

			FtpGoUp ftgu = new FtpGoUp();
			ftgu.execute(1);

		}

		return;
	}

	class FtpGoUp extends AsyncTask<Integer, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(Integer... params) {

			Log.d("ftp", "before directory change");
			if (ftp_functions.ftpChangeDirectoryToParent()) {

				Log.d("ftp", "after directory change");

				Log.d("ftp", "before create list");
				CreateRemoteFileList();
				Log.d("ftp", "after create list");

				// Update the Current Position
				CurrentHistoryPositionRemote = CurrentHistoryPositionRemote - 1;

				return true;
			}
			return false;

		}

		@Override
		protected void onPostExecute(Boolean status) {

			if (status) {
				// Show Listview
				LinearLayout ln = (LinearLayout) findViewById(R.id.ln_remote_lv);
				LinearLayout tv = (LinearLayout) findViewById(R.id.ln_remote_error);

				if (ln.getVisibility() != ln.VISIBLE) {
					ln.setVisibility(ln.VISIBLE);
					tv.setVisibility(tv.GONE);
				}

				// When directory is successfully changed
				TabRemoteFragment.adapter.notifyDataSetChanged();
				ProgressBar pb = (ProgressBar) ftpConnectedActivity.menuItemProgress
						.getActionView().findViewById(R.id.menuItemRefreshBar);
				Animation anim = (Animation) AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.fadein);
				pb.setAnimation(anim);
				pb.setVisibility(pb.INVISIBLE);

			} else {

			}

			// super.onPostExecute(response);
		}

		@Override
		protected void onCancelled() {

		}

		@Override
		protected void onPreExecute() {
			// super.onPreExecute();

		}
	}

	/** On Exit Confirmation Dialog **/
	private void ShowExitDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.ftp_connected_diconnect_confirm))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								Thread trd = new Thread() {
									@Override
									public void run() {
										try {

											ftp_functions.ftpDisconnect();
											finish();

										} catch (Exception e) {
										}
									}
								};
								trd.start();

							}
						})
				.setNegativeButton(getString(R.string.no),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void onListItemClick(View v) {

		Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT)
				.show();

	}

	/** Hauptmenü der Activity hinzufügen */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.connected_items, menu);
		this.menuItemProgress = menu.findItem(R.id.menuItemProgress);
		Animation fadeInAnimation = AnimationUtils.loadAnimation(this,
				R.anim.fadein);
		ProgressBar pb = (ProgressBar) this.menuItemProgress.getActionView()
				.findViewById(R.id.menuItemRefreshBar);
		pb.setAnimation(fadeInAnimation);

		return super.onCreateOptionsMenu(menu);
	}

	// Button Functions
	public void imb_bb_back_click(View v) {
		// If tab is on Local
		if (mActionBar.getSelectedNavigationIndex() == 0) {

			if (CurrentHistoryPositionLocal - 1 == 0) {
				// Navigate to SD card
				CreateLocalFileList(ROOTDIR);
				TabLocalFragment.adapter.notifyDataSetChanged();

				// Disable Bottombar and Backbutton
				// ImageButton imb_back = (ImageButton)
				// findViewById(R.id.imb_arrow_left);
				// imb_back.setImageResource(R.drawable.ic_action_arrow_left_disabled);
				// imb_back.setEnabled(false);

				LinearLayout ln = (LinearLayout) findViewById(R.id.ln_bottombar);
				Animation fadeOutAnimation = AnimationUtils.loadAnimation(this,
						R.anim.fadeout);
				ln.setAnimation(fadeOutAnimation);
				ln.setVisibility(ln.GONE);

				// Change Current Position
				CurrentHistoryPositionLocal = CurrentHistoryPositionLocal - 1;

				// Reset the History
				HistoryListLocal.clear();
				HistoryListLocal.add(ROOTDIR);
				CurrentHistoryPositionLocal = 0;

			} else {
				// Navigate One Directory Back
				String NewPath = HistoryListLocal
						.get(CurrentHistoryPositionLocal - 1);
				CreateLocalFileList(NewPath);
				TabLocalFragment.adapter.notifyDataSetChanged();

				// Update the Current Position
				CurrentHistoryPositionLocal = CurrentHistoryPositionLocal - 1;

			}

			// Überprüfen ob ein forward-navigate möglich ist
			if (ftpConnectedActivity.CurrentHistoryPositionLocal >= ftpConnectedActivity.HistoryListLocal
					.size() - 1) {

				// ImageButton imb = (ImageButton)
				// findViewById(R.id.imb_arrow_right);
				// imb.setEnabled(false);
				// imb.setImageResource(R.drawable.ic_action_arrow_right_disabled);

			} else {
				// ImageButton imb = (ImageButton)
				// findViewById(R.id.imb_arrow_right);
				// imb.setEnabled(true);
				// imb.setImageResource(R.drawable.ic_action_arrow_right);
			}

		}
		// if tab is on Remote
		if (mActionBar.getSelectedNavigationIndex() == 1) {

			if (CurrentHistoryPositionRemote >= 1) {

				Thread trd = new Thread() {
					@Override
					public void run() {
						try {

							// ftp_functions.ftpChangeDirectory("test");
							Log.d("ftp", "before directory change");
							if (ftp_functions.ftpChangeDirectoryToParent()) {
								Log.d("ftp", "after directory change");
								Log.d("ftp", "before create list");
								CreateRemoteFileList();
								Log.d("ftp", "after create list");

								if (ftpConnectedActivity.CurrentHistoryPositionRemote == 1) {
									handler.sendEmptyMessage(3);
								} else {
									handler.sendEmptyMessage(2);
								}

							} else {

							}

						} catch (Exception e) {
						}
					}
				};
				trd.start();
			}

		}

	}

	public void imb_bb_home_click(View v) {

		if (mActionBar.getSelectedNavigationIndex() == 0) {

			// Navigate to SD card
			CreateLocalFileList(ROOTDIR);
			TabLocalFragment.adapter.notifyDataSetChanged();

			// Disable Bottombar and Backbutton
			// ImageButton imb_back = (ImageButton)
			// findViewById(R.id.imb_arrow_left);
			// imb_back.setImageResource(R.drawable.ic_action_arrow_left_disabled);
			// imb_back.setEnabled(false);

			// Remove all Buttons in the Fucking Bottombar

			// Get the Layout in the Scroll view
			LinearLayout ln_bottombar = (LinearLayout) this
					.findViewById(R.id.ln_bottombar_scroll);

			ln_bottombar.removeAllViews();

			LinearLayout ln = (LinearLayout) findViewById(R.id.ln_bottombar);
			Animation fadeOutAnimation = AnimationUtils.loadAnimation(this,
					R.anim.fadeout);
			ln.setAnimation(fadeOutAnimation);
			ln.setVisibility(ln.GONE);

			// Disable Bottombar
			// LinearLayout ln = (LinearLayout) findViewById(R.id.ln_bottombar);
			// Animation fadeOutAnimation = AnimationUtils.loadAnimation(this,
			// R.anim.fadeout);
			// ln.setAnimation(fadeOutAnimation);
			// ln.setVisibility(ln.GONE);

			// Change Current Position
			CurrentHistoryPositionLocal = CurrentHistoryPositionLocal - 1;

			// Reset the History
			HistoryListLocal.clear();
			HistoryListLocal.add(ROOTDIR);
			CurrentHistoryPositionLocal = 0;

		}
		if (mActionBar.getSelectedNavigationIndex() == 1) {
			// Tab Remote

			Thread trd = new Thread() {
				@Override
				public void run() {
					try {

						// ftp_functions.ftpChangeDirectory("test");
						Log.d("ftp", "before directory change");
						if (ftp_functions.ftpChangeDirectory("/")) {
							Log.d("ftp", "after directory change");
							Log.d("ftp", "before create list");
							CreateRemoteFileList();
							Log.d("ftp", "after create list");
							handler.sendEmptyMessage(3);

						} else {

						}

					} catch (Exception e) {
					}
				}
			};
			trd.start();

		}

	}

	public void imb_bb_forward_click(View v) {
		if (mActionBar.getSelectedNavigationIndex() == 0) {

			// Check if Index is aviable
			if (CurrentHistoryPositionLocal < HistoryListLocal.size() - 1) {

				// Change Directory
				String NewPath = HistoryListLocal
						.get(CurrentHistoryPositionLocal + 1);
				CreateLocalFileList(NewPath);
				TabLocalFragment.adapter.notifyDataSetChanged();

				// Change current Position
				CurrentHistoryPositionLocal = CurrentHistoryPositionLocal + 1;

			}

		}
	}

	private Boolean CreateRemoteFileList() {

		try {

			FragmentListRemote.items = ftp_functions.RemoteFiles();

			ftpConnectedActivity.RemoteFileListTemp.clear();

			if (FragmentListRemote.items != null) {

				if (FragmentListRemote.items.size() > 0) {

					for (int i = 0; i < FragmentListRemote.items.size(); i++) {
						FileOb fl = new FileOb(FragmentListRemote.items.get(i),
								"/" + FragmentListRemote.items.get(i), false,
								true);
						if (FragmentListRemote.items.get(i).contains("/")) {
							String originalName = FragmentListRemote.items
									.get(i);
							String tempString = originalName.substring(0,
									originalName.length() - 1);
							fl.setFilename(tempString);

							fl.isDirectory(true);
						} else {

							fl.isDirectory(false);
						}

						ftpConnectedActivity.RemoteFileListTemp.add(fl);

					}
					// Sort the Temp Array and Write it in the Original Array
					// This For schleife is for the Direcotries
					ftpConnectedActivity.RemoteFileList.clear();
					for (int i = 0; i < ftpConnectedActivity.RemoteFileListTemp
							.size(); i++) {

						if (ftpConnectedActivity.RemoteFileListTemp.get(i)
								.isDirectory()) {
							ftpConnectedActivity.RemoteFileList
									.add(ftpConnectedActivity.RemoteFileListTemp
											.get(i));

						}

					}

					// this is for the Files in the Temp list

					for (int i = 0; i < ftpConnectedActivity.RemoteFileListTemp
							.size(); i++) {

						if (!ftpConnectedActivity.RemoteFileListTemp.get(i)
								.isDirectory()) {
							ftpConnectedActivity.RemoteFileList
									.add(ftpConnectedActivity.RemoteFileListTemp
											.get(i));

						}

					}
				}
			}
		} catch (Exception e) {
			Log.e("FTP", "Error: " + e.getMessage());
			return false;
		}
		return true;

	}

	private void onNetworkChange(Boolean _connected) {

		TextView txt = (TextView) findViewById(R.id.txt_network_info);
		LinearLayout ln_nt_info = (LinearLayout) findViewById(R.id.ln_connection_info);

		if (_connected) {
			if (ln_nt_info.getVisibility() != ln_nt_info.GONE) {

				ln_nt_info.setVisibility(ln_nt_info.GONE);
			}

		} else {
			if (ln_nt_info.getVisibility() != ln_nt_info.VISIBLE) {

				LinearLayout ln_bottombar = (LinearLayout) findViewById(R.id.ln_bottombar);
				ln_bottombar.setVisibility(ln_bottombar.VISIBLE);

				txt.setText("Network connection is interrupted..");
				Animation fadeInAnimation = AnimationUtils.loadAnimation(
						getApplicationContext(), R.anim.fadein);
				ln_nt_info.setAnimation(fadeInAnimation);
				ln_nt_info.setVisibility(ln_nt_info.VISIBLE);

			}

		}

	}

	private Boolean getNetworkStateString(NetworkInfo.State state) {
		String stateString = "Unknown";
		Boolean IsConnected = false;

		switch (state) {
		case CONNECTED:
			stateString = "Connected";
			IsConnected = true;
			break;
		case CONNECTING:
			stateString = "Connecting";
			IsConnected = false;
			break;
		case DISCONNECTED:
			stateString = "Disconnected";
			IsConnected = false;
			break;
		case DISCONNECTING:
			stateString = "Disconnecting";
			IsConnected = false;
			break;
		case SUSPENDED:
			stateString = "Suspended";
			IsConnected = true;
			break;
		default:
			stateString = "Unknown";
			break;
		}

		return IsConnected;
	}

	private class ConnectivityReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			NetworkInfo info = intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

			if (null != info) {
				// Boolean state = getNetworkStateString(info.getState());
				// String stateString = info.toString().replace(',', '\n');

				Boolean IsConnected = getNetworkStateString(info.getState());

				// String infoString = String.format(
				// "Network Type: %s\nNetwork State: %s\n\n%s",
				// info.getTypeName(), state, stateString);

				// Log.i("ConnTest", info.getTypeName());
				// Log.i("ConnTest", state);
				// Log.i("ConnTest", info.toString());

				onNetworkChange(IsConnected);

			}
		}
	}

	@Override
	public void onDestroy() {
		// if (EnableAds) {
		// adView.destroy();
		// }
		unregisterReceiver(receiver);
		super.onDestroy();
	}

}
