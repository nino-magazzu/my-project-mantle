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
import com.project.mantle_v1.R;


public class HomeDatabase extends Activity{

	private MioDatabaseHelper myDatabase;
	private Button addFriend;
	private Button searchFriend;
	private EditText edit_name;
	private EditText edit_surname;
	private EditText edit_email;
	
	final static private int REQUEST_CODE = 9;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_db);
        
        addFriend = (Button) findViewById(R.id.AddFriendButton);
        searchFriend = (Button) findViewById(R.id.SearchFriendButton);
        edit_name = (EditText)findViewById(R.id.editName);
        edit_surname = (EditText)findViewById(R.id.editSurname);
        edit_email = (EditText)findViewById(R.id.editEmail);        
        
        
        Intent theIntent = this.getIntent();
        //Ottengo un puntatore al database
        final MioDatabaseHelper db = new MioDatabaseHelper(getApplicationContext());
        
        addFriend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				
			}
			
		});
    
        searchFriend.setOnClickListener(new View.OnClickListener() {

        	@Override
			public void onClick(View v) {
/*
        		String[] Friend, Contact;
        		
    		    Friend = myDatabase.showFrind(db);
      		Contact = myDatabase.showContact(db);

    	    	Intent intent = new Intent(HomeDatabase.this, FriendsList.class);
    	    	intent.putExtra("Friend", Friend);
    	    	intent.putExtra("Contact", Contact);
    	    	startActivityForResult(intent, REQUEST_CODE);
*/
        	}
        	
        });
        
        
    }
}
