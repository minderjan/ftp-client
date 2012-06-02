package com.minderonline.ftpclient.general;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.minderonline.ftpclient.R;
import com.minderonline.ftpclient.ftpConnectedActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FileManagerListAdapter extends BaseAdapter {

	private Activity activity;
	private String[] data;
	public ImageLoaderAPK imageLoader;
	

	private List<FileOb> FileObjectList;

	private static LayoutInflater inflater = null;

	public FileManagerListAdapter(Activity a, List<FileOb> _FileObject) {
		activity = a;
		FileObjectList = _FileObject;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public int getCount() {
		return FileObjectList.size();
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
			vi = inflater.inflate(R.layout.tab_file_row_layout, null);

		TextView text = (TextView) vi.findViewById(R.id.title);

		ImageView imgv = (ImageView) vi.findViewById(R.id.img_icon);
		ImageView img_chb = (ImageView) vi.findViewById(R.id.img_chb);

		if (FileObjectList.get(position).isDirectory) {

			imgv.setImageResource(R.drawable.folder_small);
			if (ftpConnectedActivity.isSelectingMod) {
				if (FileObjectList.get(position).isChecked()) {

					img_chb.setImageResource(R.drawable.chb_checked);

					vi.setBackgroundColor(convertView.getResources().getColor(
							R.color.transparent_blue_dark));
				} else {
					img_chb.setImageResource(R.drawable.chb_unchecked);

					vi.setBackgroundColor(Color.TRANSPARENT);
				}
			} else {
				img_chb.setImageResource(0);
			}

			text.setText(FileObjectList.get(position).getFileName());
		} else {

			Log.v("FileManagerAdapter", FileObjectList.get(position)
					.getFileName());
			// Boolean checked = FileObjectList.get(position).isChecked();

			if (ftpConnectedActivity.isSelectingMod) {
				if (FileObjectList.get(position).isChecked()) {
					img_chb.setImageResource(R.drawable.chb_checked);

				} else {
					img_chb.setImageResource(R.drawable.chb_unchecked);
				}
			} else {

				img_chb.setImageResource(0);
			}

			String FileName = FileObjectList.get(position).getFileName();

			// Getting the extention
			String ext = FileName.substring((FileName.lastIndexOf(".") + 1),
					FileName.length());
			// setting the filename
			text.setText(FileObjectList.get(position).getFileName());
			// Set the Filetype image
			if (FileName.endsWith(".apk")) {

				
			
				
				imageLoader = new ImageLoaderAPK(activity.getApplicationContext());
				imageLoader.DisplayImage(FileObjectList.get(position).getFilePath(), imgv,
						activity.getApplicationContext());
				
				
				

				// imgv.setImageResource(getImage(FileObjectList.get(position).getFileName()));
			} else {
				imgv.setImageResource(getImage(FileObjectList.get(position)
						.getFileName()));
			}
			// File fl = new File(FileObjectList.get(position).getFilePath());
			// String fileExtension =
			// MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(fl).toString());
			// imgv.setImageDrawable(getDrawableForMimetype(fl,
			// MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)));

		}

		return vi;
	}

	public static String removeChar(String s, char c) {
		StringBuffer r = new StringBuffer(s.length());
		r.setLength(s.length());
		int current = 0;
		for (int i = 0; i < s.length(); i++) {
			char cur = s.charAt(i);
			if (cur != c)
				r.setCharAt(current++, cur);
		}
		return r.toString();
	}

	private int getImage(String _FileName) {
		int bp = R.drawable.file;
		String ShorterName = _FileName.toLowerCase();

		if (ShorterName.endsWith(".mp3")) {
			bp = R.drawable.f_mp3;
		}
		if (ShorterName.endsWith(".jpg")) {
			bp = R.drawable.f_jpg;
		}
		if (ShorterName.endsWith(".pdf")) {
			bp = R.drawable.f_pdf;
		}
		if (ShorterName.endsWith(".gif")) {
			bp = R.drawable.f_gif;
		}
		if (ShorterName.endsWith(".xml")) {
			bp = R.drawable.f_xml;
		}
		if (ShorterName.endsWith(".psd")) {
			bp = R.drawable.f_psd;
		}
		if (ShorterName.endsWith(".rar")) {
			bp = R.drawable.f_rar;
		}
		if (ShorterName.endsWith(".avi")) {
			bp = R.drawable.f_avi;
		}
		if (ShorterName.endsWith(".bmp")) {
			bp = R.drawable.f_bmp;
		}
		if (ShorterName.endsWith(".css")) {
			bp = R.drawable.f_css;
		}
		// if(ShorterName.endsWith(".docx")){bp = R.drawable.f_docx;}
		// if(ShorterName.endsWith(".eml")){bp = R.drawable.f_eml;}
		// if(ShorterName.endsWith(".ini")){bp = R.drawable.f_ini;}
		// if(ShorterName.endsWith(".html")){bp = R.drawable.f_html;}
		// if(ShorterName.endsWith(".jpeg")){bp = R.drawable.f_jpeg;}
		if (ShorterName.endsWith(".zip")) {
			bp = R.drawable.f_zip;
		}
		if (ShorterName.endsWith(".xlsx")) {
			bp = R.drawable.f_xlsx;
		}
		// if(ShorterName.endsWith(".apk")){bp = R.drawable.f_apk;}

		return bp;

	}

	/**
	 * Return the Drawable that is associated with a specific mime type for the
	 * VIEW action.
	 * 
	 * @param mimetype
	 * @return
	 */
	private Drawable getDrawableForMimetype(File file, String mimetype) {
		if (mimetype == null) {
			return null;
		}

		PackageManager pm = activity.getApplicationContext()
				.getPackageManager();

		Uri data = Uri.fromFile(file);

		Intent intent = new Intent(Intent.ACTION_VIEW);
		// intent.setType(mimetype);

		// Let's probe the intent exactly in the same way as the VIEW action
		// is performed in FileManagerActivity.openFile(..)
		intent.setDataAndType(data, mimetype);

		final List<ResolveInfo> lri = pm.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);

		if (lri != null && lri.size() > 0) {
			// Log.i(TAG, "lri.size()" + lri.size());

			// return first element
			int index = 0;

			// Actually first element should be "best match",
			// but it seems that more recently installed applications
			// could be even better match.
			index = lri.size() - 1;

			final ResolveInfo ri = lri.get(index);
			return ri.loadIcon(pm);
		}

		return null;
	}

}