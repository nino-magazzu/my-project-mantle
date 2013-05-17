package com.project.mantle_v1.database;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.project.mantle_v1.dropbox.Downloader;
import com.project.mantle_v1.dropbox.DropboxAuth;
import com.project.mantle_v1.fileNavigator.MantleFile;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class DbSyncService extends IntentService {

	public DbSyncService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		try {
			Entry ent = new DropboxAuth(getApplicationContext()).getAPI().metadata("/storedFile/"
					+ DatabaseHelper.DB_NAME, 1000, null, true, null);
			 DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss ZZZZZ", Locale.ITALY);
			String dataDropbox = ent.modified;
			String dateLocale = dateFormat.format(new Date(new File("/data/com.project.mantle_v1/databases/"
					+ DatabaseHelper.DB_NAME).lastModified()));
			if(dataDropbox.compareTo(dateLocale) > 0)
				downloadDb();
			else
				uploadDb();
		} catch (DropboxException e) {
			Log.v("DbSyncService", "Problema con le Entry");
		}
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void uploadDb(){
		DatabaseHelper db = new DatabaseHelper(
				getApplicationContext());
		
		db.exportDB();
		db.close();
		
		DropboxAuth dropbox = new DropboxAuth(
				getApplicationContext());

		MantleFile file = new MantleFile();
		file.setmFile(new File(
				MantleFile.DIRECTORY_DB
				+ DatabaseHelper.DB_NAME));
		
		file.uploadNotCipherFile(dropbox.getAPI(), getApplicationContext());
	}

	public void downloadDb(){
	new Downloader(this, new DropboxAuth(getApplicationContext()).getAPI(), "/storedFile/"
				+ DatabaseHelper.DB_NAME, MantleFile.DIRECTORY_DB)
	.execute();
	DatabaseHelper db = new DatabaseHelper(
			getApplicationContext());
	db.importDB();
	}
	
}
