package com.project.mantle_v1.fileNavigator;

import java.io.File;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.mantle_v1.MantleFile;
import com.project.mantle_v1.MyHandler;
//import com.project.mantle_v1.MyApplication;
import com.project.mantle_v1.R;
import com.project.mantle_v1.database.MioDatabaseHelper;
import com.project.mantle_v1.notification_home.NoteActivity;

/**
 * A fragment representing a single File detail screen. This fragment is either
 * contained in a {@link FileListActivity} in two-pane mode (on tablets) or a
 * {@link FileDetailActivity} on handsets.
 */
public class FileDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	private final String USER_DETAILS_PREF = "user";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private MantleFile file;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FileDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			file = MyHandler.FILE_MAP
					.get(getArguments().getString(ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/*
		 * View rootView = inflater.inflate(R.layout.fragment_file_detail,
		 * container, false);
		 */
		View rootView = inflater.inflate(R.layout.fragment_photo_sharing,
				container, false);

		TextView tw = (TextView) rootView.findViewById(R.id.linkText);
		tw.setText(file.getFileName());
		ListView listView = (ListView) rootView.findViewById(R.id.listView1);
		
		//DAL DB PRELEVA I CONTATTI CON CUI HAI CONDIVISO IL FILE E SETTI LA LISTA
		
		MioDatabaseHelper db = new MioDatabaseHelper(getActivity()
				.getApplicationContext());
		
		
		
		//Button bComment = (Button) rootView.findViewById(R.id.comment);
		//final File comment = MantleFile.downloadFileFromUrl(
		//		file.getLinkComment(), (String) file.getIdFile() + ".xml");

		/*		
		
		bComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getActivity(), NoteActivity.class);
				Bundle bundle = new Bundle();
				MioDatabaseHelper db = new MioDatabaseHelper(getActivity()
						.getApplicationContext());
				SharedPreferences userDetails = getActivity()
						.getSharedPreferences(USER_DETAILS_PREF, 0);
				String username = userDetails.getString("username", " ");
				bundle.putString("username", username);
				bundle.putString("url", file.getLinkComment());
				bundle.putString("email",
						db.getEmailFromUrl(file.getLinkComment()));
				bundle.putString("filePath", comment.getAbsolutePath());
				bundle.putString("idFile", file.getIdFile());
				myIntent.putExtra("bundle", bundle);
				db.close();
				getActivity().startActivity(myIntent);
			}
		});
		
		*/
		if(file.isImage()) {
			File img = MantleFile.downloadFileFromUrl(file.getLinkFile(),
					file.getFileName());
			ImageView iv = (ImageView) rootView.findViewById(R.id.sharedImage);
			iv.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
		}
		return rootView;
	}
	
}
