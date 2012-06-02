package com.minderonline.ftpclient;

import java.util.ArrayList;
import java.util.Date;

import com.minderonline.ftpclient.ftp.ftp_functions;
import com.minderonline.ftpclient.general.FileManagerListAdapter;
import com.minderonline.ftpclient.general.FileOb;
import com.minderonline.ftpclient.general.FtpAnswer;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TabRemoteFragment extends FragmentListRemote {

	public Activity TabRemoteFragmentActivity = getActivity();
	public static FileManagerListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.frag_remote, container, false);

		return v;
	}

	public static void reload(View v) {

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// final ListView list = (ListView) findViewById(android.R.id.list);
		ArrayList<String> titleArray = new ArrayList<String>();

		// getDir(root);

		new Thread(new Runnable() {
			public void run() {

				Log.i("FTP-TabRemote", "onActivity CreateRemoteFileList start");

				CreateRemoteFileList();
				handler.sendEmptyMessage(1);

			}
		}).start();

	}

	private Boolean CreateRemoteFileList() {

		items = ftp_functions.RemoteFiles();

		Log.d("RFL", "After loading Remote Files");

		if (items != null) {

			Log.i("FTP-TabRemote", Integer.toString(items.size()));

			if (items.size() > 0) {

				ftpConnectedActivity.RemoteFileListTemp.clear();

				for (int i = 0; i < items.size(); i++) {
					FileOb fl = new FileOb(items.get(i), "/" + items.get(i),
							false, true);
					if (items.get(i).contains("/")) {
						String originalName = items.get(i);
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
			} else {
				return false;
			}
		} else {
			Log.i("FTP", "Folder is Empty..");
			return false;

			// ftpConnectedActivity.RemoteFileList.clear();
		}
		return true;

	}

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

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:

				adapter = new FileManagerListAdapter(getActivity(),
						ftpConnectedActivity.RemoteFileList);

				setListAdapter(adapter);

				ftpConnectedActivity.HideLoading(getActivity()
						.getApplicationContext());

				break;

			case 2:
				// When directory is successfully changed
				adapter.notifyDataSetChanged();
				ProgressBar pb = (ProgressBar) ftpConnectedActivity.menuItemProgress
						.getActionView().findViewById(R.id.menuItemRefreshBar);
				Animation anim = (Animation) AnimationUtils.loadAnimation(
						getActivity().getApplicationContext(), R.anim.fadein);
				pb.setAnimation(anim);
				pb.setVisibility(pb.INVISIBLE);

				break;

			}
		}
	};

	@Override
	public void onListItemClick(ListView l, View v, final int position, long id) {

		if (ftpConnectedActivity.RemoteFileList.get(position).isDirectory()) {

			// Bottombar einblenden damit der User
			// navigieren
			// kann
			LinearLayout ln = (LinearLayout) getActivity().findViewById(
					R.id.ln_bottombar);

			if (ln.getVisibility() != ln.VISIBLE) {
				ln.setVisibility(ln.VISIBLE);
			}

			// Back arrow aktiviern
			if (ftpConnectedActivity.CurrentHistoryPositionRemote == 0) {

				// ImageButton imb = (ImageButton) getActivity()
				// .findViewById(R.id.imb_arrow_left);
				// imb.setEnabled(true);
				// imb.setImageResource(R.drawable.ic_action_arrow_left);
			}

			Animation anim = (Animation) AnimationUtils.loadAnimation(
					getActivity().getApplicationContext(), R.anim.fadein);

			ProgressBar pb = (ProgressBar) ftpConnectedActivity.menuItemProgress
					.getActionView().findViewById(R.id.menuItemRefreshBar);
			pb.setAnimation(anim);
			pb.setVisibility(pb.VISIBLE);

			Thread trd = new Thread() {
				@Override
				public void run() {
					try {

						// ftp_functions.ftpChangeDirectory("test");
						Log.d("ftp", "before directory change");
						if (ftp_functions
								.ftpChangeDirectory(ftpConnectedActivity.RemoteFileList
										.get(position).getFileName())) {
							Log.d("ftp", "after directory change");
							Log.d("ftp", "before create list");
							CreateRemoteFileList();
							Log.d("ftp", "after create list");

							// Add the New Path to the History and change the
							// Current Index
							ftpConnectedActivity.HistoryListRemote
									.add(ftpConnectedActivity.RemoteFileList
											.get(position).getFilePath());

							ftpConnectedActivity.CurrentHistoryPositionRemote = ftpConnectedActivity.HistoryListRemote
									.size();

							handler.sendEmptyMessage(2);

						} else {

						}

						// handler.sendEmptyMessage(2);

					} catch (Exception e) {
					}
				}
			};
			// trd.start();

			// Start the AsyncTask
			ChangeDirectoryTask cht = new ChangeDirectoryTask();
			// Send Position
			cht.execute(position);

		} else {

			Intent i = new Intent();
			i.setClass(l.getContext(), ProgressActivity.class);
//			i.putExtra("time", "");
//			i.putExtra("counter", "");
			startActivity(i);

		}

	}

	class ChangeDirectoryTask extends AsyncTask<Integer, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(Integer... params) {

			Log.d("ftp", "before directory change");
			if (ftp_functions
					.ftpChangeDirectory(ftpConnectedActivity.RemoteFileList
							.get(params[0]).getFileName())) {

				Log.d("ftp", "after directory change");

				Log.d("ftp", "before create list");
				Boolean status = CreateRemoteFileList();

				Log.d("ftp", "after create list");

				// Add the New Path to the History and change the
				// Current Index
				ftpConnectedActivity.HistoryListRemote
						.add(ftpConnectedActivity.RemoteFileList.get(params[0])
								.getFilePath());
				ftpConnectedActivity.CurrentHistoryPositionRemote = ftpConnectedActivity.HistoryListRemote
						.size();

				if (status)

					return true;
				else
					return false;
			}
			return false;

		}

		@Override
		protected void onPostExecute(Boolean status) {

			if (status) {

				// When directory is successfully changed
				adapter.notifyDataSetChanged();

				ProgressBar pb = (ProgressBar) ftpConnectedActivity.menuItemProgress
						.getActionView().findViewById(R.id.menuItemRefreshBar);
				Animation anim = (Animation) AnimationUtils.loadAnimation(
						getActivity().getApplicationContext(), R.anim.fadein);
				pb.setAnimation(anim);
				pb.setVisibility(pb.INVISIBLE);
			}
			else
			{
				
				// Hide Listview
				LinearLayout ln = (LinearLayout)getActivity().findViewById(R.id.ln_remote_lv);
				ln.setVisibility(ln.GONE);
				LinearLayout tv = (LinearLayout)getActivity().findViewById(R.id.ln_remote_error);
				tv.setVisibility(tv.VISIBLE);
				
				// Hide Loading
				ProgressBar pb = (ProgressBar) ftpConnectedActivity.menuItemProgress
						.getActionView().findViewById(R.id.menuItemRefreshBar);
				Animation anim = (Animation) AnimationUtils.loadAnimation(
						getActivity().getApplicationContext(), R.anim.fadein);
				pb.setAnimation(anim);
				pb.setVisibility(pb.INVISIBLE);
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
}