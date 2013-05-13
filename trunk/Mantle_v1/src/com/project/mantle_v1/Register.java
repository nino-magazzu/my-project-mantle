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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//Questa Activity fornisce un interfaccia per gestire la registrazione di un utente
public class Register extends Activity {

	private Button forward;
	private EditText nameEditText;
	private EditText surnameEditText;
	private EditText emailEditText;
	private EditText emailPassEditText;
	// private EditText dropboxUserEditText;
	// private EditText dropboxPassEditText;
	private String username;
	private String password;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		nameEditText = (EditText) findViewById(R.id.nameEditText);
		surnameEditText = (EditText) findViewById(R.id.surnameEditText);
		emailEditText = (EditText) findViewById(R.id.emailEditText);
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

				//verifica non ci siano dei campi vuoti
				if (!name.equals("") && !surname.equals("")
						&& !email.equals("")) {
					
					/*
					 *  TODO: generazione della chiave pubblica dell'utente
					 *  
					 */
					
					String publicKey = "";
					
					//Inserimento dei valori nel db
					int id = (int) db.insertUser(email, username, name,
							surname, publicKey);
					
					db.insertService("Email", email, emailPass);

					// ****************//

					/*
					 *  la variabile password oltre a contenere quella che è 
					 *  la chiave di accesso all'applicazione Mantle
					 *  conterrà quella che per adesso sarà la chiave privata 
					 *  dell'utente
					 */
					
					db.insertService("mantle", username, password);

					// ****************//

					db.showAll();
					
					//Vengono create le cartelle che servono per il funzionamento dell'applicazione
					File dir = new File(MantleFile.DIRECTORY_HISTORY);

					if (!dir.exists())
						dir.mkdirs();

					dir = new File(MantleFile.DIRECTORY_TEMP);

					if (!dir.exists())
						dir.mkdirs();

					setPreferences(email, emailPass, id);
					
					//Viene esportato il db appena creato e caricato su dropbox
					db.exportDB();

					DropboxAuth dropbox = new DropboxAuth(
							getApplicationContext());

					File file = new File(MantleFile.DIRECTORY_DB, MioDatabaseHelper.DB_NAME);
					
					Uploader up = new Uploader(mContext, dropbox.getAPI(),
							"/StoredFile/", file);
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
					
					//Viene lanciata la home
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

	//Viene settato un oggetto SharedPreferences che contiene 
	// le principali informazioni dell'utente che devono avere visibilità globale  
	private void setPreferences(String email, String emailpswd, int id) {
		SharedPreferences userDetails = getSharedPreferences(User.USER_DETAILS_PREF,
				0);
		Editor edit = userDetails.edit();
		edit.clear();
		edit.putString("username", username);
		edit.putString("email", email);
		edit.putString("emailpswd", emailpswd);
		edit.putString("privateKey", password);
		edit.putInt("idUser", id);
		edit.commit();
	}
}
