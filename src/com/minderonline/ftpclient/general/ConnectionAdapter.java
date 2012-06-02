package com.minderonline.ftpclient.general;

import java.util.ArrayList;

import com.minderonline.ftpclient.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ConnectionAdapter extends BaseAdapter {

	private Activity activity;
	private String[] data;
	

	private ArrayList<String> titleArray;
	private ArrayList<String> descArray;

	private static LayoutInflater inflater = null;

	public ConnectionAdapter(Activity a, ArrayList<String> _titleArray,
			ArrayList<String> _descArray) {
		activity = a;
		titleArray = _titleArray;
		descArray = _descArray;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	
	public int getCount() {
		return titleArray.size();
	}

	
	public Object getItem(int position) {
		return position;
	}

	
	public long getItemId(int position) {
		return position;
	}

	
	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.connection_rowlayout, null);

		TextView text = (TextView) vi.findViewById(R.id.title);
		TextView txdesc = (TextView) vi.findViewById(R.id.desc);
		
		text.setText(titleArray.get(position));

		txdesc.setText("Host: " + descArray.get(position));

		return vi;
	}
}