package com.project.mantle_v1;

import com.project.mantle_v1.database.MioDatabaseHelper;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddService extends Activity{
	
	private MioDatabaseHelper db;
	private Button addService;
	private EditText edit_service;
	private EditText edit_user;
	private EditText edit_password;
	private String service;
	private String user;
	private String password;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.add_service);
        
        addService = (Button) findViewById(R.id.addServicebutton);
        
        
        edit_user = (EditText)findViewById(R.id.usernameEditText);
        
        edit_password = (EditText)findViewById(R.id.passwordEditText);
        edit_service = (EditText)findViewById(R.id.serviceEditText);
        
        db = new MioDatabaseHelper(getApplicationContext());
        addService.setOnClickListener(new View.OnClickListener() {

        	@Override
			public void onClick(View arg0) {
        		service = edit_service.getText().toString();
        		user = edit_user.getText().toString();
        		password = edit_password.getText().toString();
        		
        		if(!service.equals("")||!user.equals("")||!password.equals("")){
        			db.insertService(service, user, password);
        			Toast mes = Toast.makeText(AddService.this,"insertion was successful", Toast.LENGTH_LONG);
					mes.show();
					edit_password.setText("");
					edit_service.setText("");
					edit_user.setText("");
        			db.showAll();
        		}
        		else {
        			Log.w("ADD_SERVICE","insert all the value");
        			Toast error = Toast.makeText(AddService.this,"Invalid value", Toast.LENGTH_LONG);
					error.show();
					}
			}
        	
    	});	
	}	

}
