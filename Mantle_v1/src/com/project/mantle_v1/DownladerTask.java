package com.project.mantle_v1;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.http.util.ByteArrayBuffer;
import android.os.AsyncTask;
import android.util.Log;

public class DownladerTask extends AsyncTask<Void, Long, File> {

	public DownladerTask(String fileUrl, String Filename) {
		try {
			File root = android.os.Environment.getExternalStorageDirectory();
			this.mUrl = new URL(fileUrl);
			Log.d(TAG, fileUrl);
			this.mFile = new File(root, Filename);
			Log.d(TAG, Filename);
		} catch (IOException ex) {
			Log.e(TAG, ex.getMessage());
		}
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
			Log.d(TAG, "download ready in"
					+ ((System.currentTimeMillis() - StartingTime) / 1000)
					+ " sec");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return mFile;
	}

	private final String TAG = getClass().getName();
	private File mFile;
	private URL mUrl;
}
