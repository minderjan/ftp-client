package com.minderonline.ftpclient;

import java.io.File;
import java.util.ArrayList;

import com.minderonline.ftpclient.general.FileManagerListAdapter;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.webkit.MimeTypeMap;

public class TabLocalFragment extends FragmentListLocal {
	public static FileManagerListAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.frag_local, container, false);
		
		return v;
	}

	public void reload() {

		FileManagerListAdapter adapter = new FileManagerListAdapter(
				getActivity(), ftpConnectedActivity.LocalFileList);

		setListAdapter(adapter);

	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	    ContextMenuInfo menuInfo) {
	
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
	    menu.setHeaderTitle("Options");
	   
	      menu.add("test");
	      menu.add("test2");
	    
	  
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// final ListView list = (ListView) findViewById(android.R.id.list);
		ArrayList<String> titleArray = new ArrayList<String>();

		ftpConnectedActivity.CreateLocalFileList(ftpConnectedActivity.ROOTDIR);

		adapter = new FileManagerListAdapter(getActivity(),
				ftpConnectedActivity.LocalFileList);

		setListAdapter(adapter);
		registerForContextMenu(getListView());
		getListView().setOnItemClickListener(
				new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> arg0, View v,
							int Position, long arg3) {
						// TODO Auto-generated method stub

						Object o = getListView().getItemAtPosition(Position);

						String CurrentPath = ftpConnectedActivity.LocalFileList
								.get(Position).getFilePath();

						ImageView img_chb = (ImageView) v
								.findViewById(R.id.img_chb);

						if (ftpConnectedActivity.isSelectingMod) {
							if (ftpConnectedActivity.LocalFileList
									.get(Position).isChecked()) {
								img_chb.setImageResource(R.drawable.chb_unchecked);
								ftpConnectedActivity.LocalFileList
										.get(Position).setChecked(false);
								
								// Reset the Background color
								v.setBackgroundColor(getActivity().getResources()
										.getColor(android.R.color.transparent));
								
							} else {

								img_chb.setImageResource(R.drawable.chb_checked);
								
								// Set the Background color to half transparent dark blue
								v.setBackgroundColor(getActivity().getResources()
										.getColor(R.color.transparent_blue_dark));
								
								
									
								
								ftpConnectedActivity.LocalFileList
										.get(Position).setChecked(true);
							}

						} else {

							// Create The LocalListTemp new
							if (ftpConnectedActivity.LocalFileList
									.get(Position).isDirectory()) {

								ftpConnectedActivity
										.CreateLocalFileList(CurrentPath);

								adapter.notifyDataSetChanged();

								LinearLayout ln = (LinearLayout) getActivity()
										.findViewById(R.id.ln_bottombar);

								// Bottombar einblenden damit der User
								// navigieren
								// kann
								if (ln.getVisibility() != ln.VISIBLE) {
									ln.setVisibility(ln.VISIBLE);
								}

								if (ftpConnectedActivity.CurrentHistoryPositionLocal == 0) {

									// ImageButton imb = (ImageButton)
									// getActivity()
									// .findViewById(R.id.imb_arrow_left);
									// imb.setEnabled(true);
									// imb.setImageResource(R.drawable.ic_action_arrow_left);
								}

								// Der History den neuen Pfad hinzufügen

								ftpConnectedActivity.HistoryListLocal
										.add(CurrentPath);
								ftpConnectedActivity.CurrentHistoryPositionLocal = ftpConnectedActivity.CurrentHistoryPositionLocal + 1;

								// Get the actual FolderName
								String FolderName = "";
								int index = ftpConnectedActivity.HistoryListLocal
										.get(ftpConnectedActivity.CurrentHistoryPositionLocal)
										.lastIndexOf('/');

								if (index > 0) {
									FolderName = ftpConnectedActivity.HistoryListLocal
											.get(ftpConnectedActivity.CurrentHistoryPositionLocal)
											.substring(index + 1);
								}

								// Button der Bottombar hinzufügen
								Button bt = new Button(getActivity());

								bt.setBackgroundResource(R.drawable.button_style);
								bt.setTextColor(getActivity().getResources()
										.getColor(android.R.color.white));
								bt.setText(FolderName);
								bt.setId(ftpConnectedActivity.CurrentHistoryPositionLocal);
								bt.setLayoutParams(new LayoutParams(
										ViewGroup.LayoutParams.WRAP_CONTENT,
										ViewGroup.LayoutParams.WRAP_CONTENT));

								// Set the Onclick event to the Button
								bt.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										Toast.makeText(getActivity(),
												"clicked", Toast.LENGTH_SHORT)
												.show();

										// Get the Layout in the Scroll view
										LinearLayout ln_bottombar = (LinearLayout) getActivity()
												.findViewById(
														R.id.ln_bottombar_scroll);

										// Get the Current Index of this Button
										int childcount = ln_bottombar
												.getChildCount();

										int NewHistoryIndex = 0;
										int CurrentButtonIndex = 0;
										for (int i = 0; i < childcount; i++) {
											View bt_child = (Button) ln_bottombar
													.getChildAt(i);
											if (bt_child == v) {
												CurrentButtonIndex = i;
												NewHistoryIndex = i + 1;
												break;

											}
										}

										// Navigate to the Clicked Path
										ftpConnectedActivity
												.CreateLocalFileList(ftpConnectedActivity.HistoryListLocal
														.get(NewHistoryIndex));

										// Refresh the Listview
										adapter.notifyDataSetChanged();

										// Set the Current History Position
										ftpConnectedActivity.CurrentHistoryPositionLocal = NewHistoryIndex;

										Log.d("LOG", "Anzahl Buttons: "
												+ childcount);

										// Remove the Unused Buttons
										for (int i = childcount - 1; i >= 0; i--) {

											if (i > CurrentButtonIndex) {
												View bt_child = (Button) ln_bottombar
														.getChildAt(i);
												ln_bottombar
														.removeView(bt_child);
											}
										}

										// Remove the unused History entries
										for (int i = 0; i < ftpConnectedActivity.HistoryListLocal
												.size(); i++) {
											if (i > NewHistoryIndex) {
												ftpConnectedActivity.HistoryListLocal
														.remove(i);

											}
										}
									}
								});

								// Get the Layout in the Scroll view
								LinearLayout ln_bottombar = (LinearLayout) getActivity()
										.findViewById(R.id.ln_bottombar_scroll);

								// Add the new Button to the Layout
								ln_bottombar.addView(bt);

								// Add a new Divider to the layout
								// This is a seperator Line
								View divider = new View(getActivity());
								divider.setLayoutParams(new LayoutParams(
										getPixels(1), getPixels(30)));
								divider.setBackgroundColor(getActivity()
										.getResources().getColor(
												android.R.color.darker_gray));
								// ln_bottombar.addView(divider);

							} else {
								// Open a SingleFile
								try {
									String CurrentPath2 = ftpConnectedActivity.LocalFileList
											.get(Position).getFilePath();
									File file = new File(CurrentPath2);
									
									
									Intent myIntent = new Intent(
											android.content.Intent.ACTION_VIEW);
									
									String extension = android.webkit.MimeTypeMap
											.getFileExtensionFromUrl(Uri
													.fromFile(file).toString());
									String mimetype = android.webkit.MimeTypeMap
											.getSingleton()
											.getMimeTypeFromExtension(extension);
									myIntent.setDataAndType(Uri.fromFile(file),
											mimetype);
									startActivity(myIntent);
									
									
								
								} catch (Exception e) {
									// TODO: handle exception
									String data = e.getMessage();
								}

							}
						}
					}

				});
	}

	// This function ist to convert the dpi size to the Real Pixel Size of the
	// Screen
	private int getPixels(int dipValue) {
		Resources r = getResources();
		int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dipValue, r.getDisplayMetrics());
		return px;
	}

}
