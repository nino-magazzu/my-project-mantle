package com.project.mantle_v1.fileNavigator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.project.mantle_v1.R;
import com.project.mantle_v1.database.DatabaseHelper;
import com.project.mantle_v1.notification_home.MyHandler;
import com.project.mantle_v1.notification_home.NotificationDetailFragment;

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
	public static final String ARG_ITEM_ID = "File_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private MantleFile file;

	private ListView listView;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FileDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(getClass().getSimpleName(), getArguments().keySet().toString());

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			file = MyHandler.FILE_MAP
					.get(getArguments().getString(ARG_ITEM_ID));
			getArguments().clear();
		}

		else if (getArguments().containsKey(
				NotificationDetailFragment.ARG_ITEM_ID)) {

			file = new MantleFile(getActivity().getApplicationContext(),
					getArguments().getString(
							NotificationDetailFragment.ARG_ITEM_ID));

			getArguments().clear();
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

		listView = (ListView) rootView.findViewById(R.id.listView1);

		DatabaseHelper db = new DatabaseHelper(getActivity()
				.getApplicationContext());

		String[] sharers = db.getSharers(file.getIdFile());
		showSharers(sharers);

		if (file.isImage()) {
			File img = new File(MantleFile.DIRECTORY_TEMP,
					getTumbName(file.getFileName()));
			if (!img.exists()) {
				file.downloadFileFromUrl(MantleFile.THUMBNAIL,
						getTumbName(file.getFileName()),
						MantleFile.DIRECTORY_TEMP);
				img = file.getmFile();
			}
			ImageView iv = (ImageView) rootView.findViewById(R.id.sharedImage);
			iv.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
		}
		return rootView;
	}

	public void showSharers(String[] sharers) {

		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		for (int j = 0; j < sharers.length; j = j + 2) {

			Map<String, String> datum = new HashMap<String, String>(2);
			datum.put("name", sharers[j]);
			datum.put("username", sharers[j + 1]);
			data.add(datum);
		}

		SimpleAdapter adapter = new SimpleAdapter(getActivity(), data,
				android.R.layout.simple_list_item_2, new String[] { "name",
						"username" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		listView.setAdapter(adapter);
	}

	private String getTumbName(String fileName) {
		final int lastPeriodPos = fileName.lastIndexOf('.');
		if (lastPeriodPos <= 0)
			return fileName + "_t";
		else
			return fileName.substring(0, lastPeriodPos) + "_t";
	}

}
