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
	
	/**** PRIORITY TYPE ****/

	public static final int NEEDFUL_FILE = 3;
	public static final int NORMAL_FILE = 2;
	public static final int USELESS_FILE = 1;
	public static final int NOT_OWN_FILE = 0;

	
	/**** APPLICATION DIRECTORY ****/
	
	public static final String DIRECTORY_TEMP = Environment.getExternalStorageDirectory() + "/Mantle/tmp/";
	public static final String MAIN_DIR = Environment.getExternalStorageDirectory() + "/Mantle/";
	public static final String DIRECTORY_DB = Environment.getExternalStorageDirectory() + "/Mantle/db/";
	public static final String DIRECTORY_HISTORY = Environment.getExternalStorageDirectory() + "/Mantle/history/";
	
	
	/**** TYPE OF FILE TO DOWNLOAD ****/
	public static final String THUMBNAIL = "thumbnail";
	public static final String FILE = "full";
	public static final String COMMENT = "comment";
	
	
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
				User.USER_DETAILS_PREF, 0);
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

	
	public boolean downloadFileFromUrl(String type, String fileName, String path) {
		String url = this.linkFile;
		if(type.equals(THUMBNAIL))
			url = this.linkThumb;
		else if(type.equals(COMMENT))
			url = this.linkComment;
		
		DownladerTask down = new DownladerTask(url, fileName, path);
		down.execute();
		try {
			this.mFile = down.get();
			
			/*
			 * TODO: decifrare di mFile usando la chiave che si trova
			 * in fileKey. Il file qui ottenuto va salvato in mFile
			 */
			
			return true;
		} catch (InterruptedException e) {
			Log.i(TAG, "Error authenticating", e);
			return false;
		} catch (ExecutionException e) {
			Log.i(TAG, "Error authenticating", e);
			return false;
		}
	}

	public boolean uploadFile(DropboxAPI<?> mApi) {
		
		/*
		 * TODO: cifrare il file contenuto in mFile che poi sarà passato
		 * alla funzione UploaderTask che si occuperà di caricarlo su dropbox.
		 * Per la cifratura usare la chiave contenuta in fileKey
		 */
		
		UploaderTask upl = new UploaderTask(mApi, this.mFile);
		upl.execute();
		boolean bl = false;
		try {
			bl = upl.get();
		} catch (InterruptedException e) {
			Log.i(TAG, "Error authenticating", e);
		} catch (ExecutionException e) {
			Log.i(TAG, "Error authenticating", e);
		}
		return bl;
	}
	
	public File createThumbnail() {
		final int THUMBNAIL_SIZE = 128;

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
			Log.v(TAG, "File non trovato");
			return null;
		} catch (IOException e) {
			Log.v(TAG, "IOException: " + e.getMessage());
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

	public File getmFile() {
		return mFile;
	}

	public void setmFile(File mFile) {
		this.mFile = mFile;
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