package com.project.mantle_v1;

import com.project.mantle_v1.database.FriendsList;
import com.project.mantle_v1.database.MioDatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Team extends Activity{
	private Button createTeam;
	private EditText teamName;
	private MioDatabaseHelper db;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.create_team);
		
		db = new MioDatabaseHelper(getApplicationContext());
		createTeam = (Button)findViewById(R.id.createTeambutton);
		
		teamName = (EditText) findViewById(R.id.editText1);

		createTeam.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(db.teamExist(teamName.getText().toString())){
					Toast mes = Toast.makeText(Team.this,
							"Team already exists", Toast.LENGTH_LONG);
					mes.show();
				}
				else {
					int idTeam = (int)db.insertTeam(teamName.getText().toString());
					db.showAll();
					teamName.setText("");
					Toast mes = Toast.makeText(Team.this,
							"insertion was successful", Toast.LENGTH_LONG);
					mes.show();
					Intent intent = new Intent(
							Team.this,
							FriendsList.class);
					intent.putExtra("idTeam", idTeam);
					intent.putExtra("flag", 2);
					
					startActivity(intent);
				}
			}

		});

	}
}
