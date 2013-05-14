package com.project.mantle_v1.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxLink;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.project.mantle_v1.MantleFile;
import com.project.mantle_v1.database.MioDatabaseHelper;
import com.project.mantle_v1.xml.WriterXml;

/**
 * Here we show uploading a file in a background thread, trying to show typical
 * exception handling and flow of control for an app that uploads a file from
 * Dropbox.
 */
public class Uploader extends AsyncTask<Void, Long, Integer> {
	private final String TAG = this.getClass().getSimpleName();
	private final String USER_DETAILS_PREF = "user";

	private DropboxAPI<?> mApi;
	private String mPath;
	private File mFile;

	private long mFileLen;
	private UploadRequest mRequest;
	private Context mContext;
	private final ProgressDialog mDialog;

	private String mErrorMsg;
	private DropboxLink shareLink;
	private String ThumbAddress;
	private String fileKey;
	private int ID;

	public Uploader(Context context, DropboxAPI<?> api, String dropboxPath,
			File file) {

		// Inizializzazione delle variabili e della progress bar

		mContext = context.getApplicationContext();
		mFileLen = file.length();
		mApi = api;
		mPath = dropboxPath;
		mFile = file;
		fileKey = "";

		mDialog = new ProgressDialog(context);
		mDialog.setMax(100);
		mDialog.setMessage("Uploading " + file.getName());
		mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDialog.setProgress(0);
		mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
				new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Annulla l'upload del file
						mRequest.abort();
					}
				});
		mDialog.show();
	}

	@Override
	protected void onPreExecute() {
		mDialog.setTitle("Please wait");
		mDialog.show();
	}

	@Override
	protected Integer doInBackground(Void... params) {
		try {

			/*
			 * ##########################
			 * 
			 * In questo punto va inserita la cifratura del file (mFile) che
			 * sarà caricato su dropbox. La chiave simmetrica di cifratura del
			 * file verrà inserita nell'attributo fileKey e salvata quindi sul
			 * database
			 * 
			 * Il valore di ritorno dovrebbe essere di tipo File. Va passato al
			 * costruttore del FileInputStream al posto di mFile.
			 * 
			 * ###########################
			 */

			FileInputStream fis = new FileInputStream(mFile);
			String path = mPath + mFile.getName();
			mRequest = mApi.putFileOverwriteRequest(path, fis, mFile.length(),
					null);

			if (mRequest != null) {
				Entry ent = mRequest.upload();

				shareLink = mApi.share(ent.path);

				String shareAddress = getShareURL(shareLink.url).replaceFirst(
						"https://www", "https://dl");

				if (ent.mimeType.contains("image")) {
					uploadingThumbnail();
				}

				// creazione istanza del database
				MioDatabaseHelper db = new MioDatabaseHelper(mContext);

				// inserimento del fil ne db
				if (ThumbAddress == null)
					ID = (int) db.insertFile(mFile.getName(), shareAddress, "",
							"", fileKey, ent.mimeType, MantleFile.NORMAL_FILE);
				else
					ID = (int) db.insertFile(mFile.getName(), shareAddress,
							ThumbAddress, "", fileKey, ent.mimeType,
							MantleFile.NORMAL_FILE);

				SharedPreferences userDetails = mContext.getSharedPreferences(
						USER_DETAILS_PREF, 0);

				db.insertHistory(ID, userDetails.getInt("idUser", 1),
						new Date(System.currentTimeMillis()).toString());

				// if(mFile.getName().compareTo(MioDatabaseHelper.DB_NAME) != 0)
				return uploadingXML(db);
				// else
				// return ID;
			}

		} catch (DropboxUnlinkedException e) {
			// This session wasn't authenticated properly or user unlinked
			mErrorMsg = "This app wasn't authenticated properly.";
		} catch (DropboxFileSizeException e) {
			// File size too big to upload via the API
			mErrorMsg = "This file is too big to upload";
		} catch (DropboxPartialFileException e) {
			// We canceled the operation
			mErrorMsg = "Upload canceled";
		} catch (DropboxServerException e) {
			// Server-side exception. These are examples of what could happen,
			// but we don't do anything special with them here.
			if (e.error == DropboxServerException._401_UNAUTHORIZED) {
				// Unauthorized, so we should unlink them. You may want to
				// automatically log the user out in this case.
				mErrorMsg = "UNAUTHORIZED";
			} else if (e.error == DropboxServerException._403_FORBIDDEN) {
				// Not allowed to access this
				mErrorMsg = "FORBIDDEN";
			} else if (e.error == DropboxServerException._404_NOT_FOUND) {
				// path not found (or if it was the thumbnail, can't be
				// thumbnailed)
				mErrorMsg = "NOT_FOUND";
			} else if (e.error == DropboxServerException._507_INSUFFICIENT_STORAGE) {
				// user is over quota
				mErrorMsg = "INSUFFICIENT_STORAGE";
			} else {
				// Something else
				mErrorMsg = "SOMETHING ELSE";
			}
			// This gets the Dropbox error, translated into the user's language
			mErrorMsg = e.body.userError;
			if (mErrorMsg == null) {
				mErrorMsg = "Error: " + e.body.error;
			}
		} catch (DropboxIOException e) {
			// Happens all the time, probably want to retry automatically.
			mErrorMsg = "Network error.  Try again.";
		} catch (DropboxParseException e) {
			// Probably due to Dropbox server restarting, should retry
			mErrorMsg = "Dropbox error.  Try again.";
		} catch (DropboxException e) {
			// Unknown error
			mErrorMsg = "Unknown error.  Try again.";
		} catch (FileNotFoundException e) {
			mErrorMsg = "File not Found.  Try again.";
		}
		return null;
	}

	private Integer uploadingXML(MioDatabaseHelper db)
			throws FileNotFoundException, DropboxException {
		FileInputStream fis;
		Entry ent;
		String shareAddress;
		Log.v(TAG, "*** Creating XML*****");

		WriterXml com = new WriterXml();
		String pathComment = MantleFile.DIRECTORY_TEMP;
		try {
			com.createComment(String.valueOf(ID) + ".xml", pathComment);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * #################################
		 * 
		 * Va cifrato anche mFile che invece va a contenere i commenti come
		 * sopra va usata sempre la stessa chiave simmetrica per cifrarlo
		 * 
		 * ###################################
		 */

		mFile = new File(pathComment, String.valueOf(ID) + ".xml");
		fis = new FileInputStream(mFile);
		mRequest = mApi.putFileOverwriteRequest(mPath + mFile.getName(), fis,
				mFile.length(), new ProgressListener() {
					@Override
					public long progressInterval() {
						// Update the progress bar every half-second or
						// so
						return 500;
					}

					@Override
					public void onProgress(long bytes, long total) {
						publishProgress(bytes);
					}
				});

		if (mRequest != null) {
			ent = mRequest.upload();
			shareLink = mApi.share(ent.path);
			shareAddress = getShareURL(shareLink.url).replaceFirst(
					"https://www", "https://dl");
			db.insertLinkComment(ID, shareAddress);
			mFile.delete();
		}
		return ID;
	}

	private void uploadingThumbnail() throws FileNotFoundException,
			DropboxException {
		Log.v(TAG, "*** Creating thumbnail ***");

		/*
		 * ##########################
		 * 
		 * Nel caso in cui un file sia un'immagine verrà creato anche il
		 * thumbnail, che sarà caricato anch'esso su dropbox cifrato con la
		 * stessa chiave simmetrica usata per il file originale.
		 * 
		 * Va cifrato l'oggetto thumb. Come sopra l'operazione di cifratura
		 * dovrebbe restituire un oggetto di tipo File e passato al
		 * FileInputStream
		 * 
		 * ###########################
		 */

		MantleFile original = new MantleFile(mFile);
		File thumb = original.createThumbnail();

		FileInputStream fIs = new FileInputStream(thumb);
		String thumbPath = mPath + thumb.getName();
		mRequest = mApi.putFileOverwriteRequest(thumbPath, fIs, thumb.length(),
				null);

		if (mRequest != null) {
			Entry entThumb = mRequest.upload();
			thumb.delete();
			shareLink = mApi.share(entThumb.path);
			ThumbAddress = getShareURL(shareLink.url).replaceFirst(
					"https://www", "https://dl");
		}
	}

	/*
	 * @Override protected void onProgressUpdate(Long... progress) { int percent
	 * = (int) (100.0 * (double) progress[0] / mFileLen + 0.5);
	 * mDialog.setProgress(percent); }
	 */

	@Override
	protected void onPostExecute(Integer result) {
		mDialog.dismiss();
		if (result != null) {
			showToast("File successfully uploaded");
		} else {
			showToast(mErrorMsg);
		}
	}

	private void showToast(String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}

	private String getShareURL(String strURL) {
		URLConnection conn = null;
		try {
			URL inputURL = new URL(strURL);
			conn = inputURL.openConnection();

		} catch (MalformedURLException e) {
			Log.d(TAG, "Please input a valid URL: " + e.getMessage());
		} catch (IOException ioe) {
			Log.d(TAG, "Can not connect to the URL: " + ioe.getMessage());
		} catch (Exception e) {
			Log.d(TAG, "Exception: " + e.getMessage());
		}
		return conn.getHeaderField("location");
	}

}