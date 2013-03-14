package com.project.mantle_v1.database;

import com.project.mantle_v1.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FriendsList extends Activity{

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);

        Intent theIntent = this.getIntent();
		String [] Friend = (String[])theIntent.getSerializableExtra("Friend");
		
		final String [] Contact = (String[])theIntent.getSerializableExtra("Contact");
		
		// Paramenter
        // First - Context
        // Second - Layout for the row
        // Third - ID of the TextView to which the data is written
        // Forth - the Array of data
        
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, Friend);
        ListView listView = (ListView) findViewById(R.id.list);
        // Assign adapter to ListView
        
        listView.setAdapter(adapter); 
        
        listView.setOnItemClickListener(new OnItemClickListener(){
        
        	@Override
        	 public void onItemClick ( AdapterView<?> listView, View itemView, int position,long itemId ){
        		
        		String contact = Contact[position];
        		
        		Intent data = new Intent();
        		data.putExtra("Contact", contact);
        		setResult(1, data);
        		
        		Log.d("ListViewActivity", "Hai selezionato " + listView.getItemAtPosition(position));
        		Log.d("ListViewActivity", "con id = " + itemId + " e position = " + position);
        		
        		for(int c = 0;c<Contact.length;c++)
        		{
        			Log.d("Contact", Contact[c]);
        		}
        	}
        });
 		}	 


}
