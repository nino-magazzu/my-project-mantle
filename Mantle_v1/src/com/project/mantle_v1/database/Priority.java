package com.project.mantle_v1.database;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.project.mantle_v1.MantleFile;
import com.project.mantle_v1.R;

public class Priority extends Activity {
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
		okButton = (Button) findViewById(R.id.button1);
		db = new MioDatabaseHelper(getApplicationContext());
		Intent intent = getIntent();
		idFile = intent.getIntExtra("idFile", 0);

		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// Ottieni il radio button selezionato dal radioGroup
				int selectedId = radioPriorityGroup.getCheckedRadioButtonId();
				radioButton = (RadioButton) findViewById(selectedId);
				String priority = (String) radioButton.getText();

				Log.d("PRIORITY", "Priorità selezionata = " + priority);

				if (priority.equals("Bassa")) {
					db.insertPriority(idFile, MantleFile.USELESS_FILE);
					Log.d("PRIORITY", "Bassa");
				}

				else if (priority.equals("Normale")) {
					Log.d("PRIORITY", "Normale");
					// non inserisco questo valore di priorità, perche di
					// default ha già questo valore
				} else if (priority.equals("Alta")) {
					db.insertPriority(idFile, MantleFile.NEEDFUL_FILE);
					Log.d("PRIORITY", "Alta");

				}
				db.close();
				setResult(RESULT_OK);
				Log.v("PRIORITY", "Chiusura priority");
				finish();
			}
		});
	}
}