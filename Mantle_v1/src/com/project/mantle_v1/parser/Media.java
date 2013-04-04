package com.project.mantle_v1.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.dropbox.client2.DropboxAPI.Entry;

public class Media  implements Serializable{
	/**
	 * 		La classe si occupa di raccogliere le informazioni sui media condivisibili 
	 * 		tramite l'applicazione. Permette anche la lettura e scrittura di Json 
	 * 		atti alla condivisione dei media. 
	 */
	private static final long serialVersionUID = 6107134499898867188L;
	
	
	public Media(String username, String url, String data,
			String objectType, String icon) {
		super();
		this.username = username;
		this.url = url;
		this.data = data;
		this.objectType = objectType;
		this.icon = icon;
	}

	
	public Media() {
		this.username = null;
		this.url = null;
		this.data = null;
		this.objectType = null;
		this.icon = null;
	}

	
	public Media(Entry ent, String link, String username, File file) {
		this.url = link;
		this.data = ent.modified;
		this.objectType = ent.mimeType;
		this.isImage = objectType.contains("image");
		this.bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		
		if(objectType.contains("image")) 
			this.icon = "page_white_picture48";
		else 
			this.icon = "page_white_acrobat48";
		
		this.username = username;
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

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
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
	
	private String username;
	private String url;
	private String data;
	private String objectType;
	private String icon;
	private boolean isImage;
	private Bitmap bitmap;
	private final String TAG = "Media";
}
