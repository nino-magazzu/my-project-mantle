package com.project.mantle_v1;

import java.io.File;
import java.io.FileInputStream;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import android.os.AsyncTask;
import android.util.Log;

public class UploaderTask extends AsyncTask<Void, Long, Boolean> {
	final static private String FILE_DIR = "/storedFile/";
	private DropboxAPI<?> mApi;
	private File mFile;
	private UploadRequest mRequest;

	public UploaderTask(DropboxAPI<?> api, File f) {
		this.mApi = api;
		this.mFile = f;
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

			if (mRequest != null)
				mRequest.upload();

		} catch (Exception e) {
			// This session wasn't authenticated properly or user unlinked
			Log.e("UploaderTask", e.getMessage());
		}
		return null;
	}
}