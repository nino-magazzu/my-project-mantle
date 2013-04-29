package com.project.mantle_v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.mantle_v1.database.FriendsList;
import com.project.mantle_v1.database.MioDatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class ShowMembers extends Activity {
	private MioDatabaseHelper db;
	private String teamName;
	private int idTeam;
	private ListView listView;
	ArrayList<String> arr;
	final static private int EDIT_TEAM_REQUEST_CODE = 11;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list);
		db = new MioDatabaseHelper(getApplicationContext());
		
		Intent intent = getIntent();
		teamName = intent.getStringExtra("teamName");
		Log.d("SHOW_MEMBERS", String.valueOf(teamName));
		idTeam = db.getIdTeam(teamName);
		
		arr = new ArrayList<String>();
		listView = (ListView) findViewById(R.id.list);
		String[] emails = db.getMembers(teamName);
		String[] info = db.getMembersInfo(emails);
		showInfo(info);
		
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> listView,
					View itemView, int position, long itemId) {
				
				//Ottengo la stringa relativa al subitem "email" per verificare successivamente se è effettivamente un contatto o è la stringa TEAM
				String selectedFromList = (String) (listView
						.getItemAtPosition(position).toString());
				String[] contatto = selectedFromList.split(", user=");
				String email =contatto[0].substring(7);
				Log.d("SHOW_MEMBERS", email);
				
				
				if (arr.contains(email)) {
					arr.remove(email);
				}
				else{
					arr.add(email);
				}
				return true;
			}
			
		});
		
	}
	
	
	
	public void showInfo(String[] Info) {

		List<Map<String, String>> data = new ArrayList<Map<String, String>>();

		for (int j = 0; j < Info.length; j = j + 2) {

			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("user", Info[j]);
			datum.put("email", Info[j + 1]);
			data.add(datum);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, data,
				android.R.layout.simple_list_item_2, new String[] { "user",
						"email" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		listView.setAdapter(adapter);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		menu.add("Elimina dal gruppo").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(),
								item.getTitle(), Toast.LENGTH_SHORT).show();

						Object[] array = arr.toArray();
						
						for (int j = 0; j < array.length; j++) {
							db.deleteMembers(String.valueOf(idTeam),array[j].toString());
						}
						String[] emails = db.getMembers(teamName);
						String[] info = db.getMembersInfo(emails);
						showInfo(info);
						return true;
					}
				});
		;
		
		menu.add("Aggiungi al gruppo").setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(
						ShowMembers.this,
						FriendsList.class);
				intent.putExtra("idTeam", idTeam);
				intent.putExtra("flag", 2);
				
				startActivityForResult(intent, EDIT_TEAM_REQUEST_CODE );
				return true;
			}
		});
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String[] emails = db.getMembers(teamName);
		String[] info = db.getMembersInfo(emails);
		showInfo(info);
		
	}
}
