package com.project.mantle_v1.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.xml.sax.SAXException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Environment;
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
import com.project.mantle_v1.MyApplication;
import com.project.mantle_v1.database.MioDatabaseHelper;
import com.project.mantle_v1.xml.WriterXml;

/**
 * Here we show uploading a file in a background thread, trying to show typical
 * exception handling and flow of control for an app that uploads a file from
 * Dropbox.
 */
public class Uploader extends AsyncTask<Void, Long, MantleFile> {
	private final String TAG = getClass().getName();

	private DropboxAPI<?> mApi;
	private String mPath;
	private File mFile;

	private long mFileLen;
	private UploadRequest mRequest;
	private Context mContext;
	private final ProgressDialog mDialog;

	private String mErrorMsg;
	private DropboxLink shareLink;
	private String username;

	public Uploader(Context context, DropboxAPI<?> api, String dropboxPath,
			File file, String username) {
		// We set the context this way so we don't accidentally leak activities
		mContext = context.getApplicationContext();

		this.username = username;

		mFileLen = file.length();
		mApi = api;
		mPath = dropboxPath;
		mFile = file;

		mDialog = new ProgressDialog(context);
		mDialog.setMax(100);
		mDialog.setMessage("Uploading " + file.getName());
		mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDialog.setProgress(0);
		mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
				new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// This will cancel the putFile operation
						mRequest.abort();
					}
				});
		mDialog.show();
		showToast("File ricevuto: " + mPath + mFile.getName());
	}

	@Override
	protected MantleFile doInBackground(Void... params) {
		try {
			// By creating a request, we get a handle to the putFile operation,
			// so we can cancel it later if we want to

			// TODO: inserire la funzione di cifratura del file, prima del
			// caricamento dello stesso su

			FileInputStream fis = new FileInputStream(mFile);
			String path = mPath + mFile.getName();
			mRequest = mApi.putFileOverwriteRequest(path, fis, mFile.length(),
					new ProgressListener() {
						@Override
						public long progressInterval() {
							// Update the progress bar every half-second or so
							return 500;
						}

						@Override
						public void onProgress(long bytes, long total) {
							publishProgress(bytes);
						}
					});

			if (mRequest != null) {
				Entry ent = mRequest.upload();
				shareLink = mApi.share(ent.path);
				String shareAddress = getShareURL(shareLink.url).replaceFirst(
						"https://www", "https://dl");
				Log.d(TAG, "dropbox share link " + shareAddress);

				// creazione del file
				MantleFile file = new MantleFile(ent, shareAddress, username,
						mFile);

				// creazione istanza del database
				MioDatabaseHelper db = new MioDatabaseHelper(mContext);

				// inserimento del fil ne db
				long ID = db.insertFile(file.getFileName(), file.getLinkFile(),
						"", file.getFileKey());

				db.insertShare((int) ID, ((MyApplication) mContext
						.getApplicationContext()).getID());

				WriterXml com = new WriterXml();
				String pathComment = Environment.getExternalStorageDirectory()
						.toString() + "/";
				try {
					com.createComment(String.valueOf(ID) + ".xml");
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
				mFile = new File(pathComment, String.valueOf(ID) + ".xml");
				Log.d(TAG, String.valueOf(ID));
				fis = new FileInputStream(mFile);
				// String pathComment = mPath + mFile.getName();
				mRequest = mApi.putFileOverwriteRequest(
						mPath + mFile.getName(), fis, mFile.length(),
						new ProgressListener() {
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
					db.insertLinkComment((int) ID, shareAddress);
					mFile.delete();
				}
				file.setLinkComment(shareAddress);
				return file;
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

	@Override
	protected void onProgressUpdate(Long... progress) {
		int percent = (int) (100.0 * (double) progress[0] / mFileLen + 0.5);
		mDialog.setProgress(percent);
	}

	@Override
	protected void onPostExecute(MantleFile result) {
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

	String getShareURL(String strURL) {
		URLConnection conn = null;
		try {
			URL inputURL = new URL(strURL);
			conn = inputURL.openConnection();

		} catch (MalformedURLException e) {
			Log.d(TAG, "Please input a valid URL");
		} catch (IOException ioe) {
			Log.d(TAG, "Can not connect to the URL");
		}

		return conn.getHeaderField("location");

	}
}