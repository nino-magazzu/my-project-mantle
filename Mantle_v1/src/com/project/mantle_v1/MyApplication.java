package com.project.mantle_v1;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

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

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setPswEmail(String pswEmail) {
		this.pswEmail = pswEmail;
	}

	public DropboxAPI<AndroidAuthSession> getmApi() {
		return mApi;
	}

	public void setmApi(DropboxAPI<AndroidAuthSession> mApi) {
		this.mApi = mApi;
	}

	private MantleFile media;
	private String username;
	private String email;
	private String pswEmail;
	private int ID;
	private DropboxAPI<AndroidAuthSession> mApi;
}
