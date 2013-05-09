package com.project.mantle_v1.fileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.project.mantle_v1.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

public class FileChooser extends ListActivity {

	private static String TAG;

	private File currentDir;
	private FileArrayAdapter adapter;
	private String selectedFilePath;
	private String STARTING_DIR = Environment.getExternalStorageDirectory()
			.getPath(); // "/sdcard/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();
		// Intent intent = getIntent();

		// if (intent.getBooleanExtra("upload", false)) {
		currentDir = new File(STARTING_DIR);
		fill(currentDir);
	}

	/*
	 * else { Object[] dropboxFiles = (Object[]) intent
	 * .getSerializableExtra("File"); Descriptor[] files =
	 * getList(dropboxFiles); fill(files); }
	 */
	// }
	/*
	 * public Descriptor[] getList(Object[] objs) { Descriptor[] files = new
	 * Descriptor[objs.length]; int i = 0; for (Object obj : objs) { Descriptor
	 * file = (Descriptor) obj; files[i++] = file; }
	 * 
	 * return files; }
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		Intent data = new Intent();
		switch (keyCode) {
		case KeyEvent.KEYCODE_CAMERA:
			data.putExtra("path", "null");
			setResult(1, data);
			finish();
			return true;
		case KeyEvent.KEYCODE_1:
			data.putExtra("path", "null");
			setResult(1, data);
			finish();
			return true;
		case KeyEvent.KEYCODE_HOME:
			data.putExtra("path", "null");
			setResult(1, data);
			finish();
			return true;
		case KeyEvent.KEYCODE_BACK:
			data.putExtra("path", "null");
			setResult(1, data);
			finish();
			return true;
		}

		return false;
	}

	private void fill(File f) {
		File[] dirs = f.listFiles();
		this.setTitle("Current Dir: " + f.getName());
		List<Option> dir = new ArrayList<Option>();
		List<Option> fls = new ArrayList<Option>();
		try {
			for (File ff : dirs) {
				if (ff.isDirectory())
					dir.add(new Option(ff.getName(), "Folder", ff
							.getAbsolutePath()));
				else
					fls.add(new Option(ff.getName(), "File Size: "
							+ ff.length(), ff.getAbsolutePath()));

			}
		} catch (Exception e) {

		}
		Collections.sort(dir);
		Collections.sort(fls);

		dir.addAll(fls);

		if (!f.getName().equalsIgnoreCase("sdcard"))
			dir.add(0, new Option("..", "Parent Directory", f.getParent()));
		adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view,
				dir);
		this.setListAdapter(adapter);
	}

	/*
	 * private void fill(Descriptor[] dirs) { boolean home = true;
	 * this.setTitle("Current Dir: Home"); List<Option> dir = new
	 * ArrayList<Option>(); List<Option> fls = new ArrayList<Option>(); try {
	 * for (Descriptor ff : dirs) { if (ff.isDirectory()) dir.add(new
	 * Option(ff.getName(), "folder", ff .getAbsolutePath(), Option.DESCRIPTOR,
	 * ff .listFiles())); else fls.add(new Option(ff.getName(), "File Size " +
	 * ff.getLenght(), ff.getAbsolutePath(), Option.DESCRIPTOR,
	 * ff.listFiles())); } } catch (Exception e) { Log.d(TAG, e.getMessage()); }
	 * Collections.sort(dir); Collections.sort(fls);
	 * 
	 * dir.addAll(fls);
	 * 
	 * for (int i = 0; i < dirs.length; i++) { if
	 * (!dirs[i].getName().equalsIgnoreCase("storedFile")) home = false; }
	 * 
	 * if (!home) dir.add(0, new Option("..", "Parent Directory",
	 * dirs[0].getParent(), Option.DESCRIPTOR, dirs[0].listFiles()));
	 * 
	 * adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view,
	 * dir); this.setListAdapter(adapter); }
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Option o = adapter.getItem(position);

		if (o.getData().equalsIgnoreCase("folder")
				|| o.getData().equalsIgnoreCase("parent directory")) {
			// if (o.isFile()) {
			currentDir = new File(o.getPath());
			fill(currentDir);
			// }
			/*
			 * else { fill(o.listFile()); }
			 */
		} else {
			selectedFilePath = onFileClick(o);

			Intent data = new Intent();
			data.putExtra("path", selectedFilePath);
			setResult(RESULT_OK, data);
			Log.v(TAG, selectedFilePath);
			finish();
		}
	}

	private String onFileClick(Option o) {
		return (o.getPath());
	}

	public String getFileChosed() {
		return selectedFilePath;
	}
}