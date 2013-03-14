package com.project.mantle_v1.parser;

import com.dropbox.client2.DropboxAPI.Entry;

public class MediaType {
 
	public MediaType(Entry ent, String link) {
		this.url = link;
		this.data = ent.modified;
		this.objectType = ent.mimeType;
		
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
	
	public boolean isImage() {
		return isImage;
	}

	public void setImage(boolean isImage) {
		this.isImage = isImage;
	}
	
	private String username;
	private String url;
	private String data;
	private String objectType;
	private boolean isImage;
}
