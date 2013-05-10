package com.project.mantle_v1.fileNavigator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import com.project.mantle_v1.MantleFile;
import com.project.mantle_v1.MyHandler;
import com.project.mantle_v1.R;
import com.project.mantle_v1.database.FriendsList;
import com.project.mantle_v1.database.MioDatabaseHelper;
import com.project.mantle_v1.gmail.Sender;
import com.project.mantle_v1.notification_home.NoteActivity;
import com.project.mantle_v1.notification_home.NotificationDetailFragment;
import com.project.mantle_v1.parser.MantleMessage;
import com.project.mantle_v1.parser.ParseJSON;

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

	private final String USER_DETAILS_PREF = "user";
	final static private int FRIEND_CHOOSED_CODE = 8;
	private final String TAG = this.getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_detail);
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			
			if(getIntent().hasExtra(FileDetailFragment.ARG_ITEM_ID))
				arguments.putString(FileDetailFragment.ARG_ITEM_ID, getIntent()
						.getStringExtra(FileDetailFragment.ARG_ITEM_ID));
			
			else if(getIntent().hasExtra(NotificationDetailFragment.ARG_ITEM_ID))
				arguments.putString(NotificationDetailFragment.ARG_ITEM_ID, getIntent()
						.getStringExtra(NotificationDetailFragment.ARG_ITEM_ID));
			
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

						MantleFile file = MyHandler.FILE_MAP
								.get(getIntent().getStringExtra(
										FileDetailFragment.ARG_ITEM_ID));
						final File comment = MantleFile.downloadFileFromUrl(
								file.getLinkComment(),
								(String) file.getIdFile() + ".xml");

						Intent myIntent = new Intent(getApplicationContext(),
								NoteActivity.class);
						Bundle bundle = new Bundle();
						MioDatabaseHelper db = new MioDatabaseHelper(
								getApplicationContext());
						SharedPreferences userDetails = getApplicationContext()
								.getSharedPreferences(USER_DETAILS_PREF, 0);
						String username = userDetails
								.getString("username", " ");
						bundle.putString("username", username);
						bundle.putString("url", file.getLinkComment());
						bundle.putString("email",
								db.getEmailFromUrl(file.getLinkComment()));
						bundle.putString("filePath", comment.getAbsolutePath());
						myIntent.putExtra("bundle", bundle);
						db.close();
						getApplicationContext().startActivity(myIntent);
						return true;
					}
				});
		;
		menu.add("Condividi").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						Intent intent = new Intent(getApplicationContext(), FriendsList.class);
						intent.putExtra("flag", 3);
						startActivityForResult(intent, FRIEND_CHOOSED_CODE);
						return true;
					}
				});
		;
		menu.add("Download").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						MantleFile file = MyHandler.FILE_MAP
								.get(getIntent().getStringExtra(
										FileDetailFragment.ARG_ITEM_ID));

						File sd = new File(Environment
								.getExternalStorageDirectory() + "/Mantle/tmp");
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
		;
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
				MantleFile mt = MyHandler.FILE_MAP
						.get(getIntent().getStringExtra(
								FileDetailFragment.ARG_ITEM_ID));
				String body = "";
				try {
					body = new ParseJSON(new StringWriter()).writeJson(mt);
				} catch (IOException e) {
					Log.e(TAG, "--> " + e.getMessage());
				}
				MioDatabaseHelper db = new MioDatabaseHelper(
						getApplicationContext());

				for (int j = 0; j < contacts.length; j++) {
					Log.v("Dropbox", "--> " + "Ho inviato la mail a "
							+ contacts[j]);
					db.insertShare(Integer.parseInt(mt.getIdFile()), (String) contacts[j]);
					new Sender(this, body, (String) contacts[j],
							MantleMessage.SHARING_PHOTO).execute();
				}
				db.close();
			}
			break;
		}
	}
}
