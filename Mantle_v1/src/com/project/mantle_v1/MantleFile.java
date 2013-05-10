package com.project.mantle_v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.util.Log;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.project.mantle_v1.database.MioDatabaseHelper;
import com.project.mantle_v1.dropbox.DownladerTask;
import com.project.mantle_v1.dropbox.UploaderTask;

public class MantleFile implements Serializable {
	// **** PRIORITY TYPE ****

	public static final int NEEDFUL_FILE = 3;
	public static final int NORMAL_FILE = 2;
	public static final int USELESS_FILE = 1;
	public static final int NOT_OWN_FILE = 0;

	private final String USER_DETAILS_PREF = "user";

	public MantleFile(Entry ent, String link, String username, File file) {
		this.linkFile = link;
		this.date = ent.modified;
		this.objectType = ent.mimeType;
		this.isImage = objectType.contains("image");
		this.fileName = file.getName();
		this.mFile = file;
		this.username = username;
	}

	public MantleFile(File file) {
		this.mFile = file;
	}
	
	public MantleFile() {
		super();
		this.idFile = null;
		this.fileName = null;
		this.linkFile = null;
		this.linkComment = null;
		this.fileKey = null;
	}

	public MantleFile(String idFile, String filename, String linkFile,
			String linkComment, String fileKey) {
		this.idFile = idFile;
		this.fileName = filename;
		this.linkFile = linkFile;
		this.linkComment = linkComment;
		this.fileKey = fileKey;
	}

	// Costrutore per prendere uno specifico file
	public MantleFile(Context cont, String idFile) {
		db = new MioDatabaseHelper(cont);
		String[] file = db.getFile(idFile);
		this.idFile = file[0];
		this.fileName = file[1];
		this.linkFile = file[2];
		this.linkThumb = file[3];
		this.linkComment = file[4];
		this.fileKey = file[5];
		this.objectType = file[6];
		this.priority = Integer.parseInt(file[7]);

		SharedPreferences userDetails = cont.getSharedPreferences(
				USER_DETAILS_PREF, 0);
		this.username = userDetails.getString("username", "");

		this.date = db.getDateFile(Integer.parseInt(idFile));

		this.isImage = objectType.contains("image");

		db.close();
	}

	/**
	 * Permette di scaricare un file passando l'url e il nome del file di
	 * destinazione. Il file verrà salvato sulla sdcard.
	 * 
	 * @param imageURL
	 *            : url del file da scaricare
	 * @param fileName
	 *            : nome del file sul dispositivo
	 */

	public void downloadFileFromUrl(String fileName) {
		DownladerTask down = new DownladerTask(this.linkFile, fileName);
		down.execute();

		try {
			this.mFile = down.get();
		} catch (InterruptedException e) {
			Log.i(TAG, "Error authenticating", e);
		} catch (ExecutionException e) {
			Log.i(TAG, "Error authenticating", e);
		}

	}

	public static void uploadFile(File f, DropboxAPI<?> mApi) {
		UploaderTask upl = new UploaderTask(mApi, f);
		upl.execute();
	}

	public static File downloadFileFromUrl(String url, String fileName) {
		DownladerTask down = new DownladerTask(url, fileName);
		down.execute();
		File file = null;
		try {
			file = down.get();
		} catch (InterruptedException e) {
			Log.i("MantleFile", "Error authenticating", e);
		} catch (ExecutionException e) {
			Log.i("MantleFile", "Error authenticating", e);
		}
		return file;
	}

	public File createThumbnail() {
		final int THUMBNAIL_SIZE = 64;

        FileInputStream fis;
		try {
			fis = new FileInputStream(mFile);
			Bitmap imageBitmap = BitmapFactory.decodeStream(fis);
		   // imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
		    imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE);
		    File fOut = new File(Environment.getExternalStorageDirectory() + "/Mantle/tmp", getTumbName(mFile.getName()));
		    fOut.createNewFile();
			FileOutputStream fos = new FileOutputStream(fOut);
		    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
		    fos.flush();
		    fos.close();
		    return fOut;
		    
		} catch (FileNotFoundException e) {
			Log.v("MantleFile", "File non trovato");
			return null;
		} catch (IOException e) {
			Log.v("MantleFile", "IOException: " + e.getMessage());
			return null;
		}
	}
	
	private String getTumbName(String fileName) {
		final int lastPeriodPos = fileName.lastIndexOf('.');
		if (lastPeriodPos <= 0) 
	        return fileName + "_t.jpg";
		else 
			return fileName.substring(0, lastPeriodPos) + "_t.jpg";
	}
	
	public String getIdFile() {
		return idFile;
	}

	public void setIdFile(String idFile) {
		this.idFile = idFile;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLinkFile() {
		return linkFile;
	}

	public void setLinkFile(String linkFile) {
		this.linkFile = linkFile;
	}

	public String getLinkComment() {
		return linkComment;
	}

	public void setLinkComment(String linkComment) {
		this.linkComment = linkComment;
	}

	public String getFileKey() {
		return fileKey;
	}

	public void setFileKey(String fileKey) {
		this.fileKey = fileKey;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
		this.isImage = objectType.contains("image");
	}

	public boolean isImage() {
		return isImage;
	}

	public void setImage(boolean isImage) {
		this.isImage = isImage;
	}

	public Bitmap getBitmap() {
		this.bitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSender_email() {
		return ownerMail;
	}

	public void setSender_email(String ownerMail) {
		this.ownerMail = ownerMail;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getLinkThumb() {
		return linkThumb;
	}

	public void setLinkThumb(String linkThumb) {
		this.linkThumb = linkThumb;
	}

	@Override
	public String toString() {
		return this.fileName;
	}

	private static final long serialVersionUID = 6107134499898867188L;
	private final String TAG = this.getClass().getSimpleName();
	
	private String idFile;
	private String fileName;
	private String linkFile;
	private String linkComment;
	private String linkThumb;
	private String fileKey;
	private MioDatabaseHelper db;
	private File mFile;
	private String objectType;
	private boolean isImage;
	private Bitmap bitmap;
	private String date;
	private String username;
	private String ownerMail;
	private int priority;
}