package com.minderonline.ftpclient.general;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.minderonline.ftpclient.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoaderAPK {

	FileCache fileCache;
	long maxBytes = 512;
	BitmapCache<String> cache = new BitmapCache<String>(maxBytes);
	
	

	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;

	public ImageLoaderAPK(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	final int PlaceHolder = R.drawable.file;

	final int stub_id = R.drawable.f_loading;

	public void DisplayImage(String path, ImageView imageView, Context ctx) {
		imageViews.put(imageView, path);
		Bitmap bitmap = cache.get(path);

		if (bitmap != null) {
			Log.d("LOG", "Bitmap laodet from Cache..");
			imageView.setImageBitmap(bitmap);
		} else {
			Log.d("LOG", "Bitmap loadet from File..");
			queuePhoto(path, imageView, ctx);
			imageView.setImageResource(stub_id);
		}
	}



	public void queuePhoto(String path, ImageView imageView, Context ctx) {
		PhotoToLoad p = new PhotoToLoad(path, imageView, ctx);
		executorService.submit(new PhotosLoader(p));
	}

	public Bitmap getBitmap(String path, Context ctx) {
		// File f=fileCache.getFile(path);

		// //from SD cache
		// Bitmap b = decodeFile(f);
		// if(b!=null)
		// return b;

		Bitmap bmpIcon = null;

		// from web
		try {

			Thread.sleep(120);

			PackageInfo packageInfo = ctx.getPackageManager()
					.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);

			if (packageInfo != null) {
				ApplicationInfo appInfo = packageInfo.applicationInfo;
				if (Build.VERSION.SDK_INT >= 8) {
					appInfo.sourceDir = path;
					appInfo.publicSourceDir = path;
				}

				Drawable icon = appInfo.loadIcon(ctx.getPackageManager());
				bmpIcon = ((BitmapDrawable) icon).getBitmap();

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

		return bmpIcon;
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {

			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;

			/**
			 * while(true){ if(width_tmp/2<REQUIRED_SIZE ||
			 * height_tmp/2<REQUIRED_SIZE) break; width_tmp/=2; height_tmp/=2;
			 * scale*=2; }
			 **/

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);

		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String path;
		public ImageView imageView;
		public Context ctx;

		public PhotoToLoad(String p, ImageView i, Context _ctx) {
			path = p;
			imageView = i;
			ctx = _ctx;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;

			Bitmap bmp = getBitmap(photoToLoad.path, photoToLoad.ctx);

			// Putt the Image to cache
			 cache.put(photoToLoad.path, bmp);

				// memoryCache.put("test", bmp);

				if (imageViewReused(photoToLoad))
					return;
				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
				Activity a = (Activity) photoToLoad.imageView.getContext();
				a.runOnUiThread(bd);
			}
		}

	

	boolean imageViewReused(PhotoToLoad photoToLoad) {

		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.path))
			return true;

		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public void clearCache() {
		cache.clear();
		fileCache.clear();
	}

}
