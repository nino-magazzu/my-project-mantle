package com.project.mantle_v1.login;

import com.project.mantle_v1.R;
import com.project.mantle_v1.database.FriendsList;
import com.project.mantle_v1.database.HomeDatabase;
import com.project.mantle_v1.database.MioDatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity {

	private Button bOk;
	private Button bEsci;
	private EditText edit_username;
	private EditText edit_email;
	private EditText edit_password;
	private MioDatabaseHelper db;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.login);
	        
	        db = new MioDatabaseHelper(this);
	        
	        edit_username = (EditText) findViewById(R.id.usernameText);
	        edit_email = (EditText) findViewById(R.id.emailText);
	        edit_password = (EditText) findViewById(R.id.passwordText);
	        
	        bOk = (Button) findViewById(R.id.LOGIN_OK);
	        bEsci = (Button) findViewById(R.id.LOGIN_EXIT);
	        
	        bOk.setOnClickListener(new View.OnClickListener() {
	        	
				@Override
				public void onClick(View v) {
					
					
					String mUsername = edit_username.getText().toString();
					String mPassword = edit_password.getText().toString();
					
					///////////////
					//db.deleteAll();
					db.showAll();
					///////////////
					
					String[] res = db.login();
					
					///////////////
					Log.d("LOGIN","res[0]= " + res[0] + "res[1]= " + res[1]);
					Log.d("LOGIN","username = " + mUsername+"password = "+mPassword);
					///////////////
					
					if(res[0].equals("")){
						///////////////
						Log.d("LOGIN","l'utente non è registrato");
						///////////////
						
						// Inserisco i dati username e password come nuovo db e 
						// avvio il form per la registrazione
					}
					
					if(res[0].equals(mUsername)&&res[1].equals(mPassword)){
						///////////////						
						Log.d("LOGINNNNNN :)", "Le stringe sono uguli");
						///////////////
						// Avviare il login
						}
					if((!res[0].equals(mUsername)||!res[1].equals(mPassword))&&(!res[0].equals(""))){
						///////////////
						Log.d("LOGGGG :(", "le stringe sono diverse");
						///////////////	
						}
					///////////////
					Log.d("LOGGIN","log che non deve essere mai raggiunto");
					///////////////
					
				
 				}
			});
	        
	        bEsci.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					db.insertService("mantle", "a","s");
					db.showAll();
					//finish();
				}
			});
	 }
	 	
}
