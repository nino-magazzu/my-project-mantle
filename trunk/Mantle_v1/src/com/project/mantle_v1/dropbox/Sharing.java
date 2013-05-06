package com.project.mantle_v1.dropbox;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.ExecutionException;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.project.mantle_v1.MantleFile;
import com.project.mantle_v1.MyApplication;
import com.project.mantle_v1.User;
import com.project.mantle_v1.database.FriendsList;
import com.project.mantle_v1.database.MioDatabaseHelper;
import com.project.mantle_v1.fileChooser.FileChooser;
import com.project.mantle_v1.gmail.Sender;
import com.project.mantle_v1.parser.MantleMessage;
import com.project.mantle_v1.parser.ParseJSON;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

public class Sharing extends Activity {

	final static protected String HOME_DIR = Environment
			.getExternalStorageDirectory().getAbsoluteFile() + "/Mantle";
	
	final static private String FILE_DIR = "/storedFile/";
	
	final static private int UPLOAD_REQUEST_CODE = 10;
	final static private int PRIORITY_CHOOSED_CODE = 9;
	final static private int FRIEND_CHOOSED_CODE = 8;
	
	private DropboxAPI<AndroidAuthSession> mApi;
	private MantleFile mt;
	
	private final String TAG = this.getClass().getSimpleName();
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		DropboxAuth auth = new DropboxAuth(this);
		this.mApi = auth.getAPI();
		
		startFileUploadChooser();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) {
			
		case UPLOAD_REQUEST_CODE:
			String filePath = data.getStringExtra("path");
			if (filePath != null) {
				if (filePath.compareTo("null") == 0)
					finish();
				else {
					File file = new File(filePath);
					Uploader upload = new Uploader(this, mApi, FILE_DIR, file,
							new User(getApplicationContext()).getUsername());
					upload.execute();
					try {
						mt = upload.get();
						setID();
						Log.d(TAG, mt.getObjectType());
					} catch (InterruptedException e) {
						Log.i(TAG, "Error authenticating", e);
					} catch (ExecutionException e) {
						Log.i(TAG, "Error authenticating", e);
					}
					Intent intent = new Intent(this, Priority.class);
					intent.putExtra("idFile", Integer.valueOf(mt.getIdFile()));
					startActivityForResult(intent, PRIORITY_CHOOSED_CODE);
					
				}
			}
			break;
			
		case PRIORITY_CHOOSED_CODE:
			Intent intent = new Intent(this, FriendsList.class);
			intent.putExtra("flag", 3);
			startActivityForResult(intent, FRIEND_CHOOSED_CODE);
			break;
			
		case FRIEND_CHOOSED_CODE:
			Object[] contacts = (Object[]) data
			.getSerializableExtra("contacts");
			if (contacts != null) {
		//		SharedPreferences fileDetails = getSharedPreferences("file", 0);
				//	if(fileDetails.contains("idFile")) 
				//	Log.e(TAG, "c'è");
				//String idFile = fileDetails.getString("idFile", null);

				mt = ((MyApplication) getApplicationContext()).media;//new MantleFile(getApplicationContext(), idFile);
				String body = "";
				try {
					body = new ParseJSON(new StringWriter()).writeJson(mt);
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
				MioDatabaseHelper db = new MioDatabaseHelper(getApplicationContext());
				int idFile = Integer.getInteger(mt.getIdFile());
				for (int j = 0; j < contacts.length; j++) {
					Log.d("Dropbox", "Ho inviato la mail a " + contacts[j]);
					db.insertShare(idFile, (String) contacts[j]);
					new Sender(this, body, (String) contacts[j], MantleMessage.SHARING_PHOTO).execute();
				}
				db.close();
			}
			break;
		}
		
	}
	
	
	private void startFileUploadChooser() {
		Intent intent = new Intent(this, FileChooser.class);
		intent.putExtra("upload", true);
		startActivityForResult(intent, UPLOAD_REQUEST_CODE);

	}
	
	private void setID() {
		SharedPreferences fileDetails = getSharedPreferences("file", MODE_PRIVATE);
		Editor editor = fileDetails.edit();
		editor.clear();
		editor.putString("idFile", mt.getIdFile());
		editor.commit();
		
	}
}
