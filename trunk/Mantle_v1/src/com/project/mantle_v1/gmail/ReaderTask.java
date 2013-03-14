package com.project.mantle_v1.gmail;

import android.os.Handler;
import android.util.Log;

public class ReaderTask extends Thread {
	private final String TAG = "READER TASK";
	private Handler handler;
	private Reader email;
	  
	public ReaderTask(Handler handler){
		this.handler = handler;
	}
	
	@Override
	public void run() {
		try {
			
			/*
			 * 		WARINING: inserire funzioni che leggano i dati relativi all'indirizzo email di default del dispositivo. 
			 * 		Classe da migliorare con l'integrazione di altri webmail service se magari si volesse attuare un porting su 
			 * 		dispositivi Apple
			 */
			
			
			email = new Reader("nmagazzu", "89vale46", handler);
			email.readMail();
			email.readNewMail();

		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
		}
	}
}
