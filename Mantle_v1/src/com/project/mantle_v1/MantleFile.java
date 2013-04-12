package com.project.mantle_v1;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.dropbox.client2.DropboxAPI.Entry;
import com.project.mantle_v1.database.MioDatabaseHelper;


public class MantleFile implements Serializable {
	
	private static final long serialVersionUID = 6107134499898867188L;
	private final String TAG = MantleFile.class.getName();
	
	private String idFile;
	private String fileName;
	private String linkFile;
	private String linkComment;
	private String fileKey;
	private MioDatabaseHelper db;

	private String objectType;
	private String icon;
	private boolean isImage;
	private Bitmap bitmap;
	private String date;
	
	
	/* TODO: da gestire in seguito alla modifica delal struttura del db
	 * 
	 * private MantleImage thumbnail;
	 * private MantleImage fullImage;
	 * 
	*/
	
	public MantleFile(Entry ent, String link, String username, File file) {
		this.linkFile = link;
		this.date = ent.modified;
		this.objectType = ent.mimeType;
		this.isImage = objectType.contains("image");
		this.fileName = file.getName();
		if(isImage)
			this.bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		
		if(objectType.contains("image")) 
			this.icon = "page_white_picture48";
		else 
			this.icon = "page_white_acrobat48";
	}
	
	
	public MantleFile(){
		super();
		this.idFile = null;
		this.fileName = null;
		this.linkFile = null;
		this.linkComment = null;
		this.fileKey = null;
	//	this.fullImage = null;
	//	this.thumbnail = null;
	}
	
	public MantleFile(String idFile, String filename, String linkFile, String linkComment, String fileKey){
		this.idFile = idFile;
		this.fileName = filename;
		this.linkFile = linkFile;
		this.linkComment = linkComment;
		this.fileKey = fileKey;
	}

	// Costrutore per prendere uno specifico file
	public MantleFile(Context cont, int idUser) {
		db = new MioDatabaseHelper(cont);
		String[] file = db.getFile(String.valueOf(idUser));
		this.idFile = file[0];
		this.fileName = file[1];
		this.linkFile = file[2];
		this.linkComment = file[3];
		this.fileKey = file[4];
		db.close();
	}

	public void downloadFromUrl(String imageURL, String fileName) {
		try {
			File root = android.os.Environment.getExternalStorageDirectory(); 
			URL url = new URL(imageURL);
			File file = new File(root, fileName);
			long StartingTime = System.currentTimeMillis();
			
            Log.d(TAG, "download begining");
            Log.d(TAG, "download url:" + url);
            Log.d(TAG, "downloaded file name:" + fileName);
            
            /* Open a connection to that URL. */
            URLConnection ucon = url.openConnection();

            /*
             * Define InputStreams to read from the URLConnection.
             */
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            /*
             * Read bytes to the Buffer until there is nothing more to read(-1).
             */
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                    baf.append((byte) current);
            }

            /* Convert the Bytes read to a String. */
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baf.toByteArray());
            fos.close();
            Log.d(TAG, "download ready in"
                            + ((System.currentTimeMillis() - StartingTime) / 1000)
                            + " sec");

			} catch (IOException e) {
            Log.d(TAG, "Error: " + e);
			}
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
	public MantleImage getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(MantleImage thumbnail) {
		this.thumbnail = thumbnail;
	}

	public MantleImage getFullImage() {
		return fullImage;
	}

	public void setFullImage(MantleImage fullImage) {
		this.fullImage = fullImage;
	}
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
}
