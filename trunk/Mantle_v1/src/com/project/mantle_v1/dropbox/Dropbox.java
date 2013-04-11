package com.project.mantle_v1.dropbox;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.ExecutionException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import com.project.mantle_v1.MyApplication;
import com.project.mantle_v1.R;
import com.project.mantle_v1.User;
import com.project.mantle_v1.database.FriendsList;
import com.project.mantle_v1.fileChooser.FileChooser;
import com.project.mantle_v1.gmail.Sender;
import com.project.mantle_v1.parser.MantleMessage;
import com.project.mantle_v1.parser.Media;
import com.project.mantle_v1.parser.ParseJSON;

public class Dropbox extends Activity {
	
	final static private String TAG = "Dropbox";

    final static private String APP_KEY = "6k7t4o9zc6jbz9n";
    final static private String APP_SECRET = "ln2raywl1xmqrd7";

    // Imponiano l'accesso dell'applicazione alla sola cartella dell'app 
    
    final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

    ///////////////////////////////////////////////////////////////////////////
    //                      End app-specific settings.                       //
    ///////////////////////////////////////////////////////////////////////////

    
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    
    final static private int UPLOAD_REQUEST_CODE = 10;
    final static private int DOWNLOAD_REQUEST_CODE = 9;
    final static private int FRIEND_CHOOSED_CODE = 8;
    
    final static private String FILE_DIR = "/storedFile/";
    final static protected String HOME_DIR = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Mantle";
    
    /*
     *  per rendere univoci i nomi delle chiavi passate
     *  è consigliato (la doc dice 'must') aggiungere il nome del package davanti al nome della variabile usando putExtra() 
     */
    
    
    private DropboxAPI<AndroidAuthSession> mApi;

    private boolean mLoggedIn;

    // Android widgets
    private Button login;
    private Button download;
    private Button upload;
    private TextView welcome;
    //private boolean isDownload;
    private ListOfFileDownloader dbFile;
    private Media mt;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    
        // We create a new AuthSession so that we can use the Dropbox API.
        AndroidAuthSession session = buildSession();
        
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        
        // Basic Android widgets
        setContentView(R.layout.dropbox_home);

        checkAppKeySetup();
        welcome = (TextView) findViewById(R.id.usernameTextView);
                
        login = (Button)findViewById(R.id.button3);

        login.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // This logs you out if you're logged in, or vice versa
                if (mLoggedIn) {
                    logOut();
                } else {
                    // Start the remote authentication
                    mApi.getSession().startAuthentication(Dropbox.this);
                }
            }
        });

        download = (Button)findViewById(R.id.download);

        download.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	startFileDownloadChooser();
            }
        });


        upload = (Button)findViewById(R.id.upload);

        upload.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	startFileUploadChooser();
            }
        });

        // Display the proper UI state if logged in or not
       setLoggedIn(mApi.getSession().isLinked());

    }
    
    /*
     * Procedura per carica un file nella propria cartella su dropbox
     */
    
    private void startFileUploadChooser() {
    	Intent intent = new Intent(this, FileChooser.class);
    	intent.putExtra("upload", true);
    	startActivityForResult(intent, UPLOAD_REQUEST_CODE);
    }
    
    /*
     * Procedura per scaricare un file dalla propria cartella su Dropbox o dalla cartella di un amico se si tratta di un file 
     * che ci è stato condiviso
     */
    
    private void startFileDownloadChooser() {
    	Descriptor[] dropboxFile = null; 
    	
    	dbFile = new ListOfFileDownloader(mApi, this);
    	dbFile.execute();
    	
    	try {
			dropboxFile = dbFile.get();
		} catch (InterruptedException e) {
			Log.i(TAG, "Error authenticating", e);
		} catch (ExecutionException e) {
			Log.i(TAG, "Error authenticating", e);
		}
    	
    	if(dropboxFile.length == 0) {
    		showToast("Nessun file trovato su dropbox");
    	}
    	
    	else { 
//    		showToast(String.valueOf(dropboxFile.length));
    		Intent intent = new Intent(this, FileChooser.class);
	    	intent.putExtra("upload", false);
	    	intent.putExtra("File", dropboxFile);
	    	startActivityForResult(intent, DOWNLOAD_REQUEST_CODE);
		}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	switch(requestCode) {
    	
    		case UPLOAD_REQUEST_CODE :
    			String filePath = data.getStringExtra("path");
    				if(filePath != null) {
    					if(filePath.compareTo("null") == 0)
    						finish();
    					else {
    						File file = new File(filePath);
    						Uploader upload = new Uploader(this, mApi, FILE_DIR, file, new User(getApplicationContext()).getUsername());
    						upload.execute();
    						try {
    							mt = upload.get();
    							((MyApplication) getApplicationContext()).media = mt;
    							Log.d(TAG, mt.getObjectType());
    						} catch (InterruptedException e) {
    							Log.i(TAG, "Error authenticating", e);
    						} catch (ExecutionException e) {
    							Log.i(TAG, "Error authenticating", e);
    						}
    						Intent intent = new Intent(this, FriendsList.class);
    						startActivityForResult(intent, FRIEND_CHOOSED_CODE);        			
    					}
    				}
    				break;
    	
    			case DOWNLOAD_REQUEST_CODE:
    				filePath = data.getStringExtra("path");
    				long size = data.getLongExtra("Size", 100);
    				showToast(filePath);
    				if(filePath != null) {
    					String savingPath = HOME_DIR;   
    					Downloader down = new Downloader(this, mApi, filePath, savingPath, size);
    					down.execute();
    				}
    				break;
    	
    			case FRIEND_CHOOSED_CODE:
    				Object[] contacts = (Object[]) data.getSerializableExtra("contacts");
    				Log.e(TAG, (String)contacts[0]);
    				//mt = new Media("Pino", "www", "10/12/12", "image/jpg", "large");
    				mt = ((MyApplication) getApplicationContext()).media;
    				Log.e(TAG, mt.getObjectType());
    				String body = "";
    				try {
    					body = new ParseJSON(new StringWriter()).writeJson(mt);
    				} catch (IOException e) {
    					Log.e(TAG, e.getMessage());
    				}
    				for(int j=0;j<contacts.length;j++){
    					Log.d("Dropbox", "Ho inviato la mail a " + contacts[j]);
    					Sender sender = new Sender(this, body, (String) contacts[j], MantleMessage.SHARING_PHOTO);	
    					sender.execute();
    				}
    				break;
    	
    			default :
    				Log.e(TAG, "Out Of Options. " + requestCode);
    				break;
    		}
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
                
                welcome = (TextView) findViewById(R.id.usernameTextView);

                setLoggedIn(true);
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }
    
    private void logOut() {
        // Remove credentials from the session
        mApi.getSession().unlink();
        welcome.setText("Nessun account ha effettuato l'accesso");
        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        setLoggedIn(false);
    }

    /**
     * Convenience function to change UI state based on being logged in
     */
    private void setLoggedIn(boolean loggedIn) {
    	mLoggedIn = loggedIn;
    	if (loggedIn) {
    		login.setText("Unlink from Dropbox");
    		
    		upload.setEnabled(true);
            download.setEnabled(true);
            /*
            Account account;
    		try {
    			account = mApi.accountInfo();
    			welcome.setText("Benvenuto" + account.displayName);
    		} catch (DropboxException e) {
    			welcome.setText("Nessun account ha effettuato l'accesso");
    			showToast(e.getMessage());
    		}
            */
    	} else {
    		login.setText("Link with Dropbox");
    		
    		upload.setEnabled(false);
            download.setEnabled(false);
            /*
            Account account;
    		try {
    			account = mApi.accountInfo();
    			welcome.setText("Benvenuto" + account.displayName);
    		} catch (DropboxException e) {
    			welcome.setText("Nessun account ha effettuato l'accesso");
    		}
         */   
    	}
    }

    private void checkAppKeySetup() {
        // Check to make sure that we have a valid app key
        if (APP_KEY.startsWith("CHANGE") ||
                APP_SECRET.startsWith("CHANGE")) {
            showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
            finish();
            return;
        }

        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + APP_KEY;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            showToast("URL scheme in your app's " +
                    "manifest is not set up correctly. You should have a " +
                    "com.dropbox.client2.android.AuthActivity with the " +
                    "scheme: " + scheme);
            finish();
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     *
     * @return Array of [access_key, access_secret], or null if none stored
     */
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
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }
    
    // Creazione sessione Dropbox
    
    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }
        return session;
    }
}