package com.minderonline.ftpclient;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.minderonline.ftpclient.general.FileManagerListAdapter;

public class FragmentListLocal extends ListFragment {
	int mNum;

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	static FragmentListLocal newInstance(int num) {
		FragmentListLocal f = new FragmentListLocal();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);

		return f;
	}

	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNum = (getArguments() != null ? getArguments().getInt("num") : 1) + 1;
	}
	
	

	
	
}
