package com.project.mantle_v1.dropbox;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxInputStream;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Downloader extends AsyncTask<Void, Long, Boolean> {
	private DropboxAPI<?> mApi;
	private Context mContext;

	private FileOutputStream mFos;
	private String file_path;
	private String saving_path;

	private final ProgressDialog mDialog;

	private String mErrorMsg;
	private boolean mCanceled;

	//private long mLenght;

	public Downloader(Context context, DropboxAPI<?> api, String file_path,
			String saving_path) {
		mApi = api;
		mContext = context.getApplicationContext();
		this.file_path = file_path;
		this.saving_path = saving_path;

		//mLenght = size;

		mDialog = new ProgressDialog(context);
		mDialog.setMax(100);
		mDialog.setMessage("Downloading database.. ");
		mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDialog.setProgress(0);

		mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mCanceled = true;
						mErrorMsg = "Canceled";
						if (mFos != null) {
							try {
								mFos.close();
							} catch (IOException e) {
							}
						}
					}
				});
		mDialog.show();

	}

	@Override
	protected Boolean doInBackground(Void... params) {
		BufferedInputStream br = null;
		BufferedOutputStream bw = null;
		DropboxInputStream downloadedFile = null;

		if (mCanceled) {
			return false;
		}
		
			try {
				downloadedFile = mApi.getFileStream(file_path, null);
			} catch (Exception e1) {
				//showToast(e1.getMessage());
			}
		

		if (mCanceled) {
			return false;
		}

		br = new BufferedInputStream(downloadedFile);
		try {
			bw = new BufferedOutputStream(new FileOutputStream(new File(
					saving_path + "/" + getFileName())));
			byte[] buffer = new byte[4096];
			int read;
			while (true) {
				read = br.read(buffer);
				if (read <= 0) {
					break;
				}
				bw.write(buffer, 0, read);
			}
		} catch (FileNotFoundException e) {
			mErrorMsg = e.getMessage();
		} catch (IOException e) {
			mErrorMsg = e.getMessage();
		} finally {
			try {// in finally block:
				if (bw != null) {
					bw.close();
				}
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				mErrorMsg = e.getMessage();
			}
		}
		return true;
	}
/*
	@Override
	protected void onProgressUpdate(Long... progress) {
		int percent = (int) (100.0 * (double) progress[0] / mLenght + 0.5);
		mDialog.setProgress(percent);
	}
*/
	@Override
	protected void onPostExecute(Boolean result) {
		mDialog.dismiss();
		if (result) {
			showToast("File successfully downloaded");
		} else {
			showToast(mErrorMsg);
		}
	}

	private void showToast(String msg) {
		//Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
		//error.show();
		Log.v("DOWNLOADER", msg);
	}

	private String getFileName() {
		String[] splitted = file_path.split("/");
		int index = splitted.length - 1;
		return splitted[index];
	}
}