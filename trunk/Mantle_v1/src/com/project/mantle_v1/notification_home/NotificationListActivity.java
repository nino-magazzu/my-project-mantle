package com.project.mantle_v1.notification_home;

import com.project.mantle_v1.AddService;
import com.project.mantle_v1.Home;
import com.project.mantle_v1.MyHandler;
import com.project.mantle_v1.R;
import com.project.mantle_v1.database.AddFriend;
import com.project.mantle_v1.database.FriendsList;
import com.project.mantle_v1.database.AddFriend;
import com.project.mantle_v1.database.MioDatabaseHelper;
import com.project.mantle_v1.dropbox.Dropbox;
import com.project.mantle_v1.fileChooser.FileChooser;
import com.project.mantle_v1.gmail.ReaderTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification_list);

				
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
		
		Handler handler = new MyHandler(NotificationListActivity.this);
		new ReaderTask(handler).start();
	}

	/**
	 * Callback method from {@link NotificationListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
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
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this,
					NotificationDetailActivity.class);
			detailIntent.putExtra(NotificationDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {

		menu.add("Add a service").setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
           	 	Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
           	 	Intent intent = new Intent(NotificationListActivity.this, AddService.class);
           	 	startActivity(intent);
           	 	return true;
            	}
			});;
		
		menu.add("Add a friend").setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
            	Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
           	 	Intent intent = new Intent(NotificationListActivity.this, AddFriend.class);
           	 	startActivity(intent);
           	 	return true;
            	}
			});;
		
		menu.add("File").setOnMenuItemClickListener(new OnMenuItemClickListener() {
             public boolean onMenuItemClick(MenuItem item) {
            	 Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            	 Intent intent = new Intent(NotificationListActivity.this, Dropbox.class);
             	 startActivity(intent);
            	 return true;
             	}
			});;
		  
		// MODIFICA LA CLASSE CHE MOSTRA LA FRIENDLIST NON DEVE VOLERE IL VETTORE IN INGRESSO MA SE LO DEVE CALCOLARE LUI 
		menu.add("Friend List").setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
           	 	Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
           	 	Intent intent = new Intent(NotificationListActivity.this, FriendsList.class);
           	 	startActivity(intent);
           	 	return true;
            	}
			});;

		
		return true;
		  
    }
	
	
}
