package com.project.mantle_v1.database;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
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
        
        myDatabase = new MioDatabaseHelper(this);
        final SQLiteDatabase db = myDatabase.getWritableDatabase();
        
        
        //cliccato il pulsante vengono eseguite le query di inseriemnto nel db
        addFriend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				
				String name = edit_name.getText().toString();
				String surname = edit_surname.getText().toString();
				String email = edit_email.getText().toString();
				long i =-1;
				long j =-1;
				
				
				if (TextUtils.isEmpty(name)||TextUtils.isEmpty(surname)||TextUtils.isEmpty(email)){
					Toast error = Toast.makeText(HomeDatabase.this, "Inserisci tutti i campi", Toast.LENGTH_LONG);
					error.show();
				}
				
				else {
					i = myDatabase.insertFriend(db,name, surname);
					j = myDatabase.insertContact(db, name, surname, email);
				}
			
				if(i>0&&j>0){
				
					edit_name.setText("");
					edit_surname.setText("");
					edit_email.setText("");
					
					Toast succes = Toast.makeText(HomeDatabase.this, "Inserimento eseguito", Toast.LENGTH_LONG);
					succes.show();
				}
				
				else{
				
					Toast error = Toast.makeText(HomeDatabase.this, "Inserimento non riuscito", Toast.LENGTH_LONG);
					error.show();
				}
			}
		});
    
        //cliccato il pulsante viene mostrata una lista di amici presenti nel db
        searchFriend.setOnClickListener(new View.OnClickListener() {

        	@Override
			public void onClick(View v) {

        		String[] Friend, Contact;
        		
    		    Friend = myDatabase.showFrind(db);
        		Contact = myDatabase.showContact(db);

    	    	Intent intent = new Intent(HomeDatabase.this, FriendsList.class);
    	    	intent.putExtra("Friend", Friend);
    	    	intent.putExtra("Contact", Contact);
    	    	startActivityForResult(intent, REQUEST_CODE);

        	}
        	
        });
        
        /*
        myDatabase.showFrind(db);
        myDatabase.showContact(db);
        myDatabase.showFile(db);
        
        myDatabase.deleteAll(db);
        */
        
    }
}
