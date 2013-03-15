package com.project.mantle_v1.parser;

import com.dropbox.client2.DropboxAPI.Entry;

public class MediaType {
 
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
}
