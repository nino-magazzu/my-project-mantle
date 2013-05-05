package com.project.mantle_v1.dropbox;

import com.project.mantle_v1.R;
import com.project.mantle_v1.database.MioDatabaseHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Priority extends Activity{
	private RadioGroup radioPriorityGroup;
	private RadioButton radioButton;
	private Button okButton;
	private MioDatabaseHelper db;
	private int idFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_priority);
		radioPriorityGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		okButton = (Button)findViewById(R.id.button1);
		db = new MioDatabaseHelper(getApplicationContext());
		Intent intent = getIntent();
		idFile = intent.getIntExtra("idFile", 0);
		
		
		okButton.setOnClickListener(new OnClickListener() {
			 
			@Override
			public void onClick(View v) {
	 
			    // Ottieni il radio button selezionato dal radioGroup
				int selectedId = radioPriorityGroup.getCheckedRadioButtonId();
				radioButton = (RadioButton) findViewById(selectedId);
				String priority=(String) radioButton.getText();
				
				Log.d("PRIORITY","Priorità selezionata = " + priority);
				
				if(priority.equals("Bassa")){
					Log.d("PRIORITY", "Bassa");
					db.insertPriority(idFile, 0);
				}
					
				else if(priority.equals("Normale")){
					Log.d("PRIORITY", "Normale");
					db.insertPriority(idFile, 1);
				}
				else if(priority.equals("Alta")){
					Log.d("PRIORITY", "Alta");
					db.insertPriority(idFile, 2);
				}
				
				Toast.makeText(Priority.this,
					priority, Toast.LENGTH_SHORT).show();
	 
			}

		});

		
		
	}
	
}
