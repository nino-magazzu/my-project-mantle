package com.project.mantle_v1.dropbox;

import java.util.ArrayList;
import java.util.Date;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.RESTUtility;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ListOfFileDownloader extends AsyncTask<Void, Entry, Descriptor[]> {

	final static private String FILE_DIR = "/storedFile";
	final static private String TAG = "DropboxFile";
	private DropboxAPI<AndroidAuthSession> mApi;
	private ProgressDialog mDialog;
	private Context context;

	public ListOfFileDownloader(DropboxAPI<AndroidAuthSession> mApi,
			Context cont) {
		this.mApi = mApi;
		this.context = cont;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mDialog = new ProgressDialog(context);
		mDialog.setTitle("");
		mDialog.setMessage("Caricamento in corso...");
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(false);
		mDialog.show();
	}

	@Override
	protected Descriptor[] doInBackground(Void... params) {

		/*
		 * public DropboxAPI.Entry metadata(java.lang.String path, int
		 * fileLimit, java.lang.String hash, boolean list, java.lang.String rev)
		 * 
		 * Parameters: path - the Dropbox path to the file or directory for
		 * which to get metadata.
		 * 
		 * fileLimit - the maximum number of children to return for a directory.
		 * Default is 25,000 if you pass in 0 or less. If there are too many
		 * entries to return, you will get a 406 DropboxServerException. Pass in
		 * 1 if getting metadata for a file.
		 * 
		 * hash - if you previously got metadata for a directory and have it
		 * stored, pass in the returned hash. If the directory has not changed
		 * since you got the hash, a 304 DropboxServerException will be thrown.
		 * Pass in null for files or unknown directories.
		 * 
		 * list - if true, returns metadata for a directory's immediate
		 * children, or just the directory entry itself if false. Ignored for
		 * files.
		 * 
		 * rev - optionally gets metadata for a file at a prior rev (does not
		 * apply to folders). Use null for the latest metadata.
		 */

		Descriptor[] fnames = null;

		ArrayList<Descriptor> files = new ArrayList<Descriptor>();
		files = getList(FILE_DIR);
		fnames = files.toArray(new Descriptor[files.size()]);
		return fnames;
	}

	private Descriptor EntryToDescriptor(Entry ent) {
		Descriptor des = new Descriptor(ent.path, ent.bytes, ent.isDir);

		Date data = RESTUtility.parseDate(ent.modified);
		des.setLastModified(data.getTime());

		return des;
	}

	@Override
	protected void onPostExecute(Descriptor[] result) {
		super.onPostExecute(result);
		if (mDialog.isShowing())
			mDialog.dismiss();
	}

	private ArrayList<Descriptor> getList(String path) {
		try {
			Entry dirent = mApi.metadata(path, 1000, null, true, null);
			ArrayList<Descriptor> dir = new ArrayList<Descriptor>();

			for (Entry ent : dirent.contents) {
				dir.add(EntryToDescriptor(ent));
				if (ent.isDir) {
					ArrayList<Descriptor> temp = getList(ent.path);
					dir.get(dir.size() - 1).setFiles(
							temp.toArray(new Descriptor[temp.size()]));
				} else
					continue;
			}
			return dir;
		} catch (DropboxException e) {
			Log.d(TAG, e.getMessage());
			return null;
		}
	}

}