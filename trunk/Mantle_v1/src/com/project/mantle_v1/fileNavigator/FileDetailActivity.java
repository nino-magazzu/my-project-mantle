package com.project.mantle_v1.fileNavigator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.xml.sax.SAXException;

import com.project.mantle_v1.MantleFile;
import com.project.mantle_v1.MyHandler;
import com.project.mantle_v1.R;
import com.project.mantle_v1.User;
import com.project.mantle_v1.database.FriendsList;
import com.project.mantle_v1.database.MioDatabaseHelper;
import com.project.mantle_v1.dropbox.DropboxAuth;
import com.project.mantle_v1.gmail.Sender;
import com.project.mantle_v1.notification_home.Note;
import com.project.mantle_v1.notification_home.NoteActivity;
import com.project.mantle_v1.notification_home.NotificationDetailFragment;
import com.project.mantle_v1.parser.MantleMessage;
import com.project.mantle_v1.parser.ParseJSON;
import com.project.mantle_v1.xml.WriterXml;

import android.app.AlertDialog;
import android.app.Dialog;
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
			
			if(getIntent().hasExtra(FileDetailFragment.ARG_ITEM_ID)) {
				arguments.putString(FileDetailFragment.ARG_ITEM_ID, getIntent()
						.getStringExtra(FileDetailFragment.ARG_ITEM_ID));
				
				file = MyHandler.FILE_MAP.get(getIntent().getStringExtra(
								FileDetailFragment.ARG_ITEM_ID));
			}
			
			else if(getIntent().hasExtra(NotificationDetailFragment.ARG_ITEM_ID)) {
				arguments.putString(NotificationDetailFragment.ARG_ITEM_ID, getIntent()
						.getStringExtra(NotificationDetailFragment.ARG_ITEM_ID));
				
				if(getIntent().hasExtra("Commento")){
					Log.d(TAG,"");
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
					public boolean onMenuItemClick(MenuItem item) {
						
						final File comment = MantleFile.downloadFileFromUrl(
								file.getLinkComment(),
								(String) file.getIdFile() + ".xml");

						Intent myIntent = new Intent(getApplicationContext(),
								NoteActivity.class);
						Bundle bundle = new Bundle();
						MioDatabaseHelper db = new MioDatabaseHelper(
								getApplicationContext());
						SharedPreferences userDetails = getApplicationContext()
								.getSharedPreferences(User.USER_DETAILS_PREF, 0);
						String username = userDetails
								.getString("username", " ");
						bundle.putString("username", username);
						bundle.putString("url", file.getLinkComment());
						bundle.putString("email",
								db.getEmailFromUrl(file.getLinkComment()));
						bundle.putString("filePath", comment.getAbsolutePath());
						bundle.putString("idFile", file.getIdFile());
						myIntent.putExtra("bundle", bundle);
						db.close();
						startActivity(myIntent);
						return true;
						
					}
				});
		
		menu.add("Condividi").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						Intent intent = new Intent(getApplicationContext(), FriendsList.class);
						intent.putExtra("flag", 3);
						startActivityForResult(intent, FRIEND_CHOOSED_CODE);
						return true;
					}
				});
		
		menu.add("Download").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						MantleFile file = MyHandler.FILE_MAP
								.get(getIntent().getStringExtra(
										FileDetailFragment.ARG_ITEM_ID));

						File sd = new File(MantleFile.DIRECTORY_TEMP);
						File download = Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
						Log.d("FILE_DETAIL_ACTIVITY",
								Environment.getExternalStoragePublicDirectory(
										Environment.DIRECTORY_DOWNLOADS)
										.toString());
						String currentFilePath = file.getFileName();
						String backupFilePath = file.getFileName();

						File currentFile = new File(sd, backupFilePath);
						Log.d("FILE_DETAIL_ACTIVITY", "1");
						File backupFile = new File(download, currentFilePath);
						Log.d("FILE_DETAIL_ACTIVITY", "2");

						try {
							FileChannel src = new FileInputStream(currentFile)
									.getChannel();
							FileChannel dst = new FileOutputStream(backupFile)
									.getChannel();
							dst.transferFrom(src, 0, src.size());
							src.close();
							dst.close();

						} catch (Exception e) {
							Log.w("FILE_DETAIL_ACTIVITY", "Execption : " + e);
						}
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
				MioDatabaseHelper db = new MioDatabaseHelper(
						getApplicationContext());

				for (int j = 0; j < contacts.length; j++) {
					Log.v("Dropbox", "--> " + "Ho inviato la mail a "
							+ contacts[j]);
					db.insertShare(Integer.parseInt(file.getIdFile()), (String) contacts[j]);
					if(file.isImage())
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
		builder.setPositiveButton("Accetta", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(TAG,"Commento accettato");
			
				MioDatabaseHelper db = new MioDatabaseHelper(
						getApplicationContext());
		
				int idFile = db.getIdFile(note.getCommentLink());
				File cFile = MantleFile.downloadFileFromUrl(note.getCommentLink(), idFile + ".xml");
				WriterXml xml = new WriterXml();

					try {
						xml.addComment(
								note.getUser(),
								note.getDate(),
								note.getContent(),cFile );
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

					DropboxAuth auth = new DropboxAuth(getApplicationContext());
					boolean bl = MantleFile.uploadFile(cFile, auth.getAPI());
					
					Log.v(TAG, "upload: "+bl);
					
					ParseJSON parser = new ParseJSON(new StringWriter());
					
					try {
						parser.writeJson(note);
					} catch (IOException ex) {
						Log.e(TAG, ex.getMessage());

					}

					String[] emails = db.getEmailsFilesShared(idFile);
					for (int i = 0; i < emails.length; i++) {
						new Sender(FileDetailActivity.this, parser.toString(),
								emails[i], MantleMessage.NOTE).execute();
					}
					
				dialog.cancel();
			}
		});
		
		builder.setNegativeButton("Rifiuta", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d(TAG,"Commento annullato");
				
				ParseJSON parser = new ParseJSON(new StringWriter());
				note.setContent("Comment Refused");
				
				try {
					parser.writeJson(note);
				} catch (IOException ex) {
					Log.e(TAG, ex.getMessage());
				}
				
				new Sender(FileDetailActivity.this, parser.toString(),
						note.getSender_mail() , MantleMessage.SYSTEM).execute();
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}