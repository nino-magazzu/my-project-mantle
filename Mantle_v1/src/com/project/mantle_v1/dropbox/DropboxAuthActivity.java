package com.project.mantle_v1.dropbox;

import java.io.File;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import com.project.mantle_v1.R;
import com.project.mantle_v1.User;
import com.project.mantle_v1.database.DatabaseHelper;
import com.project.mantle_v1.fileNavigator.MantleFile;
import com.project.mantle_v1.login.LoginActivity;
import com.project.mantle_v1.login.Register;
import com.project.mantle_v1.notification_home.NotificationListActivity;

public class DropboxAuthActivity extends Activity {
	static private String TAG;
	final static private String ACCOUNT_PREFS_NAME = "prefs";
	final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

	final static private String APP_KEY = "6k7t4o9zc6jbz9n";
	final static private String APP_SECRET = "ln2raywl1xmqrd7";

	private final String USER_DETAILS_PREF = "user";

	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

	private String username;
	private String pswd;

	private DropboxAPI<AndroidAuthSession> mApi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		username = intent.getStringExtra("username");
		pswd = intent.getStringExtra("password");

		setContentView(R.layout.activity_login);

		TAG = this.getClass().getSimpleName();

		// We create a new AuthSession so that we can use the Dropbox API.
		AndroidAuthSession session = buildSession();

		mApi = new DropboxAPI<AndroidAuthSession>(session);

		mApi.getSession().startAuthentication(DropboxAuthActivity.this);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AndroidAuthSession session = mApi.getSession();

		// The next part must be inserted in the onResume() method of the
		// activity from which session.startAuthentication() was called, so
		// that Dropbox authentication completes properly.
		if (session.authenticationSuccessful()) {
			try {
				// Mandatory call to complete the auth
				session.finishAuthentication();

				// Store it locally in our app for later use
				TokenPair tokens = session.getAccessTokenPair();
				storeKeys(tokens.key, tokens.secret);
				// Intent intent = new Intent(DropboxAuthActivity.this,
				// NotificationListActivity.class);
				// startActivity(intent);

				File sd = new File(Environment.getExternalStorageDirectory()
						+ "/Mantle/db");
				if (!sd.exists())
					sd.mkdirs();

				Log.v(TAG, " ** Downloading... **");

				boolean isDownloaded = false;

				Downloader down = new Downloader(this, mApi, "/storedFile/"
						+ DatabaseHelper.DB_NAME, MantleFile.DIRECTORY_DB);

				down.execute();

				try {
					isDownloaded = down.get();
				} catch (InterruptedException e) {
					Log.v(TAG, e.getMessage());
					isDownloaded = false;
				} catch (ExecutionException e) {
					Log.v(TAG, e.getMessage());
					isDownloaded = false;
				}

				Log.v(TAG, " isDownloaded: " + isDownloaded);

				if (isDownloaded) {

					/*
					 * decifrare il fine del db appena scaricato che si trova in
					 * MantleFile.DIRECTORY_DB utilizzando la chiave d'accesso a
					 * mantle. Se viene ricostruito il db la chiave è corretta e
					 * si continua con l'applicazione altrimenti si può anche
					 * cancellare il file
					 */

					DatabaseHelper db = new DatabaseHelper(
							getApplicationContext());
					db.importDB();
					String[] AccessData = db.login();

					if (AccessData[0].equals(username)
							&& AccessData[1].equals(pswd)) {
						// /////////////
						Log.d("LOGIN ", "Le stringe sono uguli");
						// /////////////

						setPreferences();

						Intent intent = new Intent(DropboxAuthActivity.this,
								NotificationListActivity.class);
						startActivity(intent);
					}

					else if ((!AccessData[0].equals(username))
							&& (!AccessData[0].equals(" "))) {
						// /////////////
						Log.d("LOGIN ", "le stringe sono diverse");
						// /////////////
						Intent intent = new Intent(DropboxAuthActivity.this,
								LoginActivity.class);
						startActivity(intent);
					}

					else if (!AccessData[1].equals(pswd)
							&& (!AccessData[1].equals(" "))) {
						// /////////////
						Log.d("LOGIN ", "le stringe sono diverse");
						// /////////////
						Intent intent = new Intent(DropboxAuthActivity.this,
								LoginActivity.class);
						startActivity(intent);
					}
				}

				else {
					Log.v(TAG, " ** Starting register.. **");

					Intent MyIntent = new Intent(DropboxAuthActivity.this,
							Register.class);

					MyIntent.putExtra("username", username);
					MyIntent.putExtra("password", pswd);

					startActivity(MyIntent);
				}

			} catch (IllegalStateException e) {
				showToast("Couldn't authenticate with Dropbox:"
						+ e.getLocalizedMessage());
				Log.i(TAG, "Error authenticating", e);
			}
		}
	}

	private void showToast(String msg) {
		Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		error.show();
	}

	private String[] getKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(ACCESS_KEY_NAME, null);
		String secret = prefs.getString(ACCESS_SECRET_NAME, null);
		if (key != null && secret != null) {
			String[] ret = new String[2];
			ret[0] = key;
			ret[1] = secret;
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a
	 * local store, rather than storing user name & password, and
	 * re-authenticating each time (which is not to be done, ever).
	 */
	private void storeKeys(String key, String secret) {
		// Save the access key for later
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.putString(ACCESS_KEY_NAME, key);
		edit.putString(ACCESS_SECRET_NAME, secret);
		edit.commit();
	}

	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;

		String[] stored = getKeys();
		if (stored != null) {
			AccessTokenPair accessToken = new AccessTokenPair(stored[0],
					stored[1]);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE,
					accessToken);
		} else {
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}
		return session;
	}

	public DropboxAPI<AndroidAuthSession> getAPI() {
		return mApi;
	}

	private void setPreferences() {
		SharedPreferences userDetails = getSharedPreferences(USER_DETAILS_PREF,
				0);
		User user = new User(getApplicationContext());
		Editor edit = userDetails.edit();
		edit.clear();
		edit.putString("username", user.getUsername());
		String email = user.getEmail();
		edit.putString("email", email);
		DatabaseHelper db = new DatabaseHelper(getApplicationContext());
		edit.putString("emailpswd", db.getPassword(email));
		edit.putInt("idUser", db.getId(email));
		edit.commit();
		db.close();
	}
}