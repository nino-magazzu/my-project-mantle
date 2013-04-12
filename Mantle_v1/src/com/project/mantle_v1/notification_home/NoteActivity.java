package com.project.mantle_v1.notification_home;

import com.project.mantle_v1.R;

import android.app.Activity;
import android.os.Bundle;

public class NoteActivity extends Activity {

	final static private String TAG = NoteActivity.class.getName();
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ((ListView) rootView.findViewById(R.id.notification_detail))
		.setAdapter(new NoteAdapter(getApplicationContext(), R.layout.note_layout, mItem.getNotes()));
        
	}

	
}
