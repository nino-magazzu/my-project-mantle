package com.project.mantle_v1.notification_home;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.xml.sax.SAXException;
import com.project.mantle_v1.MantleFile;
import com.project.mantle_v1.R;
import com.project.mantle_v1.database.MioDatabaseHelper;
import com.project.mantle_v1.dropbox.DropboxAuth;
import com.project.mantle_v1.gmail.Sender;
import com.project.mantle_v1.parser.MantleMessage;
import com.project.mantle_v1.parser.ParseJSON;
import com.project.mantle_v1.xml.ReaderXml;
import com.project.mantle_v1.xml.WriterXml;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class NoteActivity extends Activity {

	static private String TAG;
	private final String USER_DETAILS_PREF = "user";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();

		setContentView(R.layout.add_comment);

		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("bundle");
		this.email = bundle.getString("email");
		this.username = bundle.getString("username");
		this.url = bundle.getString("url");
		this.filePath = bundle.getString("filePath");
		this.idFile = bundle.getString("idFile");
		this.cFile = null;

		Log.v(TAG, idFile);

		if (filePath == null)
			cFile = MantleFile.downloadFileFromUrl(url, idFile + ".xml");
		else
			cFile = new File(filePath);

		ReaderXml reader = new ReaderXml();
		try {
			reader.parseComment(cFile);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		notes = reader.getParsedData();

		cFile.delete();

		final NoteAdapter adapter = new NoteAdapter(getApplicationContext(),
				R.layout.note_layout, notes);

		((ListView) findViewById(R.id.listView1)).setAdapter(adapter);

		Button bComment = (Button) findViewById(R.id.commentButton);
		bComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText commentEditText = (EditText) findViewById(R.id.editText1);
				String comment = commentEditText.getText().toString();

				SharedPreferences userDetails = getSharedPreferences(
						USER_DETAILS_PREF, 0);

				if (email.equals(userDetails.getString("email", " "))) {
					WriterXml xml = new WriterXml();

					try {
						xml.addComment(
								username,
								new Date(System.currentTimeMillis()).toString(),
								comment, cFile);
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TransformerFactoryConfigurationError e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TransformerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					DropboxAuth auth = new DropboxAuth(getApplicationContext());
					MantleFile.uploadFile(cFile, auth.getAPI());
					MioDatabaseHelper db = new MioDatabaseHelper(
							getApplicationContext());

					ParseJSON parser = new ParseJSON(new StringWriter());
					Note note = new Note(username, comment, new Date(System
							.currentTimeMillis()).toString(), url);
					try {
						parser.writeJson(note);
					} catch (IOException ex) {
						Log.e(TAG, ex.getMessage());

					}

					String[] emails = db.getEmailsFilesShared(Integer
							.parseInt(idFile));
					for (int i = 0; i < emails.length; i++) {
						new Sender(NoteActivity.this, parser.toString(),
								emails[i], MantleMessage.NOTE).execute();

					}

				} else {
					ParseJSON parser = new ParseJSON(new StringWriter());
					Note note = new Note(username, comment, new Date(System
							.currentTimeMillis()).toString(), url);
					try {
						parser.writeJson(note);
					} catch (IOException ex) {
						Log.e(TAG, ex.getMessage());

					}
					new Sender(NoteActivity.this, parser.toString(), email,
							MantleMessage.NOTE).execute();

				}
				commentEditText.setText("");
				Note note = new Note(username, comment, new Date(System
						.currentTimeMillis()).toString());
				notes.add(note);
				adapter.notifyDataSetChanged();

			}
		});
	}

	private String username;
	private String email;
	private String url;
	private String filePath;
	private File cFile;
	private List<Note> notes;
	private String idFile;
}
