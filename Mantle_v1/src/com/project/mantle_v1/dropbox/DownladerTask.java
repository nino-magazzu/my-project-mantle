package com.project.mantle_v1.dropbox;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class DownladerTask extends AsyncTask<Void, Long, File> {

	public DownladerTask(String fileUrl, String Filename, String path,
			Context cont) {
		try {
			File root = new File(path);
			if (!root.exists())
				root.mkdirs();
			this.mUrl = new URL(fileUrl);
			Log.d(TAG, fileUrl);
			this.mFile = new File(root, Filename);
			Log.d(TAG, Filename);
		} catch (IOException ex) {
			Log.e(TAG, ex.getMessage());
		}

		mDialog = new ProgressDialog(cont);
		mDialog.setMessage("downloading data ");
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.show();

	}

	@Override
	protected File doInBackground(Void... params) {

		try {
			long StartingTime = System.currentTimeMillis();
			/* Open a connection to that URL. */
			URLConnection ucon = mUrl.openConnection();
			/*
			 * Define InputStreams to read from the URLConnection.
			 */
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			/*
			 * Read bytes to the Buffer until there is nothing more to read(-1).
			 */
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			/* Convert the Bytes read to a String. */
			FileOutputStream fos = new FileOutputStream(mFile);
			fos.write(baf.toByteArray());
			fos.close();
			Log.d(TAG, "download ready in "
					+ ((System.currentTimeMillis() - StartingTime) / 1000)
					+ " sec");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		Log.v(TAG, "*** Download Compleatato ***");
		return mFile;
	}

	@Override
	protected void onPostExecute(File result) {
		mDialog.dismiss();
	}

	private final String TAG = this.getClass().getSimpleName();
	private File mFile;
	private URL mUrl;
	private final ProgressDialog mDialog;

}
