package com.project.mantle_v1.database;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.mantle_v1.Home;
import com.project.mantle_v1.R;
import com.project.mantle_v1.dropbox.Uploader;
import com.project.mantle_v1.gmail.Sender;

public class AddFriend extends Activity{

	private MioDatabaseHelper db;
	private Button addFriend;
	private Button showFriends;
	private EditText edit_email;
	private String email;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.add_friend);
        
        addFriend = (Button) findViewById(R.id.AddFriendButton);
        showFriends =(Button) findViewById(R.id.showFriendbutton);
        edit_email = (EditText)findViewById(R.id.emailAddFriendEditText);
        
        //Ottengo un puntatore al database
        db = new MioDatabaseHelper(getApplicationContext());
        
        addFriend.setOnClickListener(new View.OnClickListener() {
			
        	public void onClick(View v) {
				
				email = edit_email.getText().toString();
				
				if(email.equals("")||!email.contains("@")){
					Toast error = Toast.makeText(AddFriend.this,"Invalid value", Toast.LENGTH_LONG);
					error.show();
				}
				
				else if(!db.isAlreadyFriend(email)&&email.contains("@")){
					Toast error = Toast.makeText(AddFriend.this,"Request is sent", Toast.LENGTH_LONG);
					error.show();
					Sender upload = new Sender(AddFriend.this,"RICHIESTA DI AMICIZIA","cann.alberto91@gmail.com");
        			upload.execute();
        			
					//manda la richiesta d'amicizia
				}
					else{
						//L'utente è gia tuo amico la richiesta non viene inviata
						Toast error = Toast.makeText(AddFriend.this,"Is already your friend", Toast.LENGTH_LONG);
						error.show();
						
					}				
        	}
		});
    
        showFriends.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String[] friends = db.getFriends();
				Intent intent = new Intent(AddFriend.this,FriendsList.class);
	    	    intent.putExtra("Friends", friends);
				startActivity(intent);
				
			}
        });	
        
    }
    
}
