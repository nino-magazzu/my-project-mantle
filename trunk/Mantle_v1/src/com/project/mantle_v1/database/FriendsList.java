package com.project.mantle_v1.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.SimpleAdapter;

public class FriendsList extends Activity{

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);
        int i;
        Intent theIntent = this.getIntent();
		String [] Friend = (String[])theIntent.getSerializableExtra("Friends");
		
		ListView listView = (ListView) findViewById(R.id.list);
        
		
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		
		for (i=0;i<Friend.length;i=i+2) {
		    Map<String, String> datum = new HashMap<String, String>(2);
		    datum.put("user",Friend[i]);
		    datum.put("email",Friend[i+1]);
		    data.add(datum);
		}
	  
		SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,new String[] {"user", "email"},new int[] {android.R.id.text1,android.R.id.text2});
		listView.setAdapter(adapter);		
		
		listView.setOnItemClickListener(new OnItemClickListener(){
	        
        	@Override
        	 public void onItemClick ( AdapterView<?> listView, View itemView, int position,long itemId ){
        		
        		Log.d("ListViewActivity", "Hai selezionato " + listView.getItemAtPosition(position));
        		Log.d("ListViewActivity", "con id = " + itemId + " e position = " + position);
        		
        	}
        	
        });
	
 		}	 


}
