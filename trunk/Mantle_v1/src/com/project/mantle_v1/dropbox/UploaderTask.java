package com.project.mantle_v1.dropbox;

import java.io.File;
import java.io.FileInputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;

public class UploaderTask extends AsyncTask<Void, Long, Boolean> {

	private DropboxAPI<?> mApi;
	private File mFile;
	private UploadRequest mRequest;
	private String FILE_DIR;

	private final ProgressDialog mDialog;

	public UploaderTask(DropboxAPI<?> api, File f, String dir, Context cont) {
		this.mApi = api;
		this.mFile = f;
		this.FILE_DIR = dir;

		mDialog = new ProgressDialog(cont);
		mDialog.setMessage("Uploading ");
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Annulla l'upload del file
						mRequest.abort();
					}
				});
		mDialog.show();

	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			// By creating a request, we get a handle to the putFile operation,
			// so we can cancel it later if we want to

			// TODO: inserire la funzione di cifratura del file, prima del
			// caricamento dello stesso su

			FileInputStream fis = new FileInputStream(mFile);
			String path = FILE_DIR + mFile.getName();
			Log.d("UploaderTask", mApi.toString());
			mRequest = mApi.putFileOverwriteRequest(path, fis, mFile.length(),
					new ProgressListener() {
						@Override
						public long progressInterval() {
							// Update the progress bar every half-second or so
							return 500;
						}

						@Override
						public void onProgress(long bytes, long total) {
							publishProgress(bytes);
						}
					});

			if (mRequest != null) {
				mRequest.upload();
				return true;
			}
			return false;
		} catch (Exception e) {
			// This session wasn't authenticated properly or user unlinked
			Log.e("UploaderTask", e.getMessage());
			return false;
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		mDialog.dismiss();
	}
}
