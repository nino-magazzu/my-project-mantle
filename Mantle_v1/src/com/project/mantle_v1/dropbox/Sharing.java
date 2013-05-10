package com.project.mantle_v1.dropbox;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.ExecutionException;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.project.mantle_v1.MantleFile;
import com.project.mantle_v1.R;
import com.project.mantle_v1.database.FriendsList;
import com.project.mantle_v1.database.MioDatabaseHelper;
import com.project.mantle_v1.database.Priority;
import com.project.mantle_v1.fileChooser.FileChooser;
import com.project.mantle_v1.gmail.Sender;
import com.project.mantle_v1.parser.MantleMessage;
import com.project.mantle_v1.parser.ParseJSON;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;


public class Sharing extends Activity {

	final static protected String HOME_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/Mantle";

	final static private String FILE_DIR = "/storedFile/";

	final static private int UPLOAD_REQUEST_CODE = 10;
	final static private int PRIORITY_CHOOSED_CODE = 9;
	final static private int FRIEND_CHOOSED_CODE = 8;

	private DropboxAPI<AndroidAuthSession> mApi;
	private int FILE_ID;
	private final String TAG = this.getClass().getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vuoto);
		DropboxAuth auth = new DropboxAuth(this);
		this.mApi = auth.getAPI();
		startActivityForResult(new Intent(this, FileChooser.class),
				UPLOAD_REQUEST_CODE);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {

		case UPLOAD_REQUEST_CODE:
			String filePath = data.getStringExtra("path");
			if (filePath != null) {
				if (filePath.compareTo("null") == 0)
					finish();
				else {
					Uploader upload = new Uploader(Sharing.this, mApi,
							FILE_DIR, new File(filePath));
					upload.execute();
					try {
						FILE_ID = upload.get();
					} catch (InterruptedException e) {
						Log.e(TAG,
								"Error authenticating (Interrupted): "
										+ e.getMessage(), e);
					} catch (ExecutionException e) {
						Log.e(TAG,
								"Error authenticating (Execution): "
										+ e.getMessage(), e);
					}
					Log.v(TAG,
							"--> iD File caricato: " + String.valueOf(FILE_ID));
					Intent intent = new Intent(this, Priority.class);
					intent.putExtra("idFile", FILE_ID);
					startActivityForResult(intent, PRIORITY_CHOOSED_CODE);

				}
			}
			break;

		case PRIORITY_CHOOSED_CODE:
			Log.v(TAG, "--> " + "PRIORITY_CHOOSED_CODE");
			Intent intent = new Intent(this, FriendsList.class);
			intent.putExtra("flag", 3);
			startActivityForResult(intent, FRIEND_CHOOSED_CODE);
			break;

		case FRIEND_CHOOSED_CODE:
			Log.v(TAG, "FRIEND_CHOOSED_CODE)");
			Object[] contacts = (Object[]) data
					.getSerializableExtra("contacts");
			if (contacts != null) {
				MantleFile mt = new MantleFile(getApplicationContext(),
						String.valueOf(FILE_ID));
				String body = "";
				try {
					body = new ParseJSON(new StringWriter()).writeJson(mt);
				} catch (IOException e) {
					Log.e(TAG, "--> " + e.getMessage());
				}
				MioDatabaseHelper db = new MioDatabaseHelper(
						getApplicationContext());

				for (int j = 0; j < contacts.length; j++) {
					Log.v("Dropbox", "--> " + "Ho inviato la mail a "
							+ contacts[j]);
					db.insertShare(FILE_ID, (String) contacts[j]);
					new Sender(this, body, (String) contacts[j],
							MantleMessage.SHARING_PHOTO).execute();
				}
				db.close();
				finish();
			}
			break;
		}
	}
}
