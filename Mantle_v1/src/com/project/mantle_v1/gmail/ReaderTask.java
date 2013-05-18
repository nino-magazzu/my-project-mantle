package com.project.mantle_v1.gmail;

import android.util.Log;

import com.project.mantle_v1.notification_home.MyHandler;

public class ReaderTask extends Thread {
	private final String TAG = this.getClass().getSimpleName();
	private MyHandler handler;
	private String account;
	private String pass;

	public ReaderTask(MyHandler handler, String email, String pass) {
		this.handler = handler;
		this.pass = pass;
		this.account = email.substring(0, email.indexOf("@"));
	}

	@Override
	public void run() {
		try {

			/*
			 * WARINING: inserire funzioni che leggano i dati relativi
			 * all'indirizzo email di default del dispositivo. Classe da
			 * migliorare con l'integrazione di altri webmail service se magari
			 * si volesse attuare un porting su dispositivi Apple
			 */

			Reader read = new Reader(account, pass, handler);
			read.detectMail();
			
			while(true){
				//Reader readerNew = new Reader(account, pass, handler);
				read.detectMail();
				Thread.sleep(10000);
				Log.d(TAG, "Check for a new email");
			}
			

		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
			Log.d(TAG, account);
		}
	}
}
