package com.project.mantle_v1.notification_home;

public class Note {
	public String user;
	public String content;
	public String id;
	
	public Note(String username, String content, String id) {
		this.user = username;
		this.content = content;
		this.id = id;
	}
	
	@Override
	public String toString() {
		return content;
	}
}