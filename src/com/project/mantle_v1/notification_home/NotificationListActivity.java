package com.project.mantle_v1.notification_home;

import java.util.Date;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Toast;
import com.project.mantle_v1.R;
import com.project.mantle_v1.database.AddService;
import com.project.mantle_v1.database.DatabaseHelper;
import com.project.mantle_v1.database.DbSyncService;
import com.project.mantle_v1.database.FriendsList;
import com.project.mantle_v1.database.Team;
import com.project.mantle_v1.database.User;
import com.project.mantle_v1.dropbox.Sharing;
import com.project.mantle_v1.fileNavigator.FileDetailActivity;
import com.project.mantle_v1.fileNavigator.FileListActivity;
import com.project.mantle_v1.fileNavigator.MantleFile;
import com.project.mantle_v1.gmail.ReaderTask;
import com.project.mantle_v1.parser.MantleMessage;

/*
 * An activity representing a list of Notifications. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link NotificationDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link NotificationListFragment} and the item details (if present) is a
 * {@link NotificationDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link NotificationListFragment.Callbacks} interface to listen for item
 * selections.
 */

/*
 *  Questa è la prima activity lanciata. Nella quale risiedono le due liste:
 *  - NotificationListFragment
 *  - NotificationDetailFragment
 *  nel caso in cui l'applicazione venga usata su tablet. 
 *  
 *  Se usato invece su dispositivi con display più piccoli, viene visualizzata la lista
 *  delle notifiche, cliccando sulle quali vengono visualizzati i dettagli nella 
 *  NotificationDetailActivity 
 */

public class NotificationListActivity extends FragmentActivity implements
		NotificationListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private MyHandler handler;
	private ReaderTask rt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_list);

		handler = new MyHandler(getApplicationContext());

		SharedPreferences userDetails = getSharedPreferences(
				User.USER_DETAILS_PREF, 0);

		rt = new ReaderTask(handler, userDetails.getString("email", " "),
				userDetails.getString("emailpswd", " "));

		if (!rt.isAlive())
			rt.start();

		setTitle("Salve " + userDetails.getString("username", " "));
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (findViewById(R.id.notification_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((NotificationListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.notification_list))
					.setActivateOnItemClick(true);

		}

		// TODO: If exposing deep links into your app, handle intents here.

	}

	/**
	 * Callback method from {@link NotificationListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		/*
		  Codice che si occupa della visualizzazione della lista e dei file nei Tablet. Non funziona la visualizzazione. Bisogna rivedere questa 
		  parte per riuscire a poter ottimizzare l'interfaccia   (altra parte commentata in FileList Activity)
		*/
		
		/*
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(NotificationDetailFragment.ARG_ITEM_ID, id);
			NotificationDetailFragment fragment = new NotificationDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.notification_detail_container, fragment)
					.commit();

		} else {
		*/
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Notifica n = MyHandler.NOTIFICA_MAP.get(id);
			notifyMessage(Integer.parseInt(id));
			if (n.getNotificationType().equals(MantleMessage.SHARING_PHOTO)
					|| n.getNotificationType().equals(
							MantleMessage.SHARING_FILE)) {

				Log.v("Notification List Activity", "FileDetailActivity");
				DatabaseHelper db = new DatabaseHelper(getApplicationContext());
				long ID ;
					/*
					 * TODO Decifrare la chiave contenuta in
					 * n.getmFile().getFileKey() con la chiave d'accesso a mantle e
					 * salvarla all'intero del db (sostituendola alle viroglette)
					 */
	
					ID = db.insertFile(n.getmFile().getFileName(), n
							.getmFile().getLinkFile(), n.getmFile().getLinkThumb(),
							n.getmFile().getLinkComment(), "", n.getmFile()
									.getObjectType(), MantleFile.NOT_OWN_FILE);
	
					n.getmFile().setIdFile(String.valueOf(ID));
	
					int ID_User = db.getId(n.getmFile().getSender_email());
					db.insertHistory((int) ID, ID_User,
							new Date(System.currentTimeMillis()).toString());
					
					
				db.close();
				Intent detailIntent = new Intent(this, FileDetailActivity.class);

				/*
				 * Passo alla File Detail Activity l'id del file che mi è stato
				 * condiviso
				 */

				detailIntent.putExtra(NotificationDetailFragment.ARG_ITEM_ID,
						String.valueOf(ID));

				startActivity(detailIntent);

			} else if (n.getNotificationType().equals(MantleMessage.NOTE)) {

				/*
				 * Passo alla File Detail Activity l'id del file che mi è stato
				 * condiviso
				 */

				DatabaseHelper db = new DatabaseHelper(getApplicationContext());
				String idFile = String.valueOf(db.getIdFile(n.getNote()
						.getCommentLink()));
				Intent detailIntent = new Intent(this, FileDetailActivity.class);
				detailIntent.putExtra(NotificationDetailFragment.ARG_ITEM_ID,
						idFile);
				detailIntent.putExtra("Commento", n.getNote());
				startActivity(detailIntent);
			}

			else {
				Log.v("Notification List Activity",
						"NotificationDetailFragment");

				Intent detailIntent = new Intent(this,
						NotificationDetailActivity.class);
				detailIntent.putExtra(NotificationDetailFragment.ARG_ITEM_ID,
						id);
				startActivity(detailIntent);
			}
		}
	//}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem menu1 = menu.add(0, 0, 0, "Amici");
		{
			menu1.setIcon(R.drawable.ic_action_friend);
			menu1.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
					| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		menu1.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Toast.makeText(getApplicationContext(), item.getTitle(),
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(NotificationListActivity.this,
						FriendsList.class);
				intent.putExtra("flag", 1);
				startActivity(intent);
				return true;
			}
		});

		MenuItem menu2 = menu.add(1, 1, 1, "Condividi");
		{
			menu2.setIcon(R.drawable.ic_action_share);
			menu2.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
					| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		menu2.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Toast.makeText(getApplicationContext(), item.getTitle(),
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(NotificationListActivity.this,
						Sharing.class);
				startActivity(intent);
				return true;
			}
		});

		menu.add("Handle File").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(),
								item.getTitle(), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(
								NotificationListActivity.this,
								FileListActivity.class);
						startActivity(intent);
						return true;
					}
				});

		menu.add("Add Service").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(),
								item.getTitle(), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(
								NotificationListActivity.this, AddService.class);
						startActivity(intent);
						return true;
					}
				});

		menu.add("Db Upload").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(),
								item.getTitle(), Toast.LENGTH_SHORT).show();
						startService(new Intent(NotificationListActivity.this,
								DbSyncService.class));
						return true;
					}
				});

		menu.add("Team").setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Toast.makeText(getApplicationContext(),
								item.getTitle(), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(
								NotificationListActivity.this, Team.class);
						startActivity(intent);
						return true;
					}
				});

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!rt.isAlive())
			rt.start();
	}
	
	private void notifyMessage(int id) {
		android.os.Message msg = handler.obtainMessage();
		Bundle b = new Bundle();
		b.putInt(MyHandler.CLICKED_POS, id);
		msg.setData(b);
		handler.sendMessage(msg);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        moveTaskToBack(true);
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
}