package com.project.mantle_v1.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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

import com.project.mantle_v1.R;

public class FriendsList extends Activity {

	ArrayList<String> status;
	ArrayList<String> arr;
	private DatabaseHelper db;
	ListView listView;
	int flag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_list);

		arr = new ArrayList<String>();
		status = new ArrayList<String>();

		db = new DatabaseHelper(getApplicationContext());

		Intent intent = getIntent();
		flag = intent.getIntExtra("flag", 0);

		Log.d("FRIEND_LIST", String.valueOf(flag));

		String[] friends = db.getFriends();
		String[] teams = db.getTeams();
		listView = (ListView) findViewById(R.id.list);
		showFriends(friends, teams);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View itemView,
					int position, long itemId) {
				Log.d("ListViewActivity",
						"Hai selezionato "
								+ listView.getItemAtPosition(position));
				Log.d("ListViewActivity", "con id = " + itemId
						+ " e position = " + position);

				String selectedFromList = (listView.getItemAtPosition(position)
						.toString());
				String[] contatto = selectedFromList.split(", user=");
				String email = contatto[0].substring(7);

				if (email.contains("@")) {
					// apro le info dell'utente selezionato in base alla sua
					// email.
					Intent intent = new Intent(FriendsList.this,
							ShowFriend.class);
					intent.putExtra("email", email);
					startActivity(intent);
				} else {
					// apro le informazioni relative al gruppo selezionato
					String nameTeam = contatto[1];
					Log.d("ListViewActivity", "Hai cliccato su un gruppo : "
							+ nameTeam);
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

				// Ottengo la stringa relativa al subitem "email" per verificare
				// successivamente se è effettivamente un contatto o è la
				// stringa TEAM
				String selectedFromList = (listView.getItemAtPosition(position)
						.toString());
				String[] contatto = selectedFromList.split(", user=");
				String email = contatto[0].substring(7);

				// controllo se l'item è stato già selezionato
				if (status.contains(String.valueOf(position))) {
					Log.d("ListViewActivity",
							"L'elemento è gia inserito lo tolgo");

					// E' stato selezionato un utente che già era presente lo
					// elimino dall'array degli utenti selezionati
					if (email.contains("@")) {
						status.remove(status.indexOf(String.valueOf(position)));
						arr.remove(contatto[0].substring(7));
						Toast.makeText(
								getApplicationContext(),
								contatto[0].substring(7)
										+ " delete from receiver",
								Toast.LENGTH_SHORT).show();
					}
					// E' stato selezionato un team già presente
					else {
						// elimino la parte finale della striga relativa
						// all'item principale per ottenere il nome del gruppo
						int lastChar = contatto[1].indexOf("}");
						// ricavo le email degli utenti appartenenti al gruppo
						String[] emails = db.getMembers(contatto[1].substring(
								0, lastChar));
						// elimino le email
						for (int k = 0; k < emails.length; k++) {
							arr.remove(emails[k]);
							Toast.makeText(getApplicationContext(),
									emails[k] + " delete from receiver",
									Toast.LENGTH_SHORT).show();
						}
					}

				} else {
					// l'item non era stato selezionato lo aggiungo all'array
					// degli elementi selezionati
					status.add(String.valueOf(position));

					Log.d("ListViewActivity", "Sto inserendo il valore "
							+ position);

					Log.d("LIST_FRIENDS", email);

					// Verifico se l'item selezionato è un Team o un utente
					if (email.contains("@")) {
						// in contatto[0] viene salvato la prima parte della
						// stringa
						// fino a ",user=" in contatto[1] la restante parte
						// della
						// stringa
						// substring mi serve per eliminare "{email=" e mi
						// restituisce solo l'indirizzo email
						arr.add(email);

					}

					else if (email.equals("TEAM")) {
						int lastChar = contatto[1].indexOf("}");
						String[] emails = db.getMembers(contatto[1].substring(
								0, lastChar));

						for (int k = 0; k < emails.length; k++) {
							if (!arr.contains(emails[k])) {
								arr.add(emails[k]);
							}

						}
					}

					// Stampo un toast con tutti gliutenti che sono stati
					// selezionati
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
	 * FLAG: | Classe chimante | operazioni disponibili |
	 * 1)NotificationListAcivity(cancellare amici) 2)Team(cancellare
	 * amici,aggiungere utenti al gruppo) 3)Dropbox(cancellare amici,aggiungere
	 * utenti all'array della condivisione)
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add("Delete friends").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
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

		menu.add("Add friends").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(),
								item.getTitle(), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(FriendsList.this,
								AddFriend.class);
						startActivity(intent);
						return true;
					}
				});

		switch (flag) {

		case 2:
			menu.add("Add to team").setOnMenuItemClickListener(
					new OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							Toast.makeText(getApplicationContext(),
									item.getTitle(), Toast.LENGTH_SHORT).show();

							Intent intent = getIntent();
							int idTeam = intent.getIntExtra("idTeam", 0);

							if (arr.isEmpty()) {
								Toast.makeText(getApplicationContext(),
										"Select friends", Toast.LENGTH_SHORT)
										.show();
							} else {
								Object[] array = arr.toArray();
								db.insertMembers(array, idTeam);
								db.showAll();
								finish();
							}
							return true;
						}
					});
			;
			break;

		case 3:
			menu.add("Share").setOnMenuItemClickListener(
					new OnMenuItemClickListener() {
						@Override
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

		for (int k = 0; k < Team.length; k++) {
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        finish();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
}
