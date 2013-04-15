package com.project.mantle_v1.notification_home;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import com.project.mantle_v1.MyApplication;
import com.project.mantle_v1.R;
import com.project.mantle_v1.database.AddFriend;
import com.project.mantle_v1.gmail.Sender;
import com.project.mantle_v1.parser.MantleMessage;
import com.project.mantle_v1.parser.ParseJSON;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class NoteActivity extends Activity {

	final static private String TAG = NoteActivity.class.getName();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_comment);
        
        Intent intent = getIntent();
        List<Note> notes = (List<Note>) intent.getSerializableExtra("notes");
        
        ((ListView) findViewById(R.id.listView1))
		.setAdapter(new NoteAdapter(getApplicationContext(), R.layout.note_layout, notes));
        
        Button bComment = (Button) findViewById(R.id.button1);
        bComment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText commentEditText = (EditText) findViewById(R.id.editText1);
				String comment = commentEditText.getText().toString();
				// TODO: email del proprietario del file
				
				ParseJSON parser = new ParseJSON(new StringWriter());
				Note note = new Note(((MyApplication) getApplicationContext()).username, comment, new Date(System.currentTimeMillis()).toString());
				try {
					parser.writeJson(note);
				} catch(IOException ex) {
					Log.e(TAG, ex.getMessage());
					
				}
				new Sender(NoteActivity.this, parser.toString(), ((MyApplication) getApplicationContext()).email, MantleMessage.NOTE).execute();
				
			}
		});
	}

	
}
