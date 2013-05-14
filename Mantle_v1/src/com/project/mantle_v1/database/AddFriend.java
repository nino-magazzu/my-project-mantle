package com.project.mantle_v1.database;

import java.io.IOException;
import java.io.StringWriter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.mantle_v1.R;
import com.project.mantle_v1.User;
import com.project.mantle_v1.gmail.Sender;
import com.project.mantle_v1.parser.MantleMessage;
import com.project.mantle_v1.parser.ParseJSON;

public class AddFriend extends Activity {

	private String TAG = this.getClass().getSimpleName();
	private MioDatabaseHelper db;
	private Button addFriend;
	private Button showFriends;
	private EditText edit_email;
	private String email;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add_friend);

		addFriend = (Button) findViewById(R.id.AddFriendButton);
		showFriends = (Button) findViewById(R.id.showFriendbutton);
		edit_email = (EditText) findViewById(R.id.emailAddFriendEditText);

		// Ottengo un puntatore al database
		db = new MioDatabaseHelper(getApplicationContext());

		addFriend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				email = edit_email.getText().toString();

				if (email.equals("") || !email.contains("@")) {
					// l'indirizzo è scritto male o non è stato riempito il
					// campo
					Toast error = Toast.makeText(AddFriend.this,
							"Invalid value", Toast.LENGTH_LONG);
					error.show();
				}

				else if (!db.isAlreadyFriend(email) && email.contains("@")) {
					Toast error = Toast.makeText(AddFriend.this,
							"Request is sent", Toast.LENGTH_LONG);
					error.show();

					ParseJSON parser = new ParseJSON(new StringWriter());
					try {
						parser.writeJson(new User(getApplicationContext()));
					} catch (IOException e) {
						Log.e(TAG, e.getMessage());
					}
					Log.d(TAG, parser.toString());
					db.close();
					new Sender(AddFriend.this, parser.toString(), email,
							MantleMessage.FRIENDSHIP_REQUEST).execute();
					finish();
				} else {
					// L'utente è gia tuo amico la richiesta non viene inviata
					Toast error = Toast.makeText(AddFriend.this,
							"Is already your friend", Toast.LENGTH_LONG);
					error.show();
				}
			}
		});

		showFriends.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AddFriend.this, FriendsList.class);
				startActivity(intent);
			}
		});

	}
}
