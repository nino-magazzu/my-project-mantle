package com.project.mantle_v1.fileNavigator;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Toast;

import com.project.mantle_v1.R;
import com.project.mantle_v1.database.FriendsList;
import com.project.mantle_v1.database.DatabaseHelper;
import com.project.mantle_v1.database.User;
import com.project.mantle_v1.dropbox.DropboxAuth;
import com.project.mantle_v1.gmail.Sender;
import com.project.mantle_v1.notification_home.MyHandler;
import com.project.mantle_v1.notification_home.Note;
import com.project.mantle_v1.notification_home.NoteActivity;
import com.project.mantle_v1.notification_home.NotificationDetailFragment;
import com.project.mantle_v1.parser.MantleMessage;
import com.project.mantle_v1.parser.ParseJSON;
import com.project.mantle_v1.xml.WriterXml;

/**
 * An activity representing a single File detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link FileListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link FileDetailFragment}.
 */
public class FileDetailActivity extends FragmentActivity {

	final static private int FRIEND_CHOOSED_CODE = 8;
	protected static final int DIALOG_ALERT_ID = 1;
	private final String TAG = this.getClass().getSimpleName();
	private MantleFile file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_detail);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();

			if (getIntent().hasExtra(FileDetailFragment.ARG_ITEM_ID)) {
				arguments.putString(FileDetailFragment.ARG_ITEM_ID, getIntent()
						.getStringExtra(FileDetailFragment.ARG_ITEM_ID));

				file = MyHandler.FILE_MAP.get(getIntent().getStringExtra(
						FileDetailFragment.ARG_ITEM_ID));
			}

			else if (getIntent().hasExtra(
					NotificationDetailFragment.ARG_ITEM_ID)) {
				arguments.putString(
						NotificationDetailFragment.ARG_ITEM_ID,
						getIntent().getStringExtra(
								NotificationDetailFragment.ARG_ITEM_ID));

				if (getIntent().hasExtra("Commento")) {
					Log.d(TAG, "");
					showMyDialog();
				}

				file = new MantleFile(getApplicationContext(), getIntent()
						.getStringExtra(NotificationDetailFragment.ARG_ITEM_ID));

			}

			FileDetailFragment fragment = new FileDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.file_detail_container, fragment).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpTo(this,
					new Intent(this, FileListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add("Commenti").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {

						file.downloadFileFromUrl(MantleFile.COMMENT,
								file.getIdFile() + ".xml",
								MantleFile.DIRECTORY_TEMP);

						Intent myIntent = new Intent(getApplicationContext(),
								NoteActivity.class);
						Bundle bundle = new Bundle();
						DatabaseHelper db = new DatabaseHelper(
								getApplicationContext());
						SharedPreferences userDetails = getApplicationContext()
								.getSharedPreferences(User.USER_DETAILS_PREF, 0);
						String username = userDetails
								.getString("username", " ");
						bundle.putString("username", username);
						bundle.putString("url", file.getLinkComment());
						bundle.putString("email",
								db.getEmailFromUrl(file.getLinkComment()));
						bundle.putString("filePath", file.getmFile()
								.getAbsolutePath());
						bundle.putString("idFile", file.getIdFile());
						myIntent.putExtra("bundle", bundle);
						db.close();
						startActivity(myIntent);
						return true;

					}
				});
		
		if(file.getPriority() != MantleFile.NOT_OWN_FILE) {
			menu.add("Condividi").setOnMenuItemClickListener(
					new OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							Intent intent = new Intent(getApplicationContext(),
									FriendsList.class);
							intent.putExtra("flag", 3);
							startActivityForResult(intent, FRIEND_CHOOSED_CODE);
							return true;
						}
					});
		}
		menu.add("Download").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						
						file.downloadFileFromUrl(
								MantleFile.FILE,
								file.getFileName(),
								Environment.getExternalStoragePublicDirectory(
										Environment.DIRECTORY_DOWNLOADS)
										.getAbsolutePath());
						showToast("Download completato!");
						return true;
					}
				});

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case FRIEND_CHOOSED_CODE:
			Log.v(TAG, "FRIEND_CHOOSED_CODE)");
			Object[] contacts = (Object[]) data
					.getSerializableExtra("contacts");
			if (contacts != null) {
				String body = "";
				try {
					body = new ParseJSON(new StringWriter()).writeJson(file);
				} catch (IOException e) {
					Log.e(TAG, "--> " + e.getMessage());
				}
				DatabaseHelper db = new DatabaseHelper(
						getApplicationContext());

				for (int j = 0; j < contacts.length; j++) {
					Log.v("Dropbox", "--> " + "Ho inviato la mail a "
							+ contacts[j]);
					db.insertShare(Integer.parseInt(file.getIdFile()),
							(String) contacts[j]);
					if (file.isImage())
						new Sender(this, body, (String) contacts[j],
								MantleMessage.SHARING_PHOTO).execute();
					else
						new Sender(this, body, (String) contacts[j],
								MantleMessage.SHARING_FILE).execute();
				}
				db.close();
			}
			break;
		}
	}

	protected void showMyDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Conferma");
		final Note note = (Note) getIntent().getSerializableExtra("Commento");
		builder.setMessage("Vuoi aggiungere il commento : " + note.getContent());
		builder.setCancelable(false);
		builder.setPositiveButton("Accetta",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(TAG, "Commento accettato");

						DatabaseHelper db = new DatabaseHelper(
								getApplicationContext());

						int idFile = db.getIdFile(note.getCommentLink());
						MantleFile cFile = new MantleFile(
								getApplicationContext(), String.valueOf(idFile));
						cFile.downloadFileFromUrl(MantleFile.COMMENT, idFile
								+ ".xml", MantleFile.DIRECTORY_TEMP);
						WriterXml xml = new WriterXml();

						try {
							xml.addComment(note.getUser(), note.getDate(),
									note.getContent(), cFile.getmFile());
						} catch (ParserConfigurationException e) {
							Log.e(TAG, e.getMessage());
							e.printStackTrace();
						} catch (SAXException e) {
							Log.e(TAG, e.getMessage());
							e.printStackTrace();
						} catch (IOException e) {
							Log.e(TAG, e.getMessage());
							e.printStackTrace();
						} catch (TransformerFactoryConfigurationError e) {
							Log.e(TAG, e.getMessage());
							e.printStackTrace();
						} catch (TransformerException e) {
							Log.e(TAG, e.getMessage());
							e.printStackTrace();
						}

						cFile.uploadFile(new DropboxAuth(
								getApplicationContext()).getAPI());

						ParseJSON parser = new ParseJSON(new StringWriter());

						try {
							parser.writeJson(note);
						} catch (IOException ex) {
							Log.e(TAG, ex.getMessage());

						}

						String[] emails = db.getEmailsFilesShared(idFile);
						for (int i = 0; i < emails.length; i++) {
							new Sender(FileDetailActivity.this, parser
									.toString(), emails[i],
									MantleMessage.SYSTEM).execute();
						}

						dialog.cancel();
					}
				});

		builder.setNegativeButton("Rifiuta",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(TAG, "Commento annullato");

						ParseJSON parser = new ParseJSON(new StringWriter());
						note.setContent("Comment Refused");

						try {
							parser.writeJson(note);
						} catch (IOException ex) {
							Log.e(TAG, ex.getMessage());
						}

						new Sender(FileDetailActivity.this, parser.toString(),
								note.getSender_mail(), MantleMessage.SYSTEM)
								.execute();
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
}