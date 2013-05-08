package com.project.mantle_v1;

import java.io.File;
import java.util.concurrent.ExecutionException;
import com.project.mantle_v1.database.MioDatabaseHelper;
import com.project.mantle_v1.dropbox.DropboxAuth;
import com.project.mantle_v1.dropbox.Uploader;
import com.project.mantle_v1.notification_home.NotificationListActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity {

	private Button forward;
	private EditText nameEditText;
	private EditText surnameEditText;
	private EditText emailEditText;
	private EditText emailPassEditText;
	//private EditText dropboxUserEditText;
	//private EditText dropboxPassEditText;
	private String username;
	private String password;
	private Context mContext;

	private final String USER_DETAILS_PREF = "user";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		nameEditText = (EditText) findViewById(R.id.nameEditText);
		surnameEditText = (EditText) findViewById(R.id.surnameEditText);
		emailEditText = (EditText) findViewById(R.id.emailEditText);
		//dropboxUserEditText = (EditText) findViewById(R.id.dropUsernameEditText);
		//dropboxPassEditText = (EditText) findViewById(R.id.dropboxPassEditText);
		emailPassEditText = (EditText) findViewById(R.id.emailPassEditText);
		forward = (Button) findViewById(R.id.forwardButton);

		final MioDatabaseHelper db = new MioDatabaseHelper(
				getApplicationContext());

		Intent theIntent = this.getIntent();
		username = theIntent.getStringExtra("username");
		Log.d("REGISTER", username);
		password = theIntent.getStringExtra("password");
		Log.d("REGISTER", password);

		mContext = this;
		
		forward.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				String name = nameEditText.getText().toString();
				String surname = surnameEditText.getText().toString();
				String email = emailEditText.getText().toString();
				String emailPass = emailPassEditText.getText().toString();
				//String dropboxUser = dropboxUserEditText.getText().toString();
				//String dropboxPass = dropboxPassEditText.getText().toString();

				if (!name.equals("") && !surname.equals("")
						&& !email.equals("") /*&& !dropboxPass.equals("")
						&& !dropboxUser.equals("")*/) {
					int id = (int) db.insertUser(email, username, name,
							surname, password);
					//db.insertService("Dropbox", dropboxUser, dropboxPass);
					db.insertService("Email", email, emailPass);
					
					//****************//
					
					db.insertService("mantle", username, password);
					
					//****************//
					
					db.showAll();
					
					
					File dir = new File(Environment.getExternalStorageDirectory() + "/Mantle/history");
					
					if(!dir.exists())
						dir.mkdirs();
					
					dir = new File(Environment.getExternalStorageDirectory() + "/Mantle/tmp");
					
					if(!dir.exists())
						dir.mkdirs();
					
					
					setPreferences(email, emailPass, id);

			//		MyHandler handler = new MyHandler(getApplicationContext());
				//	new ReaderTask(handler, email, emailPass).start();
					
					db.exportDB();
					
					DropboxAuth dropbox = new DropboxAuth(getApplicationContext());
					
					
					Uploader up = new Uploader(mContext, dropbox.getAPI(), "/StoredFile/", new File(Environment.getExternalStorageDirectory() + "/Mantle/db/Mantle"));
					up.execute();
					
					try {
						up.get();
					} catch (InterruptedException e) {
						Log.v("REGISTER", e.getMessage());
						e.printStackTrace();
					} catch (ExecutionException e) {
						Log.v("REGISTER", e.getMessage());
						e.printStackTrace();
					}
					
					db.close();
					
					Intent intent = new Intent(Register.this,
							NotificationListActivity.class);
					startActivity(intent);
				}

				else {
					Toast error = Toast.makeText(Register.this,
							"Insert all the values", Toast.LENGTH_LONG);
					error.show();
				}
			}

		});
	}

	private void setPreferences(String email, String emailpswd, int id) {
		SharedPreferences userDetails = getSharedPreferences(USER_DETAILS_PREF,
				0);
		Editor edit = userDetails.edit();
		edit.clear();
		edit.putString("username", username);
		edit.putString("email", email);
		// MioDatabaseHelper db = new
		// MioDatabaseHelper(getApplicationContext());
		edit.putString("emailpswd", emailpswd);
		edit.putInt("idUser", id);
		edit.commit();
		// db.close();
	}
}
