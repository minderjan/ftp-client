package com.minderonline.ftpclient;

import java.util.ArrayList;

import com.minderonline.ftpclient.ftp.ftp_functions;
import com.minderonline.ftpclient.general.FileManagerListAdapter;
import com.minderonline.ftpclient.general.FileOb;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FragmentListRemote extends ListFragment {

	
	int mNum;
	static ArrayList<String> items = new ArrayList<String>();

	/**
	 * Create a new instance of CountingFragment, providing "num" as an
	 * argument.
	 */
	static FragmentListRemote newInstance(int num) {
		FragmentListRemote f = new FragmentListRemote();

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
