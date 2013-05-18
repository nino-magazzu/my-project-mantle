package com.project.mantle_v1.database;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.dropbox.client2.DropboxAPI.DropboxInputStream;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.exception.DropboxException;
import com.project.mantle_v1.dropbox.DropboxAuth;
import com.project.mantle_v1.fileNavigator.MantleFile;

public class DbSyncService extends Service {
	private Entry ent;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(getClass().getSimpleName(), "***** DbSyncService *****: onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(getClass().getSimpleName(), "Starting service");

		new Thread() {
			@Override
			public void run() {
				try {
					ent = new DropboxAuth(getApplicationContext()).getAPI()
							.metadata("/storedFile/" + DatabaseHelper.DB_NAME,
									1000, null, true, null);

					DateFormat dateFormat = new SimpleDateFormat(
							"EEE, dd MMM yyyy kk:mm:ss ZZZZZ", Locale.ITALY);
					String dataDropbox = ent.modified;

					Log.v(getClass().getSimpleName(), "data Dropbox db: "
							+ dataDropbox);

					Log.v(getClass().getSimpleName(),
							getDatabasePath(DatabaseHelper.DB_NAME)
									.getAbsolutePath());

					String dataLocale = dateFormat.format(new Date(
							getDatabasePath(DatabaseHelper.DB_NAME)
									.lastModified()));

					Log.v(getClass().getSimpleName(), "data Device db: "
							+ dataLocale);

					if (dataDropbox.compareTo(dataLocale) > 0)
						downloadDb();
					else
						uploadDb();
				} catch (DropboxException e) {
					Log.v(getClass().getSimpleName(), "Problema con le Entry");
				}
			}
		}.start();
		return Service.START_STICKY;
	}

	/**
	 * carica il database in locale su Dropbox
	 */

	public void uploadDb() {
		DatabaseHelper db = new DatabaseHelper(getApplicationContext());

		db.exportDB();
		db.close();

		new AsyncTask<Void, Long, Boolean>() {
			private UploadRequest mRequest;

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					File mFile = getDatabasePath(DatabaseHelper.DB_NAME);
					DropboxAuth dropbox = new DropboxAuth(
							getApplicationContext());

					FileInputStream fis = new FileInputStream(mFile);
					String path = MantleFile.FILE_DIR + mFile.getName();
					mRequest = dropbox.getAPI().putFileOverwriteRequest(path,
							fis, mFile.length(), null);

					if (mRequest != null) {
						mRequest.upload();
						return true;
					}
					return false;
				} catch (Exception e) {
					// This session wasn't authenticated properly or user
					// unlinked
					Log.e("UploaderTask", e.getMessage());
					return false;
				}
			}
		}.execute();

	}

	/**
	 * Download del database presente su dropbox sul device
	 */

	public void downloadDb() {

		new AsyncTask<Void, Long, Boolean>() {
			DropboxAuth dropbox = new DropboxAuth(getApplicationContext());
			String file_path = MantleFile.FILE_DIR + DatabaseHelper.DB_NAME;
			String saving_path = MantleFile.DIRECTORY_DB
					+ DatabaseHelper.DB_NAME;

			@Override
			protected Boolean doInBackground(Void... params) {
				BufferedInputStream br = null;
				BufferedOutputStream bw = null;
				DropboxInputStream downloadedFile = null;

				try {
					downloadedFile = dropbox.getAPI().getFileStream(file_path,
							null);
				} catch (Exception e) {
					Log.e(getClass().getSimpleName(), e.getMessage());
					return false;
				}

				br = new BufferedInputStream(downloadedFile);
				try {
					bw = new BufferedOutputStream(new FileOutputStream(
							new File(saving_path)));
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
					Log.e(getClass().getSimpleName(), e.getMessage());
					return false;
				} catch (IOException e) {
					Log.e(getClass().getSimpleName(), e.getMessage());
					return false;
				} finally {
					try {// in finally block:
						if (bw != null) {
							bw.close();
						}
						if (br != null) {
							br.close();
						}
					} catch (IOException e) {
						Log.e(getClass().getSimpleName(), e.getMessage());
					}
				}
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					DatabaseHelper db = new DatabaseHelper(
							getApplicationContext());
					db.importDB();
					db.close();
				}
			}
		}.execute();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
