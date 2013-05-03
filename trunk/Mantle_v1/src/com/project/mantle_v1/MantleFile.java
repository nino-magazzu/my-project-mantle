package com.project.mantle_v1;

import java.io.File;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.project.mantle_v1.database.MioDatabaseHelper;
import com.project.mantle_v1.dropbox.DownladerTask;
import com.project.mantle_v1.dropbox.UploaderTask;

public class MantleFile implements Serializable {

	/*
	 * TODO: da gestire in seguito alla modifica della struttura del db
	 * 
	 * private MantleImage thumbnail; private MantleImage fullImage;
	 */

	public MantleFile(Entry ent, String link, String username, File file) {
		this.linkFile = link;
		this.date = ent.modified;
		this.objectType = ent.mimeType;
		this.isImage = objectType.contains("image");
		this.fileName = file.getName();
		this.mFile = file;
		if (objectType.contains("image"))
			this.icon = "page_white_picture48";
		else
			this.icon = "page_white_acrobat48";
		this.username = username;
	}

	public MantleFile() {
		super();
		this.idFile = null;
		this.fileName = null;
		this.linkFile = null;
		this.linkComment = null;
		this.fileKey = null;
		// this.fullImage = null;
		// this.thumbnail = null;
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
		this.linkComment = file[3];
		this.fileKey = file[4];
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

	/*
	 * public MantleImage getThumbnail() { return thumbnail; }
	 * 
	 * public void setThumbnail(MantleImage thumbnail) { this.thumbnail =
	 * thumbnail; }
	 * 
	 * public MantleImage getFullImage() { return fullImage; }
	 * 
	 * public void setFullImage(MantleImage fullImage) { this.fullImage =
	 * fullImage; }
	 */
	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
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
	private String fileKey;
	private MioDatabaseHelper db;
	private File mFile;
	private String objectType;
	private String icon;
	private boolean isImage;
	private Bitmap bitmap;
	private String date;
	private String username;
	private String ownerMail;
	private int priority;
}