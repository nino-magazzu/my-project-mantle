package com.project.mantle_v1.notification_home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.project.mantle_v1.MyHandler;
import com.project.mantle_v1.R;
import com.project.mantle_v1.ShowToast;
import com.project.mantle_v1.database.MioDatabaseHelper;

/**
 * A fragment representing a single Notification detail screen. This fragment is
 * either contained in a {@link NotificationListActivity} in two-pane mode (on
 * tablets) or a {@link NotificationDetailActivity} on handsets.
 */
public class NotificationDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Notifica mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	
	
	public NotificationDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = MyHandler.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = null;
		
		if (mItem != null) {
			
			if(mItem.getNotificationType() == Notifica.FRIENDSHIP || mItem.getNotificationType() == Notifica.SYSTEM) {
				
				rootView = inflater.inflate(R.layout.fragment_friendship,
						container, false);

				((TextView) rootView.findViewById(R.id.FriendshipRequest))
				.setText(mItem.getNotificationBody());
				
				Button bAccept = (Button) rootView.findViewById(R.id.accetta);
				bAccept.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						new ShowToast().showToast("Accetta",v.getContext());
						MioDatabaseHelper db = new MioDatabaseHelper(v.getContext());
						db.insertUser(mItem.getUser().getEmail(), mItem.getUser().getUsername(), mItem.getUser().getName(), mItem.getUser().getSurname(), mItem.getUser().getKey());
					}
				});
		
			}
			else {
				rootView = inflater.inflate(R.layout.fragment_photo_sharing,
						container, false);

				// Show the dummy content as text in a TextView.
		
				((ListView) rootView.findViewById(R.id.notification_detail))
				.setAdapter(new NoteAdapter(container.getContext(), R.layout.note_layout,mItem.getNotes()));
		
			}
		
		}	
		return rootView;

	}
}