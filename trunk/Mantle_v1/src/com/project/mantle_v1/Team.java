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
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class Team extends Activity {
	private Button createTeam;
	private EditText teamName;
	private MioDatabaseHelper db;
	private ListView listView;
	ArrayList<String> arr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.create_team);

		db = new MioDatabaseHelper(getApplicationContext());
		createTeam = (Button) findViewById(R.id.createTeambutton);
		teamName = (EditText) findViewById(R.id.editText1);
		listView = (ListView) findViewById(R.id.listView1);
		arr = new ArrayList<String>();

		showTeams(db.getTeams());

		createTeam.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (db.teamExist(teamName.getText().toString())) {
					Toast mes = Toast.makeText(Team.this,
							"Team already exists", Toast.LENGTH_LONG);
					mes.show();
				} else {
					int idTeam = (int) db.insertTeam(teamName.getText()
							.toString());
					db.showAll();
					teamName.setText("");
					Toast mes = Toast.makeText(Team.this,
							"insertion was successful", Toast.LENGTH_LONG);
					mes.show();
					Intent intent = new Intent(Team.this, FriendsList.class);
					intent.putExtra("idTeam", idTeam);
					intent.putExtra("flag", 2);

					startActivity(intent);
				}
			}

		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View itemView,
					int position, long itemId) {

				String selectedFromList = (String) (listView
						.getItemAtPosition(position).toString());
				int lastChar = selectedFromList.indexOf("}");
				// elimino dalla stringa selezionata i caratteri {team=...}
				selectedFromList = selectedFromList.substring(6, lastChar);
				Intent intent = new Intent(Team.this, ShowMembers.class);
				intent.putExtra("teamName", selectedFromList);
				startActivity(intent);
				Log.d("TEAM", selectedFromList);

			}

		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> listView,
					View itemView, int position, long itemId) {
				String selectedFromList = (String) (listView
						.getItemAtPosition(position).toString());
				int lastChar = selectedFromList.indexOf("}");
				// elimino dalla stringa selezionata i caratteri {team=...}
				selectedFromList = selectedFromList.substring(6, lastChar);
				if (arr.contains(selectedFromList)) {
					arr.remove(selectedFromList);
				} else {
					arr.add(selectedFromList);
				}
				return true;
			}

		});

	}

	public void showTeams(String[] Team) {

		List<Map<String, String>> data = new ArrayList<Map<String, String>>();

		for (int k = 0; k < Team.length; k++) {
			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("team", Team[k]);
			data.add(datum);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, data,
				android.R.layout.simple_list_item_1, new String[] { "team" },
				new int[] { android.R.id.text1 });
		listView.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Elimina gruppo").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(),
								item.getTitle(), Toast.LENGTH_SHORT).show();

						if (arr.size() == 0) {
							Toast.makeText(getApplicationContext(),
									"Select a team", Toast.LENGTH_SHORT).show();
						} else {
							Object[] array = arr.toArray();

							for (int j = 0; j < array.length; j++) {
								db.deleteTeam(array[j].toString());
							}
							showTeams(db.getTeams());
						}
						return true;
					}
				});

		return true;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		showTeams(db.getTeams());

	}
}
