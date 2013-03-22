package com.project.mantle_v1;

import com.project.mantle_v1.database.MioDatabaseHelper;
import com.project.mantle_v1.notification_home.NotificationListActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity{
	
	private Button forward;
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText emailEditText;
    private EditText emailPassEditText;
    private EditText dropboxUserEditText;
    private EditText dropboxPassEditText;
    private String username;
    private String password;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        surnameEditText = (EditText) findViewById(R.id.surnameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        dropboxUserEditText = (EditText) findViewById(R.id.dropUsernameEditText);
        dropboxPassEditText = (EditText) findViewById(R.id.dropboxPassEditText);
        emailPassEditText = (EditText) findViewById(R.id.emailPassEditText);
        forward = (Button) findViewById(R.id.forwardButton);
        
        
        final MioDatabaseHelper db = new MioDatabaseHelper(getApplicationContext());
        
        Intent theIntent = this.getIntent();
		username = (String)theIntent.getSerializableExtra("username");
		Log.d("REGISTER", username);
		password = (String)theIntent.getSerializableExtra("password");
		Log.d("REGISTER", password);
		
		
		
		forward.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				
				
				String name = nameEditText.getText().toString();
				String surname = surnameEditText.getText().toString();
				String email = emailEditText.getText().toString();
				String emailPass = emailPassEditText.getText().toString();
				String dropboxUser = dropboxUserEditText.getText().toString();
				String dropboxPass = dropboxPassEditText.getText().toString();
				
				
				if(!name.equals("")&&!surname.equals("")&&!email.equals("")&&!dropboxPass.equals("")&&!dropboxUser.equals("")){
					db.insertUser(email, username, name, surname, password);
					db.insertService("Dropbox", dropboxUser,dropboxPass);
					db.insertService("Email", email, emailPass);
					db.showAll();
					Intent intent = new Intent(Register.this, NotificationListActivity.class);//Home.class);
		    	    startActivity(intent);
				}
				
				else{
					Toast error = Toast.makeText(Register.this,"Insert all the values", Toast.LENGTH_LONG);
					error.show();
					}
			}
			
		});
	}
}
