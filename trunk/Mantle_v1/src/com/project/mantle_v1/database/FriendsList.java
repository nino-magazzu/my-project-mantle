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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class FriendsList extends Activity {

	ArrayList<String> status;
	ArrayList<String> arr;
	private MioDatabaseHelper db;
	ListView listView;
	int flag;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list);
		
		arr = new ArrayList<String>();
		status = new ArrayList<String>();

		db = new MioDatabaseHelper(getApplicationContext());
		
		Intent intent = getIntent();
		flag = intent.getIntExtra("flag", 0);
		
		Log.d("FRIEND_LIST", String.valueOf(flag));
		
		String[] friends = db.getFriends();
		String[] teams = db.getTeams();
		listView = (ListView) findViewById(R.id.list);
		showFriends(friends,teams);

		
		listView.setOnItemClickListener(new OnItemClickListener(){
	        
        	@Override
        	 public void onItemClick ( AdapterView<?> listView, View itemView, int position,long itemId ){
        		Log.d("ListViewActivity", "Hai selezionato " + listView.getItemAtPosition(position));
        		Log.d("ListViewActivity", "con id = " + itemId + " e position = " + position);

        		
        		String selectedFromList =(String) (listView.getItemAtPosition(position).toString());
        		String[] contatto = selectedFromList.split(", user=");
        		String email =contatto[0].substring(7);
        		
        		if(email.contains("@")){
        			//apro le info dell'utente selezionato in base alla sua email.
            		Intent intent = new Intent(FriendsList.this, ShowFriend.class);
            		intent.putExtra("email", email);
            		startActivity(intent);	
        		}
        		else{
        			String nameTeam = contatto[1];
        			Log.d("ListViewActivity", "Hai cliccato su un gruppo : " + nameTeam);
        		}
        	}
        	
        });
		

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> listView,
					View itemView, int position, long itemId) {
				Log.d("ListViewActivity",
						"Hai tenuto premuto  "
								+ listView.getItemAtPosition(position));
				Log.d("ListViewActivity", "con id = " + itemId
						+ " e position = " + position);
				
				String selectedFromList = (String) (listView
						.getItemAtPosition(position).toString());
				String[] contatto = selectedFromList.split(", user=");
				
				
				if (status.contains(String.valueOf(position))) {
					Log.d("ListViewActivity",
							"L'elemento è gia inserito lo tolgo");
					
					status.remove(status.indexOf(String.valueOf(position)));
					
					arr.remove(contatto[0].substring(7));

					Toast.makeText(getApplicationContext(),
							contatto[0].substring(7) + " delete from receiver",
							Toast.LENGTH_SHORT).show();
				} else {
					status.add(String.valueOf(position));
					
					Log.d("ListViewActivity", "Sto inserendo il valore "
							+ position);

					// in contatto[0] viene salvato la prima parte della stringa
					// fino a ",user=" in contatto[1] la restante parte della
					// stringa
					// substring mi serve per eliminare "{email=" e mi
					// restituisce solo l'indirizzo email
					arr.add(contatto[0].substring(7));

					String receiver = "Receiver = ";
					for (int j = 0; j < arr.size(); j++) {
						receiver = receiver + arr.get(j) + ", ";
					}

					Toast.makeText(getApplicationContext(), receiver,
							Toast.LENGTH_SHORT).show();
				}

				return true;
			}

		});

	}
	
	/*
	 * FLAG:
	 * | Classe chimante | operazioni disponibili |
	 * 1)NotificationListAcivity(cancellare amici)
	 * 2)Team(cancellare amici,aggiungere utenti al gruppo)
	 * 3)Dropbox(cancellare amici,aggiungere utenti all'array della condivisione)
	 */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add("Elimina amici").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(),
								item.getTitle(), Toast.LENGTH_SHORT).show();
						Object[] array = arr.toArray();
						for (int j = 0; j < array.length; j++) {
							db.deleteFriend(array[j].toString());
						}
						String[] friends = db.getFriends();
						showFriends(friends);
						return true;
					}
				});
		;
		
		
		switch(flag){

			case 2 : menu.add("Aggiungi al gruppo").setOnMenuItemClickListener(
					new OnMenuItemClickListener() {
						public boolean onMenuItemClick(MenuItem item) {
							Toast.makeText(getApplicationContext(),
									item.getTitle(), Toast.LENGTH_SHORT).show();
							
							Intent intent = getIntent();
							int idTeam = intent.getIntExtra("idTeam", 0);
						
							if (arr.isEmpty()) {
								Toast.makeText(getApplicationContext(),
									"Select friends", Toast.LENGTH_SHORT).show();
							}
							else{
								Object[] array = arr.toArray();
								db.insertMembers(array, idTeam);
								db.showAll();
							}
							return true;
						}
					});
			;
		break;

			case 3 : menu.add("Condividi").setOnMenuItemClickListener(
					new OnMenuItemClickListener() {
						public boolean onMenuItemClick(MenuItem item) {
							Toast.makeText(getApplicationContext(),
									item.getTitle(), Toast.LENGTH_SHORT).show();

							if (arr.isEmpty()) {

							}
							Object[] array = arr.toArray();

							Intent data = new Intent();
							data.putExtra("contacts", array);
							setResult(8, data);
							finish();
							return true;
						}
					});
			;
			break;
		}
		return true;
	}

	// IMPLEMENTARE I COMANDI DA ESEGUIRE; (Elimina amico, Condividi).

	public void showFriends(String[] Friends) {

		List<Map<String, String>> data = new ArrayList<Map<String, String>>();

		for (int j = 0; j < Friends.length; j = j + 2) {

			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("user", Friends[j]);
			datum.put("email", Friends[j + 1]);
			data.add(datum);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, data,
				android.R.layout.simple_list_item_2, new String[] { "user",
						"email" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		listView.setAdapter(adapter);

	}
	public void showFriends(String[] Friends, String[] Team) {

		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		
		
		for (int j = 0; j < Friends.length; j = j + 2) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("user", Friends[j]);
			datum.put("email", Friends[j + 1]);
			data.add(datum);
		}

		for(int k = 0;k < Team.length;k++){
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("user", Team[k]);
			datum.put("email", "TEAM");
			data.add(datum);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, data,
				android.R.layout.simple_list_item_2, new String[] { "user",
						"email" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		listView.setAdapter(adapter);

	}
}
