package com.project.mantle_v1.parser;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;

import android.util.Log;

import com.dropbox.client2.DropboxAPI.Entry;

public class MediaType  implements Serializable{
	/**
	 * 		La classe si occupa di raccogliere le informazioni sui media condivisibili 
	 * 		tramite l'applicazione. Permette anche la lettura e scrittura di Json 
	 * 		atti alla condivisione dei media. 
	 */
	private static final long serialVersionUID = 6107134499898867188L;
	
	
	public MediaType(String username, String url, String data,
			String objectType, String icon) {
		super();
		this.username = username;
		this.url = url;
		this.data = data;
		this.objectType = objectType;
		this.icon = icon;
	}

	
	public MediaType() {
		this.username = null;
		this.url = null;
		this.data = null;
		this.objectType = null;
		this.icon = null;
	}

	
	public MediaType(Entry ent, String link, String username) {
		this.url = link;
		this.data = ent.modified;
		this.objectType = ent.mimeType;
	
		if(objectType.contains("image")) 
			this.icon = "page_white_picture48";
		else 
			this.icon = "page_white_acrobat48";
		
		this.username = username;
	}
	
	
	public void getFromJson(StringReader sr) {
		ParseJSON parser = new ParseJSON(this);
		try {
			parser.readJson(sr);
		} catch (IOException e) {
			Log.d(MEDIA, e.getMessage());
		}
	}
	
	
	public String getWritableJson() {
		StringWriter sw = new StringWriter();
		ParseJSON parser = new ParseJSON(this);
		try {
			parser.writeJson(sw);
		} catch (IOException e) {
			Log.d(MEDIA, e.getMessage());
			return null;
		}
		return sw.toString();
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
	
	private String username;
	private String url;
	private String data;
	private String objectType;
	private String icon;
	private final String MEDIA = "MediaType";
}
