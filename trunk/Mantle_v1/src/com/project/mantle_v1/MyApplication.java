package com.project.mantle_v1;

import android.app.Application;

public class MyApplication extends Application {

	public MantleFile getMedia() {
		return media;
	}

	public void setMedia(MantleFile media) {
		this.media = media;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPswEmail() {
		return pswEmail;
	}

	public void setPswEmail(String pswEmail) {
		this.pswEmail = pswEmail;
	}

	private MantleFile media;
	private String username;
	private String email;
	private String pswEmail;
}
