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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class FriendsList extends Activity{

	ArrayList<String> arr;
	private MioDatabaseHelper db;
	ListView listView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);
        arr = new ArrayList<String> ();
        int i;
        Intent theIntent = this.getIntent();
		String [] Friend = (String[])theIntent.getSerializableExtra("Friends");
        db = new MioDatabaseHelper(getApplicationContext());

		listView = (ListView) findViewById(R.id.list);
		
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
        		//arr.add ((String) listView.getItemAtPosition(position));
        		Log.d("ListViewActivity", "Hai selezionato " + listView.getItemAtPosition(position));
        		Log.d("ListViewActivity", "con id = " + itemId + " e position = " + position);
        		
        		
        		String selectedFromList =(String) (listView.getItemAtPosition(position).toString());
        		String[] contatto = selectedFromList.split(", user=");
        		arr.add(contatto[0].substring(7));
        		
        		//Operazioni da fare alla fine della scelta dei contatti
        		//Object[] array= arr.toArray();
        		//for(int j=0;j<array.length;j++)
        		//	Log.d("l'elemento dell'array",array[j].toString());
        	}
        	
        });
	
 		}	 
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add("Elimina amici").setOnMenuItemClickListener(new OnMenuItemClickListener() {
             public boolean onMenuItemClick(MenuItem item) {
            	 Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            	 Object[] array= arr.toArray();
            	 for(int j=0;j<array.length;j++){
            		 db.deleteFriend(array[j].toString());
            	 }
            	 String[] friends = db.getFriends();
            	 showFriends(friends);
            	 return true;
             }
		});;
     	
		menu.add("Condividi").setOnMenuItemClickListener(new OnMenuItemClickListener() {
             public boolean onMenuItemClick(MenuItem item) {
            	 Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            	 return true;
             }
		});;
		
		
     return true;
    }
	
	//IMPLEMENTARE I COMANDI DA ESEGUIRE; (Elimina amico, Condividi).
	
	
	public void showFriends(String[] Friends){

		
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		
		for (int j=0;j<Friends.length;j=j+2) {
		    
			Map<String, String> datum = new HashMap<String, String>(2);
		    datum.put("user",Friends[j]);
		    datum.put("email",Friends[j+1]);
		    data.add(datum);
		}
	  
		SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2,new String[] {"user", "email"},new int[] {android.R.id.text1,android.R.id.text2});
		listView.setAdapter(adapter);		
		
	}
}
