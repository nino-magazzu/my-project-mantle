package com.project.mantle_v1.login;

import com.project.mantle_v1.R;
import com.project.mantle_v1.database.MioDatabaseHelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity {

	private Button bOk;
	private Button bEsci;
	private EditText edit_username;
	private EditText edit_email;
	private EditText edit_password;
	private MioDatabaseHelper myDatabase;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.login);
	        
//	        myDatabase = new MioDatabaseHelper(this);
	        
	        edit_username = (EditText) findViewById(R.id.usernameText);
	        edit_email = (EditText) findViewById(R.id.emailText);
	        edit_password = (EditText) findViewById(R.id.passwordText);
	        
	        bOk = (Button) findViewById(R.id.LOGIN_OK);
	        bEsci = (Button) findViewById(R.id.LOGIN_EXIT);
	        
	        bOk.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					String username = edit_username.getText().toString();
					String email = edit_email.getText().toString();
					String password = edit_password.getText().toString();
				
					//--chiama il metodo inizializzazione mio databaseHelper
						// se restituisce true parte la home (con notifiche immagine ecc..)
						// se restituisce false scelta di un servizio di storage
					
 				}
			});
	        
	        bEsci.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
				}
			});
	 }
	 	
}
