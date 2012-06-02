package com.minderonline.ftpclient.ftp;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.*;

import com.minderonline.ftpclient.connection_overview;
import com.minderonline.ftpclient.general.FtpAnswer;

import android.util.Log;

public class ftp_functions {

	// Public FTP Client object
	public static FTPClient mFTPClient;
	static String TAG = "FTP";

	public static FtpAnswer ftpConnect(String host, String username,
			String password, int port) {
		FtpAnswer fa = new FtpAnswer();

		try {
			if(mFTPClient != null)
			{
			//Log.d("FTP","Status: "+ mFTPClient.getStatus().toString());
			}
		
			mFTPClient = new FTPClient();
			mFTPClient.setConnectTimeout(20000);

			mFTPClient.connect(host, port);
			// mFTPClient.setControlEncoding("UFT-8");

			// now check the reply code, if positive mean connection success
			if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {

				fa.setIsConnected(true);

				// login using username & password

				boolean status = mFTPClient.login(username, password);

				fa.setIsLogin(status);

				if (status) {

					/*
					 * Set File Transfer Mode
					 * 
					 * To avoid corruption issue you must specified a correct
					 * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
					 * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE for
					 * transferring text, image, and compressed files.
					 */

					mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);

					mFTPClient.enterLocalPassiveMode();

				} else {
					Log.e("FTP", "Error to Login: " + mFTPClient.getReplyCode()
							+ "String: " + mFTPClient.getReplyString());

					fa.setLoginError(mFTPClient.getReplyString());
					fa.setLoginCode(mFTPClient.getReplyCode());

				}

			}
		} catch (Exception e) {

			Log.d(TAG, "Error: " + mFTPClient.getReplyCode());

			fa.setIsConnected(false);

		}
		return fa;
	}

	public static String ftpGetCurrentWorkingDirectory() {
		try {
			String workingDir = mFTPClient.printWorkingDirectory();
			return workingDir;
		} catch (Exception e) {
			Log.d(TAG, "Error: could not get current working directory.");
		}

		return null;
	}

	public static ArrayList<String> RemoteFiles() {

		Log.d("FTP", "Before Loading the List");
		String dirPath = "/";

		ArrayList<String> item = new ArrayList<String>();
		ArrayList<String> path = new ArrayList<String>();

		item = new ArrayList<String>();

		path = new ArrayList<String>();

		FTPFile[] files = null;
		try {
			Log.i("FTP", "Before list files..");

			files = mFTPClient.listFiles();

			Log.d("FTP", "After Loading the List");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("FTP", e.getMessage());

			// e.printStackTrace();
			return null;
		}

		Log.i("FTP", Integer.toBinaryString(files.length));
		if(files.length > 0)
		{
		
		for (int i = 0; i < files.length; i++)

		{

			FTPFile file = files[i];

			path.add(file.getLink());

			if (file.isDirectory())

				item.add(file.getName() + "/");

			else

				item.add(file.getName());

		}
		}
		else
		{
			return null;
		}
		return item;
	}

	public static boolean ftpChangeDirectory(String directory_path) {
		try {
			mFTPClient.changeWorkingDirectory(directory_path);

			return true;
		} catch (Exception e) {
			Log.d(TAG, "Error: could not change directory to " + directory_path);
		}

		return false;
	}

	public static boolean ftpChangeDirectoryToParent() {
		try {

			mFTPClient.changeToParentDirectory();
			return true;

		} catch (Exception e) {
			Log.d(TAG, "Error: could not change directory to Parent ");

		}
		return false;
	}

	public boolean ftpMakeDirectory(String new_dir_path) {
		try {
			boolean status = mFTPClient.makeDirectory(new_dir_path);
			return status;
		} catch (Exception e) {
			Log.d(TAG, "Error: could not create new directory named "
					+ new_dir_path);
		}

		return false;
	}



	public static boolean ftpDisconnect() {

		try {
			mFTPClient.logout();
			mFTPClient.disconnect();
			Log.i("FTP", "Successfully disconnected from Server");
			return true;

		} catch (Exception e) {
			Log.d(TAG, "Error occurred while disconnecting from ftp server.");
			Log.e(TAG, "Disconnecting Error: " + e.toString());
		}

		return false;
	}

}
