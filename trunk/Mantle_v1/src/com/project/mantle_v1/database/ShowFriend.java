package com.project.mantle_v1.database;

import com.project.mantle_v1.R;
import com.project.mantle_v1.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ShowFriend extends Activity{

	private MioDatabaseHelper db;
	private TextView tv_username;
	private TextView tv_name;
	private TextView tv_surname;
	private TextView tv_email;
	private TextView tv_key;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_sharing);
		db = new MioDatabaseHelper(getApplicationContext());

		tv_username = (TextView) findViewById(R.id.textViewUsername);
		tv_name = (TextView) findViewById(R.id.textViewName);
		tv_surname = (TextView) findViewById(R.id.textViewSurname);
		tv_email = (TextView) findViewById(R.id.textViewEmail);
		tv_key = (TextView) findViewById(R.id.textViewKey);
		
		
		Intent intent = getIntent();
		String email = intent.getStringExtra("email");
		User friend = new User();
		friend = db.getUser(email);
		
		tv_username.setText(friend.getUsername());
		tv_name.setText(friend.getName());
		tv_surname.setText(friend.getSurname());
		tv_email.setText(friend.getEmail());
		tv_key.setText(friend.getKey());
	}
}
